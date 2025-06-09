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

    // Sửa: Nhận int maDonHang
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
     * @param dt Đối tượng DoiTra (không cần idDT ban đầu).
     * @return idDT (Integer) tự sinh nếu thành công, hoặc null nếu thất bại.
     */
    public static Integer themYeuCauDoiTraAndGetId(DoiTra dt) {
        if (dt == null) {
            System.err.println("Lỗi thêm yêu cầu đổi trả: Đối tượng DoiTra là null.");
            return null;
        }
        // Kiểm tra MaDonHang (int) có tồn tại không
        if (!kiemTraMaDonHangTonTai(dt.getMaDonHang())) {
            System.err.println("Từ chối thêm yêu cầu đổi trả: Mã đơn hàng '" + dt.getMaDonHang() + "' không tồn tại.");
            // Có thể throw new SQLException("Mã đơn hàng không tồn tại") để Controller bắt và hiển thị lỗi cụ thể hơn
            return null;
        }

        // iddt là SERIAL, không cần truyền vào
        String sql = "INSERT INTO doitra (makh, masp, madonhang, ngaydoitra, lydo, trangthai) VALUES (?, ?, ?, ?, ?, ?)";
        ResultSet generatedKeys = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, dt.getMaKH());         // makh là int
            stmt.setInt(2, dt.getMaSP());         // masp là int
            stmt.setInt(3, dt.getMaDonHang());    // madonhang là int
            stmt.setDate(4, Date.valueOf(dt.getNgayDoiTra())); // Chuyển LocalDate sang java.sql.Date
            stmt.setString(5, dt.getLyDo());
            stmt.setString(6, dt.getTrangThai()); // Trạng thái mặc định đã được gán trong model

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("DoiTraQuery (themAndGetId): Chèn yêu cầu đổi trả thất bại.");
                return null;
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Trả về idDT tự sinh
            } else {
                System.err.println("DoiTraQuery (themAndGetId): Chèn thành công nhưng không lấy được ID.");
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm yêu cầu đổi/trả. Lỗi: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
             if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    public static List<DoiTra> getAllDoiTra() {
        List<DoiTra> list = new ArrayList<>();
        String sql = "SELECT iddt, makh, masp, madonhang, ngaydoitra, lydo, trangthai FROM doitra ORDER BY ngaydoitra DESC, iddt DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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
            System.err.println("Lỗi khi lấy danh sách đổi trả: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static boolean capNhatTrangThaiDoiTra(int idDT, String trangThaiMoi) { // idDT là int
        String sql = "UPDATE doitra SET trangthai = ? WHERE iddt = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThaiMoi);
            stmt.setInt(2, idDT); // Sử dụng setInt
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật trạng thái đổi trả cho ID " + idDT + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}