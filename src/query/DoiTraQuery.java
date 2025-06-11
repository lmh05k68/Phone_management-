package query;
import java.util.logging.Level;
import java.util.logging.Logger;
import dbConnection.DBConnection;
import model.DoiTra;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DoiTraQuery {
    private static boolean kiemTraMaDonHangTonTai(int maDonHang, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM hoadonxuat WHERE mahdx = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maDonHang);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    private static final Logger logger = Logger.getLogger(DoiTraQuery.class.getName());
    public static Integer themYeuCauDoiTraAndGetId(DoiTra dt) {
        if (dt == null) {
            System.err.println("DOITRA_QUERY (themAndGetId): Lỗi: Đối tượng DoiTra là null.");
            return null;
        }
        String sql = "INSERT INTO doitra (maspcuthe, makh, mahdx, ngaydoitra, lydo, trangthai) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false); 
            if (!kiemTraMaDonHangTonTai(dt.getMaDonHang(), conn)) {
                System.err.println("DOITRA_QUERY (themAndGetId): Mã đơn hàng '" + dt.getMaDonHang() + "' không tồn tại.");
                conn.rollback();
                return null;
            }
            stmt.setString(1, dt.getMaSPCuThe()); // Cột 1 là MaSPCuThe (VARCHAR)
            stmt.setInt(2, dt.getMaKH());         // Cột 2 là MaKH (INT)
            stmt.setInt(3, dt.getMaDonHang());    // Cột 3 là MaHDX (INT)
            stmt.setDate(4, Date.valueOf(dt.getNgayDoiTra()));
            stmt.setString(5, dt.getLyDo());
            stmt.setString(6, dt.getTrangThai());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return null;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    conn.commit(); // Commit transaction thành công
                    return generatedId;
                } else {
                    conn.rollback();
                    return null;
                }
            }

        } catch (SQLException e) {
            System.err.println("DOITRA_QUERY (themAndGetId): Lỗi SQL khi thêm yêu cầu đổi/trả: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static List<DoiTra> getAllDoiTra() {
        List<DoiTra> list = new ArrayList<>();
        String sql = "SELECT iddt, maspcuthe, makh, mahdx, ngaydoitra, lydo, trangthai FROM doitra ORDER BY ngaydoitra DESC, iddt DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LocalDate ngayDoiTra = rs.getDate("ngaydoitra") != null ? rs.getDate("ngaydoitra").toLocalDate() : null;
                list.add(new DoiTra(
                        rs.getInt("iddt"),
                        rs.getString("maspcuthe"),
                        rs.getInt("makh"),
                        rs.getInt("mahdx"),
                        ngayDoiTra,
                        rs.getString("lydo"),
                        rs.getString("trangthai")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách đổi trả.", e);
        }
        return list;
    }

    public static boolean capNhatTrangThaiDoiTra(int idDT, String trangThaiMoi) {
        String sql = "UPDATE doitra SET trangthai = ? WHERE iddt = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThaiMoi);
            stmt.setInt(2, idDT);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("DOITRA_QUERY (capNhatTrangThai): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public static boolean processReturnRequest(int idDT, String maSPCuThe, String newStatus) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Bước 1: Luôn cập nhật trạng thái của yêu cầu trong bảng doitra
            String updateDoiTraSql = "UPDATE doitra SET trangthai = ? WHERE iddt = ?";
            try (PreparedStatement stmtDoiTra = conn.prepareStatement(updateDoiTraSql)) {
                stmtDoiTra.setString(1, newStatus);
                stmtDoiTra.setInt(2, idDT);
                if (stmtDoiTra.executeUpdate() == 0) {
                    throw new SQLException("Không tìm thấy yêu cầu đổi trả với ID: " + idDT + " để cập nhật.");
                }
            }
             logger.info("Đã cập nhật trạng thái bảng doitra cho ID " + idDT + " thành " + newStatus);


            // Bước 2: Nếu yêu cầu được "Duyệt", cập nhật thêm trạng thái của sản phẩm
            // Giả sử trạng thái mới của sản phẩm trả về là 'Da Tra Hang'
            if ("Đã duyệt".equalsIgnoreCase(newStatus)) {
                String updateSPCTSql = "UPDATE sanphamcuthe SET trangthai = 'Da Tra Hang' WHERE maspcuthe = ?";
                try (PreparedStatement stmtSPCT = conn.prepareStatement(updateSPCTSql)) {
                    stmtSPCT.setString(1, maSPCuThe);
                    if (stmtSPCT.executeUpdate() == 0) {
                         throw new SQLException("Không tìm thấy sản phẩm cụ thể với mã: " + maSPCuThe + " để cập nhật.");
                    }
                }
                logger.info("Đã cập nhật trạng thái bảng sanphamcuthe cho MaSP: " + maSPCuThe + " thành 'Da Tra Hang'");
            }

            // Nếu tất cả các bước trên thành công, commit transaction
            conn.commit();
            logger.info("Transaction xử lý yêu cầu đổi trả ID " + idDT + " thành công.");
            return true;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi SQL trong quá trình xử lý yêu cầu ID " + idDT + ". Đang rollback...", e);
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.info("Rollback transaction thành công.");
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Lỗi nghiêm trọng khi rollback.", ex);
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Trả lại trạng thái mặc định cho connection pool
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Lỗi khi đóng kết nối.", e);
                }
            }
        }
    }
}