package view.employee;

import javax.swing.*;
import java.awt.*;
import controller.common.AuthManager; // << THÊM IMPORT
import controller.employee.ReturnRequestListController;
public class EmployeeView extends JFrame {
    private static final long serialVersionUID = 1L;
    private String maNV;
    private JButton btnLogout; // << THÊM NÚT ĐĂNG XUẤT

    public EmployeeView(String maNV) {
        this.maNV = maNV;
        System.out.println("EMPLOYEE_VIEW: Constructor EmployeeView cho MaNV: " + maNV);
        setTitle("Giao diện nhân viên");
        setSize(450, 480); // Tăng chiều cao
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        System.out.println("EMPLOYEE_VIEW: Constructor EmployeeView kết thúc.");
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTitle = new JLabel("Chào mừng Nhân viên mã: " + maNV);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(lblTitle);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(0, 1, 0, 12));
        btnPanel.setOpaque(false);

        JButton btnBanHang = createStyledButton("Bán hàng", new Color(59, 89, 182));
        JButton btnXemYeuCauDoiTra = createStyledButton("Xem Yêu cầu đổi/trả", new Color(59, 89, 182));
        JButton btnNhapHang = createStyledButton("Nhập hàng", new Color(59, 89, 182));
        JButton btnQuanLyKH = createStyledButton("Quản lý khách hàng", new Color(59, 89, 182));
        JButton btnQLBH = createStyledButton("Quản lý bảo hành", new Color(59, 89, 182));

        btnPanel.add(btnBanHang);
        btnPanel.add(btnXemYeuCauDoiTra);
        btnPanel.add(btnNhapHang);
        btnPanel.add(btnQuanLyKH);
        btnPanel.add(btnQLBH);
        mainPanel.add(btnPanel);

        // THÊM NÚT ĐĂNG XUẤT
        mainPanel.add(Box.createVerticalStrut(15)); // Khoảng cách
        btnLogout = createStyledButton("Đăng xuất", new Color(220, 53, 69)); // Màu đỏ
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(btnLogout);

        setContentPane(mainPanel);

        // Action Listeners
        btnBanHang.addActionListener(e -> {
            System.out.println("EMPLOYEE_VIEW: Nút 'Bán hàng' được nhấn.");
            new SellProductView(maNV).setVisible(true);
        });
        btnXemYeuCauDoiTra.addActionListener(e -> {
            System.out.println("EMPLOYEE_VIEW: Nút 'Xem Yêu cầu đổi/trả' được nhấn.");
            ReturnRequestListView listView = new ReturnRequestListView();
            new ReturnRequestListController(listView);
            listView.setVisible(true);
        });
        btnNhapHang.addActionListener(e -> new ImportProductView(maNV).setVisible(true));
        btnQuanLyKH.addActionListener(e -> new ManageCustomerView(maNV).setVisible(true));
        btnQLBH.addActionListener(e -> new WarrantyManagementView(maNV).setVisible(true));

        btnLogout.addActionListener(e -> {
            System.out.println("EMPLOYEE_VIEW: Nút 'Đăng xuất' được nhấn.");
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
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
        return btn;
    }

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
        SwingUtilities.invokeLater(() -> new EmployeeView("NV001").setVisible(true));
    }
}