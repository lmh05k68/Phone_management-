package controller.common;

import view.admin.AdminView;
import view.customer.CustomerView; // Đảm bảo import
import view.employee.EmployeeView; // Đảm bảo import
import view.common.LoginView;
import view.common.RegisterView; // Nếu cần cho các action khác
import view.common.ChangePasswordView; // Nếu cần
import query.TaiKhoanQuery; // Đảm bảo import

import javax.swing.*;

public class LoginController {
    private LoginView loginView;

    public LoginController(LoginView view) {
        this.loginView = view;

        view.loginButton.addActionListener(e -> handleLogin());
        view.registerButton.addActionListener(e -> {
            System.out.println("LOGIN_CONTROLLER: Nút 'Đăng ký' được nhấn.");
            RegisterView registerView = new RegisterView();
            registerView.setVisible(true);
        });
        view.changePasswordButton.addActionListener(e -> {
            System.out.println("LOGIN_CONTROLLER: Nút 'Đổi mật khẩu' được nhấn.");
            new ChangePasswordView().setVisible(true);
        });

        loadRememberedCredentials();
    }

    private void loadRememberedCredentials() {
        String[] credentials = RememberPassword.loadCredentials(); // Giả sử bạn có hàm này
        if (credentials != null && credentials.length == 2) {
            loginView.usernameField.setText(credentials[0]);
            loginView.passwordField.setText(credentials[1]);
            loginView.rememberCheckBox.setSelected(true);
            System.out.println("LOGIN_CONTROLLER: Đã tải username và password đã nhớ.");
        } else {
             System.out.println("LOGIN_CONTROLLER: Không có username/password nào được nhớ.");
        }
    }

    private void handleLogin() {
        System.out.println("LOGIN_CONTROLLER: Bắt đầu xử lý đăng nhập.");
        String username = loginView.usernameField.getText().trim();
        String password = new String(loginView.passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginView, "Vui lòng nhập tên đăng nhập và mật khẩu.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Bước 1: Xác thực
        boolean success = TaiKhoanQuery.verifyLogin(username, password);

        if (success) {
            // Bước 2: Lấy vai trò SAU KHI xác thực thành công
            String role = TaiKhoanQuery.getRole(username);
            System.out.println("LOGIN_CONTROLLER: Đăng nhập thành công cho user: " + username + ", role lấy từ DB: '" + role + "'");


            if (role == null || role.trim().isEmpty()) {
                System.err.println("LOGIN_CONTROLLER: Lỗi nghiêm trọng - Vai trò là null hoặc rỗng sau khi đăng nhập thành công cho user: " + username);
                JOptionPane.showMessageDialog(loginView, "Lỗi hệ thống: Không thể xác định vai trò người dùng.", "Lỗi vai trò", JOptionPane.ERROR_MESSAGE);
                return; // Không nên tiếp tục
            }

            // Hiển thị thông báo đăng nhập thành công một lần
            JOptionPane.showMessageDialog(loginView, "Đăng nhập thành công! Vai trò: " + role.trim(), "Đăng nhập thành công", JOptionPane.INFORMATION_MESSAGE);

            if (loginView.rememberCheckBox.isSelected()) {
                RememberPassword.saveCredentials(username, password);
            } else {
                RememberPassword.clearCredentials();
            }

            loginView.dispose();

            String roleLower = role.trim().toLowerCase(); // Chuẩn hóa vai trò
            System.out.println("LOGIN_CONTROLLER: Chuẩn hóa roleLower: '" + roleLower + "'");


            switch (roleLower) {
                case "admin":
                    System.out.println("LOGIN_CONTROLLER: Mở AdminView.");
                    new AdminView().setVisible(true);
                    break; // << QUAN TRỌNG

                case "nhanvien":
                    System.out.println("LOGIN_CONTROLLER: Xử lý vai trò nhân viên.");
                    String maNV = TaiKhoanQuery.getMaDoiTuong(username);
                    System.out.println("LOGIN_CONTROLLER: MaNV lấy từ DB cho user " + username + ": '" + maNV + "'");
                    if (maNV != null && !maNV.trim().isEmpty()) {
                        System.out.println("LOGIN_CONTROLLER: Mở EmployeeView cho MaNV: " + maNV);
                        new EmployeeView(maNV).setVisible(true);
                    } else {
                        System.err.println("LOGIN_CONTROLLER: Không tìm thấy mã nhân viên cho user: " + username + " (MaNV là null hoặc rỗng).");
                        JOptionPane.showMessageDialog(null, "Lỗi: Không tìm thấy thông tin nhân viên liên kết với tài khoản này.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                        // Quyết định hành động tiếp theo: mở lại LoginView hoặc thoát
                        new LoginView().setVisible(true);
                    }
                    break; // << QUAN TRỌNG

                case "khachhang":
                    System.out.println("LOGIN_CONTROLLER: Xử lý vai trò khách hàng.");
                    String maKH = TaiKhoanQuery.getMaKHByTenDangNhap(username); // Hoặc dùng getMaDoiTuong nếu logic giống nhau
                     System.out.println("LOGIN_CONTROLLER: MaKH lấy từ DB cho user " + username + ": '" + maKH + "'");
                    if (maKH != null && !maKH.trim().isEmpty()) {
                        System.out.println("LOGIN_CONTROLLER: Mở CustomerView cho MaKH: " + maKH);
                        new CustomerView(maKH).setVisible(true);
                    } else {
                        System.err.println("LOGIN_CONTROLLER: Không tìm thấy mã khách hàng cho user: " + username + " (MaKH là null hoặc rỗng).");
                        JOptionPane.showMessageDialog(null, "Lỗi: Không tìm thấy thông tin khách hàng liên kết với tài khoản này.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                        new LoginView().setVisible(true);
                    }
                    break; // << QUAN TRỌNG

                default:
                    // Dòng log bạn thấy xuất hiện ở đây
                    System.err.println("LOGIN_CONTROLLER: Vai trò không hợp lệ sau khi chuẩn hóa: '" + roleLower + "' cho user: " + username);
                    JOptionPane.showMessageDialog(null, "Vai trò '" + roleLower + "' không được hỗ trợ trong hệ thống.", "Lỗi vai trò", JOptionPane.ERROR_MESSAGE);
                    new LoginView().setVisible(true);
                    break; // << QUAN TRỌNG
            }

        } else {
            System.out.println("LOGIN_CONTROLLER: Đăng nhập thất bại cho user: " + username);
            JOptionPane.showMessageDialog(loginView, "Đăng nhập thất bại! Sai tên đăng nhập hoặc mật khẩu.", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}