package view.employee;
import model.NhanVien;
import query.NhanVienQuery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UpdateEmployeeProfileView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int maNV;
    private JTextField tfTenNV, tfNgaySinh, tfSoDienThoai;
    private JSpinner spinnerLuong;
    private JLabel lblMaNVValue;
    private JButton btnCapNhat, btnHuy;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD

    public UpdateEmployeeProfileView(int maNV) {
        this.maNV = maNV;
        setTitle("Cập Nhật Thông Tin Nhân Viên - NV: " + maNV);
        setSize(500, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initUI();
        loadEmployeeData();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Cập Nhật Thông Tin Cá Nhân", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 14);

        int gridY = 0;
        // Mã NV
        gbc.gridx = 0; gbc.gridy = gridY; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblMaNVText = new JLabel("Mã Nhân Viên:");
        lblMaNVText.setFont(labelFont);
        formPanel.add(lblMaNVText, gbc);
        gbc.gridx = 1; gbc.gridy = gridY++; gbc.anchor = GridBagConstraints.LINE_START;
        lblMaNVValue = new JLabel();
        lblMaNVValue.setFont(valueFont.deriveFont(Font.BOLD));
        formPanel.add(lblMaNVValue, gbc);

        // Tên NV
        gbc.gridx = 0; gbc.gridy = gridY; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblTenNV = new JLabel("Tên Nhân Viên*:");
        lblTenNV.setFont(labelFont);
        formPanel.add(lblTenNV, gbc);
        gbc.gridx = 1; gbc.gridy = gridY++; gbc.anchor = GridBagConstraints.LINE_START;
        tfTenNV = new JTextField(20);
        tfTenNV.setFont(valueFont);
        formPanel.add(tfTenNV, gbc);

        // Ngày Sinh
        gbc.gridx = 0; gbc.gridy = gridY; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblNgaySinh = new JLabel("Ngày Sinh (YYYY-MM-DD):");
        lblNgaySinh.setFont(labelFont);
        formPanel.add(lblNgaySinh, gbc);
        gbc.gridx = 1; gbc.gridy = gridY++; gbc.anchor = GridBagConstraints.LINE_START;
        tfNgaySinh = new JTextField(20);
        tfNgaySinh.setFont(valueFont);
        formPanel.add(tfNgaySinh, gbc);

        // Lương
        gbc.gridx = 0; gbc.gridy = gridY; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblLuong = new JLabel("Lương (VNĐ)*:");
        lblLuong.setFont(labelFont);
        formPanel.add(lblLuong, gbc);
        gbc.gridx = 1; gbc.gridy = gridY++; gbc.anchor = GridBagConstraints.LINE_START;
        spinnerLuong = new JSpinner(new SpinnerNumberModel(Double.valueOf(0.0), Double.valueOf(0.0), Double.valueOf(1_000_000_000.0), Double.valueOf(100000.0)));
        JSpinner.NumberEditor editorLuong = new JSpinner.NumberEditor(spinnerLuong, "#,##0");
        spinnerLuong.setEditor(editorLuong);
        spinnerLuong.setFont(valueFont);
        formPanel.add(spinnerLuong, gbc);

        // Số Điện Thoại
        gbc.gridx = 0; gbc.gridy = gridY; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblSdt = new JLabel("Số Điện Thoại*:");
        lblSdt.setFont(labelFont);
        formPanel.add(lblSdt, gbc);
        gbc.gridx = 1; gbc.gridy = gridY++; gbc.anchor = GridBagConstraints.LINE_START;
        tfSoDienThoai = new JTextField(20);
        tfSoDienThoai.setFont(valueFont);
        formPanel.add(tfSoDienThoai, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnCapNhat = createStyledButton("Lưu Thay Đổi", new Color(40, 167, 69));
        btnHuy = createStyledButton("Hủy", new Color(108, 117, 125));
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnHuy);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        btnCapNhat.addActionListener(e -> updateProfile());
        btnHuy.addActionListener(e -> dispose());
    }

    private void loadEmployeeData() {
        NhanVien nv = NhanVienQuery.getNhanVienById(this.maNV); // Giả sử phương thức này tồn tại
        if (nv != null) {
            lblMaNVValue.setText(String.valueOf(nv.getMaNV()));
            tfTenNV.setText(nv.getTenNV());
            if (nv.getNgaySinh() != null) {
                tfNgaySinh.setText(nv.getNgaySinh().format(dateFormatter));
            } else {
                tfNgaySinh.setText("");
            }
            spinnerLuong.setValue(nv.getLuong());
            tfSoDienThoai.setText(nv.getSoDienThoai());
        } else {
            JOptionPane.showMessageDialog(this, "Không thể tải thông tin nhân viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void updateProfile() {
        String tenMoi = tfTenNV.getText().trim();
        String ngaySinhStr = tfNgaySinh.getText().trim();
        double luongMoi = ((Number)spinnerLuong.getValue()).doubleValue();
        String sdtMoi = tfSoDienThoai.getText().trim();

        if (tenMoi.isEmpty() || sdtMoi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên và Số điện thoại không được để trống.", "Lỗi Nhập Liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (luongMoi < 0) {
            JOptionPane.showMessageDialog(this, "Lương không thể âm.", "Lỗi Nhập Liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate ngaySinhMoi = null;
        if (!ngaySinhStr.isEmpty()) {
            try {
                ngaySinhMoi = LocalDate.parse(ngaySinhStr, dateFormatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Định dạng Ngày sinh không hợp lệ (YYYY-MM-DD).", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        NhanVien nvUpdated = new NhanVien(this.maNV, tenMoi, ngaySinhMoi, luongMoi, sdtMoi);

        if (NhanVienQuery.update(nvUpdated)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên thất bại.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                new EmptyBorder(8, 20, 8, 20)
        ));
        return btn;
    }
}