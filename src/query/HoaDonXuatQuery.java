package query;

import dbConnection.DBConnection;
import model.HoaDonXuat;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HoaDonXuatQuery {
    public static Integer insertHoaDonXuatAndGetId(HoaDonXuat hdx, Connection conn) throws SQLException {
        String sql = "INSERT INTO HoaDonXuat (NgayLap, ThanhTien, MucThue, MaNV, MaKH) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, Date.valueOf(hdx.getNgayLap()));
            pstmt.setBigDecimal(2, BigDecimal.ZERO); // Giá trị tạm thời, trigger sẽ ghi đè
            pstmt.setBigDecimal(3, hdx.getMucThue());
            pstmt.setInt(4, hdx.getMaNV());
            pstmt.setInt(5, hdx.getMaKH());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Tạo hóa đơn thất bại.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Không lấy được ID của hóa đơn vừa tạo.");
                }
            }
        }
    }

    /**
     * Lấy thông tin một hóa đơn xuất bằng Mã Hóa Đơn (MaHDX).
     * @param maHDX Mã hóa đơn cần tìm.
     * @return Đối tượng HoaDonXuat hoặc null nếu không tìm thấy.
     */
    public static HoaDonXuat getHoaDonById(int maHDX) {
        String sql = "SELECT MaHDX, NgayLap, ThanhTien, MucThue, MaNV, MaKH FROM HoaDonXuat WHERE MaHDX = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maHDX);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new HoaDonXuat(
                        rs.getInt("MaHDX"),
                        rs.getDate("NgayLap").toLocalDate(),
                        rs.getBigDecimal("ThanhTien"),
                        rs.getBigDecimal("MucThue"),
                        rs.getInt("MaNV"),
                        rs.getInt("MaKH")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hóa đơn #" + maHDX + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy tất cả các hóa đơn của một khách hàng cụ thể.
     * Dùng cho chức năng xem lịch sử mua hàng.
     * @param maKH Mã khách hàng.
     * @return Danh sách các đối tượng HoaDonXuat.
     */
    public static List<HoaDonXuat> getHoaDonByKhachHang(int maKH) {
        List<HoaDonXuat> list = new ArrayList<>();
        String sql = "SELECT MaHDX, NgayLap, ThanhTien, MucThue, MaNV, MaKH " +
                     "FROM HoaDonXuat WHERE MaKH = ? ORDER BY NgayLap DESC, MaHDX DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HoaDonXuat hdx = new HoaDonXuat(
                        rs.getInt("MaHDX"),
                        rs.getDate("NgayLap").toLocalDate(),
                        rs.getBigDecimal("ThanhTien"),
                        rs.getBigDecimal("MucThue"),
                        rs.getInt("MaNV"),
                        rs.getInt("MaKH")
                    );
                    list.add(hdx);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử hóa đơn cho khách hàng #" + maKH + ": " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    public static List<HoaDonXuat> getHoaDonByMonthAndYear(int thang, int nam) {
        List<HoaDonXuat> list = new ArrayList<>();
        String sql = "SELECT MaHDX, NgayLap, ThanhTien, MucThue, MaNV, MaKH FROM HoaDonXuat " +
                     "WHERE EXTRACT(MONTH FROM NgayLap) = ? AND EXTRACT(YEAR FROM NgayLap) = ? " +
                     "ORDER BY NgayLap DESC, MaHDX DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new HoaDonXuat(
                        rs.getInt("MaHDX"),
                        rs.getDate("NgayLap").toLocalDate(),
                        rs.getBigDecimal("ThanhTien"),
                        rs.getBigDecimal("MucThue"),
                        rs.getInt("MaNV"),
                        rs.getInt("MaKH")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy HĐX theo tháng/năm: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}