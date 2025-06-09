package controller.common;

import dbConnection.DBConnection;
import model.KhachHang;
import model.NhanVien;
import model.TaiKhoan;
import query.KhachHangQuery;
import query.NhanVienQuery;
import query.TaiKhoanQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class RegisterController {
    public boolean handleRegister(String username, String password, String role,
                                  String hoTen, String sdt,
                                  LocalDate ngaySinh, double luong) {
        System.out.println("REGISTER_CONTROLLER: Bắt đầu handleRegister. Username: " + username + ", Role: " + role);

        username = username.trim();
        role = role.trim().toLowerCase();
        hoTen = (hoTen != null) ? hoTen.trim() : "";
        sdt = (sdt != null) ? sdt.trim() : "";

        if (TaiKhoanQuery.exists(username)) { 
            System.err.println("REGISTER_CONTROLLER: Tên đăng nhập '" + username + "' đã tồn tại.");
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                System.err.println("REGISTER_CONTROLLER: Không thể kết nối CSDL.");
                return false;
            }
            conn.setAutoCommit(false); 
            Integer maDoiTuongGenerated = null; 
            if ("khachhang".equals(role)) {
                KhachHang kh = new KhachHang(hoTen, sdt); 
                maDoiTuongGenerated = KhachHangQuery.insertKhachHangAndGetId(kh, conn);
                if (maDoiTuongGenerated == null || maDoiTuongGenerated <= 0) { 
                    System.err.println("REGISTER_CONTROLLER: Lỗi khi chèn KhachHang hoặc không lấy được ID. Rollback.");
                    conn.rollback();
                    return false;
                }
            } else if ("nhanvien".equals(role)) {
                NhanVien nv = new NhanVien(hoTen, ngaySinh, luong, sdt); 
                maDoiTuongGenerated = NhanVienQuery.insertNhanVienAndGetId(nv, conn); 
                 if (maDoiTuongGenerated == null || maDoiTuongGenerated <= 0) { // Kiểm tra ID hợp lệ
                    System.err.println("REGISTER_CONTROLLER: Lỗi khi chèn NhanVien hoặc không lấy được ID. Rollback.");
                    conn.rollback();
                    return false;
                }
            } else if ("admin".equals(role)) {
                // maDoiTuongGenerated sẽ là null, đã khởi tạo ở trên
                System.out.println("REGISTER_CONTROLLER: Đăng ký cho admin, không cần tạo đối tượng cá nhân.");
            } else {
                System.err.println("REGISTER_CONTROLLER: Vai trò không hợp lệ: " + role);
                // Không cần rollback vì chưa làm gì DB
                return false;
            }

            // Tạo TaiKhoan với maDoiTuongGenerated (có thể là null cho admin)
            TaiKhoan tk = new TaiKhoan(username, password, role, maDoiTuongGenerated);
            if (TaiKhoanQuery.insertTaiKhoanInTransaction(tk, conn)) { // Giả sử hàm này là static hoặc bạn có instance
                conn.commit(); // Commit transaction thành công
                System.out.println("REGISTER_CONTROLLER: Đăng ký thành công toàn bộ cho username: " + username + " với MaDoiTuong: " + maDoiTuongGenerated);
                return true;
            } else {
                System.err.println("REGISTER_CONTROLLER: Lỗi khi chèn tài khoản. Rollback.");
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            System.err.println("REGISTER_CONTROLLER: SQLException: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("REGISTER_CONTROLLER: Đang rollback do SQLException.");
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("REGISTER_CONTROLLER: Lỗi khi rollback: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Khôi phục autoCommit
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("REGISTER_CONTROLLER: Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}