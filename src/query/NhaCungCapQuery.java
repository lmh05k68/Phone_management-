package query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.NhaCungCap;
import dbConnection.DBConnection;

public class NhaCungCapQuery {
    public static List<NhaCungCap> getAll() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT MaNCC, TenNCC, DiaChi, SdtNCC FROM NhaCungCap"; // Thêm tên cột rõ ràng

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap(
                    rs.getInt("MaNCC"), 
                    rs.getString("TenNCC"),
                    rs.getString("DiaChi"),
                    rs.getString("SdtNCC")
                );
                list.add(ncc);
            }
        } catch (SQLException e) {
            System.err.println("NhaCungCapQuery (getAll): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Chèn một nhà cung cấp mới. MaNCC là tự sinh.
     * @param ncc Đối tượng NhaCungCap chỉ chứa TenNCC, DiaChi, SdtNCC.
     * @return MaNCC của nhà cung cấp mới được tạo, hoặc null nếu thất bại.
     */
    public static Integer insertNhaCungCapAndGetId(NhaCungCap ncc) {
        // MaNCC là SERIAL, không cần truyền vào
        String sql = "INSERT INTO NhaCungCap (TenNCC, DiaChi, SdtNCC) VALUES (?, ?, ?)";
        ResultSet generatedKeys = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ncc.getTenNCC());
            stmt.setString(2, ncc.getDiaChi());
            stmt.setString(3, ncc.getSdtNCC());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Trả về ID tự sinh
                }
            }
            return null; // Trả về null nếu không chèn được hoặc không lấy được ID
        } catch (SQLException e) {
            System.err.println("NhaCungCapQuery (insertNhaCungCapAndGetId): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { /* ignored */ }
        }
    }


    public static boolean updateNhaCungCap(NhaCungCap ncc) {
        String sql = "UPDATE NhaCungCap SET TenNCC = ?, DiaChi = ?, SdtNCC = ? WHERE MaNCC = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ncc.getTenNCC());
            stmt.setString(2, ncc.getDiaChi());
            stmt.setString(3, ncc.getSdtNCC());
            stmt.setInt(4, ncc.getMaNCC()); // Sửa thành setInt

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("NhaCungCapQuery (updateNhaCungCap): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteNhaCungCap(int maNCC) { // Sửa tham số thành int
        String sql = "DELETE FROM NhaCungCap WHERE MaNCC = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maNCC); // Sửa thành setInt
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("NhaCungCapQuery (deleteNhaCungCap): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<NhaCungCap> searchNhaCungCap(String keyword) {
        List<NhaCungCap> list = new ArrayList<>();
        // Tìm kiếm có thể dựa trên TenNCC, DiaChi, hoặc SdtNCC
        String sql = "SELECT MaNCC, TenNCC, DiaChi, SdtNCC FROM NhaCungCap WHERE LOWER(TenNCC) LIKE LOWER(?) OR LOWER(DiaChi) LIKE LOWER(?) OR SdtNCC LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            stmt.setString(3, likeKeyword);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new NhaCungCap(
                        rs.getInt("MaNCC"), // Sửa thành getInt
                        rs.getString("TenNCC"),
                        rs.getString("DiaChi"),
                        rs.getString("SdtNCC")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("NhaCungCapQuery (searchNhaCungCap): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isMaNCCExists(int maNCC) { // Sửa tham số thành int
        String sql = "SELECT COUNT(*) FROM NhaCungCap WHERE MaNCC = ?"; // Sửa tên bảng và cột
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maNCC); // Sửa thành setInt
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("NhaCungCapQuery (isMaNCCExists): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Các phương thức getDistinctProvinces và getDistinctPhonePrefixes giữ nguyên nếu logic là đúng
    // Nhưng nếu DiaChi có thể phức tạp hơn chỉ là tỉnh, cần xem xét lại logic
    public static List<String> getDistinctProvinces() {
        List<String> provinces = new ArrayList<>();
        // Giả sử DiaChi lưu trữ thông tin tỉnh/thành phố một cách nhất quán
        // Nếu không, bạn cần một cách parse phức tạp hơn hoặc một cột riêng cho tỉnh/thành phố.
        String sql = "SELECT DISTINCT DiaChi FROM NhaCungCap ORDER BY DiaChi";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                provinces.add(rs.getString("DiaChi"));
            }
        } catch (SQLException e) {
            System.err.println("NhaCungCapQuery (getDistinctProvinces): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return provinces;
    }

    public static List<String> getDistinctPhonePrefixes() {
        List<String> prefixes = new ArrayList<>();
        // Giả sử SdtNCC có định dạng nhất quán để lấy 3 ký tự đầu
        String sql = "SELECT DISTINCT SUBSTRING(SdtNCC FROM 1 FOR 3) AS prefix FROM NhaCungCap ORDER BY prefix";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                prefixes.add(rs.getString("prefix"));
            }
        } catch (SQLException e) {
            System.err.println("NhaCungCapQuery (getDistinctPhonePrefixes): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return prefixes;
    }

    public static NhaCungCap getNhaCungCapById(int maNCC) {
        String sql = "SELECT MaNCC, TenNCC, DiaChi, SdtNCC FROM NhaCungCap WHERE MaNCC = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maNCC);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new NhaCungCap(
                        rs.getInt("MaNCC"),
                        rs.getString("TenNCC"),
                        rs.getString("DiaChi"),
                        rs.getString("SdtNCC")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("NhaCungCapQuery (getNhaCungCapById): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}