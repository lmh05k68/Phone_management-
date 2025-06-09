package view.customer;

import javax.swing.*;
import java.awt.*;
import controller.common.AuthManager;
public class CustomerView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int maKH;
    private JButton btnLogout;
    private JButton btnCapNhatThongTin;
    private final Color CUSTOMER_FUNCTION_COLOR = new Color(0, 123, 255); // Xanh dương cho tất cả các nút chức năng KH
    private final Color DANGER_ACTION_COLOR = new Color(220, 53, 69);   // Đỏ cho Đăng xuất

    public CustomerView(int maKH) {
        this.maKH = maKH;
        setTitle("Giao Diện Khách Hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        pack();
        setMinimumSize(new Dimension(getWidth() > 400 ? getWidth() : 400, 580)); // Tăng chiều cao nếu cần
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JLabel lblTitle = new JLabel("Chào Mừng Khách Hàng (Mã: " + maKH + ")");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24)); // Font lớn hơn
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0)); // Khoảng cách dưới title
        mainPanel.add(lblTitle);

        // Panel chứa các nút chức năng, sử dụng GridBagLayout để các nút có chiều rộng bằng nhau
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false); // Để nền của mainPanel hiển thị qua
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Tất cả các nút trong một cột
        gbc.weightx = 1.0; // Cho phép nút mở rộng theo chiều ngang
        gbc.fill = GridBagConstraints.HORIZONTAL; // Nút sẽ fill theo chiều ngang
        gbc.insets = new Insets(8, 0, 8, 0); // Khoảng cách dọc giữa các nút

        // Tạo các nút với màu CUSTOMER_FUNCTION_COLOR
        btnCapNhatThongTin = createStyledButton("Cập nhật thông tin cá nhân", CUSTOMER_FUNCTION_COLOR);
        JButton btnLichSu = createStyledButton("Xem lịch sử mua hàng", CUSTOMER_FUNCTION_COLOR);
        JButton btnDoiTra = createStyledButton("Yêu cầu đổi/trả sản phẩm", CUSTOMER_FUNCTION_COLOR);
        JButton btnBaoHanh = createStyledButton("Yêu cầu bảo hành sản phẩm", CUSTOMER_FUNCTION_COLOR);
        JButton btnDoiDiem = createStyledButton("Đổi điểm lấy ưu đãi", CUSTOMER_FUNCTION_COLOR);
        JButton btnTraGop = createStyledButton("Đăng ký trả góp", CUSTOMER_FUNCTION_COLOR);

        // Thêm các nút vào buttonPanel
        gbc.gridy = 0; buttonPanel.add(btnCapNhatThongTin, gbc);
        gbc.gridy = 1; buttonPanel.add(btnLichSu, gbc);
        gbc.gridy = 2; buttonPanel.add(btnDoiTra, gbc);
        gbc.gridy = 3; buttonPanel.add(btnBaoHanh, gbc);
        gbc.gridy = 4; buttonPanel.add(btnDoiDiem, gbc);
        gbc.gridy = 5; buttonPanel.add(btnTraGop, gbc);
        mainPanel.add(buttonPanel);

        mainPanel.add(Box.createVerticalGlue()); // Đẩy nút Đăng xuất xuống dưới nếu còn không gian

        btnLogout = createStyledButton("Đăng xuất", DANGER_ACTION_COLOR); // Nút Đăng xuất vẫn màu đỏ
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(25)); // Khoảng cách phía trên nút Đăng xuất
        mainPanel.add(btnLogout);

        setContentPane(mainPanel);

        // Action Listeners
        btnCapNhatThongTin.addActionListener(e -> {
            // Giả sử UpdateCustomerProfileView đã được tạo và nhận int maKH
            new UpdateCustomerProfileView(this.maKH).setVisible(true);
        });
        btnLichSu.addActionListener(e -> new PurchaseHistoryView(maKH).setVisible(true));
        btnDoiTra.addActionListener(e -> {
            ReturnProductView returnView = new ReturnProductView(maKH);
            // Giả sử ReturnProductController đã được cập nhật constructor
            new controller.customer.ReturnProductController(returnView, maKH);
            returnView.setVisible(true);
        });
        btnBaoHanh.addActionListener(e -> new WarrantyRequestView(maKH).setVisible(true));
        btnDoiDiem.addActionListener(e -> new RedeemPointsView(maKH).setVisible(true));
        btnTraGop.addActionListener(e -> new TraGopKHView(maKH).setVisible(true));

        btnLogout.addActionListener(e -> AuthManager.logout(this));
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker().darker(), 1), // Viền đậm hơn
                BorderFactory.createEmptyBorder(12, 30, 12, 30) // Padding
        ));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height + 10)); // Cho phép mở rộng tối đa
        btn.setMinimumSize(new Dimension(300, btn.getPreferredSize().height)); // Đặt chiều rộng tối thiểu
        return btn;
    }
}