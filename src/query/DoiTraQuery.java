package query;

import dbConnection.DBConnection; // Đảm bảo import đúng
import model.DoiTra;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// import java.text.SimpleDateFormat; // Không cần ở đây nữa nếu format ở controller
import java.util.ArrayList;
import java.util.List;

public class DoiTraQuery {

    // Giả sử bảng hóa đơn là 'hoadonxuat' và cột mã hóa đơn là 'mahdx'
    private static boolean kiemTraMaDonHangTonTai(String maDonHang) {
        String sql = "SELECT 1 FROM hoadonxuat WHERE mahdx = ?"; // SỬA TÊN BẢNG VÀ CỘT NẾU CẦN
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maDonHang);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra mã đơn hàng '" + maDonHang + "': " + e.getMessage());
            // e.printStackTrace(); // Bỏ comment để debug
            return false;
        }
    }

    public static boolean themYeuCauDoiTra(DoiTra dt) {
        if (dt == null || dt.getIdDT() == null || dt.getIdDT().trim().isEmpty()) {
            System.err.println("Lỗi thêm yêu cầu đổi trả: Đối tượng DoiTra hoặc idDT không hợp lệ (null hoặc rỗng).");
            return false;
        }
        if (!kiemTraMaDonHangTonTai(dt.getMaDonHang())) {
            System.err.println("Từ chối thêm yêu cầu đổi trả: Mã đơn hàng '" + dt.getMaDonHang() + "' không tồn tại.");
            return false;
        }

        // Câu SQL phải bao gồm idDT và các cột khác với tên CSDL (chữ thường)
        String sql = "INSERT INTO doitra (iddt, makh, masp, madonhang, ngaydoitra, lydo, trangthai) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dt.getIdDT());
            stmt.setString(2, dt.getMaKH());
            stmt.setString(3, dt.getMaSP());
            stmt.setString(4, dt.getMaDonHang());
            stmt.setDate(5, new java.sql.Date(dt.getNgayDoiTra().getTime()));
            stmt.setString(6, dt.getLyDo());
            stmt.setString(7, dt.getTrangThai()); // Gán trạng thái (ví dụ: "Chờ xử lý")

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm yêu cầu đổi/trả. idDT: '" + dt.getIdDT() + "'. Lỗi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<DoiTra> getAllDoiTra() {
        List<DoiTra> list = new ArrayList<>();
        // Sử dụng tên cột chữ thường từ CSDL
        String sql = "SELECT iddt, makh, masp, madonhang, ngaydoitra, lydo, trangthai FROM doitra ORDER BY ngaydoitra DESC, iddt DESC";

        System.out.println("DEBUG QUERY: Đang thực thi SQL: " + sql); // Dòng debug

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("DEBUG QUERY: Đã thực thi SQL thành công."); // Dòng debug

            int count = 0;
            while (rs.next()) {
                count++;
                DoiTra dt = new DoiTra(
                        rs.getString("iddt"),       // Chữ thường
                        rs.getString("makh"),       // Chữ thường
                        rs.getString("masp"),       // Chữ thường
                        rs.getString("madonhang"),  // Chữ thường
                        rs.getDate("ngaydoitra"),   // Chữ thường (kiểu java.sql.Date)
                        rs.getString("lydo"),       // Chữ thường
                        rs.getString("trangthai")   // Chữ thường
                );
                list.add(dt);
                 System.out.println("DEBUG QUERY: Đã thêm DoiTra ID: " + dt.getIdDT()); // Dòng debug
            }
            System.out.println("DEBUG QUERY: Đã lấy được " + count + " bản ghi từ CSDL."); // Dòng debug

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách đổi trả: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không mong muốn khi lấy danh sách đổi trả: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static boolean capNhatTrangThaiDoiTra(String idDT, String trangThaiMoi) {
        // Sử dụng tên cột chữ thường
        String sql = "UPDATE doitra SET trangthai = ? WHERE iddt = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThaiMoi);
            stmt.setString(2, idDT);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật trạng thái đổi trả cho ID " + idDT + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}