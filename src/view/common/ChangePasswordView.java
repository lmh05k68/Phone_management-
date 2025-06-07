package view.common;

import controller.common.ChangePasswordController;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordView extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField usernameField = new JTextField(20);
    private JPasswordField newPasswordField = new JPasswordField(20);
    private JPasswordField confirmPasswordField = new JPasswordField(20);
    private JButton updateButton = new JButton("Cập nhật");

    public ChangePasswordView() {
        setTitle("Đổi mật khẩu");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTitle = new JLabel("Đổi mật khẩu");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(lblTitle);

        mainPanel.add(createLabeledField("Tên đăng nhập:", usernameField));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createLabeledField("Mật khẩu mới:", newPasswordField));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createLabeledField("Xác nhận mật khẩu:", confirmPasswordField));
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createStyledButton(updateButton));

        setContentPane(mainPanel);

        ChangePasswordController controller = new ChangePasswordController(
                usernameField, newPasswordField, confirmPasswordField, this
        );
        updateButton.addActionListener(e -> controller.handleChangePassword());
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(140, 25));
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createStyledButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(59, 89, 182));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 50, 100), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }
}