package query;

import dbConnection.DBConnection;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThongKeQuery {

    private static final Logger logger = Logger.getLogger(ThongKeQuery.class.getName());
    public static BigDecimal getDoanhThuThang(int thang, int nam) {
        String sql = "{? = call func_tinh_tong_doanh_thu(?, ?)}"; 
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.DECIMAL);
            cstmt.setInt(2, thang);
            cstmt.setInt(3, nam);
            cstmt.execute();
            BigDecimal doanhThu = cstmt.getBigDecimal(1);
            logger.info("Doanh thu tháng " + thang + "/" + nam + ": " + (doanhThu != null ? doanhThu.toString() : "0"));
            return doanhThu != null ? doanhThu : BigDecimal.ZERO;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi gọi hàm tính doanh thu tháng " + thang + "/" + nam, e);
        }
        // Trả về 0 nếu có lỗi
        return BigDecimal.ZERO; 
    }

    /**
     * Gọi hàm func_tinh_tong_chi_tieu trong CSDL để lấy tổng chi tiêu.
     * Chi tiêu được tính là tổng giá nhập của các sản phẩm cụ thể.
     * @param thang Tháng cần thống kê.
     * @param nam   Năm cần thống kê.
     * @return Tổng chi tiêu dưới dạng BigDecimal, hoặc BigDecimal.ZERO nếu có lỗi.
     */
    public static BigDecimal getChiTieuThang(int thang, int nam) {
        // Tên hàm trong SQL là func_tinh_tong_chi_tieu(thang, nam)
        String sql = "{? = call func_tinh_tong_chi_tieu(?, ?)}";

        // TỐI ƯU: Sử dụng try-with-resources
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, Types.DECIMAL);
            cstmt.setInt(2, thang);
            cstmt.setInt(3, nam);
            cstmt.execute();

            BigDecimal chiTieu = cstmt.getBigDecimal(1);
            logger.info("Chi tiêu tháng " + thang + "/" + nam + ": " + (chiTieu != null ? chiTieu.toString() : "0"));
            return chiTieu != null ? chiTieu : BigDecimal.ZERO;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi gọi hàm tính chi tiêu tháng " + thang + "/" + nam, e);
        }
        // Trả về 0 nếu có lỗi
        return BigDecimal.ZERO; 
    }
}