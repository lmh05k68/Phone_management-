package controller.common;

import dbConnection.DBConnection;
import model.KhachHang;
import model.NhanVien;
import model.TaiKhoan;
import query.KhachHangQuery;
import query.NhanVienQuery;
import query.TaiKhoanQuery;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterController {
    private static final Logger logger = Logger.getLogger(RegisterController.class.getName());

    public String handleRegister(String username, String plainTextPassword, TaiKhoan.VaiTro vaiTro,
                                 String hoTen, String sdt, LocalDate ngaySinh, BigDecimal luong) {
        
        username = username.trim();
        if (username.isEmpty() || plainTextPassword.isEmpty()) {
            return "Tên đăng nhập và mật khẩu không được để trống.";
        }
        if (TaiKhoanQuery.exists(username)) {
            return "Tên đăng nhập '" + username + "' đã tồn tại.";
        }
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // SỬA: Gọi đúng constructor của TaiKhoan
            TaiKhoan tk = new TaiKhoan(username, plainTextPassword, vaiTro);

            switch (vaiTro) {
                case KHACH_HANG:
                    // SỬA: Gọi đúng constructor của KhachHang (không cần truyền điểm tích lũy)
                    KhachHang kh = new KhachHang(hoTen, sdt);
                    Integer maKH = KhachHangQuery.insertKhachHangAndGetId(kh, conn);
                    if (maKH == null) throw new SQLException("Không thể tạo khách hàng mới.");
                    tk.setMaKH(maKH);
                    break;
                case NHAN_VIEN:
                    // Constructor của NhanVien đã đúng
                    NhanVien nv = new NhanVien(hoTen, ngaySinh, luong, sdt);
                    Integer maNV = NhanVienQuery.insertNhanVienAndGetId(nv, conn);
                    if (maNV == null) throw new SQLException("Không thể tạo nhân viên mới.");
                    tk.setMaNV(maNV);
                    break;
                case ADMIN:
                    // Không cần làm gì
                    break;
            }
            
            boolean success = TaiKhoanQuery.insertTaiKhoanInTransaction(tk, conn);
            if (!success) {
                throw new SQLException("Không thể tạo tài khoản.");
            }

            conn.commit();
            logger.info("Đăng ký thành công cho username: " + username);
            return null; // Thành công

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi SQL khi đăng ký, đang rollback...", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Lỗi nghiêm trọng khi rollback.", ex);
                }
            }
            if (e.getMessage() != null && e.getMessage().contains("sdtkh_unique")) {
                return "Số điện thoại của khách hàng này đã được đăng ký.";
            }
            if (e.getMessage() != null && e.getMessage().contains("sodienthoai_unique")) {
                return "Số điện thoại của nhân viên này đã được đăng ký.";
            }
            return "Đã xảy ra lỗi hệ thống. Vui lòng thử lại.";
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Lỗi khi đóng kết nối.", e);
                }
            }
        }
    }
}