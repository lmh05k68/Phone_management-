package query;

import dbConnection.DBConnection;
import model.KPI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KpiQuery {
    
    private static final Logger logger = Logger.getLogger(KpiQuery.class.getName());
    public static List<KPI> getKpiByMonthYear(int thang, int nam) {
        List<KPI> kpiList = new ArrayList<>();
        String sql = "SELECT k.id_kpi, k.manv, nv.TenNV, k.thang, k.nam, k.tong_doanh_so, k.thuong_muc, k.thuong_rank " +
                     "FROM kpi k " +
                     "JOIN NhanVien nv ON k.manv = nv.MaNV " +
                     "WHERE k.thang = ? AND k.nam = ? " +
                     "ORDER BY k.tong_doanh_so DESC, (k.thuong_muc + k.thuong_rank) DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    KPI kpi = new KPI();
                    kpi.setIdKpi(rs.getInt("id_kpi"));
                    kpi.setManv(rs.getInt("manv"));
                    kpi.setHoTen(rs.getString("TenNV")); // Tên cột đã sửa
                    kpi.setThang(rs.getInt("thang"));
                    kpi.setNam(rs.getInt("nam"));
                    kpi.setTongDoanhSo(rs.getBigDecimal("tong_doanh_so"));
                    kpi.setThuongMuc(rs.getBigDecimal("thuong_muc"));
                    kpi.setThuongRank(rs.getBigDecimal("thuong_rank"));
                    kpiList.add(kpi);
                }
            }
        } catch (SQLException e) {
            // TỐI ƯU: Ghi log lỗi và trả về danh sách rỗng thay vì ném exception
            logger.log(Level.SEVERE, "Lỗi khi lấy dữ liệu KPI cho tháng " + thang + "/" + nam, e);
        }
        return kpiList;
    }

    /**
     * Gọi hàm CSDL 'chot_hang_kpi_thang' để tính toán và cập nhật thưởng rank.
     * @param thang Tháng cần chốt KPI.
     * @param nam Năm cần chốt KPI.
     * @return true nếu thực thi thành công, false nếu có lỗi.
     */
    // SỬA: Chuyển thành phương thức static và đổi tên cho khớp
    public static boolean finalizeKpiRank(int thang, int nam) {
        // SỬA: Sử dụng đúng tên hàm CSDL đã tạo trước đó
        String sql = "{call chot_hang_kpi_thang(?, ?)}"; 
        
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, thang);
            cstmt.setInt(2, nam);
            cstmt.execute(); // Thực thi procedure
            
            logger.info("Đã thực thi thành công hàm chốt KPI cho tháng " + thang + "/" + nam);
            return true;
        } catch (SQLException e) {
            // TỐI ƯU: Ghi log lỗi và trả về false
            logger.log(Level.SEVERE, "Lỗi khi gọi hàm chốt KPI cho tháng " + thang + "/" + nam, e);
            return false;
        }
    }
}