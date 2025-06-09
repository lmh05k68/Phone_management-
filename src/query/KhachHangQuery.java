package query;

import model.KhachHang;
import dbConnection.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangQuery {

    /**
     * Chèn một khách hàng mới và trả về ID tự sinh.
     * Có thể sử dụng Connection được truyền vào (cho transaction) hoặc tự quản lý.
     * @param kh Đối tượng KhachHang (không cần MaKH ban đầu).
     * @param conn Connection hiện tại (có thể null).
     * @return MaKH (Integer) của khách hàng mới, hoặc null nếu thất bại.
     * @throws SQLException Nếu có lỗi SQL khi conn được truyền vào và có lỗi.
     */
    public static Integer insertKhachHangAndGetId(KhachHang kh, Connection conn) throws SQLException {
        String sql = "INSERT INTO khachhang (hoten, sdtkh, sodiemtichluy) VALUES (?, ?, ?)";
        System.out.println("KH_QUERY (insertAndGetId): Chuẩn bị insert KhachHang: HoTen=" + kh.getHoTen() + ", SdtKH=" + kh.getSdtKH() + ", Diem=" + kh.getSoDiemTichLuy());

        boolean manageConnection = (conn == null);
        Connection localConn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            if (manageConnection) {
                localConn = DBConnection.getConnection();
                if (localConn == null) {
                    System.err.println("KH_QUERY (insertAndGetId): Không thể kết nối CSDL.");
                    return null;
                }
            } else {
                localConn = conn;
            }

            pstmt = localConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, kh.getHoTen());
            pstmt.setString(2, kh.getSdtKH());
            pstmt.setInt(3, kh.getSoDiemTichLuy());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.err.println("KH_QUERY (insertAndGetId): Chèn KhachHang thất bại, không có hàng nào được thêm.");
                return null;
            }

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                System.out.println("KH_QUERY (insertAndGetId): KhachHang được chèn thành công với ID: " + id);
                return id;
            } else {
                System.err.println("KH_QUERY (insertAndGetId): Chèn KhachHang thành công nhưng không lấy được ID.");
                return null;
            }
        } catch (SQLException e) {
            if (!manageConnection) {
                throw e;
            } else {
                System.err.println("KH_QUERY (insertAndGetId - managed conn): Lỗi SQL: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { /* ignored */ }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { /* ignored */ }
            if (manageConnection && localConn != null) try { localConn.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    public static boolean exists(int maKH) {
        String sql = "SELECT 1 FROM khachhang WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("KH_QUERY (exists): Lỗi SQL khi kiểm tra MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<KhachHang> getCustomersWithAccounts() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT kh.makh, kh.hoten, kh.sdtkh, kh.sodiemtichluy FROM khachhang kh " +
                     "JOIN taikhoan tk ON kh.makh = tk.madoituong " +
                     "WHERE tk.vaitro = 'khachhang' ORDER BY kh.hoten ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new KhachHang(
                    rs.getInt("makh"),
                    rs.getString("hoten"),
                    rs.getString("sdtkh"),
                    rs.getInt("sodiemtichluy")
                ));
            }
        } catch (SQLException e) {
            System.err.println("KH_QUERY (getCustomersWithAccounts): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static List<KhachHang> searchCustomersWithAccounts(String keyword) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT kh.makh, kh.hoten, kh.sdtkh, kh.sodiemtichluy FROM khachhang kh " +
                     "JOIN taikhoan tk ON kh.makh = tk.madoituong " +
                     "WHERE tk.vaitro = 'khachhang' " +
                     "AND (LOWER(kh.hoten) ILIKE LOWER(?) OR kh.sdtkh LIKE ?) ORDER BY kh.hoten ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword.trim() + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new KhachHang(
                        rs.getInt("makh"),
                        rs.getString("hoten"),
                        rs.getString("sdtkh"),
                        rs.getInt("sodiemtichluy")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("KH_QUERY (searchCustomersWithAccounts): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static KhachHang getKhachHangById(int maKH) {
        String sql = "SELECT makh, hoten, sdtkh, sodiemtichluy FROM khachhang WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                        rs.getInt("makh"),
                        rs.getString("hoten"),
                        rs.getString("sdtkh"),
                        rs.getInt("sodiemtichluy")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("KH_QUERY (getKhachHangById): Lỗi SQL cho MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean capNhatThongTin(KhachHang kh, Connection conn) throws SQLException {
        String sql = "UPDATE khachhang SET hoten = ?, sdtkh = ?, sodiemtichluy = ? WHERE makh = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kh.getHoTen());
            stmt.setString(2, kh.getSdtKH());
            stmt.setInt(3, kh.getSoDiemTichLuy());
            stmt.setInt(4, kh.getMaKH());
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean capNhatThongTinCoBan(KhachHang kh) {
        String sql = "UPDATE khachhang SET hoten = ?, sdtkh = ? WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kh.getHoTen());
            stmt.setString(2, kh.getSdtKH());
            stmt.setInt(3, kh.getMaKH());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                 System.err.println("KH_QUERY (capNhatThongTinCoBan): Không tìm thấy MaKH " + kh.getMaKH() + " để cập nhật hoặc thông tin không đổi.");
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("KH_QUERY (capNhatThongTinCoBan): Lỗi SQL cho MaKH " + kh.getMaKH() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean congDiemTichLuy(int maKH, int soDiemCongThem, Connection conn) throws SQLException {
        if (soDiemCongThem <= 0) {
            System.out.println("KH_QUERY (congDiemTichLuy): Số điểm cộng thêm (" + soDiemCongThem + ") không dương. Không cộng điểm cho MaKH " + maKH);
            return true;
        }
        String sql = "UPDATE khachhang SET sodiemtichluy = COALESCE(sodiemtichluy, 0) + ? WHERE makh = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, soDiemCongThem);
            stmt.setInt(2, maKH);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("KH_QUERY (congDiemTichLuy): Đã cộng " + soDiemCongThem + " điểm vào sodiemtichluy cho MaKH " + maKH + " thành công.");
                return true;
            } else {
                System.err.println("KH_QUERY (congDiemTichLuy): Không tìm thấy MaKH " + maKH + " để cộng điểm vào bảng khachhang.");
                throw new SQLException("Không thể cập nhật điểm cho khách hàng không tồn tại: MaKH=" + maKH);
            }
        }
    }

    public static boolean resetDiemTichLuy(int maKH, Connection conn) throws SQLException {
        String sql = "UPDATE khachhang SET sodiemtichluy = 0 WHERE makh = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            int rowsAffected = stmt.executeUpdate();
             if (rowsAffected > 0) {
                System.out.println("KH_QUERY (resetDiemTichLuy): Reset điểm cho MaKH " + maKH + " thành công.");
                return true;
            } else {
                System.err.println("KH_QUERY (resetDiemTichLuy): Không tìm thấy MaKH " + maKH + " để reset điểm.");
                throw new SQLException("Không thể reset điểm cho khách hàng không tồn tại: MaKH=" + maKH);
            }
        }
    }

    public static int getSoDiemTichLuy(int maKH) {
        String sql = "SELECT sodiemtichluy FROM khachhang WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("sodiemtichluy");
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("KH_QUERY (getSoDiemTichLuy): Lỗi SQL khi lấy số điểm cho MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public static int tinhPhanTramGiamTuDiem(int maKH) {
        int tongDiem = getSoDiemTichLuy(maKH);
        if (tongDiem < 100) {
            return 0;
        }
        return Math.min(tongDiem / 100, 20);
    }

    public static List<KhachHang> getAllKhachHang() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT makh, hoten, sdtkh, sodiemtichluy FROM khachhang ORDER BY hoten ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new KhachHang(
                    rs.getInt("makh"),
                    rs.getString("hoten"),
                    rs.getString("sdtkh"),
                    rs.getInt("sodiemtichluy")
                ));
            }
        } catch (SQLException e) {
            System.err.println("KH_QUERY (getAllKhachHang): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}