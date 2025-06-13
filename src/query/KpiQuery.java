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
        // SỬA LỖI: Thêm NULLS LAST để đảm bảo việc sắp xếp hiển thị cũng đúng
        String sql = "SELECT k.id_kpi, k.manv, nv.TenNV, k.thang, k.nam, k.tong_doanh_so, k.thuong_muc, k.thuong_rank " +
                     "FROM kpi k " +
                     "JOIN NhanVien nv ON k.manv = nv.MaNV " +
                     "WHERE k.thang = ? AND k.nam = ? " +
                     // SỬA LỖI: Sắp xếp theo tổng thưởng, sau đó là doanh số.
                     // Thêm NULLS LAST cho cả hai để đảm bảo tính nhất quán.
                     "ORDER BY (k.thuong_muc + k.thuong_rank) DESC NULLS LAST, k.tong_doanh_so DESC NULLS LAST";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    KPI kpi = new KPI();
                    kpi.setIdKpi(rs.getInt("id_kpi"));
                    kpi.setManv(rs.getInt("manv"));
                    kpi.setHoTen(rs.getString("TenNV"));
                    kpi.setThang(rs.getInt("thang"));
                    kpi.setNam(rs.getInt("nam"));
                    kpi.setTongDoanhSo(rs.getBigDecimal("tong_doanh_so"));
                    kpi.setThuongMuc(rs.getBigDecimal("thuong_muc"));
                    kpi.setThuongRank(rs.getBigDecimal("thuong_rank"));
                    kpiList.add(kpi);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy dữ liệu KPI cho tháng " + thang + "/" + nam, e);
        }
        return kpiList;
    }

    public static boolean finalizeKpiRank(int thang, int nam) {
        String sql = "{? = call chot_hang_kpi_thang(?, ?)}"; 
        
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.setInt(2, thang);
            cstmt.setInt(3, nam);
            cstmt.execute();
            
            return cstmt.getBoolean(1);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi SQL khi gọi hàm chốt KPI cho tháng " + thang + "/" + nam, e);
            return false;
        }
    }
}