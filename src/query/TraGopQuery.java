package query;
import java.math.BigDecimal;
import dbConnection.DBConnection;
import model.TraGop;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TraGopQuery {
    private static final Logger logger = Logger.getLogger(TraGopQuery.class.getName());
    private static final String SQLSTATE_UNIQUE_VIOLATION = "23505";

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
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            if (SQLSTATE_UNIQUE_VIOLATION.equals(e.getSQLState())) {
                logger.log(Level.WARNING, "Lỗi thêm phiếu trả góp: Hóa đơn " + tg.getMaHDX() + " đã được đăng ký trả góp.");
            } else {
                logger.log(Level.SEVERE, "Lỗi SQL khi thêm phiếu trả góp.", e);
            }
        }
        return null;
    }

    public static List<TraGop> getFiltered(Integer maHDX, Boolean isCompleted) {
        List<TraGop> list = new ArrayList<>();
        // *** SỬA LỖI 1: Sửa cú pháp chuỗi SQL ***
        StringBuilder sql = new StringBuilder(
            "SELECT MaPhieuTG, MaHDX, SoThang, LaiSuat, TienGoc, NgayBatDau, DaThanhToan, " +
            "tinh_tra_gop_hang_thang(TienGoc, LaiSuat, SoThang) AS TienTraHangThang, " +
            "(NgayBatDau + (SoThang * INTERVAL '1 month')) AS NgayDaoHan " +
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

    public static void updateCompletedStatus() {
        // *** SỬA LỖI 1: Sửa cú pháp chuỗi SQL ***
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
        // *** SỬA LỖI 2: Gọi đúng constructor 9 tham số ***
        return new TraGop(
                rs.getInt("MaPhieuTG"),
                rs.getInt("MaHDX"),
                rs.getInt("SoThang"),
                rs.getBigDecimal("LaiSuat"),
                rs.getBigDecimal("TienGoc"),
                rs.getDate("NgayBatDau").toLocalDate(),
                rs.getBoolean("DaThanhToan"),
                rs.getBigDecimal("TienTraHangThang"), // Đọc cột tính toán
                rs.getDate("NgayDaoHan").toLocalDate()    // Đọc cột tính toán
        );
    }
    public static BigDecimal tinhTraGopHangThang(BigDecimal tienGoc, BigDecimal laiSuatNam, int soThang) {
        String sql = "SELECT tinh_tra_gop_hang_thang(?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, tienGoc);
            pstmt.setBigDecimal(2, laiSuatNam);
            pstmt.setInt(3, soThang);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1); // Lấy kết quả từ cột đầu tiên
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi gọi hàm tinh_tra_gop_hang_thang", e);
        }
        return null; // Trả về null nếu có lỗi
    }
}