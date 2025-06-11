package query;

import dbConnection.DBConnection;
import model.HoaDonNhap;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HoaDonNhapQuery {
    public static Integer insertHoaDonNhapAndGetId(HoaDonNhap hdn, Connection conn) throws SQLException {
        String sql = "INSERT INTO HoaDonNhap (NgayNhap, MaNV, MaNCC) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(hdn.getNgayNhap()));
            stmt.setInt(2, hdn.getMaNV());
            stmt.setInt(3, hdn.getMaNCC());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Tạo Hóa Đơn Nhập thất bại, không có hàng nào được thêm.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Tạo Hóa Đơn Nhập thành công nhưng không lấy được ID.");
                }
            }
        }
    }

    /**
     * Lấy danh sách các hóa đơn nhập theo tháng và năm cụ thể.
     * @param thang Tháng cần truy vấn (1-12).
     * @param nam Năm cần truy vấn.
     * @return Một danh sách các đối tượng HoaDonNhap.
     */
 // Đảm bảo phương thức này có trong file HoaDonNhapQuery.java
    public static List<HoaDonNhap> getHoaDonNhapByMonthAndYear(int thang, int nam) {
        List<HoaDonNhap> list = new ArrayList<>();
        String sql = "SELECT mahdn, ngaynhap, manv, mancc FROM hoadonnhap " +
                     "WHERE EXTRACT(MONTH FROM ngaynhap) = ? AND EXTRACT(YEAR FROM ngaynhap) = ? " +
                     "ORDER BY ngaynhap DESC, mahdn ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new HoaDonNhap(
                            rs.getInt("mahdn"),
                            rs.getDate("ngaynhap").toLocalDate(),
                            rs.getInt("manv"),
                            rs.getInt("mancc")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static BigDecimal tinhTongTienHoaDonNhap(int maHDN) {
        String sql = "SELECT COALESCE(SUM(GiaNhap), 0) FROM SanPhamCuThe WHERE MaHDN = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maHDN);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    public static boolean checkMaHDNExists(String maHDNString) {
        int maHDN;
        try {
            maHDN = Integer.parseInt(maHDNString);
        } catch (NumberFormatException e) {
            return false; // Không phải số thì chắc chắn không tồn tại
        }

        String sql = "SELECT 1 FROM hoadonnhap WHERE mahdn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maHDN);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // rs.next() trả về true nếu có kết quả, ngược lại false
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static int insertAndGetId(int maNV, int maNCC, Connection conn) throws SQLException {
        String sql = "INSERT INTO HoaDonNhap (MaNV, MaNCC) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, maNV);
            pstmt.setInt(2, maNCC);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Tạo Hóa Đơn Nhập thất bại, không có hàng nào được thêm.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Trả về MaHDN
                } else {
                    throw new SQLException("Tạo Hóa Đơn Nhập thành công nhưng không lấy được ID.");
                }
            }
        }
    }
}