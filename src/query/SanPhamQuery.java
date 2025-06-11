package query;

import dbConnection.DBConnection;
import model.SanPham;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SanPhamQuery {

    private static final Logger logger = Logger.getLogger(SanPhamQuery.class.getName());
    public static List<SanPham> getAllWithTonKho() {
        List<SanPham> dsSP = new ArrayList<>();
        String sql = "SELECT " +
                     "  sp.MaSP, sp.TenSP, sp.Mau, sp.GiaNiemYet, sp.NuocSX, sp.HangSX, " +
                     "  COUNT(spct.MaSPCuThe) AS SoLuongTon " +
                     "FROM SanPham sp " +
                     "LEFT JOIN SanPhamCuThe spct ON sp.MaSP = spct.MaSP AND spct.TrangThai = 'Trong Kho' " +
                     "GROUP BY sp.MaSP, sp.TenSP, sp.Mau, sp.GiaNiemYet, sp.NuocSX, sp.HangSX " +
                     "ORDER BY sp.TenSP ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SanPham sp = new SanPham(
                        rs.getInt("MaSP"),
                        rs.getString("TenSP"),
                        rs.getString("Mau"),
                        rs.getBigDecimal("GiaNiemYet"),
                        rs.getString("NuocSX"),
                        rs.getString("HangSX")
                );
                sp.setSoLuongTon(rs.getInt("SoLuongTon"));
                dsSP.add(sp);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách sản phẩm kèm tồn kho.", e);
        }
        return dsSP;
    }

    /**
     * Lấy danh sách tất cả sản phẩm (không bao gồm tồn kho).
     * @return Danh sách sản phẩm.
     */
    public static List<SanPham> getAll() {
        List<SanPham> dsSP = new ArrayList<>();
        String sql = "SELECT MaSP, TenSP, Mau, GiaNiemYet, NuocSX, HangSX FROM SanPham ORDER BY TenSP ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dsSP.add(new SanPham(
                        rs.getInt("MaSP"),
                        rs.getString("TenSP"),
                        rs.getString("Mau"),
                        rs.getBigDecimal("GiaNiemYet"),
                        rs.getString("NuocSX"),
                        rs.getString("HangSX")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách sản phẩm.", e);
        }
        return dsSP;
    }

    /**
     * Thêm một sản phẩm mới vào CSDL và trả về ID được tạo tự động.
     * @param sp Đối tượng SanPham chứa thông tin cần thêm.
     * @return ID của sản phẩm mới nếu thành công, ngược lại trả về null.
     */
    public static Integer insertSanPhamAndGetId(SanPham sp) {
        String sql = "INSERT INTO SanPham (TenSP, Mau, GiaNiemYet, NuocSX, HangSX) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, sp.getTenSP());
            pstmt.setString(2, sp.getMau());
            pstmt.setBigDecimal(3, sp.getGiaNiemYet());
            pstmt.setString(4, sp.getNuocSX());
            pstmt.setString(5, sp.getHangSX());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Trả về ID
                }
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                logger.log(Level.WARNING, "Lỗi khi thêm sản phẩm mới: Tên sản phẩm có thể đã tồn tại.", e);
            } else {
                logger.log(Level.SEVERE, "Lỗi khi thêm sản phẩm mới.", e);
            }
        }
        return null;
    }

    /**
     * Thêm một sản phẩm mới vào CSDL.
     * Phương thức này được tối ưu bằng cách gọi lại phương thức insertSanPhamAndGetId đã có.
     * @param sp Đối tượng SanPham chứa thông tin cần thêm.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public static boolean insertSanPham(SanPham sp) {
        return insertSanPhamAndGetId(sp) != null;
    }

    /**
     * Cập nhật thông tin một sản phẩm đã có trong CSDL.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public static boolean updateSanPham(SanPham sp) {
        String sql = "UPDATE SanPham SET TenSP = ?, Mau = ?, GiaNiemYet = ?, NuocSX = ?, HangSX = ? WHERE MaSP = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sp.getTenSP());
            pstmt.setString(2, sp.getMau());
            pstmt.setBigDecimal(3, sp.getGiaNiemYet());
            pstmt.setString(4, sp.getNuocSX());
            pstmt.setString(5, sp.getHangSX());
            pstmt.setInt(6, sp.getMaSP());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật sản phẩm " + sp.getMaSP(), e);
            return false;
        }
    }

    /**
     * Xóa một sản phẩm khỏi CSDL dựa vào mã sản phẩm.
     * @return true nếu xóa thành công, false nếu thất bại (thường do ràng buộc khóa ngoại).
     */
    public static boolean deleteSanPham(int maSP) {
        String sql = "DELETE FROM SanPham WHERE MaSP = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maSP);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                logger.log(Level.WARNING, "Không thể xóa sản phẩm " + maSP + " do đang được tham chiếu ở nơi khác (ví dụ: trong hóa đơn).");
            } else {
                logger.log(Level.SEVERE, "Lỗi khi xóa sản phẩm " + maSP, e);
            }
            return false;
        }
    }

    /**
     * Lấy danh sách các hãng sản xuất (không trùng lặp) để hiển thị trên ComboBox.
     * @return Danh sách tên các hãng.
     */
    public static List<String> getAllHangSX() {
        List<String> hangList = new ArrayList<>();
        String sql = "SELECT DISTINCT HangSX FROM SanPham WHERE HangSX IS NOT NULL AND HangSX != '' ORDER BY HangSX";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                hangList.add(rs.getString("HangSX"));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách hãng sản xuất.", e);
        }
        return hangList;
    }

    /**
     * *** PHƯƠNG THỨC MỚI ĐƯỢC THÊM VÀO ***
     * Lấy giá nhập gần đây nhất của một sản phẩm để gợi ý cho người dùng.
     * @param maSP Mã sản phẩm cần tìm giá.
     * @return Giá nhập gần nhất dưới dạng BigDecimal, hoặc null nếu chưa từng nhập hoặc có lỗi.
     */
    public static BigDecimal getLastImportPrice(int maSP) {
        String sql = "SELECT spct.GiaNhap " +
                     "FROM SanPhamCuThe spct " +
                     "JOIN HoaDonNhap hdn ON spct.MaHDN = hdn.MaHDN " +
                     "WHERE spct.MaSP = ? " +
                     "ORDER BY hdn.NgayNhap DESC, hdn.MaHDN DESC " + // Sắp xếp theo ngày nhập mới nhất
                     "LIMIT 1"; // Chỉ lấy 1 kết quả đầu tiên

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maSP);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("GiaNhap");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy giá nhập cuối cùng cho MaSP: " + maSP, e);
        }
        // Trả về null nếu không tìm thấy giá hoặc có lỗi
        return null;
    }
}