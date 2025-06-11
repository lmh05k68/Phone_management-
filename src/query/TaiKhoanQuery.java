package query;
import java.util.logging.Level;
import java.util.logging.Logger;
import dbConnection.DBConnection;
import model.TaiKhoan;

import java.sql.*;
public class TaiKhoanQuery {
	private static final Logger logger = Logger.getLogger(TaiKhoanQuery.class.getName());
    public static boolean exists(String username) {
        String sql = "SELECT 1 FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean insertTaiKhoanInTransaction(TaiKhoan tk, Connection conn) throws SQLException {
        String sql = "INSERT INTO TaiKhoan (TenDangNhap, MatKhau, VaiTro, MaNV, MaKH) VALUES (?, ?, ?::user_role, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tk.getTenDangNhap());
            stmt.setString(2, tk.getMatKhau()); // Lưu plain text theo yêu cầu
            stmt.setString(3, tk.getVaiTro().getValue()); 
            if (tk.getMaNV() != null) {
                stmt.setInt(4, tk.getMaNV());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            if (tk.getMaKH() != null) {
                stmt.setInt(5, tk.getMaKH());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;
        }
    }
    public static boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static TaiKhoan getTaiKhoanByUsername(String username) {
        String sql = "SELECT idTaiKhoan, TenDangNhap, MatKhau, VaiTro, MaNV, MaKH FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TaiKhoan.VaiTro vaiTro = TaiKhoan.VaiTro.fromString(rs.getString("VaiTro"));
                    
                    Integer maNV = (Integer) rs.getObject("MaNV");
                    Integer maKH = (Integer) rs.getObject("MaKH");
                    
                    return new TaiKhoan(
                        rs.getInt("idTaiKhoan"),
                        rs.getString("TenDangNhap"),
                        rs.getString("MatKhau"),
                        vaiTro,
                        maNV,
                        maKH
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi SQL khi lấy tài khoản: " + username, e);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Lỗi dữ liệu: Vai trò không hợp lệ trong CSDL cho username: " + username, e);
        }
        return null;
    }
}