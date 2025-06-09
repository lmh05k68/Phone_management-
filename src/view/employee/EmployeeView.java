package view.employee;

import javax.swing.*;
import java.awt.*;
import controller.common.AuthManager;
import controller.employee.ReturnRequestListController;
public class EmployeeView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int maNV; // Đổi kiểu dữ liệu sang int
    private JButton btnLogout;

    public EmployeeView(int maNV) { // Constructor nhận int
        this.maNV = maNV;
        System.out.println("EMPLOYEE_VIEW: Constructor EmployeeView cho MaNV: " + maNV);
        setTitle("Giao diện Nhân viên");
        // setSize(450, 480); // Sẽ dùng pack()
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        pack(); // Tự động điều chỉnh kích thước
        setMinimumSize(new Dimension(getWidth(), 450)); // Đảm bảo chiều cao tối thiểu
        System.out.println("EMPLOYEE_VIEW: Constructor EmployeeView kết thúc.");
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35)); // Tăng padding

        JLabel lblTitle = new JLabel("Chào mừng Nhân viên (Mã: " + maNV + ")"); // Hiển thị mã NV
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0)); // Tăng khoảng cách dưới
        mainPanel.add(lblTitle);

        JPanel btnPanel = new JPanel();
        // Sử dụng BoxLayout cho các nút để dễ căn chỉnh và thêm khoảng cách
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);

        // Màu cơ bản cho các nút chức năng của nhân viên
        Color employeeButtonColor = new Color(23, 162, 184); // Một màu xanh teal

        JButton btnBanHang = createStyledButton("Bán hàng", employeeButtonColor);
        JButton btnXemYeuCauDoiTra = createStyledButton("Xem Yêu cầu đổi/trả", employeeButtonColor);
        JButton btnNhapHang = createStyledButton("Nhập hàng", employeeButtonColor);
        JButton btnQuanLyKH = createStyledButton("Quản lý khách hàng", employeeButtonColor);
        JButton btnQLBH = createStyledButton("Quản lý bảo hành", employeeButtonColor);

        btnPanel.add(btnBanHang);
        btnPanel.add(Box.createVerticalStrut(12)); // Khoảng cách giữa các nút
        btnPanel.add(btnXemYeuCauDoiTra);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnNhapHang);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnQuanLyKH);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnQLBH);
        mainPanel.add(btnPanel);

        mainPanel.add(Box.createVerticalStrut(25)); 
        btnLogout = createStyledButton("Đăng xuất", new Color(220, 53, 69)); 
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(btnLogout);

        setContentPane(mainPanel);
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
        btnQuanLyKH.addActionListener(e -> new ManageCustomerView().setVisible(true)); 
        btnQLBH.addActionListener(e -> new WarrantyManagementView().setVisible(true));

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
            BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height + 5));
        return btn;
    }
}