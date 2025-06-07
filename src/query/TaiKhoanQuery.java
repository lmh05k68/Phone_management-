package query;

import dbConnection.DBConnection;
import model.TaiKhoan;

import java.sql.*;

public class TaiKhoanQuery {

    /**
     * Thêm một tài khoản mới (dùng connection riêng).
     * Giữ lại nếu bạn dùng ở nơi khác không cần transaction.
     */
    public static boolean insert(String username, String password, String vaiTro, String maDoiTuong) {
        // Giả sử tên cột CSDL là chữ thường
        String sql = "INSERT INTO taikhoan (tendangnhap, matkhau, vaitro, madoituong) VALUES (?, ?, ?, ?)";
        System.out.println("DEBUG TK_QUERY (static insert): Username=" + username);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            stmt.setString(2, password); // Mật khẩu nên được băm
            stmt.setString(3, vaiTro.trim().toLowerCase());
            
            if (maDoiTuong != null && !maDoiTuong.trim().isEmpty()) {
                stmt.setString(4, maDoiTuong.trim());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            boolean result = stmt.executeUpdate() > 0;
            System.out.println("DEBUG TK_QUERY (static insert): Result=" + result);
            return result;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi insert tài khoản (static): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Chèn một tài khoản mới vào CSDL (dùng cho RegisterController trong transaction).
     * @param tk Đối tượng TaiKhoan chứa thông tin.
     * @param conn Connection hiện tại từ RegisterController.
     * @return true nếu thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public boolean insertTaiKhoanInTransaction(TaiKhoan tk, Connection conn) throws SQLException {
        String sql = "INSERT INTO taikhoan (tendangnhap, matkhau, vaitro, madoituong) VALUES (?, ?, ?, ?)";
        System.out.println("DEBUG TK_QUERY (insertTaiKhoanInTransaction): TenDangNhap=" + tk.getTenDangNhap());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tk.getTenDangNhap());
            stmt.setString(2, tk.getMatKhau()); // Mật khẩu nên được băm
            stmt.setString(3, tk.getVaiTro().toLowerCase());
            if (tk.getMaDoiTuong() != null && !tk.getMaDoiTuong().trim().isEmpty()) {
                stmt.setString(4, tk.getMaDoiTuong());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
        // SQLException sẽ được ném ra
    }


    /**
     * Kiểm tra xem tên đăng nhập đã tồn tại chưa.
     */
    public static boolean exists(String username) {
        String sql = "SELECT 1 FROM taikhoan WHERE tendangnhap = ?"; // Giả sử tên cột là tendangnhap
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiểm tra tồn tại username '" + username + "': " + e.getMessage());
            e.printStackTrace();
            return true; // An toàn: nếu lỗi thì coi như đã tồn tại
        }
    }

    // ... (Các phương thức verifyLogin, getRole, updatePassword, getMaDoiTuong, getMaKHByTenDangNhap giữ nguyên)
    // Đảm bảo tên cột trong SQL là chữ thường nếu CSDL của bạn như vậy.
    public static boolean verifyLogin(String username, String password) {
        String sql = "SELECT matkhau FROM taikhoan WHERE tendangnhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("matkhau");
                return password.equals(storedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getRole(String username) {
        String sql = "SELECT vaitro FROM taikhoan WHERE tendangnhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("vaitro");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE taikhoan SET matkhau = ? WHERE tendangnhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username.trim());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getMaDoiTuong(String username) {
        String sql = "SELECT madoituong FROM taikhoan WHERE tendangnhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("madoituong");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMaKHByTenDangNhap(String tenDangNhap) {
        String sql = "SELECT madoituong FROM taikhoan WHERE tendangnhap = ? AND vaitro = 'khachhang'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("madoituong");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}