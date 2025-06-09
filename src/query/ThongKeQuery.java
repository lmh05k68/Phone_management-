package query;

import dbConnection.DBConnection; // Giả sử bạn có class này

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThongKeQuery {

    /**
     * Tính tổng doanh thu (sau thuế) từ hóa đơn xuất trong một tháng/năm cụ thể.
     * Doanh thu được tính bằng tổng của (thanhtien * (1 + mucthue / 100.0)).
     * @param thang Tháng cần thống kê.
     * @param nam Năm cần thống kê.
     * @return Tổng doanh thu, hoặc 0.0 nếu có lỗi hoặc không có dữ liệu.
     */
    public static double getDoanhThuThang(int thang, int nam) {
        // Giả sử tên cột trong bảng hoadonxuat là: thanhtien, mucthue, ngaylap
        String sql = "SELECT COALESCE(SUM(thanhtien * (1 + mucthue / 100.0)), 0.0) AS tong_doanh_thu " +
                     "FROM hoadonxuat " +
                     "WHERE EXTRACT(MONTH FROM ngaylap) = ? AND EXTRACT(YEAR FROM ngaylap) = ?";
        System.out.println("THONGKE_QUERY (getDoanhThuThang): SQL=" + sql + ", Thang=" + thang + ", Nam=" + nam);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            rs = stmt.executeQuery();
            if (rs.next()) {
                double doanhThu = rs.getDouble("tong_doanh_thu"); // Hoặc rs.getDouble(1)
                System.out.println("THONGKE_QUERY (getDoanhThuThang): Doanh thu tinh duoc: " + doanhThu);
                return doanhThu;
            }
        } catch (SQLException e) {
            System.err.println("THONGKE_QUERY (getDoanhThuThang): Loi khi tinh doanh thu thang " + thang + "/" + nam + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đảm bảo đóng tất cả tài nguyên
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return 0.0; // Trả về 0 nếu không có dữ liệu hoặc có lỗi
    }

    /**
     * Tính tổng chi tiêu từ hóa đơn nhập trong một tháng/năm cụ thể.
     * Chi tiêu được tính bằng tổng của (soluong * dongianhap) từ chitiethdnhap.
     * @param thang Tháng cần thống kê.
     * @param nam Năm cần thống kê.
     * @return Tổng chi tiêu, hoặc 0.0 nếu có lỗi hoặc không có dữ liệu.
     */
    public static double getChiTieuThang(int thang, int nam) {
        // SỬA TÊN CỘT: ctn.slnhap -> ctn.soluong
        // KIỂM TRA TÊN CỘT NGÀY: hdn.ngaynhap (hoặc hdn.ngaylap nếu đó là tên cột ngày trong bảng hoadonnhap)
        // Giả sử tên cột ngày trong hoadonnhap là 'ngaylap' để nhất quán với nhiều hệ thống
        String sql = "SELECT COALESCE(SUM(ctn.soluong * ctn.dongianhap), 0.0) AS tong_chi_tieu " +
                     "FROM chitiethdnhap ctn " +
                     "JOIN hoadonnhap hdn ON ctn.mahdn = hdn.mahdn " +
                     "WHERE EXTRACT(MONTH FROM hdn.ngaylap) = ? AND EXTRACT(YEAR FROM hdn.ngaylap) = ?";
                     // Nếu cột ngày trong hoadonnhap là 'ngaynhap', hãy đổi 'hdn.ngaylap' thành 'hdn.ngaynhap' ở trên
        System.out.println("THONGKE_QUERY (getChiTieuThang): SQL=" + sql + ", Thang=" + thang + ", Nam=" + nam);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            rs = stmt.executeQuery();
            if (rs.next()) {
                double chiTieu = rs.getDouble("tong_chi_tieu"); // Hoặc rs.getDouble(1)
                System.out.println("THONGKE_QUERY (getChiTieuThang): Chi tieu tinh duoc: " + chiTieu);
                return chiTieu;
            }
        } catch (SQLException e) {
            System.err.println("THONGKE_QUERY (getChiTieuThang): Loi khi tinh chi tieu thang " + thang + "/" + nam + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return 0.0; // Trả về 0 nếu không có dữ liệu hoặc có lỗi
    }
}