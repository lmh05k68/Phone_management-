package view.employee;

import javax.swing.*;
import java.awt.*;
import controller.common.AuthManager;
import controller.employee.ReturnRequestListController;
public class EmployeeView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int maNV;
    private JButton btnLogout;
    private JButton btnCapNhatThongTinNV;
    private JButton btnBanHang, btnXemYeuCauDoiTra, btnNhapHang;
    private JButton btnQuanLyKhachHang, btnQuanLyBaoHanh;
    private final Color FUNCTION_BUTTON_COLOR = new Color(0, 123, 255); 
    private final Color DANGER_ACTION_COLOR = new Color(220, 53, 69); // Đỏ cho Đăng xuất

    public EmployeeView(int maNV) {
        this.maNV = maNV;
        setTitle("Giao Diện Nhân Viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        pack();
        // Điều chỉnh kích thước tối thiểu dựa trên số lượng nút
        setMinimumSize(new Dimension(getWidth() > 400 ? getWidth() : 400, 600)); // Tăng chiều cao nếu có nhiều nút
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JLabel lblTitle = new JLabel("Chào mừng Nhân viên (Mã: " + maNV + ")");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        mainPanel.add(lblTitle);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);

        // Khởi tạo các nút với màu FUNCTION_BUTTON_COLOR
        btnCapNhatThongTinNV = createStyledButton("Cập nhật thông tin cá nhân", FUNCTION_BUTTON_COLOR);
        btnBanHang = createStyledButton("Bán hàng", FUNCTION_BUTTON_COLOR);
        btnXemYeuCauDoiTra = createStyledButton("Xem Yêu cầu đổi/trả", FUNCTION_BUTTON_COLOR);
        btnNhapHang = createStyledButton("Nhập hàng", FUNCTION_BUTTON_COLOR);
        btnQuanLyKhachHang = createStyledButton("Quản lý khách hàng", FUNCTION_BUTTON_COLOR);
        btnQuanLyBaoHanh = createStyledButton("Quản lý bảo hành", FUNCTION_BUTTON_COLOR);


        // Thêm các nút vào panel
        btnPanel.add(btnCapNhatThongTinNV);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnBanHang);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnNhapHang);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnXemYeuCauDoiTra);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnQuanLyKhachHang); // Bạn cần quyết định nhân viên có quyền này không
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnQuanLyBaoHanh);   // Bạn cần quyết định nhân viên có quyền này không

        mainPanel.add(btnPanel);

        mainPanel.add(Box.createVerticalGlue());

        btnLogout = createStyledButton("Đăng xuất", DANGER_ACTION_COLOR); // Nút Đăng xuất vẫn màu đỏ
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(btnLogout);

        setContentPane(mainPanel);

        // Action Listeners
        btnCapNhatThongTinNV.addActionListener(e -> {
            new UpdateEmployeeProfileView(this.maNV).setVisible(true);
        });
        btnBanHang.addActionListener(e -> {
            System.out.println("EMPLOYEE_VIEW: Nút 'Bán hàng' được nhấn.");
            new SellProductView(maNV).setVisible(true);
        });
        btnNhapHang.addActionListener(e -> {
            System.out.println("EMPLOYEE_VIEW: Nút 'Nhập hàng' được nhấn.");
            new ImportProductView(maNV).setVisible(true);
        });
        btnXemYeuCauDoiTra.addActionListener(e -> {
            System.out.println("EMPLOYEE_VIEW: Nút 'Xem Yêu cầu đổi/trả' được nhấn.");
            ReturnRequestListView listView = new ReturnRequestListView();
            new ReturnRequestListController(listView);
            listView.setVisible(true);
        });
        btnQuanLyKhachHang.addActionListener(e -> {
            System.out.println("EMPLOYEE_VIEW: Nút 'Quản lý khách hàng' được nhấn.");
            new ManageCustomerView().setVisible(true);
        });
        btnQuanLyBaoHanh.addActionListener(e -> {
            System.out.println("EMPLOYEE_VIEW: Nút 'Quản lý bảo hành' được nhấn.");
            new WarrantyManagementView().setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            System.out.println("EMPLOYEE_VIEW: Nút 'Đăng xuất' được nhấn.");
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
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height + 10));
        btn.setMinimumSize(new Dimension(300, btn.getPreferredSize().height)); // Tăng chiều rộng tối thiểu
        return btn;
    }
}