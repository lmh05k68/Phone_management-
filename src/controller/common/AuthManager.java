package controller.common;

import view.common.LoginView;
import javax.swing.*;

public class AuthManager {
    public static void logout(JFrame currentFrame) { // Sửa thành static
        System.out.println("AUTH_MANAGER: Yêu cầu đăng xuất từ frame: " + currentFrame.getTitle());
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            new LoginController(loginView); // Khởi tạo và gắn controller cho LoginView mới
            loginView.setVisible(true);
            System.out.println("AUTH_MANAGER: Đã mở lại LoginView.");
        });
    }
}