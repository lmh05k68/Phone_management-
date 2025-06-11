package controller.common;

import view.admin.AdminView;
import view.customer.CustomerView;
import view.employee.EmployeeView;
import view.common.LoginView;
import view.common.RegisterView;
import view.common.ChangePasswordView;
import query.TaiKhoanQuery;
import model.TaiKhoan;
import javax.swing.*;

public class LoginController {
    private final LoginView loginView;

    public LoginController(LoginView view) {
        this.loginView = view;
        this.loginView.getLoginButton().addActionListener(e -> handleLogin());
        this.loginView.getRegisterButton().addActionListener(e -> new RegisterView().setVisible(true));
        this.loginView.getChangePasswordButton().addActionListener(e -> new ChangePasswordView().setVisible(true));
        
        loadRememberedCredentials();
    }

    private void handleLogin() {
        String username = loginView.getUsernameField().getText().trim();
        String password = new String(loginView.getPasswordField().getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginView, "Vui lòng nhập tên đăng nhập và mật khẩu.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- LUỒNG ĐĂNG NHẬP MỚI, HIỆU QUẢ HƠN ---

        // Bước 1: Lấy toàn bộ thông tin tài khoản bằng một query duy nhất.
        TaiKhoan userAccount = TaiKhoanQuery.getTaiKhoanByUsername(username);

        // Bước 2: Xác thực
        if (userAccount == null || !password.equals(userAccount.getMatKhau())) {
            // Cảnh báo bảo mật: KHÔNG NÊN so sánh plain text. Nên dùng BCrypt.checkpw(password, userAccount.getMatKhauHash())
            JOptionPane.showMessageDialog(loginView, "Đăng nhập thất bại! Sai tên đăng nhập hoặc mật khẩu.", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Đăng nhập thành công ---
        JOptionPane.showMessageDialog(loginView, "Đăng nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);

        // Xử lý Ghi nhớ mật khẩu
        if (loginView.getRememberCheckBox().isSelected()) {
            // Cảnh báo bảo mật: Lưu mật khẩu plain text là cực kỳ nguy hiểm.
            RememberPassword.saveCredentials(username, password);
        } else {
            RememberPassword.clearCredentials();
        }

        loginView.dispose();

        // Bước 3: Điều hướng dựa trên vai trò đã có sẵn trong đối tượng userAccount
        switch (userAccount.getVaiTro()) {
            case ADMIN:
                new AdminView().setVisible(true);
                break;

            case NHAN_VIEN:
                Integer maNV = userAccount.getMaNV();
                if (maNV != null) {
                    new EmployeeView(maNV).setVisible(true);
                } else {
                    showDataError("Không tìm thấy thông tin nhân viên liên kết với tài khoản này.");
                }
                break;

            case KHACH_HANG:
                Integer maKH = userAccount.getMaKH();
                if (maKH != null) {
                    new CustomerView(maKH).setVisible(true);
                } else {
                    showDataError("Không tìm thấy thông tin khách hàng liên kết với tài khoản này.");
                }
                break;

            default:
                showDataError("Vai trò không hợp lệ.");
                break;
        }
    }

    private void loadRememberedCredentials() {
        // ... code của bạn đã tốt
    }
    
    private void showDataError(String message) {
        JOptionPane.showMessageDialog(null, "Lỗi dữ liệu: " + message, "Lỗi", JOptionPane.ERROR_MESSAGE);
        new LoginView().setVisible(true); // Mở lại cửa sổ đăng nhập
    }
}