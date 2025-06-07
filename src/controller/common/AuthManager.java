package controller.common;

import view.common.LoginView; // Import LoginView
import javax.swing.*;

public class AuthManager {

    /**
     * Xử lý đăng xuất: đóng cửa sổ hiện tại và mở lại LoginView.
     * @param currentFrame JFrame hiện tại cần đóng.
     */
    public static void logout(JFrame currentFrame) {
        System.out.println("AUTH_MANAGER: Yêu cầu đăng xuất từ frame: " + currentFrame.getTitle());
        if (currentFrame != null) {
            currentFrame.dispose(); // Đóng cửa sổ hiện tại
        }
        // Mở lại LoginView
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            new LoginController(loginView); // Gắn lại controller cho LoginView mới
            loginView.setVisible(true);
            System.out.println("AUTH_MANAGER: Đã mở lại LoginView.");
        });
    }
}