package query;

import dbConnection.DBConnection;
import model.TaiKhoan;
import java.sql.*;

public class TaiKhoanQuery {
    public static boolean insertTaiKhoanInTransaction(TaiKhoan tk, Connection conn) throws SQLException {
        String sql = "INSERT INTO taikhoan (tendangnhap, matkhau, vaitro, madoituong) VALUES (?, ?, ?, ?)";
        System.out.println("TK_QUERY (insertInTransaction): TenDangNhap=" + tk.getTenDangNhap() + ", MaDoiTuong=" + tk.getMaDoiTuong());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tk.getTenDangNhap());
            stmt.setString(2, tk.getMatKhau()); 
            stmt.setString(3, tk.getVaiTro().toLowerCase());
            if (tk.getMaDoiTuong() != null) {
                stmt.setInt(4, tk.getMaDoiTuong()); 
            } else {
                stmt.setNull(4, Types.INTEGER); 
            }
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("TK_QUERY (insertInTransaction): TaiKhoan được chèn thành công cho: " + tk.getTenDangNhap());
                return true;
            } else {
                System.err.println("TK_QUERY (insertInTransaction): Chèn TaiKhoan thất bại cho: " + tk.getTenDangNhap());
                return false;
            }
        }
        // SQLException sẽ được ném ra nếu có lỗi
    }


    /**
     * Kiểm tra xem tên đăng nhập đã tồn tại chưa.
     * @param username Tên đăng nhập cần kiểm tra.
     * @return true nếu tồn tại, false nếu không hoặc có lỗi.
     */
    public static boolean exists(String username) {
        String sql = "SELECT 1 FROM taikhoan WHERE tendangnhap = ?"; 
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("TK_QUERY (exists): Lỗi SQL khi kiểm tra tồn tại username '" + username + "': " + e.getMessage());
            e.printStackTrace();
            return false; // Hoặc true tùy logic, false an toàn hơn
        }
    }

    /**
     * Xác thực đăng nhập.
     * @param username Tên đăng nhập.
     * @param password Mật khẩu (chưa băm).
     * @return true nếu hợp lệ.
     */
    public static boolean verifyLogin(String username, String password) {
        String sql = "SELECT matkhau FROM taikhoan WHERE tendangnhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("matkhau");
                    // Trong thực tế, bạn sẽ so sánh password đã băm với storedPassword (đã băm)
                    // Ví dụ: return BCrypt.checkpw(password, storedPassword);
                    return password.equals(storedPassword); // Chỉ dùng cho demo nếu chưa băm
                }
            }
        } catch (SQLException e) {
            System.err.println("TK_QUERY (verifyLogin): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy vai trò của người dùng.
     * @param username Tên đăng nhập.
     * @return Chuỗi vai trò hoặc null.
     */
    public static String getRole(String username) {
        String sql = "SELECT vaitro FROM taikhoan WHERE tendangnhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("vaitro");
                }
            }
        } catch (SQLException e) {
            System.err.println("TK_QUERY (getRole): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cập nhật mật khẩu.
     * @param username Tên đăng nhập.
     * @param newPassword Mật khẩu mới (nên được băm).
     * @return true nếu thành công.
     */
    public static boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE taikhoan SET matkhau = ? WHERE tendangnhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword); // Nhắc nhở: Băm mật khẩu
            stmt.setString(2, username.trim());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("TK_QUERY (updatePassword): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy MaDoiTuong (dưới dạng Integer) từ tên đăng nhập.
     * @param username Tên đăng nhập.
     * @return MaDoiTuong (Integer) hoặc null.
     */
    public static Integer getMaDoiTuong(String username) { // Trả về Integer để có thể là null
        String sql = "SELECT madoituong FROM taikhoan WHERE tendangnhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Cột madoituong là INT trong DB, getInt trả về 0 nếu giá trị là SQL NULL.
                    // Cần kiểm tra wasNull() để phân biệt giữa 0 và NULL.
                    int ma = rs.getInt("madoituong");
                    if (rs.wasNull()) {
                        return null; // Trả về null nếu trong DB là NULL
                    }
                    return ma; // Trả về giá trị int
                }
            }
        } catch (SQLException e) {
            System.err.println("TK_QUERY (getMaDoiTuong): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy MaKH (MaDoiTuong) từ tên đăng nhập, chỉ khi vai trò là 'khachhang'.
     * @param tenDangNhap Tên đăng nhập.
     * @return MaKH (Integer) hoặc null.
     */
    public static Integer getMaKHByTenDangNhap(String tenDangNhap) { // Trả về Integer
        String sql = "SELECT madoituong FROM taikhoan WHERE tendangnhap = ? AND vaitro = 'khachhang'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int ma = rs.getInt("madoituong");
                    if (rs.wasNull()) return null; // Nên là không thể xảy ra nếu có ràng buộc
                    return ma;
                }
            }
        } catch (SQLException e) {
            System.err.println("TK_QUERY (getMaKHByTenDangNhap): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Bỏ hàm insert(String username, String password, String vaiTro, String maDoiTuong) cũ
    // nếu nó không còn được sử dụng và đã được thay thế bằng phiên bản transaction.
    // Hoặc, nếu vẫn cần, đảm bảo nó cũng xử lý MaDoiTuong như một Integer.
}