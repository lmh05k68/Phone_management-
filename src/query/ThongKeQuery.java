package query;

import dbConnection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThongKeQuery {

    /**
     * Tính tổng doanh thu thực tế (sau thuế) từ hóa đơn xuất trong một tháng/năm cụ thể.
     */
    public double getDoanhThuThang(int thang, int nam) {
        // Giả sử tên cột CSDL là chữ thường
        // Tính tổng của (thanhtien * (1 + mucthue/100))
        String sql = "SELECT COALESCE(SUM(thanhtien * (1 + mucthue / 100.0)), 0) FROM hoadonxuat " +
                     "WHERE EXTRACT(MONTH FROM ngaylap) = ? AND EXTRACT(YEAR FROM ngaylap) = ?";
        System.out.println("DEBUG THONGKE_QUERY: SQL getDoanhThuThang: " + sql + " with Thang=" + thang + ", Nam=" + nam);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double doanhThu = rs.getDouble(1);
                System.out.println("DEBUG THONGKE_QUERY: Doanh thu tinh duoc: " + doanhThu);
                return doanhThu;
            }
        } catch (SQLException e) {
            System.err.println("Loi khi tinh doanh thu thang: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Tính tổng chi tiêu từ hóa đơn nhập trong một tháng/năm cụ thể.
     * Chi tiêu được tính bằng tổng của (slnhap * dongianhap) từ chitiethdnhap.
     */
    public double getChiTieuThang(int thang, int nam) {
        // Giả sử tên cột CSDL là chữ thường
        String sql = "SELECT COALESCE(SUM(ctn.slnhap * ctn.dongianhap), 0) " +
                     "FROM chitiethdnhap ctn " +
                     "JOIN hoadonnhap hdn ON ctn.mahdn = hdn.mahdn " +
                     "WHERE EXTRACT(MONTH FROM hdn.ngaynhap) = ? AND EXTRACT(YEAR FROM hdn.ngaynhap) = ?";
        System.out.println("DEBUG THONGKE_QUERY: SQL getChiTieuThang: " + sql + " with Thang=" + thang + ", Nam=" + nam);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double chiTieu = rs.getDouble(1);
                System.out.println("DEBUG THONGKE_QUERY: Chi tieu tinh duoc: " + chiTieu);
                return chiTieu;
            }
        } catch (SQLException e) {
            System.err.println("Loi khi tinh chi tieu thang: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
}