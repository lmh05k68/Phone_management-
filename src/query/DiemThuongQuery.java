package query;

import model.DiemThuong;
import dbConnection.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement; 
public class DiemThuongQuery {
    public boolean insertDiemThuong(DiemThuong dt, Connection conn) throws SQLException {
        System.out.println("DEBUG DIEMTHUONG_QUERY: insertDiemThuong - MaKH = " + dt.getMaKH() + ", SoDiem = " + dt.getSoDiem());
        String sql = "INSERT INTO diemthuong (makh, tenkh, madonhang, sodiem, ngaytichluy, sdtkh) "
                       + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dt.getMaKH());        // makh là int
            stmt.setString(2, dt.getTenKH());
            stmt.setInt(3, dt.getMaDonHang());   // madonhang là int
            stmt.setInt(4, dt.getSoDiem());
            stmt.setDate(5, Date.valueOf(dt.getNgayTichLuy()));
            stmt.setString(6, dt.getSdtKH());

            boolean result = stmt.executeUpdate() > 0;
            System.out.println("DEBUG DIEMTHUONG_QUERY: insertDiemThuong - executeUpdate result = " + result);
            return result;
        }
        // SQLException sẽ được ném ra để lớp gọi xử lý transaction
    }

    /**
     * Lấy tổng điểm đã tích lũy từ bảng DiemThuong cho một MaKH.
     * @param maKH Mã khách hàng (int).
     * @return Tổng số điểm.
     */
    public int layTongDiemDaTichLuyTuBangDiemThuong(int maKH) { // Đổi maKH thành int
        String sql = "SELECT COALESCE(SUM(sodiem), 0) FROM diemthuong WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection(); // Tự quản lý cho việc đọc
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maKH); // Sử dụng setInt
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi layTongDiemDaTichLuyTuBangDiemThuong cho MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Chèn một bản ghi DiemThuong và trả về ID tự sinh (maTichDiem).
     * Sử dụng Connection được truyền vào.
     * @param dt Đối tượng DiemThuong.
     * @param conn Connection để thực hiện thao tác.
     * @return ID tự sinh của bản ghi DiemThuong, hoặc null nếu thất bại.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public static Integer insertDiemThuongAndGetId(DiemThuong dt, Connection conn) throws SQLException {
        String sql = "INSERT INTO diemthuong (makh, tenkh, madonhang, sodiem, ngaytichluy, sdtkh) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        System.out.println("DIEMTHUONG_QUERY (insertAndGetId): MaKH=" + dt.getMaKH() + ", MaDonHang=" + dt.getMaDonHang());
        
        ResultSet generatedKeys = null;
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, dt.getMaKH());
            stmt.setString(2, dt.getTenKH());
            stmt.setInt(3, dt.getMaDonHang());
            stmt.setInt(4, dt.getSoDiem());
            stmt.setDate(5, Date.valueOf(dt.getNgayTichLuy()));
            stmt.setString(6, dt.getSdtKH());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                // Không throw SQLException trực tiếp ở đây để phù hợp với logic trả về null của SellProduct,
                // nhưng lớp gọi (SellProduct) sẽ bắt SQLException nếu có lỗi ở executeUpdate
                System.err.println("DIEMTHUONG_QUERY (insertAndGetId): Chèn điểm thưởng thất bại, không có hàng nào được thêm.");
                return null;
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                System.out.println("DIEMTHUONG_QUERY (insertAndGetId): Điểm thưởng được chèn với MaTichDiem: " + id);
                return id;
            } else {
                System.err.println("DIEMTHUONG_QUERY (insertAndGetId): Chèn điểm thưởng thành công nhưng không lấy được ID tự sinh.");
                return null; // Hoặc ném lỗi nếu đây là tình huống không mong muốn
            }
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { /* ignored */ }
            // PreparedStatement sẽ được đóng bởi try-with-resources
        }
         // SQLException sẽ được ném ra để lớp gọi xử lý transaction
    }
}