package query;

import model.KhachHang;
import dbConnection.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangQuery {
    public static Integer insertKhachHangAndGetId(KhachHang kh, Connection conn) throws SQLException {
        String sql = "INSERT INTO khachhang (hoten, sdtkh, sodiemtichluy) VALUES (?, ?, ?)";
        System.out.println("KH_QUERY (insertAndGetId): Chuẩn bị insert KhachHang: HoTen=" + kh.getHoTen());
        
        boolean manageConnection = (conn == null); // Kiểm tra xem có cần quản lý connection không
        Connection localConn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            if (manageConnection) {
                localConn = DBConnection.getConnection();
            } else {
                localConn = conn; // Sử dụng connection được truyền vào
            }

            pstmt = localConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, kh.getHoTen());
            pstmt.setString(2, kh.getSdtKH());
            pstmt.setInt(3, kh.getSoDiemTichLuy());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.err.println("KH_QUERY (insertAndGetId): Chèn KhachHang thất bại, không có hàng nào được thêm.");
                if (manageConnection && localConn != null) localConn.rollback(); // Nếu tự quản lý
                return null;
            }

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                System.out.println("KH_QUERY (insertAndGetId): KhachHang được chèn thành công với ID: " + id);
                if (manageConnection && localConn != null) localConn.commit(); // Nếu tự quản lý
                return id;
            } else {
                System.err.println("KH_QUERY (insertAndGetId): Chèn KhachHang thành công nhưng không lấy được ID.");
                if (manageConnection && localConn != null) localConn.rollback(); // Nếu tự quản lý
                return null;
            }
        } catch (SQLException e) {
            if (manageConnection && localConn != null) {
                try { localConn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e; // Ném lại lỗi để lớp gọi xử lý transaction
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { /* ignored */ }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { /* ignored */ }
            if (manageConnection && localConn != null) try { localConn.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    public static boolean exists(int maKH) {
        String sql = "SELECT 1 FROM khachhang WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection(); // Tự quản lý connection cho việc đọc
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("KH_QUERY (exists): Lỗi SQL khi kiểm tra sự tồn tại của khách hàng MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<KhachHang> getCustomersWithAccounts() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT kh.makh, kh.hoten, kh.sdtkh, kh.sodiemtichluy FROM khachhang kh " +
                     "JOIN taikhoan tk ON kh.makh = tk.madoituong " +
                     "WHERE tk.vaitro = 'khachhang'";
        try (Connection conn = DBConnection.getConnection(); // Tự quản lý
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                    rs.getInt("makh"),
                    rs.getString("hoten"),
                    rs.getString("sdtkh"),
                    rs.getInt("sodiemtichluy")
                );
                list.add(kh);
            }
        } catch (SQLException e) {
            System.err.println("KH_QUERY (getCustomersWithAccounts): Lỗi khi lấy danh sách khách hàng có tài khoản: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static List<KhachHang> searchCustomersWithAccounts(String keyword) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT kh.makh, kh.hoten, kh.sdtkh, kh.sodiemtichluy FROM khachhang kh " +
                     "JOIN taikhoan tk ON kh.makh = tk.madoituong " +
                     "WHERE tk.vaitro = 'khachhang' " +
                     "AND (LOWER(kh.hoten) LIKE LOWER(?) OR kh.sdtkh LIKE ?)"; // Sử dụng LOWER để tìm kiếm không phân biệt hoa thường cho tên
        try (Connection conn = DBConnection.getConnection(); // Tự quản lý
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    KhachHang kh = new KhachHang(
                        rs.getInt("makh"),
                        rs.getString("hoten"),
                        rs.getString("sdtkh"),
                        rs.getInt("sodiemtichluy")
                    );
                    list.add(kh);
                }
            }
        } catch (SQLException e) {
            System.err.println("KH_QUERY (searchCustomersWithAccounts): Lỗi khi tìm kiếm khách hàng có tài khoản: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static KhachHang getKhachHangById(int maKH) {
        String sql = "SELECT makh, hoten, sdtkh, sodiemtichluy FROM khachhang WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection(); // Tự quản lý
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
            System.err.println("KH_QUERY (getKhachHangById): Lỗi SQL khi lấy khách hàng bằng ID MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cập nhật thông tin khách hàng. Sử dụng Connection được truyền vào.
     * @param kh Đối tượng KhachHang với thông tin đã cập nhật.
     * @param conn Connection để thực hiện thao tác (phải được quản lý từ bên ngoài).
     * @return true nếu thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public static boolean capNhatThongTin(KhachHang kh, Connection conn) throws SQLException {
        String sql = "UPDATE khachhang SET hoten = ?, sdtkh = ?, sodiemtichluy = ? WHERE makh = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kh.getHoTen());
            stmt.setString(2, kh.getSdtKH());
            stmt.setInt(3, kh.getSoDiemTichLuy());
            stmt.setInt(4, kh.getMaKH());
            return stmt.executeUpdate() > 0;
        }
        // SQLException sẽ được ném ra để lớp gọi xử lý transaction
    }


    /**
     * Cộng điểm tích lũy cho khách hàng. Sử dụng Connection được truyền vào.
     * @param maKH Mã khách hàng (int).
     * @param soDiemCongThem Số điểm cần cộng.
     * @param conn Connection để thực hiện thao tác.
     * @return true nếu thành công.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public static boolean congDiemTichLuy(int maKH, int soDiemCongThem, Connection conn) throws SQLException {
        String sql = "UPDATE khachhang SET sodiemtichluy = COALESCE(sodiemtichluy, 0) + ? WHERE makh = ?";
        System.out.println("KH_QUERY (congDiemTichLuy): Cộng thêm " + soDiemCongThem + " điểm cho MaKH " + maKH);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, soDiemCongThem);
            stmt.setInt(2, maKH);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("KH_QUERY (congDiemTichLuy): Cập nhật điểm thành công cho MaKH " + maKH);
                return true;
            } else {
                System.err.println("KH_QUERY (congDiemTichLuy): Không tìm thấy MaKH " + maKH + " để cập nhật điểm.");
                return false; // Có thể coi đây là một thất bại trong ngữ cảnh giao dịch
            }
        }
        // SQLException sẽ được ném ra để lớp gọi xử lý transaction
    }

    /**
     * Reset điểm tích lũy của khách hàng về 0. Sử dụng Connection được truyền vào.
     * @param maKH Mã khách hàng (int).
     * @param conn Connection để thực hiện thao tác.
     * @return true nếu thành công.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public static boolean resetDiemTichLuy(int maKH, Connection conn) throws SQLException {
        String sql = "UPDATE khachhang SET sodiemtichluy = 0 WHERE makh = ?";
        System.out.println("KH_QUERY (resetDiemTichLuy): Reset điểm tích lũy về 0 cho MaKH " + maKH + " sử dụng connection được truyền vào.");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            int rowsAffected = stmt.executeUpdate();
             if (rowsAffected > 0) {
                System.out.println("KH_QUERY (resetDiemTichLuy): Reset điểm thành công cho MaKH " + maKH);
                return true;
            } else {
                // Trong một giao dịch, nếu không tìm thấy KH để reset điểm có thể là một vấn đề.
                // Cân nhắc ném lỗi hoặc trả về false để giao dịch có thể rollback.
                System.err.println("KH_QUERY (resetDiemTichLuy): Không tìm thấy MaKH " + maKH + " để reset điểm.");
                return false;
            }
        }
        // SQLException sẽ được ném ra để lớp gọi xử lý transaction
    }

    public static int getSoDiemTichLuy(int maKH) {
        String sql = "SELECT sodiemtichluy FROM khachhang WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection(); // Tự quản lý
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("sodiemtichluy");
                } else {
                    System.out.println("KH_QUERY (getSoDiemTichLuy): Không tìm thấy MaKH " + maKH + " hoặc không có điểm.");
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("KH_QUERY (getSoDiemTichLuy): Lỗi SQL khi lấy số điểm tích lũy cho MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public static int tinhPhanTramGiamTuDiem(int maKH) {
        int tongDiem = getSoDiemTichLuy(maKH);
        if (tongDiem <= 0) return 0;
        return Math.min(tongDiem / 100, 20); // Ví dụ: 100 điểm = 1%, tối đa 20%
    }
}