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
import java.time.format.DateTimeParseException;

public class RegisterController {

    private final TaiKhoanQuery taiKhoanQueryInstance = new TaiKhoanQuery();
    private final KhachHangQuery khachHangQueryInstance = new KhachHangQuery();
    private final NhanVienQuery nhanVienQueryInstance = new NhanVienQuery();

    public boolean handleRegister(String username, String password, String role,
                                  String maDoiTuong, String hoTen, String sdt, // sdt đã là tên đúng
                                  String ngaySinhStr, String luongStr) {

        System.out.println("REGISTER_CONTROLLER: Bắt đầu handleRegister. Username: " + username + ", Role: " + role + ", MaDoiTuong: " + maDoiTuong);

        username = username.trim();
        role = role.trim().toLowerCase();
        maDoiTuong = (maDoiTuong != null) ? maDoiTuong.trim() : "";
        hoTen = (hoTen != null) ? hoTen.trim() : "";
        sdt = (sdt != null) ? sdt.trim() : ""; // Giữ nguyên sdt
        ngaySinhStr = (ngaySinhStr != null) ? ngaySinhStr.trim() : "";
        luongStr = (luongStr != null) ? luongStr.trim() : "";

        if (TaiKhoanQuery.exists(username)) {
            System.err.println("REGISTER_CONTROLLER: Tên đăng nhập '" + username + "' đã tồn tại.");
            return false;
        }

        if (("khachhang".equals(role) || "nhanvien".equals(role))) {
            if (maDoiTuong.isEmpty()) {
                System.err.println("REGISTER_CONTROLLER: Mã đối tượng không được để trống cho vai trò " + role);
                return false;
            }
            if ("khachhang".equals(role) && KhachHangQuery.exists(maDoiTuong)) {
                System.err.println("REGISTER_CONTROLLER: Mã khách hàng '" + maDoiTuong + "' đã tồn tại.");
                return false;
            }
            // Sử dụng phương thức non-static để kiểm tra trong NhanVienQuery nếu cần,
            // hoặc giữ static exists nếu nó không cần tham gia transaction nào khác
            if ("nhanvien".equals(role) && NhanVienQuery.exists(maDoiTuong)) {
                System.err.println("REGISTER_CONTROLLER: Mã nhân viên '" + maDoiTuong + "' đã tồn tại.");
                return false;
            }
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                System.err.println("REGISTER_CONTROLLER: Không thể kết nối CSDL.");
                return false;
            }
            conn.setAutoCommit(false);

            boolean doiTuongOperationSuccess = false;

            if ("khachhang".equals(role)) {
                KhachHang kh = new KhachHang(maDoiTuong, hoTen, sdt, 0);
                doiTuongOperationSuccess = khachHangQueryInstance.insertKhachHangInTransaction(kh, conn);
            } else if ("nhanvien".equals(role)) {
                double luong;
                try {
                    LocalDate.parse(ngaySinhStr); // Chỉ kiểm tra định dạng
                    luong = Double.parseDouble(luongStr);
                } catch (DateTimeParseException e) {
                    System.err.println("REGISTER_CONTROLLER: Ngày sinh không đúng định dạng YYYY-MM-DD: " + ngaySinhStr);
                    conn.rollback();
                    return false;
                } catch (NumberFormatException e) {
                    System.err.println("REGISTER_CONTROLLER: Lương không phải là số hợp lệ: " + luongStr);
                    conn.rollback();
                    return false;
                }
                // Tạo NhanVien với ngaySinhStr (String) và sdt (String)
                NhanVien nv = new NhanVien(maDoiTuong, hoTen, ngaySinhStr, luong, sdt); // sdt đã đúng
                doiTuongOperationSuccess = nhanVienQueryInstance.insertNhanVienInTransaction(nv, conn);
            } else if ("admin".equals(role)) {
                doiTuongOperationSuccess = true;
                maDoiTuong = null;
            }

            if (!doiTuongOperationSuccess && !"admin".equals(role)) {
                System.err.println("REGISTER_CONTROLLER: Lỗi khi chèn thông tin đối tượng cho " + role + ". Rollback.");
                conn.rollback();
                return false;
            }

            TaiKhoan tk = new TaiKhoan(username, password, role, maDoiTuong);
            if (taiKhoanQueryInstance.insertTaiKhoanInTransaction(tk, conn)) {
                conn.commit();
                System.out.println("REGISTER_CONTROLLER: Đăng ký thành công toàn bộ cho username: " + username);
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
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}