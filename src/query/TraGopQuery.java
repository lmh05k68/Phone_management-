package query;

import dbConnection.DBConnection;
import model.TraGop;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TraGopQuery {

    private static final Logger logger = Logger.getLogger(TraGopQuery.class.getName());
    private static final String SQLSTATE_UNIQUE_VIOLATION = "23505"; // Mã lỗi UNIQUE của PostgreSQL

    /**
     * *** PHƯƠNG THỨC BỊ THIẾU ĐÃ ĐƯỢC THÊM LẠI ***
     * Thêm một phiếu trả góp mới vào CSDL và trả về ID được tạo tự động.
     * @param tg Đối tượng TraGop chứa thông tin cần thêm.
     * @return ID của phiếu mới nếu thành công, ngược lại trả về null.
     */
    public static Integer insertPhieuTraGopAndGetId(TraGop tg) {
        String sql = "INSERT INTO PhieuTraGop (MaHDX, SoThang, LaiSuat, TienGoc, NgayBatDau, DaThanhToan) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, tg.getMaHDX());
            pstmt.setInt(2, tg.getSoThang());
            pstmt.setBigDecimal(3, tg.getLaiSuat());
            pstmt.setBigDecimal(4, tg.getTienGoc());
            pstmt.setDate(5, Date.valueOf(tg.getNgayBatDau()));
            pstmt.setBoolean(6, tg.isDaThanhToan());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warning("Thêm phiếu trả góp thất bại, không có hàng nào được thêm.");
                return null;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Trả về ID thành công
                }
            }
        } catch (SQLException e) {
            // Xử lý lỗi cụ thể nếu hóa đơn đã được đăng ký trả góp (ràng buộc UNIQUE)
            if (SQLSTATE_UNIQUE_VIOLATION.equals(e.getSQLState())) {
                logger.log(Level.WARNING, "Lỗi thêm phiếu trả góp: Hóa đơn " + tg.getMaHDX() + " đã được đăng ký trả góp.");
            } else {
                logger.log(Level.SEVERE, "Lỗi SQL khi thêm phiếu trả góp.", e);
            }
        }
        return null; // Trả về null nếu có lỗi hoặc không lấy được ID
    }

    /**
     * Lấy danh sách phiếu trả góp đã được lọc theo các tiêu chí.
     * @param maHDX       Mã hóa đơn cần tìm (nếu không tìm, để là null).
     * @param isCompleted Trạng thái hoàn thành (true/false), nếu không lọc theo trạng thái, để là null.
     * @return Danh sách các phiếu trả góp thỏa mãn điều kiện.
     */
    public static List<TraGop> getFiltered(Integer maHDX, Boolean isCompleted) {
        List<TraGop> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT MaPhieuTG, MaHDX, SoThang, LaiSuat, TienGoc, NgayBatDau, DaThanhToan " +
            "FROM PhieuTraGop WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (maHDX != null) {
            sql.append("AND MaHDX = ? ");
            params.add(maHDX);
        }
        if (isCompleted != null) {
            sql.append("AND DaThanhToan = ? ");
            params.add(isCompleted);
        }
        sql.append("ORDER BY NgayBatDau DESC, MaPhieuTG DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTraGop(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách phiếu trả góp đã lọc.", e);
        }
        return list;
    }

    /**
     * Tự động cập nhật trạng thái của các phiếu trả góp đã hết hạn.
     */
    public static void updateCompletedStatus() {
        String sql = "UPDATE PhieuTraGop SET DaThanhToan = TRUE " +
                     "WHERE DaThanhToan = FALSE AND (NgayBatDau + (SoThang * INTERVAL '1 month')) < CURRENT_DATE";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            int updatedRows = stmt.executeUpdate(sql);
            if (updatedRows > 0) {
                logger.info("Đã tự động cập nhật " + updatedRows + " phiếu trả góp đã hoàn thành.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tự động cập nhật trạng thái phiếu trả góp.", e);
        }
    }
    private static TraGop mapResultSetToTraGop(ResultSet rs) throws SQLException {
        return new TraGop(
                rs.getInt("MaPhieuTG"),
                rs.getInt("MaHDX"),
                rs.getInt("SoThang"),
                rs.getBigDecimal("LaiSuat"),
                rs.getBigDecimal("TienGoc"),
                rs.getDate("NgayBatDau").toLocalDate(),
                rs.getBoolean("DaThanhToan")
        );
    }
}