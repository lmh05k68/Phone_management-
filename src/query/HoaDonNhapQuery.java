// Updated HoaDonNhapQuery.java
package query;
import java.sql.ResultSet;
import java.sql.Connection;
import java.time.LocalDate;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import dbConnection.DBConnection;
import model.HoaDonNhap;
import model.ChiTietHDNhap;

public class HoaDonNhapQuery {
    public boolean insertHoaDonNhap(HoaDonNhap hdn) {
        String sql = "INSERT INTO HoaDonNhap (MaHDN, NgayNhap, MaNV, MaNCC) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hdn.getMaHDN());
            stmt.setDate(2, Date.valueOf(hdn.getNgayNhap()));
            stmt.setString(3, hdn.getMaNV());
            stmt.setString(4, hdn.getMaNCC());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertChiTietNhap(List<ChiTietHDNhap> listCT) {
        String sql = "INSERT INTO ChiTietHDNhap (MaSP, MaHDN, SLNhap, DonGiaNhap) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (ChiTietHDNhap ct : listCT) {
                stmt.setString(1, ct.getMaSP());
                stmt.setString(2, ct.getMaHDN());
                stmt.setInt(3, ct.getSoLuong());
                stmt.setDouble(4, ct.getDonGiaNhap());
                stmt.addBatch();
            }

            int[] result = stmt.executeBatch();
            return result.length == listCT.size();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean insertHoaDonNhapWithMa(HoaDonNhap hdn) {
        String sql = "INSERT INTO HoaDonNhap (MaHDN, NgayNhap, MaNV, MaNCC) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hdn.getMaHDN());
            stmt.setDate(2, Date.valueOf(hdn.getNgayNhap()));
            stmt.setString(3, hdn.getMaNV());
            stmt.setString(4, hdn.getMaNCC());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean insertHoaDonNhap(HoaDonNhap hdn, Connection conn) throws SQLException {
        String sql = "INSERT INTO hoadonnhap (mahdn, ngaynhap, manv, mancc) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hdn.getMaHDN());
            stmt.setDate(2, java.sql.Date.valueOf(hdn.getNgayNhap()));
            stmt.setString(3, hdn.getMaNV());
            stmt.setString(4, hdn.getMaNCC());
            return stmt.executeUpdate() > 0;
        }
    }
    public List<HoaDonNhap> getHoaDonNhapByMonthAndYear(int thang, int nam) {
        List<HoaDonNhap> list = new ArrayList<>();
        String sql = "SELECT hdn.mahdn, hdn.ngaynhap, hdn.manv, hdn.mancc " +
                     "FROM hoadonnhap hdn " +
                     "WHERE EXTRACT(MONTH FROM hdn.ngaynhap) = ? AND EXTRACT(YEAR FROM hdn.ngaynhap) = ? " +
                     "ORDER BY hdn.ngaynhap DESC, hdn.mahdn ASC";

        System.out.println("DEBUG HDN_QUERY: SQL getHoaDonNhapByMonthAndYear (basic info): " + sql);
        System.out.println("DEBUG HDN_QUERY: Thang=" + thang + ", Nam=" + nam);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate ngayNhap = null;
                if (rs.getDate("ngaynhap") != null) {
                    ngayNhap = rs.getDate("ngaynhap").toLocalDate();
                }
                // Sử dụng constructor không có tongTienNhap
                HoaDonNhap hdn = new HoaDonNhap(
                        rs.getString("mahdn"),
                        ngayNhap,
                        rs.getString("manv"),
                        rs.getString("mancc")
                );
                list.add(hdn);
            }
            System.out.println("DEBUG HDN_QUERY: Tim thay " + list.size() + " hoa don nhap (basic info) cho " + thang + "/" + nam);
        } catch (SQLException e) {
            System.err.println("Loi khi lay danh sach hoa don nhap (basic info) theo thang/nam: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tính tổng tiền cho MỘT hóa đơn nhập cụ thể dựa vào chi tiết.
     * Phương thức này có thể được gọi nếu bạn muốn hiển thị tổng tiền khi người dùng chọn 1 HĐN.
     */
    public double tinhTongTienChoMotHoaDonNhap(String maHDN) {
        String sql = "SELECT COALESCE(SUM(ctn.slnhap * ctn.dongianhap), 0) " +
                     "FROM chitiethdnhap ctn " +
                     "WHERE ctn.mahdn = ?";
        System.out.println("DEBUG HDN_QUERY: SQL tinhTongTienChoMotHoaDonNhap: " + sql + " cho MaHDN=" + maHDN);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHDN);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Loi khi tinh tong tien cho MaHDN " + maHDN + ": " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    // insertHoaDonNhap và các phương thức khác có thể giữ nguyên hoặc điều chỉnh
    // nếu chúng không cần tương tác trực tiếp với tongTienNhap như một cột trong bảng hoadonnhap.
    public boolean insertHoaDonNhap(HoaDonNhap hdn, List<model.ChiTietHDNhap> chiTietList, Connection conn) throws SQLException {
        // Bước 1: Insert vào hoadonnhap
        String sqlHdn = "INSERT INTO hoadonnhap (mahdn, ngaynhap, manv, mancc) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmtHdn = conn.prepareStatement(sqlHdn)) {
            stmtHdn.setString(1, hdn.getMaHDN());
            stmtHdn.setDate(2, java.sql.Date.valueOf(hdn.getNgayNhap()));
            stmtHdn.setString(3, hdn.getMaNV());
            stmtHdn.setString(4, hdn.getMaNCC());
            int hdnRows = stmtHdn.executeUpdate();
            if (hdnRows == 0) return false;
        }

        // Bước 2: Insert vào chitiethdnhap
        String sqlCt = "INSERT INTO chitiethdnhap (mahdn, masp, slnhap, dongianhap) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmtCt = conn.prepareStatement(sqlCt)) {
            for (model.ChiTietHDNhap ct : chiTietList) {
                stmtCt.setString(1, hdn.getMaHDN()); // Lấy MaHDN từ hoadonnhap vừa insert
                stmtCt.setString(2, ct.getMaSP());
                stmtCt.setInt(3, ct.getSoLuong());
                stmtCt.setDouble(4, ct.getDonGiaNhap());
                stmtCt.addBatch();
            }
            stmtCt.executeBatch();
        }
        return true;
        // Toàn bộ quá trình này nên được bọc trong transaction ở lớp gọi (ví dụ: ImportProductController)
    }
}