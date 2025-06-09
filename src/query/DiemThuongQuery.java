package query;

import model.DiemThuong;
import dbConnection.DBConnection; // Giả sử bạn có class này

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DiemThuongQuery {

    /**
     * Chèn một bản ghi điểm thưởng mới và trả về ID tự sinh (MaTichDiem).
     * Phương thức này nên được gọi trong một transaction do controller quản lý.
     * @param dt Đối tượng DiemThuong.
     * @param conn Connection hiện tại của transaction.
     * @return MaTichDiem (Integer) tự sinh, hoặc null nếu thất bại.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public static Integer insertDiemThuongAndGetId(DiemThuong dt, Connection conn) throws SQLException {
        String sql = "INSERT INTO diemthuong (makh, tenkh, madonhang, sodiem, ngaytichluy, sdtkh) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        System.out.println("DIEMTHUONG_QUERY (insertAndGetId): MaKH=" + dt.getMaKH() +
                           ", MaDonHang=" + dt.getMaDonHang() + ", SoDiem=" + dt.getSoDiem());

        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, dt.getMaKH());
            stmt.setString(2, dt.getTenKH());
            stmt.setInt(3, dt.getMaDonHang()); // MaDonHang (MaHDX) là int
            stmt.setInt(4, dt.getSoDiem());
            stmt.setDate(5, Date.valueOf(dt.getNgayTichLuy()));
            stmt.setString(6, dt.getSdtKH());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("DIEMTHUONG_QUERY (insertAndGetId): Chèn điểm thưởng thất bại, không có hàng nào được thêm.");
                // Ném lỗi để transaction rollback
                throw new SQLException("Chèn điểm thưởng thất bại, không có hàng nào được thêm.");
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1); // Lấy maTichDiem tự sinh
                System.out.println("DIEMTHUONG_QUERY (insertAndGetId): Điểm thưởng được chèn với MaTichDiem: " + id);
                return id;
            } else {
                System.err.println("DIEMTHUONG_QUERY (insertAndGetId): Chèn điểm thưởng thành công nhưng không lấy được ID tự sinh.");
                throw new SQLException("Không lấy được ID tự sinh cho bản ghi điểm thưởng sau khi chèn thành công.");
            }
        } catch (SQLException e) {
            System.err.println("DIEMTHUONG_QUERY (insertAndGetId): SQLException: " + e.getMessage());
            throw e; // Ném lại lỗi để controller xử lý transaction
        } finally {
            if (generatedKeys != null) {
                try { generatedKeys.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            // Connection KHÔNG được đóng ở đây, do controller quản lý
        }
    }

    /**
     * Lấy tổng số điểm đã tích lũy của một khách hàng từ bảng diemthuong.
     * Phương thức này có thể hữu ích cho mục đích báo cáo hoặc kiểm tra,
     * nhưng tổng điểm chính thức nên được lấy từ bảng khachhang.sodiemtichluy.
     * @param maKH Mã khách hàng (int).
     * @return Tổng số điểm tích lũy từ bảng diemthuong cho khách hàng đó.
     */
    public static int getTongDiemTrongBangDiemThuong(int maKH) {
        String sql = "SELECT COALESCE(SUM(sodiem), 0) AS tong_diem FROM diemthuong WHERE makh = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection(); // Tự quản lý connection cho thao tác đọc này
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maKH);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("tong_diem");
            }
        } catch (SQLException e) {
            System.err.println("DIEMTHUONG_QUERY (getTongDiemTrongBangDiemThuong): Lỗi SQL cho MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return 0; // Trả về 0 nếu có lỗi hoặc không có bản ghi
    }

    /**
     * Lấy lịch sử tích điểm của một khách hàng.
     * @param maKH Mã khách hàng (int).
     * @return Danh sách các đối tượng DiemThuong.
     */
    public static List<DiemThuong> getLichSuTichDiemByMaKH(int maKH) {
        List<DiemThuong> list = new ArrayList<>();
        String sql = "SELECT matichdiem, makh, tenkh, madonhang, sodiem, ngaytichluy, sdtkh " +
                     "FROM diemthuong WHERE makh = ? ORDER BY ngaytichluy DESC, matichdiem DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maKH);
            rs = stmt.executeQuery();
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
        } catch (SQLException e) {
            System.err.println("DIEMTHUONG_QUERY (getLichSuTichDiemByMaKH): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }
}