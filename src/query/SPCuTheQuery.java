package query;

import dbConnection.DBConnection;
import model.ChiTietDonHang;
import model.NhapHangItem;
import model.SPCuThe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SPCuTheQuery {

    private static final Logger logger = Logger.getLogger(SPCuTheQuery.class.getName());

    /**
     * Lấy danh sách các sản phẩm cụ thể đang ở trạng thái 'Trong Kho' để hiển thị cho việc bán hàng.
     * @return Danh sách các sản phẩm có thể bán.
     */
    public static List<ChiTietDonHang> getProductsForSale() {
        List<ChiTietDonHang> list = new ArrayList<>();
        String sql = "SELECT spct.MaSPCuThe, sp.TenSP, sp.Mau, sp.GiaNiemYet AS GiaBan " +
                     "FROM SanPhamCuThe spct " +
                     "JOIN SanPham sp ON spct.MaSP = sp.MaSP " +
                     "WHERE spct.TrangThai = 'Trong Kho' " +
                     "ORDER BY sp.TenSP, spct.MaSPCuThe";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new ChiTietDonHang(
                    rs.getString("MaSPCuThe"),
                    rs.getString("TenSP"),
                    rs.getString("Mau"),
                    rs.getBigDecimal("GiaBan")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách sản phẩm để bán.", e);
        }
        return list;
    }

    /**
     * Lấy chi tiết các sản phẩm trong một hóa đơn xuất cụ thể.
     * @param maHDX Mã hóa đơn xuất.
     * @return Danh sách chi tiết các sản phẩm.
     */
    public static List<ChiTietDonHang> getChiTietDonHangByHDX(int maHDX) {
        List<ChiTietDonHang> chiTietList = new ArrayList<>();
        String sql = "SELECT spct.MaSPCuThe, sp.TenSP, sp.Mau, spct.GiaXuat " +
                     "FROM SanPhamCuThe spct " +
                     "JOIN SanPham sp ON spct.MaSP = sp.MaSP " +
                     "WHERE spct.MaHDX = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maHDX);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chiTietList.add(new ChiTietDonHang(
                        rs.getString("MaSPCuThe"),
                        rs.getString("TenSP"),
                        rs.getString("Mau"),
                        rs.getBigDecimal("GiaXuat") // Lấy giá bán thực tế
                    ));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy chi tiết đơn hàng cho MaHDX " + maHDX, e);
        }
        return chiTietList;
    }

    /**
     * SỬA LỖI TẠI ĐÂY: Thêm phương thức mới để tìm sản phẩm theo cả MaHDX và MaKH.
     * Điều này đảm bảo khách hàng chỉ có thể xem/bảo hành sản phẩm từ hóa đơn của chính họ.
     *
     * @param maHDX Mã hóa đơn xuất.
     * @param maKH Mã khách hàng.
     * @return Danh sách chi tiết các sản phẩm nếu hóa đơn hợp lệ và thuộc về khách hàng.
     */
    public static List<ChiTietDonHang> getChiTietDonHangByHDXAndKH(int maHDX, int maKH) {
        List<ChiTietDonHang> chiTietList = new ArrayList<>();
        // Câu SQL này JOIN thêm bảng HoaDonXuat để có thể lọc theo MaKH
        String sql = "SELECT spct.MaSPCuThe, sp.TenSP, sp.Mau, spct.GiaXuat " +
                     "FROM SanPhamCuThe spct " +
                     "JOIN SanPham sp ON spct.MaSP = sp.MaSP " +
                     "JOIN HoaDonXuat hdx ON spct.MaHDX = hdx.MaHDX " +
                     "WHERE hdx.MaHDX = ? AND hdx.MaKH = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maHDX);
            stmt.setInt(2, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chiTietList.add(new ChiTietDonHang(
                        rs.getString("MaSPCuThe"),
                        rs.getString("TenSP"),
                        rs.getString("Mau"),
                        rs.getBigDecimal("GiaXuat")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy chi tiết đơn hàng cho MaHDX " + maHDX + " và MaKH " + maKH, e);
        }
        return chiTietList;
    }

    /**
     * Lấy thông tin một sản phẩm cụ thể bằng mã của nó.
     * @param maSPCuThe Mã sản phẩm cụ thể (IMEI/Serial).
     * @return Đối tượng SPCuThe nếu tìm thấy, ngược lại trả về null.
     */
    public static SPCuThe getById(String maSPCuThe) {
        String sql = "SELECT MaSPCuThe, MaSP, GiaNhap, GiaXuat, MaHDN, MaHDX, TrangThai FROM SanPhamCuThe WHERE MaSPCuThe = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maSPCuThe);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SPCuThe.TrangThaiSP trangThai = SPCuThe.TrangThaiSP.fromValue(rs.getString("TrangThai"));
                    
                    Integer maHDX = rs.getObject("MaHDX") != null ? rs.getInt("MaHDX") : null;

                    return new SPCuThe(
                        rs.getString("MaSPCuThe"),
                        rs.getInt("MaSP"),
                        rs.getBigDecimal("GiaNhap"),
                        rs.getBigDecimal("GiaXuat"),
                        rs.getInt("MaHDN"),
                        maHDX,
                        trangThai
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy SPCuThe by ID: " + maSPCuThe, e);
        }
        return null;
    }

    /**
     * Chèn một loạt sản phẩm cụ thể khi nhập hàng.
     * @param itemsToImport Danh sách các mặt hàng cần nhập.
     * @param maHDN Mã hóa đơn nhập tương ứng.
     * @param conn Đối tượng Connection để thực hiện trong cùng một giao dịch (transaction).
     * @throws SQLException Nếu có lỗi xảy ra.
     */
    public static void insertBatch(List<NhapHangItem> itemsToImport, int maHDN, Connection conn) throws SQLException {
        String sql = "INSERT INTO SanPhamCuThe (MaSPCuThe, MaSP, GiaNhap, MaHDN, TrangThai) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (NhapHangItem item : itemsToImport) {
                for (int i = 0; i < item.getSoLuong(); i++) {
                    // Tạo mã duy nhất bằng UUID để tránh trùng lặp
                    String maSPCuThe = String.format("%d-%s", item.getSanPham().getMaSP(), UUID.randomUUID().toString().substring(0, 8));

                    pstmt.setString(1, maSPCuThe);
                    pstmt.setInt(2, item.getSanPham().getMaSP());
                    pstmt.setBigDecimal(3, item.getGiaNhap());
                    pstmt.setInt(4, maHDN);
                    pstmt.setString(5, "Trong Kho"); // Sử dụng chuỗi trực tiếp khớp với CSDL
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch(); // Thực thi toàn bộ batch
        }
    }
    
    /**
     * Gán các sản phẩm đã bán vào một hóa đơn xuất.
     * @param maSPCuTheList Danh sách các mã sản phẩm cụ thể.
     * @param maHDX Mã hóa đơn xuất.
     * @param conn Đối tượng Connection để thực hiện trong cùng một giao dịch (transaction).
     * @return Số lượng dòng bị ảnh hưởng.
     * @throws SQLException Nếu có lỗi xảy ra.
     */
    public static int assignProductsToInvoice(List<String> maSPCuTheList, int maHDX, Connection conn) throws SQLException {
        String sql = "UPDATE SanPhamCuThe SET " +
                     "  MaHDX = ?, " +
                     "  GiaXuat = (SELECT GiaNiemYet FROM SanPham WHERE MaSP = SanPhamCuThe.MaSP) " +
                     "WHERE MaSPCuThe = ? AND TrangThai = 'Trong Kho'";

        int totalAffectedRows = 0;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String maSPCuThe : maSPCuTheList) {
                stmt.setInt(1, maHDX);
                stmt.setString(2, maSPCuThe);
                stmt.addBatch();
            }
            int[] results = stmt.executeBatch();
            for (int count : results) {
                if (count == 1) { // Chỉ đếm các update thành công
                    totalAffectedRows += count;
                }
            }
        }
        return totalAffectedRows;
    }
}