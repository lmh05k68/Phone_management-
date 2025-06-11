package view.admin;

import javax.swing.*;
import java.awt.*;
import controller.common.AuthManager;

public class AdminView extends JFrame {
    private static final long serialVersionUID = 1L;

    // --- Style Constants (đồng bộ với EmployeeView) ---
    private final Color FUNCTION_BUTTON_COLOR = new Color(0, 123, 255); // Xanh dương cho các chức năng chính
    private final Color DANGER_ACTION_COLOR = new Color(220, 53, 69);   // Đỏ cho các hành động nguy hiểm (Đăng xuất)
    
    // --- Components ---
    private JButton btnThongKe;
    private JButton btnQuanLyNhanVien;
    private JButton btnQuanLySanPham;
    private JButton btnQuanLyNhaCungCap;
    private JButton btnQuanLyTraGop;
    private JButton btnQuanLyKpi;
    private JButton btnLogout;

    public AdminView() {
        setTitle("Trang Quản Trị Viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        pack();
        // Đặt kích thước tối thiểu để đảm bảo giao diện không bị quá nhỏ
        setMinimumSize(new Dimension(450, 600));
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // --- Header ---
        JLabel lblTitle = new JLabel("BẢNG ĐIỀU KHIỂN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        mainPanel.add(lblTitle);

        // --- Button Panel (sử dụng BoxLayout giống EmployeeView) ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false); // Nền trong suốt để lấy nền của mainPanel

        // Khởi tạo các nút với màu sắc đã đồng bộ
        btnThongKe = createStyledButton("Thống Kê Kinh Doanh", FUNCTION_BUTTON_COLOR);
        btnQuanLyNhanVien = createStyledButton("Quản Lý Nhân Viên", FUNCTION_BUTTON_COLOR);
        btnQuanLySanPham = createStyledButton("Quản Lý Sản Phẩm", FUNCTION_BUTTON_COLOR);
        btnQuanLyNhaCungCap = createStyledButton("Quản Lý Nhà Cung Cấp", FUNCTION_BUTTON_COLOR);
        btnQuanLyTraGop = createStyledButton("Quản Lý Trả Góp", FUNCTION_BUTTON_COLOR);
        btnQuanLyKpi = createStyledButton("Quản Lý KPI Nhân Viên", FUNCTION_BUTTON_COLOR);

        // Thêm các nút vào panel với khoảng cách nhất quán
        buttonPanel.add(btnThongKe);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(btnQuanLyNhanVien);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(btnQuanLySanPham);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(btnQuanLyNhaCungCap);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(btnQuanLyTraGop);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(btnQuanLyKpi);

        mainPanel.add(buttonPanel);

        // Đẩy nút Logout xuống dưới cùng
        mainPanel.add(Box.createVerticalGlue()); 
        mainPanel.add(Box.createVerticalStrut(25));

        // --- Logout Button ---
        btnLogout = createStyledButton("Đăng Xuất", DANGER_ACTION_COLOR);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(btnLogout);

        setContentPane(mainPanel);
        addActions();
    }

    private void addActions() {
        btnThongKe.addActionListener(e -> showPanelInNewFrame(new ThongKeView(), "Thống Kê Kinh Doanh"));
        btnQuanLyNhanVien.addActionListener(e -> showPanelInNewFrame(new ManageEmployeeView(), "Quản Lý Nhân Viên"));
        btnQuanLySanPham.addActionListener(e -> showPanelInNewFrame(new ManageProductView(), "Quản Lý Sản Phẩm"));
        btnQuanLyNhaCungCap.addActionListener(e -> showPanelInNewFrame(new ManageSupplierView(), "Quản Lý Nhà Cung Cấp"));
        btnQuanLyTraGop.addActionListener(e -> showPanelInNewFrame(new QuanLyTraGopView(), "Quản Lý Phiếu Trả Góp"));
        btnQuanLyKpi.addActionListener(e -> showPanelInNewFrame(new KpiManagementView(), "Quản Lý KPI"));
        
        btnLogout.addActionListener(e -> AuthManager.logout(this));
    }
    private void showPanelInNewFrame(JPanel panel, String title) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(panel);
            frame.pack();
            if (frame.getWidth() < 800 || frame.getHeight() < 600) {
                 frame.setSize(1100, 700);
            }
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
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
            BorderFactory.createEmptyBorder(12, 30, 12, 30) // Tăng padding
        ));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height + 10));
        btn.setMinimumSize(new Dimension(300, btn.getPreferredSize().height));
        return btn;
    }
}