package query;

import dbConnection.DBConnection;
import model.TraGop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TraGopQuery {
    public static Integer insertPhieuTraGopAndGetId(TraGop tg) {
        String sql = "INSERT INTO PhieuTraGop (MaHDX, SoThang, LaiSuat, TienGoc, NgayBatDau, DaThanhToan) VALUES (?, ?, ?, ?, ?, ?)";
        ResultSet generatedKeys = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, tg.getMaHDX());         // MaHDX là int
            stmt.setInt(2, tg.getSoThang());
            stmt.setDouble(3, tg.getLaiSuat());
            stmt.setDouble(4, tg.getTienGoc());
            stmt.setDate(5, Date.valueOf(tg.getNgayBatDau())); // Chuyển LocalDate sang java.sql.Date
            stmt.setBoolean(6, tg.isDaThanhToan());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("TraGopQuery (insertAndGetId): Chèn phiếu trả góp thất bại, không có hàng nào được thêm.");
                return null; // Hoặc ném SQLException tùy theo cách xử lý lỗi mong muốn
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1); // Lấy MaPhieuTG tự sinh
                System.out.println("TraGopQuery (insertAndGetId): Phiếu trả góp được chèn với MaPhieuTG: " + id);
                return id;
            } else {
                System.err.println("TraGopQuery (insertAndGetId): Chèn phiếu trả góp thành công nhưng không lấy được ID tự sinh.");
                return null; // Hoặc ném SQLException
            }
        } catch (SQLException e) {
            System.err.println("TraGopQuery (insertAndGetId): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
            return null; // Hoặc ném SQLException
        } finally {
            if (generatedKeys != null) {
                try {
                    generatedKeys.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Lấy tất cả các phiếu trả góp từ cơ sở dữ liệu.
     * @return Danh sách các đối tượng TraGop.
     */
    public static List<TraGop> getAllPhieuTraGop() {
        List<TraGop> list = new ArrayList<>();
        String sql = "SELECT MaPhieuTG, MaHDX, SoThang, LaiSuat, TienGoc, NgayBatDau, DaThanhToan FROM PhieuTraGop ORDER BY NgayBatDau DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql); // Sử dụng PreparedStatement cho nhất quán
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TraGop p = new TraGop(
                        rs.getInt("MaPhieuTG"),      // MaPhieuTG là int
                        rs.getInt("MaHDX"),          // MaHDX là int
                        rs.getInt("SoThang"),
                        rs.getDouble("LaiSuat"),
                        rs.getDouble("TienGoc"),
                        rs.getDate("NgayBatDau").toLocalDate(), // Chuyển java.sql.Date sang LocalDate
                        rs.getBoolean("DaThanhToan")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("TraGopQuery (getAllPhieuTraGop): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Cập nhật trạng thái DaThanhToan của các phiếu trả góp đã đến hạn thanh toán toàn bộ.
     * (Ví dụ: Ngày bắt đầu + Số tháng đã qua ngày hiện tại).
     * Phương thức này nên được gọi định kỳ hoặc bởi một tiến trình nền.
     */
    public static void updateTrangThaiTraGopDaHoanThanh() {
        // Cú pháp cộng tháng vào ngày trong PostgreSQL là `date_column + interval '1 month' * number_of_months`
        // Hoặc an toàn hơn là dùng hàm `AGE` để so sánh
        // Ví dụ: WHERE AGE(CURRENT_DATE, NgayBatDau) >= make_interval(months := SoThang)
        // Hoặc đơn giản hơn (nhưng có thể không chính xác tuyệt đối với ngày cuối tháng):
        // WHERE NgayBatDau + (SoThang * INTERVAL '1 month') <= CURRENT_DATE
        // Ví dụ dưới đây dùng cách cộng interval, cần kiểm tra kỹ trên PostgreSQL
        String sql = "UPDATE PhieuTraGop SET DaThanhToan = TRUE " +
                     "WHERE DaThanhToan = FALSE AND (NgayBatDau + MAKE_INTERVAL(months => SoThang)) <= CURRENT_DATE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int updatedRows = stmt.executeUpdate();
            System.out.println("TraGopQuery (updateTrangThaiDaHoanThanh): Đã cập nhật trạng thái cho " + updatedRows + " phiếu trả góp đã hoàn thành.");
        } catch (SQLException e) {
            System.err.println("TraGopQuery (updateTrangThaiDaHoanThanh): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lấy danh sách phiếu trả góp theo Mã Khách Hàng.
     * Điều này yêu cầu join với bảng HoaDonXuat để lấy MaKH.
     * @param maKH Mã khách hàng.
     * @return Danh sách phiếu trả góp của khách hàng đó.
     */
    public static List<TraGop> getPhieuTraGopByMaKH(int maKH) {
        List<TraGop> list = new ArrayList<>();
        String sql = "SELECT tg.MaPhieuTG, tg.MaHDX, tg.SoThang, tg.LaiSuat, tg.TienGoc, tg.NgayBatDau, tg.DaThanhToan " +
                     "FROM PhieuTraGop tg " +
                     "JOIN HoaDonXuat hdx ON tg.MaHDX = hdx.MaHDX " +
                     "WHERE hdx.MaKH = ? " +
                     "ORDER BY tg.NgayBatDau DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TraGop p = new TraGop(
                            rs.getInt("MaPhieuTG"),
                            rs.getInt("MaHDX"),
                            rs.getInt("SoThang"),
                            rs.getDouble("LaiSuat"),
                            rs.getDouble("TienGoc"),
                            rs.getDate("NgayBatDau").toLocalDate(),
                            rs.getBoolean("DaThanhToan")
                    );
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("TraGopQuery (getPhieuTraGopByMaKH): Lỗi SQL cho MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy thông tin một phiếu trả góp theo Mã Phiếu.
     * @param maPhieuTG Mã phiếu trả góp.
     * @return Đối tượng TraGop hoặc null nếu không tìm thấy.
     */
    public static TraGop getPhieuTraGopById(int maPhieuTG) {
        String sql = "SELECT MaPhieuTG, MaHDX, SoThang, LaiSuat, TienGoc, NgayBatDau, DaThanhToan " +
                     "FROM PhieuTraGop WHERE MaPhieuTG = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maPhieuTG);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TraGop(
                            rs.getInt("MaPhieuTG"),
                            rs.getInt("MaHDX"),
                            rs.getInt("SoThang"),
                            rs.getDouble("LaiSuat"),
                            rs.getDouble("TienGoc"),
                            rs.getDate("NgayBatDau").toLocalDate(),
                            rs.getBoolean("DaThanhToan")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("TraGopQuery (getPhieuTraGopById): Lỗi SQL cho MaPhieuTG " + maPhieuTG + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}