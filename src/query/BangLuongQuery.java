package query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.BangLuong;
import dbConnection.DBConnection;
public class BangLuongQuery {
	public List<BangLuong> getBangLuongTheoThang(int thang, int nam) {
        List<BangLuong> danhSachLuong = new ArrayList<>();
        String sql = "SELECT MaNV, TenNV, LuongCoBan, TongDoanhSo, ThuongMuc, ThuongRank, TongThuong, LuongThucLanh " +
                     "FROM vw_BangLuongThang " +
                     "WHERE thang = ? AND nam = ? " +
                     "ORDER BY LuongThucLanh DESC";

        try (Connection conn = DBConnection.getConnection(); // Sử dụng try-with-resources
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, thang);
            ps.setInt(2, nam);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BangLuong bangLuong = new BangLuong();
                    bangLuong.setMaNV(rs.getInt("MaNV"));
                    bangLuong.setTenNV(rs.getString("TenNV"));
                    bangLuong.setLuongCoBan(rs.getBigDecimal("LuongCoBan"));
                    bangLuong.setTongDoanhSo(rs.getBigDecimal("TongDoanhSo"));
                    bangLuong.setThuongMuc(rs.getBigDecimal("ThuongMuc"));
                    bangLuong.setThuongRank(rs.getBigDecimal("ThuongRank"));
                    bangLuong.setTongThuong(rs.getBigDecimal("TongThuong"));
                    bangLuong.setLuongThucLanh(rs.getBigDecimal("LuongThucLanh"));
                    
                    danhSachLuong.add(bangLuong);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachLuong;
    }
    public boolean chotSoKPI(int thang, int nam) {
        String sql = "SELECT chot_hang_kpi_thang(?, ?)";
         try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, thang);
            ps.setInt(2, nam);
            ps.execute(); // Chạy function và bỏ qua kết quả trả về
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
