package view.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton changePasswordButton;
    private JCheckBox rememberCheckBox;

    public LoginView() {
        System.out.println("LOGIN_VIEW: Constructor LoginView bắt đầu.");
        setTitle("Đăng Nhập Hệ Thống"); // Sửa lại title cho đúng
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        usernameField = new JTextField(20); // Số cột ban đầu, GridBagLayout sẽ điều chỉnh
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Đăng nhập");
        registerButton = new JButton("Đăng ký");
        changePasswordButton = new JButton("Đổi mật khẩu");
        rememberCheckBox = new JCheckBox("Nhớ tài khoản");

        initUI(); // Gọi initUI sau khi đã khởi tạo các thành phần

        pack(); // Tự động điều chỉnh kích thước frame dựa trên nội dung
        setMinimumSize(new Dimension(420, getHeight())); // Đảm bảo chiều rộng tối thiểu
        System.out.println("LOGIN_VIEW: Constructor LoginView kết thúc.");
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 0, 30, 0));
        mainPanel.add(lblTitle);

        // Panel chứa các trường input, sử dụng GridBagLayout để các field bằng nhau
        JPanel inputFieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcInput = new GridBagConstraints();
        gbcInput.insets = new Insets(5, 0, 5, 0); // Khoảng cách giữa các hàng
        gbcInput.fill = GridBagConstraints.HORIZONTAL;
        gbcInput.weightx = 1.0; // Cho phép các trường text mở rộng

        // Tên đăng nhập
        gbcInput.gridx = 0; gbcInput.gridy = 0; gbcInput.weightx = 0.2; // Label chiếm ít không gian hơn
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        styleLabel(lblUsername);
        inputFieldsPanel.add(lblUsername, gbcInput);

        gbcInput.gridx = 1; gbcInput.gridy = 0; gbcInput.weightx = 0.8; // Field chiếm nhiều không gian hơn
        styleTextField(usernameField);
        inputFieldsPanel.add(usernameField, gbcInput);

        // Mật khẩu
        gbcInput.gridx = 0; gbcInput.gridy = 1; gbcInput.weightx = 0.2;
        JLabel lblPassword = new JLabel("Mật khẩu:");
        styleLabel(lblPassword);
        inputFieldsPanel.add(lblPassword, gbcInput);

        gbcInput.gridx = 1; gbcInput.gridy = 1; gbcInput.weightx = 0.8;
        styleTextField(passwordField);
        inputFieldsPanel.add(passwordField, gbcInput);

        mainPanel.add(inputFieldsPanel);
        mainPanel.add(Box.createVerticalStrut(10)); // Giảm khoảng cách một chút

        // Remember checkbox
        JPanel rememberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Căn trái checkbox
        rememberPanel.setOpaque(false);
        rememberCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rememberCheckBox.setOpaque(false);
        rememberCheckBox.setFocusPainted(false);
        rememberPanel.add(rememberCheckBox);
        mainPanel.add(rememberPanel);
        mainPanel.add(Box.createVerticalStrut(20)); // Giảm khoảng cách

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        styleGenericButton(loginButton, new Color(0, 123, 255));
        styleGenericButton(registerButton, new Color(40, 167, 69));
        styleGenericButton(changePasswordButton, new Color(108, 117, 125));

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(changePasswordButton);

        mainPanel.add(buttonPanel);
        setContentPane(mainPanel);
    }

    // Phương thức style cho JLabel
    private void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    }

    // Phương thức style cho JTextField và JPasswordField
    private void styleTextField(JComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            new EmptyBorder(5, 8, 5, 8) // Padding bên trong
        ));
        // Đảm bảo chiều cao nhất quán
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 30));
    }


    private void styleGenericButton(JButton btn, Color backgroundColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE); // Chữ trắng luôn nổi bật trên các màu nền này
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker().darker(), 1), // Viền đậm hơn
                new EmptyBorder(10, 25, 10, 25) // Giảm padding dọc một chút
        ));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Để các nút có chiều rộng bằng nhau và chiếm toàn bộ panel chứa chúng
        btn.setMinimumSize(new Dimension(250, 45)); // Chiều cao tối thiểu
        btn.setPreferredSize(new Dimension(280, 45)); // Chiều cao ưu tiên
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 45)); // Chiều cao tối đa, chiều rộng tối đa
    }

    // Getters
    public JTextField getUsernameField() { return usernameField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getRegisterButton() { return registerButton; }
    public JButton getChangePasswordButton() { return changePasswordButton; }
    public JCheckBox getRememberCheckBox() { return rememberCheckBox; }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
             System.err.println("Không thể áp dụng Nimbus Look and Feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            LoginView view = new LoginView();
            // controller.common.LoginController controller = new controller.common.LoginController(view); // Ví dụ
            view.setVisible(true);
        });
    }
}