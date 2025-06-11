package view.customer;

import controller.common.AuthManager;
import controller.customer.ReturnProductController;
import model.KhachHang;
import query.KhachHangQuery;
import javax.swing.*;
import java.awt.*;

public class CustomerView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int maKH;
    private JLabel lblTitle;
    private JButton btnLogout;
    private JButton btnCapNhatThongTin;
    private final Color CUSTOMER_FUNCTION_COLOR = new Color(0, 123, 255);
    private final Color DANGER_ACTION_COLOR = new Color(220, 53, 69);

    public CustomerView(int maKH) {
        this.maKH = maKH;
        setTitle("Giao Diện Khách Hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        loadCustomerInfo(); // Tải thông tin KH ngay sau khi khởi tạo UI

        pack();
        setMinimumSize(new Dimension(450, 550)); // Giảm chiều cao một chút cho cân đối
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // Tiêu đề
        lblTitle = new JLabel("Chào mừng...", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        mainPanel.add(lblTitle);

        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        // Khởi tạo các nút
        btnCapNhatThongTin = createStyledButton("Cập nhật thông tin cá nhân", CUSTOMER_FUNCTION_COLOR);
        JButton btnLichSu = createStyledButton("Xem lịch sử mua hàng", CUSTOMER_FUNCTION_COLOR);
        JButton btnDoiTra = createStyledButton("Yêu cầu đổi/trả sản phẩm", CUSTOMER_FUNCTION_COLOR);
        JButton btnBaoHanh = createStyledButton("Yêu cầu bảo hành sản phẩm", CUSTOMER_FUNCTION_COLOR);
        JButton btnDoiDiem = createStyledButton("Đổi điểm lấy ưu đãi", CUSTOMER_FUNCTION_COLOR);
        JButton btnTraGop = createStyledButton("Đăng ký trả góp", CUSTOMER_FUNCTION_COLOR);

        // Thêm nút vào panel với khoảng cách
        buttonPanel.add(btnCapNhatThongTin);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(btnLichSu);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(btnDoiTra);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(btnBaoHanh);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(btnDoiDiem);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(btnTraGop);
        
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue()); // Đẩy nút Đăng xuất xuống dưới

        btnLogout = createStyledButton("Đăng xuất", DANGER_ACTION_COLOR);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(btnLogout);

        setContentPane(mainPanel);

        // --- Gán sự kiện cho các nút ---
        addActions(btnLichSu, btnDoiTra, btnBaoHanh, btnDoiDiem, btnTraGop);
    }
    
    // Tách riêng phần gán sự kiện cho gọn
    private void addActions(JButton btnLichSu, JButton btnDoiTra, JButton btnBaoHanh, JButton btnDoiDiem, JButton btnTraGop) {
        btnCapNhatThongTin.addActionListener(e -> {
            UpdateCustomerProfileView updateView = new UpdateCustomerProfileView(this.maKH, this::loadCustomerInfo);
            updateView.setVisible(true);
        });

        btnLichSu.addActionListener(e -> new PurchaseHistoryView(maKH).setVisible(true));
        
        btnDoiTra.addActionListener(e -> {
            ReturnProductView returnView = new ReturnProductView(maKH);
            // SỬA LỖI: Chỉ truyền vào đối tượng view.
            // Controller có thể tự lấy maKH từ view khi cần.
            new ReturnProductController(returnView); 
            returnView.setVisible(true);
        });
        
        btnBaoHanh.addActionListener(e -> new WarrantyRequestView(maKH).setVisible(true));
        btnDoiDiem.addActionListener(e -> new RedeemPointsView(maKH).setVisible(true));
        btnTraGop.addActionListener(e -> new TraGopKHView(maKH).setVisible(true));
        btnLogout.addActionListener(e -> AuthManager.logout(this));
    }
    
    private void loadCustomerInfo() {
        KhachHang kh = KhachHangQuery.getKhachHangById(this.maKH);
        if (kh != null) {
            lblTitle.setText("Chào mừng, " + kh.getHoTen());
        } else {
            lblTitle.setText("Chào mừng Khách hàng (Mã: " + maKH + ")");
            JOptionPane.showMessageDialog(this, "Không thể tải thông tin khách hàng.", "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
        return btn;
    }
}