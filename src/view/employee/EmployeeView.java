package view.employee;

import controller.common.AuthManager;
import controller.employee.ReturnRequestListController;
import model.NhanVien; // MỚI: Import model NhanVien
import query.NhanVienQuery; // MỚI: Import NhanVienQuery

import javax.swing.*;
import java.awt.*;

public class EmployeeView extends JFrame {

    private static final long serialVersionUID = 1L;
    private final int maNV;
    private JLabel lblTitle; // MỚI: Khai báo ở đây để có thể truy cập từ các phương thức khác
    private JButton btnLogout;
    private JButton btnCapNhatThongTinNV;
    private JButton btnBanHang, btnXemYeuCauDoiTra, btnNhapHang;
    private JButton btnQuanLyKhachHang, btnQuanLyBaoHanh;
    private final Color FUNCTION_BUTTON_COLOR = new Color(0, 123, 255);
    private final Color DANGER_ACTION_COLOR = new Color(220, 53, 69);

    public EmployeeView(int maNV) {
        this.maNV = maNV;
        setTitle("Giao Diện Nhân Viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        loadNhanVienData(); 
        pack();
        setMinimumSize(new Dimension(450, 600)); // Điều chỉnh kích thước cho cân đối
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        lblTitle = new JLabel("Đang tải thông tin...", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        mainPanel.add(lblTitle);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);
        btnCapNhatThongTinNV = createStyledButton("Cập nhật thông tin cá nhân", FUNCTION_BUTTON_COLOR);
        btnBanHang = createStyledButton("Bán hàng", FUNCTION_BUTTON_COLOR);
        btnNhapHang = createStyledButton("Nhập hàng", FUNCTION_BUTTON_COLOR);
        btnXemYeuCauDoiTra = createStyledButton("Xem Yêu cầu đổi/trả", FUNCTION_BUTTON_COLOR);
        btnQuanLyKhachHang = createStyledButton("Quản lý khách hàng", FUNCTION_BUTTON_COLOR);
        btnQuanLyBaoHanh = createStyledButton("Quản lý bảo hành", FUNCTION_BUTTON_COLOR);
        btnPanel.add(btnCapNhatThongTinNV);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnBanHang);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnNhapHang);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnXemYeuCauDoiTra);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnQuanLyKhachHang);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(btnQuanLyBaoHanh);

        mainPanel.add(btnPanel);
        mainPanel.add(Box.createVerticalGlue());

        btnLogout = createStyledButton("Đăng xuất", DANGER_ACTION_COLOR);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(btnLogout);

        setContentPane(mainPanel);

        // Gán sự kiện cho các nút
        addActions();
    }

    /**
     * Tách riêng phần gán sự kiện cho gọn gàng.
     */
    private void addActions() {
        btnCapNhatThongTinNV.addActionListener(e -> {
            // Dòng này bây giờ đã hoàn toàn chính xác
            UpdateEmployeeProfileView updateView = new UpdateEmployeeProfileView(maNV, this::loadNhanVienData);
            updateView.setVisible(true);
        });

        btnBanHang.addActionListener(e -> new SellProductView(maNV).setVisible(true));
        btnNhapHang.addActionListener(e -> new ImportProductView(maNV).setVisible(true));

        btnXemYeuCauDoiTra.addActionListener(e -> {
            ReturnRequestListView listView = new ReturnRequestListView();
            new ReturnRequestListController(listView);
            listView.setVisible(true);
        });

        btnQuanLyKhachHang.addActionListener(e -> new ManageCustomerView().setVisible(true));
        btnQuanLyBaoHanh.addActionListener(e -> new WarrantyManagementView().setVisible(true));
        btnLogout.addActionListener(e -> AuthManager.logout(this));
    }

    /**
     * MỚI: Phương thức tải thông tin nhân viên từ CSDL và cập nhật tiêu đề.
     * Đây là phương thức mà callback this::loadNhanVienData tham chiếu đến.
     */
    private void loadNhanVienData() {
        NhanVien nv = NhanVienQuery.getNhanVienById(this.maNV);
        if (nv != null) {
            lblTitle.setText("Chào mừng, " + nv.getTenNV());
        } else {
            lblTitle.setText("Chào mừng Nhân viên (Mã: " + maNV + ")");
            // Có thể hiển thị cảnh báo nếu không tìm thấy nhân viên
            JOptionPane.showMessageDialog(this,
                    "Không thể tải thông tin chi tiết của nhân viên.",
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
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
        return btn;
    }
}