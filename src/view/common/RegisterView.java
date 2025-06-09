package view.common;

import controller.common.RegisterController;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException; 
public class RegisterView extends JFrame {
    private static final long serialVersionUID = 1L;

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"khachhang", "nhanvien", "admin"});
    
    private final JPanel khPanel = new JPanel();
    private final JTextField hoTenKHField = new JTextField(15);
    private final JTextField sdtKHField = new JTextField(15);
    
    private final JPanel nvPanel = new JPanel();
    private final JTextField tenNVField = new JTextField(15);
    private final JTextField ngaySinhField = new JTextField(15); // Format YYYY-MM-DD
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
        // khPanel.add(createStyledFieldPanel("Mã khách hàng*:", maKHField)); // Bỏ
        // khPanel.add(Box.createVerticalStrut(5)); // Bỏ
        khPanel.add(createStyledFieldPanel("Họ và tên*:", hoTenKHField));
        khPanel.add(Box.createVerticalStrut(5));
        khPanel.add(createStyledFieldPanel("Số điện thoại*:", sdtKHField));
        khPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(khPanel);

        nvPanel.setLayout(new BoxLayout(nvPanel, BoxLayout.Y_AXIS));
        nvPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thông tin Nhân viên"));
        // nvPanel.add(createStyledFieldPanel("Mã nhân viên*:", maNVField)); // Bỏ
        // nvPanel.add(Box.createVerticalStrut(5)); // Bỏ
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
        // maKHField.setFont(commonFont); // Bỏ
        hoTenKHField.setFont(commonFont);
        sdtKHField.setFont(commonFont);
        // maNVField.setFont(commonFont); // Bỏ
        tenNVField.setFont(commonFont);
        ngaySinhField.setFont(commonFont);
        luongField.setFont(commonFont);
        sdtNVField.setFont(commonFont);

        updateFieldVisibility();
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(520, 600)); // Điều chỉnh chiều cao nếu cần
        scrollPane.setBorder(null);

        setContentPane(scrollPane);
        // setSize(530, 650); // Điều chỉnh kích thước Frame
        pack(); // Sử dụng pack để tự động điều chỉnh kích thước
        setMinimumSize(new Dimension(500, 450));
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
        
        khPanel.setVisible(khVisible);
        nvPanel.setVisible(nvVisible);
        
        // Tự động điều chỉnh kích thước frame khi panel ẩn/hiện
        // Nên gọi pack() ở cuối hoặc khi frame đã hiển thị
        // For immediate effect, revalidate and repaint
        // ((JPanel)getContentPane().getComponent(0)).revalidate(); // Nếu dùng JScrollPane
        // ((JPanel)getContentPane().getComponent(0)).repaint();
        // Hoặc đơn giản là pack() nếu chưa hiển thị
        // pack(); // Gọi pack ở đây có thể làm frame nhấp nháy, tốt hơn là ở constructor hoặc khi hiển thị
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

        // Thông tin chung
        String hoTen = "";
        String sdt = "";
        // Thông tin riêng cho nhân viên
        String ngaySinhStr = null; 
        String luongStr = null;
        LocalDate ngaySinhDate = null;
        double luongDouble = 0.0;

        if ("khachhang".equals(role)) {
            hoTen = hoTenKHField.getText().trim();
            sdt = sdtKHField.getText().trim();
            if (hoTen.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Họ tên và SĐT cho khách hàng.", "Thiếu thông tin khách hàng", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else if ("nhanvien".equals(role)) {
            hoTen = tenNVField.getText().trim();
            sdt = sdtNVField.getText().trim();
            ngaySinhStr = ngaySinhField.getText().trim();
            luongStr = luongField.getText().trim();
            if (hoTen.isEmpty() || sdt.isEmpty() || ngaySinhStr.isEmpty() || luongStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin nhân viên.", "Thiếu thông tin nhân viên", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                ngaySinhDate = LocalDate.parse(ngaySinhStr); // Parse ngày sinh
            } catch (DateTimeParseException e) { // Bắt lỗi parse cụ thể
                JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ. Vui lòng nhập theo định dạng YYYY-MM-DD.", "Lỗi định dạng ngày sinh", JOptionPane.ERROR_MESSAGE);
                ngaySinhField.requestFocus();
                return;
            }
            try {
                luongDouble = Double.parseDouble(luongStr); // Parse lương
                if (luongDouble < 0) {
                     JOptionPane.showMessageDialog(this, "Lương không được là số âm.", "Lương không hợp lệ", JOptionPane.ERROR_MESSAGE);
                    luongField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                 JOptionPane.showMessageDialog(this, "Lương phải là một số hợp lệ.", "Lỗi định dạng lương", JOptionPane.ERROR_MESSAGE);
                luongField.requestFocus();
                return;
            }
        } else if ("admin".equals(role)) {
             System.out.println("REGISTER_VIEW: Đăng ký cho vai trò admin.");
             // không cần thông tin cá nhân cho admin trong ví dụ này
        }

        System.out.println("REGISTER_VIEW: Chuẩn bị gọi RegisterController.handleRegister với Username: " + username + ", Role: " + role);
        RegisterController controller = new RegisterController();
        
        // Gọi controller với các tham số đã được xử lý
        boolean success = controller.handleRegister(username, password, role, hoTen, sdt, ngaySinhDate, luongDouble);

        if (success) {
            JOptionPane.showMessageDialog(this, "Đăng ký tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("REGISTER_VIEW: Đăng ký thành công cho user: " + username);
            this.dispose(); // Đóng cửa sổ đăng ký
        } else {
            // Thông báo lỗi chung, chi tiết lỗi đã được log ở controller
            JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Tên đăng nhập có thể đã tồn tại, hoặc có lỗi khác. Vui lòng kiểm tra console log.", "Lỗi đăng ký", JOptionPane.ERROR_MESSAGE);
            System.err.println("REGISTER_VIEW: Đăng ký thất bại cho user: " + username);
        }
    }
}