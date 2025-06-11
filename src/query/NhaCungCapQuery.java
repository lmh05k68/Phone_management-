package query;

import dbConnection.DBConnection; 
import model.NhaCungCap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NhaCungCapQuery {

    private static final Logger logger = Logger.getLogger(NhaCungCapQuery.class.getName());
    private static final String SQLSTATE_FOREIGN_KEY_VIOLATION = "23503";
    public static List<NhaCungCap> getAll() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT MaNCC, TenNCC, DiaChi, SdtNCC FROM NhaCungCap ORDER BY TenNCC ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToNhaCungCap(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách nhà cung cấp.", e);
            // Có thể ném ra một RuntimeException ở đây nếu muốn dừng chương trình
            // throw new RuntimeException("Không thể tải danh sách nhà cung cấp.", e);
        }
        return list;
    }

    /**
     * Tìm kiếm nhà cung cấp theo từ khóa (tên, địa chỉ, hoặc SĐT).
     * @param keyword Từ khóa để tìm kiếm.
     * @return Danh sách các nhà cung cấp khớp với từ khóa.
     */
    public static List<NhaCungCap> search(String keyword) {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT MaNCC, TenNCC, DiaChi, SdtNCC FROM NhaCungCap " +
                     "WHERE LOWER(TenNCC) LIKE LOWER(?) OR LOWER(DiaChi) LIKE LOWER(?) OR SdtNCC LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword.trim() + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhaCungCap(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tìm kiếm nhà cung cấp với từ khóa: " + keyword, e);
        }
        return list;
    }

    /**
     * Lấy thông tin một nhà cung cấp dựa trên ID.
     * @param maNCC ID của nhà cung cấp.
     * @return Đối tượng NhaCungCap hoặc null nếu không tìm thấy.
     */
    public static NhaCungCap getById(int maNCC) {
        String sql = "SELECT MaNCC, TenNCC, DiaChi, SdtNCC FROM NhaCungCap WHERE MaNCC = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maNCC);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNhaCungCap(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy nhà cung cấp theo ID: " + maNCC, e);
        }
        return null;
    }

    /**
     * Chèn một nhà cung cấp mới và trả về ID được tạo.
     * @param ncc Đối tượng NhaCungCap chứa thông tin cần thêm.
     * @return ID của nhà cung cấp mới, hoặc null nếu thất bại.
     */
    public static Integer insertAndGetId(NhaCungCap ncc) {
        String sql = "INSERT INTO NhaCungCap (TenNCC, DiaChi, SdtNCC) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ncc.getTenNCC());
            pstmt.setString(2, ncc.getDiaChi());
            pstmt.setString(3, ncc.getSdtNCC());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warning("Thêm nhà cung cấp thất bại, không có hàng nào được thêm.");
                return null;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    logger.warning("Thêm nhà cung cấp thành công nhưng không lấy được ID.");
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi SQL khi thêm nhà cung cấp mới.", e);
            return null;
        }
    }

    /**
     * Cập nhật thông tin một nhà cung cấp.
     * @param ncc Đối tượng NhaCungCap chứa thông tin mới.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public static boolean update(NhaCungCap ncc) {
        String sql = "UPDATE NhaCungCap SET TenNCC = ?, DiaChi = ?, SdtNCC = ? WHERE MaNCC = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ncc.getTenNCC());
            pstmt.setString(2, ncc.getDiaChi());
            pstmt.setString(3, ncc.getSdtNCC());
            pstmt.setInt(4, ncc.getMaNCC());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật nhà cung cấp ID: " + ncc.getMaNCC(), e);
            return false;
        }
    }

    /**
     * Xóa một nhà cung cấp.
     * @param maNCC ID của nhà cung cấp cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại (thường do ràng buộc khóa ngoại).
     */
    public static boolean delete(int maNCC) {
        String sql = "DELETE FROM NhaCungCap WHERE MaNCC = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maNCC);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (SQLSTATE_FOREIGN_KEY_VIOLATION.equals(e.getSQLState())) {
                logger.log(Level.WARNING, "Không thể xóa NCC ID " + maNCC + " do có ràng buộc khóa ngoại (đang được sử dụng trong hóa đơn nhập).");
            } else {
                logger.log(Level.SEVERE, "Lỗi khi xóa nhà cung cấp ID: " + maNCC, e);
            }
            return false;
        }
    }
    
    /**
     * Helper method để chuyển đổi một dòng trong ResultSet thành đối tượng NhaCungCap.
     * @param rs ResultSet đang trỏ đến một dòng dữ liệu.
     * @return Đối tượng NhaCungCap.
     * @throws SQLException nếu có lỗi khi đọc dữ liệu từ ResultSet.
     */
    private static NhaCungCap mapResultSetToNhaCungCap(ResultSet rs) throws SQLException {
        return new NhaCungCap(
            rs.getInt("MaNCC"),
            rs.getString("TenNCC"),
            rs.getString("DiaChi"),
            rs.getString("SdtNCC")
        );
    }
}