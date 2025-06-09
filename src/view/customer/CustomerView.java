package view.customer;

import javax.swing.*;
import java.awt.*;
import controller.common.AuthManager; 
public class CustomerView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int maKH; // Đã là int
    private JButton btnLogout;

    public CustomerView(int maKH) { // Constructor đã nhận int
        this.maKH = maKH;
        System.out.println("CUSTOMER_VIEW: Constructor CustomerView cho MaKH: " + maKH);
        setTitle("Giao Diện Khách Hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        pack();
        setMinimumSize(new Dimension(getWidth(), 450));
        System.out.println("CUSTOMER_VIEW: Constructor CustomerView kết thúc.");
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel lblTitle = new JLabel("Chào Mừng Khách Hàng (Mã: " + maKH + ")");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        mainPanel.add(lblTitle);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JButton btnLichSu = createStyledButton("Xem lịch sử mua hàng", new Color(0, 123, 255));
        JButton btnDoiTra = createStyledButton("Yêu cầu đổi/trả sản phẩm", new Color(0, 123, 255));
        JButton btnBaoHanh = createStyledButton("Yêu cầu bảo hành sản phẩm", new Color(0, 123, 255));
        JButton btnDoiDiem = createStyledButton("Đổi điểm lấy ưu đãi", new Color(0, 123, 255));
        JButton btnTraGop = createStyledButton("Đăng ký trả góp", new Color(0, 123, 255));

        gbc.gridy = 0; buttonPanel.add(btnLichSu, gbc);
        gbc.gridy = 1; buttonPanel.add(btnDoiTra, gbc);
        gbc.gridy = 2; buttonPanel.add(btnBaoHanh, gbc);
        gbc.gridy = 3; buttonPanel.add(btnDoiDiem, gbc);
        gbc.gridy = 4; buttonPanel.add(btnTraGop, gbc);
        mainPanel.add(buttonPanel);

        mainPanel.add(Box.createVerticalStrut(25));
        btnLogout = createStyledButton("Đăng xuất", new Color(220, 53, 69));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(btnLogout);

        setContentPane(mainPanel);

        // Action Listeners
        // Đảm bảo các View con cũng được cập nhật để nhận maKH kiểu int nếu cần
        btnLichSu.addActionListener(e -> new PurchaseHistoryView(maKH).setVisible(true)); // Truyền int maKH

        // Giả sử các View khác cũng đã được cập nhật constructor
        btnDoiTra.addActionListener(e -> {
            // Giả sử ReturnProductView và ReturnProductController đã được cập nhật để nhận int maKH
            ReturnProductView returnView = new ReturnProductView(maKH);
            new controller.customer.ReturnProductController(returnView, maKH); // Truyền int maKH vào controller
            returnView.setVisible(true);
        });
        btnBaoHanh.addActionListener(e -> new WarrantyRequestView(maKH).setVisible(true)); // Truyền int maKH
        btnDoiDiem.addActionListener(e -> new RedeemPointsView(maKH).setVisible(true));   // Truyền int maKH
        btnTraGop.addActionListener(e -> new TraGopKHView(maKH).setVisible(true));     // Truyền int maKH

        btnLogout.addActionListener(e -> {
            System.out.println("CUSTOMER_VIEW: Nút 'Đăng xuất' được nhấn.");
            AuthManager.logout(this);
        });
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        return btn;
    }
}