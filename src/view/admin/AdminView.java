package view.admin;

import javax.swing.*;
import java.awt.*;
import controller.common.AuthManager;  
public class AdminView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JButton btnLogout; // << THÊM NÚT ĐĂNG XUẤT

    public AdminView() {
        System.out.println("ADMIN_VIEW: Constructor AdminView bắt đầu.");
        setTitle("Trang quản trị");
        setSize(450, 450); // Tăng chiều cao một chút cho nút đăng xuất
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        System.out.println("ADMIN_VIEW: Constructor AdminView kết thúc.");
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTitle = new JLabel("Chào mừng Admin!");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(lblTitle);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(0, 1, 0, 12));
        btnPanel.setOpaque(false);

        JButton btnThongKe = createStyledButton("Thống kê doanh thu", new Color(59, 89, 182));
        JButton btnQuanLyNhanVien = createStyledButton("Quản lý nhân viên", new Color(59, 89, 182));
        JButton btnQuanLySanPham = createStyledButton("Quản lý sản phẩm", new Color(59, 89, 182));
        JButton btnQuanLyTraGop = createStyledButton("Quản lý trả góp", new Color(59, 89, 182));

        btnPanel.add(btnThongKe);
        btnPanel.add(btnQuanLyNhanVien);
        btnPanel.add(btnQuanLySanPham);
        btnPanel.add(btnQuanLyTraGop);
        mainPanel.add(btnPanel);

        // THÊM NÚT ĐĂNG XUẤT
        mainPanel.add(Box.createVerticalStrut(15)); // Khoảng cách
        btnLogout = createStyledButton("Đăng xuất", new Color(220, 53, 69)); // Màu đỏ cho đăng xuất
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(btnLogout);

        // Action Listeners
        btnThongKe.addActionListener(e -> new ThongKeView().setVisible(true));
        btnQuanLyNhanVien.addActionListener(e -> new ManageEmployeeView().setVisible(true));
        btnQuanLySanPham.addActionListener(e -> new ManageProductView().setVisible(true));
        btnQuanLyTraGop.addActionListener(e -> {
            System.out.println("ADMIN_VIEW: Mở Quản lý trả góp.");
            new QuanLyTraGopView().setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            System.out.println("ADMIN_VIEW: Nút 'Đăng xuất' được nhấn.");
            AuthManager.logout(this); // Gọi phương thức đăng xuất
        });

        this.add(mainPanel);
    }

    // Sửa createStyledButton để nhận màu nền
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Đảm bảo nút chiếm toàn bộ chiều rộng trong GridLayout
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminView().setVisible(true));
    }
}