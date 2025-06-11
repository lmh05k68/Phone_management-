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
        setTitle("Đăng Nhập Hệ Thống");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Khởi tạo các thành phần
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Đăng nhập");
        registerButton = new JButton("Đăng ký");
        changePasswordButton = new JButton("Đổi mật khẩu");
        rememberCheckBox = new JCheckBox("Nhớ tài khoản");

        // Xây dựng giao diện
        initUI();

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
        gbcInput.gridx = 0; gbcInput.gridy = 0; gbcInput.weightx = 0.3; // Label chiếm ít không gian hơn
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        styleLabel(lblUsername);
        inputFieldsPanel.add(lblUsername, gbcInput);

        gbcInput.gridx = 1; gbcInput.gridy = 0; gbcInput.weightx = 0.7; // Field chiếm nhiều không gian hơn
        styleTextField(usernameField);
        inputFieldsPanel.add(usernameField, gbcInput);

        // Mật khẩu
        gbcInput.gridx = 0; gbcInput.gridy = 1; gbcInput.weightx = 0.3;
        JLabel lblPassword = new JLabel("Mật khẩu:");
        styleLabel(lblPassword);
        inputFieldsPanel.add(lblPassword, gbcInput);

        gbcInput.gridx = 1; gbcInput.gridy = 1; gbcInput.weightx = 0.7;
        styleTextField(passwordField);
        inputFieldsPanel.add(passwordField, gbcInput);

        mainPanel.add(inputFieldsPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Remember checkbox
        JPanel rememberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rememberPanel.setOpaque(false);
        rememberCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rememberCheckBox.setOpaque(false);
        rememberCheckBox.setFocusPainted(false);
        rememberPanel.add(rememberCheckBox);
        mainPanel.add(rememberPanel);
        mainPanel.add(Box.createVerticalStrut(20));

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
        // Đảm bảo chiều cao nhất quán và đủ lớn cho các ký tự
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35)); // SỬA LỖI: Tăng chiều cao từ 30 lên 35
    }

    private void styleGenericButton(JButton btn, Color backgroundColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker().darker(), 1),
                new EmptyBorder(10, 25, 10, 25)
        ));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMinimumSize(new Dimension(250, 45));
        btn.setPreferredSize(new Dimension(280, 45));
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 45));
    }

    // Getters
    public JTextField getUsernameField() { return usernameField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getRegisterButton() { return registerButton; }
    public JButton getChangePasswordButton() { return changePasswordButton; }
    public JCheckBox getRememberCheckBox() { return rememberCheckBox; }
}