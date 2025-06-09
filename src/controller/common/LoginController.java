package controller.common;

import view.admin.AdminView;
import view.customer.CustomerView;
import view.employee.EmployeeView;
import view.common.LoginView;
import view.common.RegisterView;
import view.common.ChangePasswordView;
import query.TaiKhoanQuery; 
import javax.swing.*;

public class LoginController {
    private LoginView loginView;

    public LoginController(LoginView view) {
        this.loginView = view;
        view.getLoginButton().addActionListener(e -> handleLogin()); 
        view.getRegisterButton().addActionListener(e -> {
            System.out.println("LOGIN_CONTROLLER: Nút 'Đăng ký' được nhấn.");
            RegisterView registerView = new RegisterView();
            registerView.setVisible(true);
        });
        view.getChangePasswordButton().addActionListener(e -> {
            System.out.println("LOGIN_CONTROLLER: Nút 'Đổi mật khẩu' được nhấn.");
            new ChangePasswordView().setVisible(true); 
        });

        loadRememberedCredentials();
    }

    private void loadRememberedCredentials() {
        String[] credentials = RememberPassword.loadCredentials();
        if (credentials != null && credentials.length == 2) {
            loginView.getUsernameField().setText(credentials[0]); 
            loginView.getPasswordField().setText(credentials[1]); 
            loginView.getRememberCheckBox().setSelected(true);     
            System.out.println("LOGIN_CONTROLLER: Đã tải username và password đã nhớ.");
        } else {
             System.out.println("LOGIN_CONTROLLER: Không có username/password nào được nhớ.");
        }
    }

    private void handleLogin() {
        System.out.println("LOGIN_CONTROLLER: Bắt đầu xử lý đăng nhập.");
        String username = loginView.getUsernameField().getText().trim(); // Sử dụng getter
        String password = new String(loginView.getPasswordField().getPassword()).trim(); // Sử dụng getter

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginView, "Vui lòng nhập tên đăng nhập và mật khẩu.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Bước 1: Xác thực
        // Nhắc nhở: Trong thực tế, password nên được băm trước khi so sánh.
        // TaiKhoanQuery.verifyLogin nên nhận password đã băm (nếu bạn băm ở client)
        // hoặc nhận password thô và tự băm để so sánh với CSDL.
        boolean loginSuccess = TaiKhoanQuery.verifyLogin(username, password);

        if (loginSuccess) {
            // Bước 2: Lấy vai trò SAU KHI xác thực thành công
            String role = TaiKhoanQuery.getRole(username);
            System.out.println("LOGIN_CONTROLLER: Đăng nhập thành công cho user: " + username + ", role lấy từ DB: '" + role + "'");

            if (role == null || role.trim().isEmpty()) {
                System.err.println("LOGIN_CONTROLLER: Lỗi nghiêm trọng - Vai trò là null hoặc rỗng sau khi đăng nhập thành công cho user: " + username);
                JOptionPane.showMessageDialog(loginView, "Lỗi hệ thống: Không thể xác định vai trò người dùng.", "Lỗi vai trò", JOptionPane.ERROR_MESSAGE);
                return; 
            }

            // Hiển thị thông báo đăng nhập thành công một lần
            JOptionPane.showMessageDialog(loginView, "Đăng nhập thành công! Vai trò: " + role.trim(), "Đăng nhập thành công", JOptionPane.INFORMATION_MESSAGE);

            if (loginView.getRememberCheckBox().isSelected()) { // Sử dụng getter
                RememberPassword.saveCredentials(username, password);
            } else {
                RememberPassword.clearCredentials();
            }

            loginView.dispose(); // Đóng cửa sổ đăng nhập

            String roleLower = role.trim().toLowerCase(); 
            System.out.println("LOGIN_CONTROLLER: Chuẩn hóa roleLower: '" + roleLower + "'");

            switch (roleLower) {
                case "admin":
                    System.out.println("LOGIN_CONTROLLER: Mở AdminView.");
                    new AdminView().setVisible(true);
                    break;

                case "nhanvien":
                    System.out.println("LOGIN_CONTROLLER: Xử lý vai trò nhân viên.");
                    Integer maNVInteger = TaiKhoanQuery.getMaDoiTuong(username); 
                    System.out.println("LOGIN_CONTROLLER: MaNV (Integer) lấy từ DB cho user " + username + ": " + maNVInteger);

                    if (maNVInteger != null && maNVInteger > 0) { 
                        System.out.println("LOGIN_CONTROLLER: Mở EmployeeView cho MaNV: " + maNVInteger);
                        new EmployeeView(maNVInteger).setVisible(true); 
                    } else {
                        System.err.println("LOGIN_CONTROLLER: Không tìm thấy mã nhân viên hợp lệ cho user: " + username + " (MaNV là null hoặc không hợp lệ).");
                        JOptionPane.showMessageDialog(null, "Lỗi: Không tìm thấy thông tin nhân viên liên kết với tài khoản này.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                        new LoginView().setVisible(true); 
                    }
                    break; 

                case "khachhang":
                    System.out.println("LOGIN_CONTROLLER: Xử lý vai trò khách hàng.");
                    Integer maKHInteger = TaiKhoanQuery.getMaKHByTenDangNhap(username); 
                    System.out.println("LOGIN_CONTROLLER: MaKH (Integer) lấy từ DB cho user " + username + ": " + maKHInteger);

                    if (maKHInteger != null && maKHInteger > 0) { 
                        System.out.println("LOGIN_CONTROLLER: Mở CustomerView cho MaKH: " + maKHInteger);
                        new CustomerView(maKHInteger).setVisible(true);
                    } else {
                        System.err.println("LOGIN_CONTROLLER: Không tìm thấy mã khách hàng hợp lệ cho user: " + username + " (MaKH là null hoặc không hợp lệ).");
                        JOptionPane.showMessageDialog(null, "Lỗi: Không tìm thấy thông tin khách hàng liên kết với tài khoản này.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                        new LoginView().setVisible(true); 
                    }
                    break;

                default:
                    System.err.println("LOGIN_CONTROLLER: Vai trò không hợp lệ sau khi chuẩn hóa: '" + roleLower + "' cho user: " + username);
                    JOptionPane.showMessageDialog(null, "Vai trò '" + roleLower + "' không được hỗ trợ trong hệ thống.", "Lỗi vai trò", JOptionPane.ERROR_MESSAGE);
                    new LoginView().setVisible(true);
                    break;
            }

        } else {
            System.out.println("LOGIN_CONTROLLER: Đăng nhập thất bại cho user: " + username);
            JOptionPane.showMessageDialog(loginView, "Đăng nhập thất bại! Sai tên đăng nhập hoặc mật khẩu.", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}