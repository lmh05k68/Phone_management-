package query;

import dbConnection.DBConnection; 
import model.DoiTra;
import java.sql.Connection;
import java.sql.Date; // Dùng java.sql.Date
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Cho RETURN_GENERATED_KEYS
import java.time.LocalDate; // Model dùng LocalDate
import java.util.ArrayList;
import java.util.List;

public class DoiTraQuery {
    private static boolean kiemTraMaDonHangTonTai(int maDonHang, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM hoadonxuat WHERE mahdx = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maDonHang);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("DOITRA_QUERY (kiemTraMaDonHangTonTai): Lỗi kiểm tra mã đơn hàng '" + maDonHang + "': " + e.getMessage());
            throw e; // Ném lại lỗi để tầng gọi xử lý
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            // Không đóng conn ở đây nếu nó được truyền vào
        }
    }
     private static boolean kiemTraMaDonHangTonTai(int maDonHang) {
        String sql = "SELECT 1 FROM hoadonxuat WHERE mahdx = ?"; // Tên cột MaHDX là int
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maDonHang); // Sử dụng setInt
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra mã đơn hàng '" + maDonHang + "': " + e.getMessage());
            return false;
        }
    }


    /**
     * Thêm một yêu cầu đổi trả mới. idDT là tự sinh.
     * Phương thức này tự quản lý Connection.
     * @param dt Đối tượng DoiTra (không cần idDT ban đầu).
     * @return idDT (Integer) tự sinh nếu thành công, hoặc null nếu thất bại hoặc mã đơn hàng không hợp lệ.
     */
    public static Integer themYeuCauDoiTraAndGetId(DoiTra dt) {
        if (dt == null) {
            System.err.println("DOITRA_QUERY (themAndGetId): Lỗi thêm yêu cầu đổi trả: Đối tượng DoiTra là null.");
            return null;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection(); // Mở connection mới
            conn.setAutoCommit(false); // Bắt đầu transaction (nếu cần cho kiểm tra và insert)

            // Kiểm tra MaDonHang (int) có tồn tại không
            if (!kiemTraMaDonHangTonTai(dt.getMaDonHang(), conn)) { // Truyền connection vào
                System.err.println("DOITRA_QUERY (themAndGetId): Từ chối thêm yêu cầu đổi trả: Mã đơn hàng '" + dt.getMaDonHang() + "' không tồn tại.");
                conn.rollback(); // Rollback nếu mã đơn hàng không tồn tại
                return null; // Hoặc throw new IllegalArgumentException("Mã đơn hàng không tồn tại.");
            }

            // iddt là SERIAL, không cần truyền vào
            String sql = "INSERT INTO doitra (makh, masp, madonhang, ngaydoitra, lydo, trangthai) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, dt.getMaKH());
            stmt.setInt(2, dt.getMaSP());
            stmt.setInt(3, dt.getMaDonHang());
            stmt.setDate(4, Date.valueOf(dt.getNgayDoiTra())); // Chuyển LocalDate sang java.sql.Date
            stmt.setString(5, dt.getLyDo());
            stmt.setString(6, dt.getTrangThai());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("DOITRA_QUERY (themAndGetId): Chèn yêu cầu đổi trả thất bại.");
                conn.rollback();
                return null;
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                conn.commit(); // Commit transaction
                System.out.println("DOITRA_QUERY (themAndGetId): Chèn yêu cầu thành công với IDDT: " + generatedId);
                return generatedId;
            } else {
                System.err.println("DOITRA_QUERY (themAndGetId): Chèn thành công nhưng không lấy được ID.");
                conn.rollback();
                return null; // Hoặc throw new SQLException("Không lấy được ID tự sinh sau khi chèn.");
            }

        } catch (SQLException e) {
            System.err.println("DOITRA_QUERY (themAndGetId): Lỗi SQL khi thêm yêu cầu đổi/trả: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("DOITRA_QUERY (themAndGetId): Lỗi khi rollback: " + ex.getMessage());
                }
            }
            return null;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Trả lại trạng thái auto-commit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<DoiTra> getAllDoiTra() {
        List<DoiTra> list = new ArrayList<>();
        String sql = "SELECT iddt, makh, masp, madonhang, ngaydoitra, lydo, trangthai FROM doitra ORDER BY ngaydoitra DESC, iddt DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate ngayDoiTra = null;
                Date sqlDateDoiTra = rs.getDate("ngaydoitra");
                if (sqlDateDoiTra != null) {
                    ngayDoiTra = sqlDateDoiTra.toLocalDate();
                }

                DoiTra dt = new DoiTra(
                        rs.getInt("iddt"),
                        rs.getInt("makh"),
                        rs.getInt("masp"),
                        rs.getInt("madonhang"),
                        ngayDoiTra,
                        rs.getString("lydo"),
                        rs.getString("trangthai")
                );
                list.add(dt);
            }
        } catch (SQLException e) {
            System.err.println("DOITRA_QUERY (getAllDoiTra): Lỗi khi lấy danh sách đổi trả: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }

    /**
     * Cập nhật trạng thái của một yêu cầu đổi trả.
     * @param idDT Mã yêu cầu đổi trả (int).
     * @param trangThaiMoi Trạng thái mới (String).
     * @return true nếu cập nhật thành công, false nếu không hoặc có lỗi.
     */
    public static boolean capNhatTrangThaiDoiTra(int idDT, String trangThaiMoi) {
        String sql = "UPDATE doitra SET trangthai = ? WHERE iddt = ?";
        System.out.println("DOITRA_QUERY (capNhatTrangThai): IDDT=" + idDT + ", TrangThaiMoi=" + trangThaiMoi);
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, trangThaiMoi);
            stmt.setInt(2, idDT);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DOITRA_QUERY (capNhatTrangThai): Cap nhat trang thai thanh cong cho IDDT " + idDT);
                return true;
            } else {
                System.err.println("DOITRA_QUERY (capNhatTrangThai): Khong tim thay IDDT " + idDT + " de cap nhat hoac trang thai khong thay doi.");
                return false; // Không có hàng nào được cập nhật
            }
        } catch (SQLException e) {
            System.err.println("DOITRA_QUERY (capNhatTrangThai): Lỗi SQL khi cập nhật trạng thái cho IDDT " + idDT + ": " + e.getMessage());
            e.printStackTrace();
            return false; // Lỗi SQL
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}