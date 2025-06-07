package view.customer;

import javax.swing.*;
import java.awt.*;
import controller.common.AuthManager; // << THÊM IMPORT
import controller.customer.ReturnProductController;
public class CustomerView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final String maKH;
    private JButton btnLogout; // << THÊM NÚT ĐĂNG XUẤT

    public CustomerView(String maKH) {
        this.maKH = maKH;
        System.out.println("CUSTOMER_VIEW: Constructor CustomerView cho MaKH: " + maKH);
        setTitle("Giao Diện Khách Hàng");
        setSize(480, 530); // Tăng chiều cao cho nút đăng xuất
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        System.out.println("CUSTOMER_VIEW: Constructor CustomerView kết thúc.");
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTitle = new JLabel("Chào Mừng Khách Hàng Mã: " + maKH); // Hiển thị mã KH
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        mainPanel.add(lblTitle);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0); // Giảm khoảng cách một chút

        JButton btnLichSu = createStyledButton("Xem lịch sử mua hàng", new Color(33, 150, 243));
        JButton btnDoiTra = createStyledButton("Yêu cầu đổi/trả sản phẩm", new Color(33, 150, 243));
        JButton btnBaoHanh = createStyledButton("Yêu cầu bảo hành sản phẩm", new Color(33, 150, 243));
        JButton btnDoiDiem = createStyledButton("Đổi điểm lấy ưu đãi", new Color(33, 150, 243));
        JButton btnTraGop = createStyledButton("Đăng ký trả góp", new Color(33, 150, 243));

        gbc.gridy = 0; buttonPanel.add(btnLichSu, gbc);
        gbc.gridy = 1; buttonPanel.add(btnDoiTra, gbc);
        gbc.gridy = 2; buttonPanel.add(btnBaoHanh, gbc);
        gbc.gridy = 3; buttonPanel.add(btnDoiDiem, gbc);
        gbc.gridy = 4; buttonPanel.add(btnTraGop, gbc);
        mainPanel.add(buttonPanel);

        // THÊM NÚT ĐĂNG XUẤT
        mainPanel.add(Box.createVerticalStrut(20)); // Khoảng cách
        btnLogout = createStyledButton("Đăng xuất", new Color(220, 53, 69)); // Màu đỏ
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa nút đăng xuất
        mainPanel.add(btnLogout);


        setContentPane(mainPanel);

        // Action Listeners
        btnLichSu.addActionListener(e -> new PurchaseHistoryView(maKH).setVisible(true));
        btnDoiTra.addActionListener(e -> {
            ReturnProductView returnView = new ReturnProductView(maKH);
            new ReturnProductController(returnView);
            returnView.setVisible(true);
        });
        btnBaoHanh.addActionListener(e -> new WarrantyRequestView(maKH).setVisible(true));
        btnDoiDiem.addActionListener(e -> new RedeemPointsView(maKH).setVisible(true));
        btnTraGop.addActionListener(e -> new TraGopKHView(maKH).setVisible(true));

        btnLogout.addActionListener(e -> {
            System.out.println("CUSTOMER_VIEW: Nút 'Đăng xuất' được nhấn.");
            AuthManager.logout(this);
        });
    }

    // Sửa createStyledButton để nhận màu nền
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker()),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        // Căn giữa nút trong GridBagLayout (không cần thiết nếu fill = HORIZONTAL)
        // btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }
}