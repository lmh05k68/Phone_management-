package query;

import dbConnection.DBConnection;
import model.NhanVien;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger; 
public class NhanVienQuery {
    private static final String SQLSTATE_FOREIGN_KEY_VIOLATION = "23503"; 
    private static final Logger logger = Logger.getLogger(NhanVienQuery.class.getName());
    private static final String SQLSTATE_UNIQUE_VIOLATION = "23505"; // Postgres SQLState for unique_violation

    // Helper to create NhanVien from ResultSet
    private static NhanVien mapResultSetToNhanVien(ResultSet rs) throws SQLException {
        LocalDate ngaySinh = null;
        Date sqlDate = rs.getDate("NgaySinh");
        if (!rs.wasNull()) {
            ngaySinh = sqlDate.toLocalDate();
        }
        return new NhanVien(
                rs.getInt("MaNV"),
                rs.getString("TenNV"),
                ngaySinh,
                rs.getBigDecimal("Luong"),
                rs.getString("SoDienThoai")
        );
    }

    public static Integer insertNhanVienAndGetId(NhanVien nv, Connection conn) throws SQLException {
         // Connection should be managed outside if part of a transaction
        String sql = "INSERT INTO NhanVien (TenNV, NgaySinh, Luong, SoDienThoai) VALUES (?, ?, ?, ?)";
         boolean closeConnection = (conn == null);
         Connection connection = (conn == null) ? DBConnection.getConnection() : conn;

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nv.getTenNV());
            if (nv.getNgaySinh() != null) {
                pstmt.setDate(2, Date.valueOf(nv.getNgaySinh()));
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            // Use setBigDecimal
             if(nv.getLuong() != null) {
                  pstmt.setBigDecimal(3, nv.getLuong());
             } else {
                   pstmt.setNull(3, Types.DECIMAL);
             }
            pstmt.setString(4, nv.getSoDienThoai());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                 throw new SQLException("Chèn NhanVien thất bại, không có hàng nào được thêm.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // MaNV
                } else {
                    throw new SQLException("Chèn NhanVien thành công nhưng không lấy được ID.");
                }
            }
        } catch (SQLException e) {
             logger.log(Level.SEVERE, "Lỗi SQL khi thêm nhân viên: " + e.getMessage(), e);
              if (SQLSTATE_UNIQUE_VIOLATION.equals(e.getSQLState()) && e.getMessage().toLowerCase().contains("sodienthoai")) {
                  throw new SQLException("Số điện thoại '" + nv.getSoDienThoai() + "' đã tồn tại.", e.getSQLState(), e);
              }
             throw e; // Re-throw
        } finally {
              if(closeConnection && connection != null) {
                 try { connection.close(); } catch(SQLException ignored){}
              }
        }
    }

    public static List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT MaNV, TenNV, NgaySinh, Luong, SoDienThoai FROM NhanVien ORDER BY MaNV ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToNhanVien(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách nhân viên.", e);
        }
        return list;
    }

    public static NhanVien getNhanVienById(int maNV) {
        String sql = "SELECT MaNV, TenNV, NgaySinh, Luong, SoDienThoai FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maNV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                   return mapResultSetToNhanVien(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi SQL khi tìm nhân viên ID=" + maNV + ": " + e.getMessage(), e);
             // Return null or throw RuntimeException
        }
        return null; // Not found or error
    }
    public static boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET TenNV = ?, NgaySinh = ?, Luong = ?, SoDienThoai = ? WHERE MaNV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nv.getTenNV());
            if (nv.getNgaySinh() != null) {
                pstmt.setDate(2, Date.valueOf(nv.getNgaySinh()));
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            pstmt.setBigDecimal(3, nv.getLuong());
            pstmt.setString(4, nv.getSoDienThoai());
            pstmt.setInt(5, nv.getMaNV());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (SQLSTATE_UNIQUE_VIOLATION.equals(e.getSQLState())) {
                logger.log(Level.WARNING, "Lỗi cập nhật: Số điện thoại '" + nv.getSoDienThoai() + "' đã được sử dụng.", e);
            } else {
                logger.log(Level.SEVERE, "Lỗi SQL khi cập nhật nhân viên ID: " + nv.getMaNV(), e);
            }
            return false;
        }
    }
    public static boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (TenNV, NgaySinh, Luong, SoDienThoai) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nv.getTenNV());
            if (nv.getNgaySinh() != null) {
                pstmt.setDate(2, Date.valueOf(nv.getNgaySinh()));
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            pstmt.setBigDecimal(3, nv.getLuong());
            pstmt.setString(4, nv.getSoDienThoai());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (SQLSTATE_UNIQUE_VIOLATION.equals(e.getSQLState())) {
                logger.log(Level.WARNING, "Lỗi thêm nhân viên: Số điện thoại '" + nv.getSoDienThoai() + "' đã tồn tại.", e);
            } else {
                logger.log(Level.SEVERE, "Lỗi SQL khi thêm nhân viên mới.", e);
            }
            return false;
        }
    }
    public static boolean delete(int maNV) {
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maNV);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (SQLSTATE_FOREIGN_KEY_VIOLATION.equals(e.getSQLState())) {
                logger.log(Level.WARNING, "Không thể xóa NV ID " + maNV + " do đang được sử dụng trong hóa đơn hoặc tài khoản.", e);
            } else {
                logger.log(Level.SEVERE, "Lỗi SQL khi xóa nhân viên ID: " + maNV, e);
            }
            return false;
        }
    }
}