package view.common;

import controller.common.RegisterController;
import model.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class RegisterView extends JFrame {
    private static final long serialVersionUID = 1L;

    // --- Components ---
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<TaiKhoan.VaiTro> roleComboBox;

    // Customer Panel Components
    private JPanel khPanel;
    private JTextField hoTenKHField;
    private JTextField sdtKHField;

    // Employee Panel Components
    private JPanel nvPanel;
    private JTextField tenNVField;
    private JTextField ngaySinhField;
    private JSpinner luongSpinner;
    private JTextField sdtNVField;

    // Buttons
    private JButton registerButton;
    private JButton backButton;

    // --- Utilities ---
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public RegisterView() {
        setTitle("Đăng Ký Tài Khoản Mới");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Khởi tạo các thành phần trước khi gọi initUI
        initComponents();
        // Cấu hình giao diện
        initUI();
        // Gán sự kiện
        initActions();

        pack();
        setLocationRelativeTo(null); // Đặt frame ở giữa màn hình
    }

    private void initComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        roleComboBox = new JComboBox<>(
                new TaiKhoan.VaiTro[]{TaiKhoan.VaiTro.KHACH_HANG, TaiKhoan.VaiTro.NHAN_VIEN, TaiKhoan.VaiTro.ADMIN}
        );
        roleComboBox.setRenderer(new VaiTroRenderer());

        // Customer panel
        hoTenKHField = new JTextField(15);
        sdtKHField = new JTextField(15);

        // Employee panel
        tenNVField = new JTextField(15);
        ngaySinhField = new JTextField(15);
        sdtNVField = new JTextField(15);

        // Spinner cho lương với model tùy chỉnh
        BigDecimalSpinnerModel luongModel = new BigDecimalSpinnerModel(
                new BigDecimal("5000000"),  // initial value
                new BigDecimal("0"),        // min value
                null,                       // max value (unbounded)
                new BigDecimal("100000")    // step
        );
        luongSpinner = new JSpinner(luongModel);
        JSpinner.NumberEditor editorLuong = new JSpinner.NumberEditor(luongSpinner, "#,##0 VNĐ");
        luongSpinner.setEditor(editorLuong);

        // Buttons
        registerButton = new JButton("Đăng ký");
        backButton = new JButton("Quay lại");
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- Title ---
        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 0, 30, 0));
        mainPanel.add(lblTitle);

        // --- Input Fields Panel (sử dụng GridBagLayout để căn chỉnh) ---
        JPanel inputFieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tên đăng nhập
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; // Label
        JLabel lblUsername = new JLabel("Tên đăng nhập*:");
        styleLabel(lblUsername);
        inputFieldsPanel.add(lblUsername, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7; // Field
        styleTextField(usernameField);
        inputFieldsPanel.add(usernameField, gbc);

        // Mật khẩu
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPassword = new JLabel("Mật khẩu*:");
        styleLabel(lblPassword);
        inputFieldsPanel.add(lblPassword, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        styleTextField(passwordField);
        inputFieldsPanel.add(passwordField, gbc);

        // Vai trò
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblRole = new JLabel("Vai trò*:");
        styleLabel(lblRole);
        inputFieldsPanel.add(lblRole, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        inputFieldsPanel.add(roleComboBox, gbc);

        mainPanel.add(inputFieldsPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // --- Customer Info Panel ---
        khPanel = createRolePanel("Thông tin Khách hàng");
        addGridBagComponent(khPanel, new JLabel("Họ tên*:"), 0, 0);
        addGridBagComponent(khPanel, hoTenKHField, 1, 0);
        addGridBagComponent(khPanel, new JLabel("SĐT*:"), 0, 1);
        addGridBagComponent(khPanel, sdtKHField, 1, 1);
        mainPanel.add(khPanel);

        // --- Employee Info Panel ---
        nvPanel = createRolePanel("Thông tin Nhân viên");
        addGridBagComponent(nvPanel, new JLabel("Tên nhân viên*:"), 0, 0);
        addGridBagComponent(nvPanel, tenNVField, 1, 0);
        addGridBagComponent(nvPanel, new JLabel("SĐT*:"), 0, 1);
        addGridBagComponent(nvPanel, sdtNVField, 1, 1);
        addGridBagComponent(nvPanel, new JLabel("Ngày sinh (dd/MM/yyyy):"), 0, 2);
        addGridBagComponent(nvPanel, ngaySinhField, 1, 2);
        addGridBagComponent(nvPanel, new JLabel("Lương (VNĐ)*:"), 0, 3);
        addGridBagComponent(nvPanel, luongSpinner, 1, 3);
        styleTextField(ngaySinhField); // Style cho các field còn lại
        styleTextField(luongSpinner); // Áp dụng style chung
        mainPanel.add(nvPanel);

        mainPanel.add(Box.createVerticalStrut(20));

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        styleGenericButton(registerButton, new Color(40, 167, 69)); // Green
        styleGenericButton(backButton, new Color(108, 117, 125));  // Gray

        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel);
        setContentPane(mainPanel);
    }

    private void initActions() {
        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> this.dispose()); // Đóng cửa sổ hiện tại
        roleComboBox.addActionListener(e -> updateFieldVisibility());
        updateFieldVisibility(); // Gọi lần đầu để thiết lập trạng thái ban đầu
    }

    // --- Helper methods for UI building ---

    private JPanel createRolePanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private void addGridBagComponent(JPanel panel, JComponent comp, int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = x;
        gbc.gridy = y;

        if (comp instanceof JLabel) {
            gbc.weightx = 0.3;
            styleLabel((JLabel) comp);
        } else {
            gbc.weightx = 0.7;
            if(comp instanceof JTextField) {
                styleTextField(comp);
            }
        }
        panel.add(comp, gbc);
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    }

    private void styleTextField(JComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(5, 8, 5, 8) // Padding bên trong
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35));
    }

    private void styleGenericButton(JButton btn, Color backgroundColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, btn.getPreferredSize().height));
    }

    // --- Logic methods ---

    private void updateFieldVisibility() {
        TaiKhoan.VaiTro selectedRole = (TaiKhoan.VaiTro) roleComboBox.getSelectedItem();
        if (selectedRole == null) return;

        khPanel.setVisible(selectedRole == TaiKhoan.VaiTro.KHACH_HANG);
        nvPanel.setVisible(selectedRole == TaiKhoan.VaiTro.NHAN_VIEN);

        pack(); // Rất quan trọng: Điều chỉnh lại kích thước cửa sổ cho vừa với nội dung mới
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        TaiKhoan.VaiTro role = (TaiKhoan.VaiTro) roleComboBox.getSelectedItem();

        String hoTen = "", sdt = "";
        LocalDate ngaySinh = null;
        BigDecimal luong = BigDecimal.ZERO;

        try {
            // Validate common fields
            if (username.isEmpty() || password.isEmpty() || role == null) {
                throw new IllegalArgumentException("Tên đăng nhập, mật khẩu và vai trò không được để trống.");
            }

            switch (role) {
                case KHACH_HANG:
                    hoTen = hoTenKHField.getText().trim();
                    sdt = sdtKHField.getText().trim();
                    if (hoTen.isEmpty() || sdt.isEmpty()) {
                        throw new IllegalArgumentException("Vui lòng nhập đủ họ tên và SĐT khách hàng.");
                    }
                    break;
                case NHAN_VIEN:
                    hoTen = tenNVField.getText().trim();
                    sdt = sdtNVField.getText().trim();
                    if (hoTen.isEmpty() || sdt.isEmpty()) {
                        throw new IllegalArgumentException("Vui lòng nhập đủ tên và SĐT nhân viên.");
                    }
                    if (!ngaySinhField.getText().trim().isEmpty()) {
                        ngaySinh = LocalDate.parse(ngaySinhField.getText().trim(), dateFormatter);
                    }
                    luong = (BigDecimal) luongSpinner.getValue();
                    break;
                case ADMIN:
                    // Admin không cần thông tin thêm
                    break;
            }

            RegisterController controller = new RegisterController();
            String errorMessage = controller.handleRegister(username, password, role, hoTen, sdt, ngaySinh, luong);

            if (errorMessage == null) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, errorMessage, "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + e.getMessage(), "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày sinh không hợp lệ. Vui lòng dùng dd/MM/yyyy.", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Inner classes for custom rendering/models ---

    private static class VaiTroRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof TaiKhoan.VaiTro) {
                switch ((TaiKhoan.VaiTro) value) {
                    case ADMIN: setText("Quản Trị Viên"); break;
                    case NHAN_VIEN: setText("Nhân Viên"); break;
                    case KHACH_HANG: setText("Khách Hàng"); break;
                }
            }
            return this;
        }
    }

    private static class BigDecimalSpinnerModel extends SpinnerNumberModel {
        private static final long serialVersionUID = 1L;
        public BigDecimalSpinnerModel(BigDecimal value, Comparable<BigDecimal> minimum, Comparable<BigDecimal> maximum, BigDecimal stepSize) {
            super(value, minimum, maximum, stepSize);
        }

        @Override
        public Object getNextValue() {
            BigDecimal currentValue = (BigDecimal) super.getValue();
            BigDecimal step = (BigDecimal) getStepSize();
            BigDecimal nextValue = currentValue.add(step);
            BigDecimal max = (BigDecimal) getMaximum();
            if (max != null && max.compareTo(nextValue) < 0) {
                return getMaximum(); // Trả về giá trị max thay vì null
            }
            return nextValue;
        }

        @Override
        public Object getPreviousValue() {
            BigDecimal currentValue = (BigDecimal) super.getValue();
            BigDecimal step = (BigDecimal) getStepSize();
            BigDecimal previousValue = currentValue.subtract(step);
            BigDecimal min = (BigDecimal) getMinimum();
            if (min != null && min.compareTo(previousValue) > 0) {
                return getMinimum(); // Trả về giá trị min thay vì null
            }
            return previousValue;
        }
    }
}