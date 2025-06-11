package query;

import model.KhachHang;
import dbConnection.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class KhachHangQuery {
	public static Integer insertKhachHangAndGetId(KhachHang kh, Connection conn) throws SQLException {
	    String sql = "INSERT INTO KhachHang (HoTen, SdtKH) VALUES (?, ?)";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, kh.getHoTen());
	        pstmt.setString(2, kh.getSdtKH());
	        
	        int affectedRows = pstmt.executeUpdate();
	        if (affectedRows == 0) {
	            throw new SQLException("Chèn KhachHang thất bại, không có hàng nào được thêm.");
	        }

	        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                return generatedKeys.getInt(1);
	            } else {
	                throw new SQLException("Chèn KhachHang thành công nhưng không lấy được ID.");
	            }
	        }
	    }
	}
    public static List<KhachHang> getCustomersWithAccounts() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT kh.MaKH, kh.HoTen, kh.SdtKH, kh.SoDiemTichLuy FROM KhachHang kh " +
                     "JOIN TaiKhoan tk ON kh.MaKH = tk.MaKH " +
                     "ORDER BY kh.HoTen ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToKhachHang(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static void themDiemThuong(int maKH, int maHDX, int soDiem, Connection conn) throws SQLException {
        if (soDiem <= 0) return;
        String sql = "INSERT INTO DiemThuong (MaKH, MaHDX, SoDiem) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            stmt.setInt(2, maHDX);
            stmt.setInt(3, soDiem);
            stmt.executeUpdate();
        }
    }
    public static int getSoDiemTichLuy(int maKH) {
        String sql = "SELECT SoDiemTichLuy FROM KhachHang WHERE MaKH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("SoDiemTichLuy");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Trả về 0 nếu có lỗi hoặc không tìm thấy KH
    }
    public static int getSoDiemTichLuy(int maKH, Connection conn) throws SQLException {
        String sql = "SELECT SoDiemTichLuy FROM KhachHang WHERE MaKH = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("SoDiemTichLuy");
                }
            }
        }
        return 0;
    }
    public static void suDungDiemThuong(int maKH, int maHDX, int soDiemDaDung, Connection conn) throws SQLException {
        if (soDiemDaDung <= 0) return;
        String sql = "INSERT INTO DiemThuong (MaKH, MaHDX, SoDiem) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            stmt.setInt(2, maHDX);
            stmt.setInt(3, -soDiemDaDung); // <<< Ghi nhận số điểm ÂM
            stmt.executeUpdate();
        }
    }
    public static KhachHang getKhachHangById(int maKH) {
        String sql = "SELECT MaKH, HoTen, SdtKH, SoDiemTichLuy FROM KhachHang WHERE MaKH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                        rs.getInt("MaKH"),
                        rs.getString("HoTen"),
                        rs.getString("SdtKH"),
                        rs.getInt("SoDiemTichLuy")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean capNhatThongTinCoBan(KhachHang kh) {
        String sql = "UPDATE KhachHang SET HoTen = ?, SdtKH = ? WHERE MaKH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, kh.getHoTen());
            stmt.setString(2, kh.getSdtKH());
            stmt.setInt(3, kh.getMaKH());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Mã lỗi cho UNIQUE violation trong PostgreSQL
                System.err.println("Lỗi cập nhật: Số điện thoại '" + kh.getSdtKH() + "' đã tồn tại.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }
    public static List<KhachHang> searchCustomersWithAccounts(String keyword) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT kh.MaKH, kh.HoTen, kh.SdtKH, kh.SoDiemTichLuy FROM KhachHang kh " +
                     "JOIN TaiKhoan tk ON kh.MaKH = tk.MaKH " +
                     "WHERE CAST(kh.MaKH AS TEXT) = ? " + // Tìm theo MaKH chính xác
                     "OR LOWER(kh.HoTen) LIKE LOWER(?) " + // Tìm theo tên không phân biệt hoa thường
                     "OR kh.SdtKH LIKE ?"; // Tìm theo SĐT
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, keyword); // Tìm chính xác theo MaKH
            stmt.setString(2, likeKeyword); // Tìm tương đối theo Tên
            stmt.setString(3, likeKeyword); 
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToKhachHang(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    private static KhachHang mapResultSetToKhachHang(ResultSet rs) throws SQLException {
        return new KhachHang(
            rs.getInt("MaKH"),
            rs.getString("HoTen"),
            rs.getString("SdtKH"),
            rs.getInt("SoDiemTichLuy")
        );
    }
}