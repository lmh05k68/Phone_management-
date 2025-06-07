package controller.common;

import query.TaiKhoanQuery;

import javax.swing.*;

public class ChangePasswordController {
    private JTextField usernameField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JFrame parent;

    public ChangePasswordController(JTextField usernameField,
                                    JPasswordField newPasswordField,
                                    JPasswordField confirmPasswordField,
                                    JFrame parent) {
        this.usernameField = usernameField;
        this.newPasswordField = newPasswordField;
        this.confirmPasswordField = confirmPasswordField;
        this.parent = parent;
    }

    public void handleChangePassword() {
        String username = usernameField.getText().trim();
        String newPass = new String(newPasswordField.getPassword()).trim();
        String confirmPass = new String(confirmPasswordField.getPassword()).trim();

        if (username.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(parent, "Mật khẩu xác nhận không khớp.");
            return;
        }
        if (!TaiKhoanQuery.exists(username)) {
            JOptionPane.showMessageDialog(parent, "Tài khoản không tồn tại.");
            return;
        }
        boolean updated = TaiKhoanQuery.updatePassword(username, newPass);
        if (updated) {
            JOptionPane.showMessageDialog(parent, "Cập nhật mật khẩu thành công!");
            parent.dispose();
        } else {
            JOptionPane.showMessageDialog(parent, "Cập nhật thất bại. Vui lòng thử lại.");
        }
    }
}