package view.common;

// import controller.common.RememberPassword; // Không cần import trực tiếp ở đây nữa, Controller sẽ xử lý

import javax.swing.*;
import javax.swing.border.EmptyBorder; // Thêm import
import java.awt.*;

public class LoginView extends JFrame {
    private static final long serialVersionUID = 1L;

    // Các thành phần được khai báo là public để Controller có thể truy cập
    // Nếu bạn muốn tuân thủ đóng gói chặt chẽ hơn, hãy tạo các getter.
    public JTextField usernameField = new JTextField(20);
    public JPasswordField passwordField = new JPasswordField(20);
    public JButton loginButton = new JButton("Đăng nhập");
    public JButton registerButton = new JButton("Đăng ký");
    public JButton changePasswordButton = new JButton("Đổi mật khẩu");
    public JCheckBox rememberCheckBox = new JCheckBox("Nhớ tài khoản"); // Sửa text cho nhất quán

    public LoginView() {
        System.out.println("LOGIN_VIEW: Constructor LoginView bắt đầu.");
        setTitle("Đăng nhập hệ thống");
        setSize(450, 450); // Điều chỉnh kích thước nếu cần
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(25, 35, 25, 35)); // Padding

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        mainPanel.add(lblTitle);

        // Input fields
        mainPanel.add(createStyledFieldPanel("Tên đăng nhập:", usernameField));
        mainPanel.add(Box.createVerticalStrut(12)); // Khoảng cách
        mainPanel.add(createStyledFieldPanel("Mật khẩu:", passwordField));
        mainPanel.add(Box.createVerticalStrut(12)); // Khoảng cách

        // Remember checkbox
        JPanel rememberPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rememberPanel.setOpaque(false);
        rememberCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Style cho checkbox
        rememberPanel.add(rememberCheckBox);
        mainPanel.add(rememberPanel);
        mainPanel.add(Box.createVerticalStrut(20)); // Khoảng cách

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 0, 10)); // 0 hàng, 1 cột, khoảng cách dọc 10
        buttonPanel.setOpaque(false); // Để nền của mainPanel hiển thị

        // Style các nút trước khi thêm vào panel
        styleGenericButton(loginButton);
        styleGenericButton(registerButton);
        styleGenericButton(changePasswordButton);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(changePasswordButton);
        mainPanel.add(buttonPanel);

        setContentPane(mainPanel);
        System.out.println("LOGIN_VIEW: Constructor LoginView kết thúc. Việc tải credentials sẽ do Controller thực hiện.");
    }

    // Phương thức tạo panel cho label và field với style
    private JPanel createStyledFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(8, 5)); // Khoảng cách giữa label và field
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(110, 28)); // Đặt kích thước label để các trường thẳng hàng
        panel.add(label, BorderLayout.WEST);

        // Thiết lập font cho các trường nhập liệu
        if (field instanceof JTextField || field instanceof JPasswordField) {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
             field.setBorder(BorderFactory.createCompoundBorder(
                field.getBorder(), // Giữ viền gốc
                BorderFactory.createEmptyBorder(3, 5, 3, 5))); // Thêm padding bên trong
        }
        panel.add(field, BorderLayout.CENTER);
        // Giới hạn chiều cao tối đa của panel
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, field.getPreferredSize().height + 10));
        return panel;
    }

    // Phương thức style chung cho các nút
    private void styleGenericButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 123, 255)); // Màu nền xanh dương (Bootstrap primary)
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179), 1), // Viền tối hơn
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Tăng padding
        ));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT); // Cần thiết nếu panel cha dùng BoxLayout theo trục Y
        // Đặt kích thước ưa thích để các nút có chiều rộng full và chiều cao đồng nhất
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 45)); // Tăng chiều cao nút
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 45)); // Cho phép nút mở rộng full chiều ngang
    }

    // Main method để test LoginView (nếu cần controller, phải khởi tạo LoginController)
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
            LoginView loginView = new LoginView();
            // Để test đầy đủ chức năng "Nhớ mật khẩu", bạn cần khởi tạo LoginController ở đây
            // controller.common.LoginController loginController = new controller.common.LoginController(loginView);
            loginView.setVisible(true);
        });
    }
}