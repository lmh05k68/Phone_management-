package query;
import java.math.BigDecimal;
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
    public static List<ChiTietDonHang> getChiTietDonHangByHDXAndKH(int maHDX, int maKH) {
        List<ChiTietDonHang> chiTietList = new ArrayList<>();
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
    public static void insertBatch(List<NhapHangItem> itemsToImport, int maHDN, Connection conn) throws SQLException {
        String sql = "INSERT INTO SanPhamCuThe (MaSPCuThe, MaSP, GiaNhap, MaHDN, TrangThai) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (NhapHangItem item : itemsToImport) {
                for (int i = 0; i < item.getSoLuong(); i++) {
                    String maSPCuThe = String.format("%d-%s", item.getSanPham().getMaSP(), UUID.randomUUID().toString().substring(0, 8));

                    pstmt.setString(1, maSPCuThe);
                    pstmt.setInt(2, item.getSanPham().getMaSP());
                    pstmt.setBigDecimal(3, item.getGiaNhap());
                    pstmt.setInt(4, maHDN);
                    pstmt.setString(5, "Trong Kho"); 
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch(); // Thực thi toàn bộ batch
        }
    }
 public static int sellProducts(List<String> maSPCuTheList, int maHDX, Connection conn) throws SQLException {
     String sql = "UPDATE SanPhamCuThe SET " +
                  "  MaHDX = ?, " +
                  "  TrangThai = 'Da Ban', " + // Quan trọng: Thay đổi trạng thái
                  "  GiaXuat = (SELECT GiaNiemYet FROM SanPham WHERE MaSP = SanPhamCuThe.MaSP) " + // Lấy giá niêm yết làm giá bán
                  "WHERE MaSPCuThe = ? AND TrangThai = 'Trong Kho'"; // Điều kiện cốt lõi: Chỉ bán hàng còn trong kho

     int totalAffectedRows = 0;
     try (PreparedStatement stmt = conn.prepareStatement(sql)) {
         for (String maSPCuThe : maSPCuTheList) {
             stmt.setInt(1, maHDX);
             stmt.setString(2, maSPCuThe);
             stmt.addBatch();
         }
         int[] results = stmt.executeBatch();
         for (int count : results) {
              if (count >= 0 || count == PreparedStatement.SUCCESS_NO_INFO) {
                  totalAffectedRows++;
             }
         }
         if (totalAffectedRows != maSPCuTheList.size()) {
             throw new SQLException("Một hoặc nhiều sản phẩm không còn khả dụng để bán. Giao dịch đã bị hủy.");
         }
     }
     return totalAffectedRows;
 }
    public static boolean insertSPCuThe(SPCuThe spct, Connection conn) throws SQLException {
        String sql = "INSERT INTO SanPhamCuThe (MaSPCuThe, MaSP, GiaNhap, MaHDN, TrangThai) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spct.getMaSPCuThe());
            pstmt.setInt(2, spct.getMaSP());
            pstmt.setBigDecimal(3, spct.getGiaNhap());
            pstmt.setInt(4, spct.getMaHDN());
            pstmt.setString(5, spct.getTrangThai().getValue()); // Ví dụ: "Trong Kho"
            
            return pstmt.executeUpdate() > 0;
        }
    }
    public static List<ChiTietDonHang> getAvailableProductsForExchange() {
        List<ChiTietDonHang> list = new ArrayList<>();
        String sql = "SELECT spct.MaSPCuThe, sp.TenSP, sp.Mau, sp.GiaNiemYet AS GiaBan " +
                     "FROM SanPhamCuThe spct " +
                     "JOIN SanPham sp ON spct.MaSP = sp.MaSP " +
                     "WHERE spct.TrangThai = 'Trong Kho' AND spct.MaHDX IS NULL " + // << THÊM ĐIỀU KIỆN MỚI
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
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách sản phẩm để đổi hàng.", e);
        }
        return list;
    }
    public static BigDecimal getGiaXuatByMaSPCuThe(String maSPCuThe) {
        String sql = "SELECT GiaXuat FROM SanPhamCuThe WHERE MaSPCuThe = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, maSPCuThe);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal giaXuat = rs.getBigDecimal("GiaXuat");
                    if (giaXuat == null) {
                         logger.log(Level.WARNING, "Sản phẩm " + maSPCuThe + " đã được bán nhưng chưa có giá xuất (GiaXuat is NULL).");
                    }
                    return giaXuat;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy giá xuất của SP " + maSPCuThe, e);
        }
        return null; // Trả về null nếu không tìm thấy sản phẩm hoặc có lỗi
    }
}