package query;

import dbConnection.DBConnection;
import model.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamQuery {

    // Giữ nguyên: getAll() sẽ trả về danh sách sắp xếp theo tên (default)
    public static List<SanPham> getAll() { // Đổi tên từ getAllSanPham() cho ngắn gọn
        return getAllSanPhamOrderedByTenSP(true);
    }

    // Phương thức này được gọi bởi getAll()
    public static List<SanPham> getAllSanPhamOrderedByTenSP(boolean ascending) {
        List<SanPham> dsSP = new ArrayList<>();
        String sql = "SELECT masp, tensp, mau, dongia, nuocsx, hangsx, soluong FROM sanpham";
        if (ascending) {
            sql += " ORDER BY LOWER(tensp) ASC"; // Sắp xếp theo tên không phân biệt hoa thường
        } else {
            sql += " ORDER BY LOWER(tensp) DESC";
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dsSP.add(new SanPham(
                        rs.getInt("masp"),
                        rs.getString("tensp"),
                        rs.getString("mau"),
                        rs.getDouble("dongia"),
                        rs.getString("nuocsx"),
                        rs.getString("hangsx"),
                        rs.getInt("soluong")
                ));
            }
        } catch (SQLException e) {
            System.err.println("SP_QUERY (getAllSanPhamOrderedByTenSP): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return dsSP;
    }

    // Phương thức này có thể được dùng nếu ManageProduct muốn sắp xếp theo MaSP từ Query
    public static List<SanPham> getAllSanPhamOrderedByMaSP(boolean ascending) {
        List<SanPham> dsSP = new ArrayList<>();
        String sql = "SELECT masp, tensp, mau, dongia, nuocsx, hangsx, soluong FROM sanpham";
        if (ascending) {
            sql += " ORDER BY masp ASC";
        } else {
            sql += " ORDER BY masp DESC";
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dsSP.add(new SanPham(
                        rs.getInt("masp"),
                        rs.getString("tensp"),
                        rs.getString("mau"),
                        rs.getDouble("dongia"),
                        rs.getString("nuocsx"),
                        rs.getString("hangsx"),
                        rs.getInt("soluong")
                ));
            }
        } catch (SQLException e) {
            System.err.println("SP_QUERY (getAllSanPhamOrderedByMaSP): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return dsSP;
    }


    public static List<SanPham> getAllSanPhamActiving() {
        List<SanPham> dsSP = new ArrayList<>();
        String sql = "SELECT masp, tensp, mau, dongia, nuocsx, hangsx, soluong FROM sanpham WHERE soluong > 0 ORDER BY LOWER(tensp) ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                 dsSP.add(new SanPham(
                        rs.getInt("masp"),
                        rs.getString("tensp"),
                        rs.getString("mau"),
                        rs.getDouble("dongia"),
                        rs.getString("nuocsx"),
                        rs.getString("hangsx"),
                        rs.getInt("soluong")
                ));
            }
        } catch (SQLException e) {
            System.err.println("SP_QUERY (getAllActiving): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return dsSP;
    }

    public static List<SanPham> searchSanPhamByTen(String keyword) {
        List<SanPham> dsSP = new ArrayList<>();
        String sql = "SELECT masp, tensp, mau, dongia, nuocsx, hangsx, soluong FROM sanpham WHERE LOWER(tensp) ILIKE LOWER(?) ORDER BY LOWER(tensp) ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword.trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dsSP.add(new SanPham(
                        rs.getInt("masp"),
                        rs.getString("tensp"),
                        rs.getString("mau"),
                        rs.getDouble("dongia"),
                        rs.getString("nuocsx"),
                        rs.getString("hangsx"),
                        rs.getInt("soluong")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("SP_QUERY (searchByTen): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return dsSP;
    }

    public static List<SanPham> filterSanPhamByHangSX(String hangSX) {
        List<SanPham> dsSP = new ArrayList<>();
        String sql = "SELECT masp, tensp, mau, dongia, nuocsx, hangsx, soluong FROM sanpham WHERE hangsx = ? ORDER BY LOWER(tensp) ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hangSX);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                     dsSP.add(new SanPham(
                        rs.getInt("masp"),
                        rs.getString("tensp"),
                        rs.getString("mau"),
                        rs.getDouble("dongia"),
                        rs.getString("nuocsx"),
                        rs.getString("hangsx"),
                        rs.getInt("soluong")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("SP_QUERY (filterByHangSX): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return dsSP;
    }

    public static int getSoLuongTonKho(int maSP, Connection conn) throws SQLException {
        String sql = "SELECT soluong FROM sanpham WHERE masp = ?";
        boolean manageConnection = (conn == null);
        Connection localConn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int soLuong = -1;

        try {
            if (manageConnection) {
                localConn = DBConnection.getConnection();
                if (localConn == null) throw new SQLException("Không thể kết nối CSDL để lấy tồn kho.");
            } else {
                localConn = conn; // Sử dụng connection được truyền vào
            }
            stmt = localConn.prepareStatement(sql);
            stmt.setInt(1, maSP);
            rs = stmt.executeQuery();

            if (rs.next()) {
                soLuong = rs.getInt("soluong");
            } else {
                System.err.println("SP_QUERY (getSoLuongTonKho): Không tìm thấy sản phẩm với mã: " + maSP);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (manageConnection && localConn != null && !localConn.isClosed()) { // Chỉ đóng nếu tự quản lý và chưa đóng
                 try { localConn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return soLuong;
    }

    public static boolean updateSoLuong(int maSP, int soLuongThayDoi, Connection conn) throws SQLException {
        if (conn == null) {
            throw new SQLException("Connection không được null cho thao tác update số lượng trong transaction.");
        }
        String sql;
        if (soLuongThayDoi < 0) {
            sql = "UPDATE sanpham SET soluong = soluong + ? WHERE masp = ? AND soluong >= ?";
        } else {
            sql = "UPDATE sanpham SET soluong = soluong + ? WHERE masp = ?";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, soLuongThayDoi);
            stmt.setInt(2, maSP);
            if (soLuongThayDoi < 0) {
                stmt.setInt(3, -soLuongThayDoi);
            }
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0 && soLuongThayDoi < 0) {
                 System.err.println("SP_QUERY (updateSoLuong): Cập nhật số lượng thất bại cho MaSP: " + maSP +
                                    " (giảm). Nguyên nhân có thể: SP không tồn tại, hoặc không đủ tồn kho.");
            } else if (affectedRows == 0 && soLuongThayDoi >= 0){
                 System.err.println("SP_QUERY (updateSoLuong): Cập nhật số lượng thất bại cho MaSP: " + maSP +
                                    " (tăng/không đổi). SP có thể không tồn tại.");
            }
            return affectedRows > 0;
        }
    }

    public static boolean tangSoLuong(int maSP, int soLuongTangThem, Connection conn) throws SQLException {
        if (soLuongTangThem < 0) {
            throw new IllegalArgumentException("Số lượng tăng thêm không thể âm.");
        }
        return updateSoLuong(maSP, soLuongTangThem, conn);
    }

    public static boolean giamSoLuongKhiBan(int maSP, int soLuongGiam, Connection conn) throws SQLException {
        if (soLuongGiam < 0) {
            throw new IllegalArgumentException("Số lượng giảm không thể âm.");
        }
        return updateSoLuong(maSP, -soLuongGiam, conn);
    }


    public static boolean updateSanPham(SanPham sp) {
        String sql = "UPDATE sanpham SET tensp = ?, mau = ?, dongia = ?, nuocsx = ?, hangsx = ?, soluong = ? WHERE masp = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sp.getTenSP());
            stmt.setString(2, sp.getMau());
            stmt.setDouble(3, sp.getDonGia());
            stmt.setString(4, sp.getNuocSX());
            stmt.setString(5, sp.getHangSX());
            stmt.setInt(6, sp.getSoLuong());
            stmt.setInt(7, sp.getMaSP());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SP_QUERY (updateSanPham): Lỗi SQL cho MaSP " + sp.getMaSP() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteSanPham(int maSP) {
        String sql = "DELETE FROM sanpham WHERE masp = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maSP);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SP_QUERY (deleteSanPham): Lỗi SQL cho MaSP " + maSP + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Integer insertSanPhamAndGetId(SanPham sp) {
        // MaSP là SERIAL, không cần truyền vào
        String sql = "INSERT INTO sanpham (tensp, mau, dongia, nuocsx, hangsx, soluong) VALUES (?, ?, ?, ?, ?, ?)";
        ResultSet generatedKeys = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, sp.getTenSP());
            stmt.setString(2, sp.getMau());
            stmt.setDouble(3, sp.getDonGia());
            stmt.setString(4, sp.getNuocSX());
            stmt.setString(5, sp.getHangSX());
            stmt.setInt(6, sp.getSoLuong());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("SP_QUERY (insertSanPhamAndGetId): Chèn sản phẩm thất bại.");
                return null;
            }
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1); // Lấy MaSP tự sinh
                System.out.println("SP_QUERY (insertSanPhamAndGetId): Sản phẩm được chèn với MaSP: " + id);
                return id;
            } else {
                System.err.println("SP_QUERY (insertSanPhamAndGetId): Chèn sản phẩm thành công nhưng không lấy được ID.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("SP_QUERY (insertSanPhamAndGetId): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    public static List<String> getAllHangSX() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT hangsx FROM sanpham WHERE hangsx IS NOT NULL AND hangsx <> '' ORDER BY hangsx ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("hangsx"));
            }
        } catch (SQLException e) {
            System.err.println("SP_QUERY (getAllHangSX): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static SanPham getSanPhamById(int maSP) {
        String sql = "SELECT masp, tensp, mau, dongia, nuocsx, hangsx, soluong FROM sanpham WHERE masp = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maSP);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new SanPham(
                        rs.getInt("masp"),
                        rs.getString("tensp"),
                        rs.getString("mau"),
                        rs.getDouble("dongia"),
                        rs.getString("nuocsx"),
                        rs.getString("hangsx"),
                        rs.getInt("soluong")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("SP_QUERY (getSanPhamById): Lỗi SQL cho MaSP " + maSP + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}