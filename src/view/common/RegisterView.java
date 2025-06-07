package view.common;

import controller.common.RegisterController; 
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
public class RegisterView extends JFrame {
    private static final long serialVersionUID = 1L;

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"khachhang", "nhanvien", "admin"});
    private final JPanel khPanel = new JPanel();
    private final JTextField maKHField = new JTextField(15);
    private final JTextField hoTenKHField = new JTextField(15);
    private final JTextField sdtKHField = new JTextField(15);
    private final JPanel nvPanel = new JPanel();
    private final JTextField maNVField = new JTextField(15);
    private final JTextField tenNVField = new JTextField(15);
    private final JTextField ngaySinhField = new JTextField(15);
    private final JTextField luongField = new JTextField(15);
    private final JTextField sdtNVField = new JTextField(15);

    private JButton registerButton;

    public RegisterView() {
        System.out.println("REGISTER_VIEW: Constructor RegisterView bắt đầu.");
        setTitle("Đăng ký tài khoản mới");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(lblTitle);

        mainPanel.add(createStyledFieldPanel("Tên đăng nhập*:", usernameField));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createStyledFieldPanel("Mật khẩu*:", passwordField));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createStyledFieldPanel("Đăng ký với vai trò*:", roleComboBox));
        mainPanel.add(Box.createVerticalStrut(15));

        khPanel.setLayout(new BoxLayout(khPanel, BoxLayout.Y_AXIS));
        khPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thông tin Khách hàng"));
        khPanel.add(createStyledFieldPanel("Mã khách hàng*:", maKHField));
        khPanel.add(Box.createVerticalStrut(5));
        khPanel.add(createStyledFieldPanel("Họ và tên*:", hoTenKHField));
        khPanel.add(Box.createVerticalStrut(5));
        khPanel.add(createStyledFieldPanel("Số điện thoại*:", sdtKHField));
        khPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(khPanel);

        nvPanel.setLayout(new BoxLayout(nvPanel, BoxLayout.Y_AXIS));
        nvPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thông tin Nhân viên"));
        nvPanel.add(createStyledFieldPanel("Mã nhân viên*:", maNVField));
        nvPanel.add(Box.createVerticalStrut(5));
        nvPanel.add(createStyledFieldPanel("Tên nhân viên*:", tenNVField));
        nvPanel.add(Box.createVerticalStrut(5));
        nvPanel.add(createStyledFieldPanel("Ngày sinh (YYYY-MM-DD)*:", ngaySinhField));
        nvPanel.add(Box.createVerticalStrut(5));
        nvPanel.add(createStyledFieldPanel("Lương (VNĐ)*:", luongField));
        nvPanel.add(Box.createVerticalStrut(5));
        nvPanel.add(createStyledFieldPanel("Số điện thoại*:", sdtNVField));
        nvPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(nvPanel);

        mainPanel.add(Box.createVerticalStrut(20));

        registerButton = new JButton("Đăng ký");
        styleGenericButton(registerButton, new Color(40, 167, 69));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerButton.addActionListener(e -> {
            System.out.println("REGISTER_VIEW: Nút 'Đăng ký' trong RegisterView được nhấn.");
            handleRegister();
        });
        mainPanel.add(registerButton);

        roleComboBox.addActionListener(e -> {
            System.out.println("REGISTER_VIEW: Vai trò thay đổi sang: " + roleComboBox.getSelectedItem());
            updateFieldVisibility();
        });

        Font commonFont = new Font("Segoe UI", Font.PLAIN, 14);
        usernameField.setFont(commonFont);
        passwordField.setFont(commonFont);
        roleComboBox.setFont(commonFont);
        maKHField.setFont(commonFont);
        hoTenKHField.setFont(commonFont);
        sdtKHField.setFont(commonFont);
        maNVField.setFont(commonFont);
        tenNVField.setFont(commonFont);
        ngaySinhField.setFont(commonFont);
        luongField.setFont(commonFont);
        sdtNVField.setFont(commonFont);

        updateFieldVisibility();
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(520, 650));
        scrollPane.setBorder(null);

        setContentPane(scrollPane);
        setSize(530, 700);
        setMinimumSize(new Dimension(500, 500));
        setLocationRelativeTo(null);
        System.out.println("REGISTER_VIEW: Constructor RegisterView kết thúc.");
    }

    private JPanel createStyledFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(170, 28));
        panel.add(label, BorderLayout.WEST);

        if (field instanceof JComboBox) {
            field.setBackground(Color.WHITE);
        } else if (field instanceof JTextField || field instanceof JPasswordField) {
             field.setBorder(BorderFactory.createCompoundBorder(
                field.getBorder(), 
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));
        }

        panel.add(field, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, field.getPreferredSize().height + 10));
        return panel;
    }
    
    private void styleGenericButton(JButton btn, Color backgroundColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(200, btn.getPreferredSize().height));
        btn.setMaximumSize(new Dimension(250, btn.getPreferredSize().height));
    }

    private void updateFieldVisibility() {
        String role = (String) roleComboBox.getSelectedItem();
        if (role == null) return;

        boolean khVisible = "khachhang".equals(role);
        boolean nvVisible = "nhanvien".equals(role);
        boolean adminSelected = "admin".equals(role);


        khPanel.setVisible(khVisible);
        nvPanel.setVisible(nvVisible);
        if (adminSelected) {
            khPanel.setVisible(false);
            nvPanel.setVisible(false);
        }


        System.out.println("REGISTER_VIEW_updateFieldVisibility: KhachHang Panel visible: " + khPanel.isVisible() + ", NhanVien Panel visible: " + nvPanel.isVisible() + ", Role selected: " + role);
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = "";
        if (roleComboBox.getSelectedItem() != null) {
            role = roleComboBox.getSelectedItem().toString().toLowerCase();
        }

        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập, mật khẩu và chọn vai trò.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự.", "Mật khẩu yếu", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        String maDoiTuong = ""; 
        String hoTen = "";
        String sdt = "";
        String ngaySinh = null;
        String luongStr = null;

        if ("khachhang".equals(role)) {
            maDoiTuong = maKHField.getText().trim();
            hoTen = hoTenKHField.getText().trim();
            sdt = sdtKHField.getText().trim();
            if (maDoiTuong.isEmpty() || hoTen.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin khách hàng (Mã KH, Họ tên, SĐT).", "Thiếu thông tin khách hàng", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else if ("nhanvien".equals(role)) {
            maDoiTuong = maNVField.getText().trim();
            hoTen = tenNVField.getText().trim();
            sdt = sdtNVField.getText().trim();
            ngaySinh = ngaySinhField.getText().trim();
            luongStr = luongField.getText().trim();
            if (maDoiTuong.isEmpty() || hoTen.isEmpty() || sdt.isEmpty() || ngaySinh.isEmpty() || luongStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin nhân viên.", "Thiếu thông tin nhân viên", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                LocalDate.parse(ngaySinh); 
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ. Vui lòng nhập theo định dạng YYYY-MM-DD.", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                ngaySinhField.requestFocus();
                return;
            }
            try {
                Double.parseDouble(luongStr);
            } catch (NumberFormatException e) {
                 JOptionPane.showMessageDialog(this, "Lương phải là một số hợp lệ.", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                luongField.requestFocus();
                return;
            }
        } else if ("admin".equals(role)) {
             System.out.println("REGISTER_VIEW: Đăng ký cho vai trò admin.");
        }

        System.out.println("REGISTER_VIEW: Chuẩn bị gọi RegisterController.handleRegister với Username: " + username + ", Role: " + role + ", MaDoiTuong: " + maDoiTuong);
        RegisterController controller = new RegisterController();
        boolean success = controller.handleRegister(username, password, role, maDoiTuong, hoTen, sdt, ngaySinh, luongStr);

        if (success) {
            JOptionPane.showMessageDialog(this, "Đăng ký tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("REGISTER_VIEW: Đăng ký thành công cho user: " + username);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Tên đăng nhập hoặc Mã đối tượng có thể đã tồn tại, hoặc có lỗi khác.", "Lỗi đăng ký", JOptionPane.ERROR_MESSAGE);
            System.err.println("REGISTER_VIEW: Đăng ký thất bại cho user: " + username);
        }
    }
}