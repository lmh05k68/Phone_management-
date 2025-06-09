package query;

import java.sql.ResultSet;
import java.sql.Statement; // Thêm import này
import java.sql.Connection;
import java.time.LocalDate;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;      // Thêm import này
import java.util.List;
import java.util.ArrayList;
import dbConnection.DBConnection;
import model.HoaDonNhap;
import model.ChiTietHDNhap;

public class HoaDonNhapQuery {

    /**
     * Chèn một hóa đơn nhập mới (không có MaHDN ban đầu, vì MaHDN tự sinh)
     * và trả về MaHDN tự sinh.
     * Phương thức này phải được gọi trong một transaction.
     * @param hdn Đối tượng HoaDonNhap (MaNV, MaNCC, NgayNhap phải có).
     * @param conn Connection hiện tại của transaction.
     * @return Integer là MaHDN tự sinh, hoặc null nếu thất bại.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public static Integer insertHoaDonNhapAndGetId(HoaDonNhap hdn, Connection conn) throws SQLException {
        // MaHDN là SERIAL trong DB, không cần cung cấp trong VALUES
        String sql = "INSERT INTO hoadonnhap (ngaynhap, manv, mancc) VALUES (?, ?, ?)";
        System.out.println("HDN_QUERY (insertAndGetId): Chuẩn bị insert HĐN. MaNV=" + hdn.getMaNV() + ", MaNCC=" + hdn.getMaNCC());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, java.sql.Date.valueOf(hdn.getNgayNhap()));
            // MaNV và MaNCC là int, nên dùng setInt. Kiểm tra null nếu chúng là Integer
            if (hdn.getMaNV() > 0) { // Giả sử ID hợp lệ > 0
                 stmt.setInt(2, hdn.getMaNV());
            } else {
                 stmt.setNull(2, Types.INTEGER); // Hoặc throw exception nếu MaNV bắt buộc
            }
            if (hdn.getMaNCC() > 0) {
                stmt.setInt(3, hdn.getMaNCC());
            } else {
                stmt.setNull(3, Types.INTEGER); // Hoặc throw exception nếu MaNCC bắt buộc
            }
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("HDN_QUERY (insertAndGetId): Chèn HĐN thất bại, không có hàng nào được thêm.");
                return null;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    System.out.println("HDN_QUERY (insertAndGetId): HĐN được chèn thành công với MaHDN (tự sinh): " + generatedId);
                    return generatedId;
                } else {
                    System.err.println("HDN_QUERY (insertAndGetId): Chèn HĐN thành công nhưng không lấy được ID tự sinh.");
                    // Đây là lỗi nghiêm trọng, có thể ném SQLException
                    throw new SQLException("Không thể lấy ID tự sinh cho Hóa Đơn Nhập.");
                }
            }
        }
    }

    /**
     * Chèn danh sách chi tiết hóa đơn nhập.
     * MaHDN cho mỗi ChiTietHDNhap trong danh sách phải đã được gán (là ID tự sinh từ HoaDonNhap).
     * Phương thức này phải được gọi trong một transaction.
     * @param dsChiTiet Danh sách các đối tượng ChiTietHDNhap.
     * @param conn Connection hiện tại của transaction.
     * @return true nếu tất cả chi tiết được chèn thành công, false nếu có lỗi.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public static boolean insertListChiTietNhap(List<ChiTietHDNhap> dsChiTiet, Connection conn) throws SQLException {
        // MaSP, MaHDN, SLNhap, DonGiaNhap đều là int hoặc double/numeric
        String sql = "INSERT INTO chitiethdnhap (masp, mahdn, slnhap, dongianhap) VALUES (?, ?, ?, ?)";
        if (dsChiTiet == null || dsChiTiet.isEmpty()) {
            System.out.println("HDN_QUERY (insertListChiTietNhap): Danh sách chi tiết rỗng, không có gì để chèn.");
            return true; // Không có lỗi, nhưng không có gì được thực hiện
        }
        System.out.println("HDN_QUERY (insertListChiTietNhap): Chuẩn bị chèn " + dsChiTiet.size() + " chi tiết HĐN.");

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (ChiTietHDNhap ct : dsChiTiet) {
                if (ct.getMaHDN() <= 0) { // Kiểm tra MaHDN đã được gán chưa
                    throw new SQLException("MaHDN trong ChiTietHDNhap không hợp lệ (<=0) cho MaSP: " + ct.getMaSP());
                }
                stmt.setInt(1, ct.getMaSP());
                stmt.setInt(2, ct.getMaHDN()); // MaHDN là int
                stmt.setInt(3, ct.getSoLuong());
                stmt.setDouble(4, ct.getDonGiaNhap());
                stmt.addBatch();
                System.out.println("HDN_QUERY (insertListChiTietNhap): Đã thêm vào batch: MaSP=" + ct.getMaSP() + ", MaHDN=" + ct.getMaHDN());
            }

            int[] result = stmt.executeBatch();
            // Kiểm tra xem tất cả các batch có thành công không
            for (int res : result) {
                if (res == Statement.EXECUTE_FAILED) {
                    System.err.println("HDN_QUERY (insertListChiTietNhap): Một hoặc nhiều chi tiết HĐN chèn thất bại.");
                    return false; 
                }
            }
            System.out.println("HDN_QUERY (insertListChiTietNhap): Tất cả " + result.length + " chi tiết HĐN đã được chèn thành công.");
            return result.length == dsChiTiet.size(); 
        }
    }
    
    /**
     * Lấy danh sách hóa đơn nhập (chỉ thông tin cơ bản) theo tháng và năm.
     * @param thang Tháng.
     * @param nam Năm.
     * @return Danh sách HoaDonNhap.
     */
    public static List<HoaDonNhap> getHoaDonNhapByMonthAndYear(int thang, int nam) {
        List<HoaDonNhap> list = new ArrayList<>();
        // Tên cột trong DB giả sử là chữ thường
        String sql = "SELECT hdn.mahdn, hdn.ngaynhap, hdn.manv, hdn.mancc " +
                     "FROM hoadonnhap hdn " +
                     "WHERE EXTRACT(MONTH FROM hdn.ngaynhap) = ? AND EXTRACT(YEAR FROM hdn.ngaynhap) = ? " +
                     "ORDER BY hdn.ngaynhap DESC, hdn.mahdn ASC";

        System.out.println("HDN_QUERY (getByMonthYear): SQL=" + sql + ", Thang=" + thang + ", Nam=" + nam);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate ngayNhap = null;
                    Date sqlDateNgayNhap = rs.getDate("ngaynhap");
                    if (sqlDateNgayNhap != null) {
                        ngayNhap = sqlDateNgayNhap.toLocalDate();
                    }
                    
                    // MaHDN, MaNV, MaNCC giờ là int
                    HoaDonNhap hdn = new HoaDonNhap(
                            rs.getInt("mahdn"),
                            ngayNhap,
                            rs.getInt("manv"), // Hoặc rs.getObject("manv", Integer.class) nếu có thể null
                            rs.getInt("mancc")  // Hoặc rs.getObject("mancc", Integer.class) nếu có thể null
                    );
                    list.add(hdn);
                }
            }
            System.out.println("HDN_QUERY (getByMonthYear): Tìm thấy " + list.size() + " HĐN cho " + thang + "/" + nam);
        } catch (SQLException e) {
            System.err.println("HDN_QUERY (getByMonthYear): Lỗi khi lấy HĐN theo tháng/năm: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tính tổng tiền cho MỘT hóa đơn nhập cụ thể dựa vào chi tiết.
     * @param maHDN Mã hóa đơn nhập (int).
     * @return Tổng tiền của hóa đơn.
     */
    public static double tinhTongTienChoMotHoaDonNhap(int maHDN) { // Tham số là int
        String sql = "SELECT COALESCE(SUM(ctn.slnhap * ctn.dongianhap), 0.0) " +
                     "FROM chitiethdnhap ctn " +
                     "WHERE ctn.mahdn = ?";
        System.out.println("HDN_QUERY (tinhTong): SQL=" + sql + " cho MaHDN=" + maHDN);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maHDN); // Sử dụng setInt
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("HDN_QUERY (tinhTong): Lỗi khi tính tổng tiền cho MaHDN " + maHDN + ": " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
    public static boolean checkMaHDNExists(String maHDNString) {
        String sql = "SELECT 1 FROM hoadonnhap WHERE mahdn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHDNString);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("HDN_QUERY (checkMaHDNExists): Lỗi khi kiểm tra MaHDN: " + e.getMessage());
            e.printStackTrace();
            return false; 
        }
    }
}