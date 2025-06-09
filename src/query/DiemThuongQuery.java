package query;

import model.DiemThuong;
import dbConnection.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList; // Nếu cần các phương thức get khác
import java.util.List;    // Nếu cần các phương thức get khác

public class DiemThuongQuery {

    /**
     * Chèn một bản ghi DiemThuong mới vào cơ sở dữ liệu và trả về maTichDiem tự sinh.
     * Phương thức này yêu cầu một Connection được truyền vào, phù hợp khi là một phần của transaction lớn hơn.
     * @param dt Đối tượng DiemThuong (không cần set MaTichDiem ban đầu).
     * @param conn Connection để thực hiện thao tác.
     * @return maTichDiem (Integer) tự sinh, hoặc null nếu thất bại.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public static Integer insertDiemThuongAndGetId(DiemThuong dt, Connection conn) throws SQLException {
        // matichdiem là SERIAL (int tự sinh) và là khóa chính, không cần truyền vào câu INSERT
        String sql = "INSERT INTO diemthuong (makh, tenkh, madonhang, sodiem, ngaytichluy, sdtkh) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        System.out.println("DIEMTHUONG_QUERY (insertAndGetId): MaKH=" + dt.getMaKH() +
                           ", MaDonHang=" + dt.getMaDonHang() + ", SoDiem=" + dt.getSoDiem());

        ResultSet generatedKeys = null;
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, dt.getMaKH());
            stmt.setString(2, dt.getTenKH());
            stmt.setInt(3, dt.getMaDonHang()); // MaDonHang (MaHDX) là int
            stmt.setInt(4, dt.getSoDiem());
            stmt.setDate(5, Date.valueOf(dt.getNgayTichLuy()));
            stmt.setString(6, dt.getSdtKH());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("DIEMTHUONG_QUERY (insertAndGetId): Chèn điểm thưởng thất bại, không có hàng nào được thêm.");
                // Không ném lỗi ở đây nếu controller có logic kiểm tra null,
                // nhưng nếu có lỗi thực sự, executeUpdate sẽ ném SQLException.
                return null;
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1); // Lấy maTichDiem tự sinh
                System.out.println("DIEMTHUONG_QUERY (insertAndGetId): Điểm thưởng được chèn với MaTichDiem: " + id);
                return id;
            } else {
                // Điều này không nên xảy ra nếu RETURN_GENERATED_KEYS được hỗ trợ và insert thành công
                System.err.println("DIEMTHUONG_QUERY (insertAndGetId): Chèn điểm thưởng thành công nhưng không lấy được ID tự sinh.");
                throw new SQLException("Không lấy được ID tự sinh cho bản ghi điểm thưởng sau khi chèn thành công.");
            }
        } finally {
            if (generatedKeys != null) {
                try {
                    generatedKeys.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Lấy tổng số điểm đã tích lũy của một khách hàng từ bảng diemthuong.
     * Lưu ý: Thông thường, tổng điểm nên được lưu và cập nhật trong bảng khachhang.
     * Phương thức này dùng để kiểm tra hoặc tính toán lại nếu cần.
     * @param maKH Mã khách hàng (int).
     * @return Tổng số điểm tích lũy từ bảng diemthuong.
     */
    public static int getTongDiemTrongBangDiemThuong(int maKH) {
        String sql = "SELECT COALESCE(SUM(sodiem), 0) AS tong_diem FROM diemthuong WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection(); // Tự quản lý connection cho thao tác đọc này
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("tong_diem");
                }
            }
        } catch (SQLException e) {
            System.err.println("DIEMTHUONG_QUERY (getTongDiemTrongBangDiemThuong): Lỗi SQL cho MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
        }
        return 0; // Trả về 0 nếu có lỗi hoặc không có bản ghi
    }


    // Bạn có thể thêm các phương thức khác nếu cần, ví dụ:
    // Lấy lịch sử tích điểm của một khách hàng
    public static List<DiemThuong> getLichSuTichDiemByMaKH(int maKH) {
        List<DiemThuong> list = new ArrayList<>();
        String sql = "SELECT matichdiem, makh, tenkh, madonhang, sodiem, ngaytichluy, sdtkh " +
                     "FROM diemthuong WHERE makh = ? ORDER BY ngaytichluy DESC, matichdiem DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DiemThuong dt = new DiemThuong(
                        rs.getInt("matichdiem"),
                        rs.getInt("makh"),
                        rs.getString("tenkh"),
                        rs.getInt("madonhang"),
                        rs.getInt("sodiem"),
                        rs.getDate("ngaytichluy").toLocalDate(),
                        rs.getString("sdtkh")
                    );
                    list.add(dt);
                }
            }
        } catch (SQLException e) {
            System.err.println("DIEMTHUONG_QUERY (getLichSuTichDiemByMaKH): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}