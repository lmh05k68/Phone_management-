package view.employee;

import model.NhanVien;
import query.NhanVienQuery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Cửa sổ giao diện để cập nhật thông tin chi tiết của một nhân viên.
 * Cung cấp các trường nhập liệu để thay đổi tên, ngày sinh, lương, và số điện thoại.
 * Tự động làm mới giao diện cha sau khi cập nhật thành công thông qua một callback.
 */
public class UpdateEmployeeProfileView extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- Hằng số UI để đảm bảo style nhất quán ---
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color CANCEL_COLOR = new Color(108, 117, 125);
    private static final int FIELD_VERTICAL_PADDING = 8; // Đệm dọc cho các trường nhập liệu

    // --- Dữ liệu và các quy tắc xác thực ---
    private final int maNV;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE; // Định dạng YYYY-MM-DD
    private final Pattern phonePattern = Pattern.compile("^\\d{8,15}$"); // Chỉ chứa số, 8-15 ký tự

    // --- Các thành phần UI ---
    private JTextField tfTenNV, tfNgaySinh, tfSoDienThoai;
    private JSpinner spinnerLuong;
    private JLabel lblMaNVValue;
    private JButton btnCapNhat, btnHuy;

    // --- Callback để làm mới giao diện cha sau khi cập nhật ---
    private final Runnable onUpdateSuccess;

    /**
     * Constructor của lớp UpdateEmployeeProfileView.
     * @param maNV ID của nhân viên cần cập nhật.
     * @param onUpdateSuccess một đối tượng Runnable sẽ được thực thi khi cập nhật thành công (ví dụ: làm mới bảng).
     */
    public UpdateEmployeeProfileView(int maNV, Runnable onUpdateSuccess) {
        this.maNV = maNV;
        this.onUpdateSuccess = onUpdateSuccess;
        
        setTitle("Cập Nhật Thông Tin Nhân Viên - NV: " + maNV);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadEmployeeData();
        
        pack(); // Điều chỉnh kích thước frame vừa với nội dung
        setMinimumSize(getSize()); // Đặt kích thước tối thiểu bằng kích thước đã pack
        setResizable(false);
    }

    /**
     * Khởi tạo và bố trí các thành phần chính của giao diện.
     */
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        // --- Tiêu đề ---
        JLabel lblTitle = new JLabel("Cập Nhật Thông Tin Cá Nhân", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // --- Form nhập liệu ---
        mainPanel.add(createFormPanel(), BorderLayout.CENTER);

        // --- Các nút chức năng ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        btnCapNhat = createStyledButton("Lưu Thay Đổi", SUCCESS_COLOR);
        btnHuy = createStyledButton("Hủy", CANCEL_COLOR);
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnHuy);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Gán sự kiện
        btnCapNhat.addActionListener(e -> updateProfile());
        btnHuy.addActionListener(e -> dispose());
    }
    
    /**
     * Tạo và trả về panel chứa form nhập liệu được bố trí bằng GridBagLayout.
     * @return Một JPanel chứa các trường nhập liệu.
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        int gridY = 0;

        // Dòng 1: Mã Nhân Viên (chỉ đọc)
        addFormFieldLabel(formPanel, gbc, "Mã Nhân Viên:", gridY);
        gbc.gridx = 1;
        lblMaNVValue = new JLabel();
        lblMaNVValue.setFont(VALUE_FONT.deriveFont(Font.BOLD));
        lblMaNVValue.setForeground(Color.GRAY);
        formPanel.add(lblMaNVValue, gbc);
        gridY++;

        // Dòng 2: Tên Nhân Viên
        tfTenNV = new JTextField(25);
        addFormField(formPanel, gbc, "Tên Nhân Viên*:", tfTenNV, gridY++);

        // Dòng 3: Ngày Sinh
        tfNgaySinh = new JTextField(25);
        tfNgaySinh.setToolTipText("Định dạng YYYY-MM-DD, ví dụ: 1990-05-20");
        addFormField(formPanel, gbc, "Ngày Sinh:", tfNgaySinh, gridY++);
        
        // Dòng 4: Lương
        addFormFieldLabel(formPanel, gbc, "Lương (VNĐ):", gridY);
        gbc.gridx = 1;
        gbc.ipady = FIELD_VERTICAL_PADDING; // Áp dụng đệm dọc cho spinner
        spinnerLuong = createLuongSpinner();
        formPanel.add(spinnerLuong, gbc);
        gbc.ipady = 0; // Reset lại đệm
        gridY++;

        // Dòng 5: Số Điện Thoại
        tfSoDienThoai = new JTextField(25);
        tfSoDienThoai.setToolTipText("Chỉ nhập số, từ 8-15 ký tự");
        addFormField(formPanel, gbc, "Số Điện Thoại*:", tfSoDienThoai, gridY++);
        
        return formPanel;
    }

    /**
     * Tải dữ liệu của nhân viên từ CSDL một cách bất đồng bộ để không làm treo UI.
     */
    private void loadEmployeeData() {
        SwingWorker<NhanVien, Void> worker = new SwingWorker<>() {
            @Override
            protected NhanVien doInBackground() throws Exception {
                return NhanVienQuery.getNhanVienById(maNV);
            }

            @Override
            protected void done() {
                try {
                    NhanVien nv = get();
                    if (nv != null) {
                        lblMaNVValue.setText(String.valueOf(nv.getMaNV()));
                        tfTenNV.setText(nv.getTenNV());
                        tfNgaySinh.setText(nv.getNgaySinh() != null ? nv.getNgaySinh().format(dateFormatter) : "");
                        spinnerLuong.setValue(nv.getLuong() != null ? nv.getLuong() : BigDecimal.ZERO);
                        tfSoDienThoai.setText(nv.getSoDienThoai() != null ? nv.getSoDienThoai() : "");
                    } else {
                        showError("Không thể tải thông tin nhân viên mã: " + maNV, "Lỗi Tải Dữ Liệu");
                        // Đóng cửa sổ nếu không tải được dữ liệu
                        SwingUtilities.invokeLater(UpdateEmployeeProfileView.this::dispose);
                    }
                } catch (Exception e) {
                    showError("Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi");
                    e.printStackTrace();
                    SwingUtilities.invokeLater(UpdateEmployeeProfileView.this::dispose);
                }
            }
        };
        worker.execute();
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Lưu Thay Đổi".
     * Xác thực dữ liệu, sau đó gọi query để cập nhật và đóng cửa sổ.
     */
    private void updateProfile() {
        if (!validateInput()) {
            return; // Dừng lại nếu dữ liệu không hợp lệ
        }

        String tenMoi = tfTenNV.getText().trim();
        String ngaySinhStr = tfNgaySinh.getText().trim();
        BigDecimal luongMoi = (BigDecimal) spinnerLuong.getValue();
        String sdtMoi = tfSoDienThoai.getText().trim();

        LocalDate ngaySinhMoi = null;
        if (!ngaySinhStr.isEmpty()) {
            // Việc parse đã được kiểm tra trong validateInput, nên ở đây có thể thực hiện an toàn
            ngaySinhMoi = LocalDate.parse(ngaySinhStr, dateFormatter);
        }
        
        NhanVien nvUpdated = new NhanVien(this.maNV, tenMoi, ngaySinhMoi, luongMoi, sdtMoi);

        try {
            boolean success = NhanVienQuery.update(nvUpdated);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                if (onUpdateSuccess != null) {
                    onUpdateSuccess.run(); // Thực thi callback để làm mới UI cha
                }
                dispose();
            } else {
                showError("Cập nhật thất bại. Số điện thoại có thể đã tồn tại.", "Lỗi Cập Nhật");
            }
        } catch (RuntimeException ex) {
            showError("Lỗi cơ sở dữ liệu khi cập nhật:\n" + ex.getMessage(), "Lỗi Hệ Thống");
        }
    }
    
    /**
     * Xác thực tất cả các trường nhập liệu trước khi lưu.
     * @return true nếu tất cả dữ liệu hợp lệ, ngược lại trả về false.
     */
    private boolean validateInput() {
        if (tfTenNV.getText().trim().isEmpty()) {
            showWarning("Tên nhân viên không được để trống.", tfTenNV);
            return false;
        }

        String sdtMoi = tfSoDienThoai.getText().trim();
        if (sdtMoi.isEmpty()) {
            showWarning("Số điện thoại không được để trống.", tfSoDienThoai);
            return false;
        }
        if (!phonePattern.matcher(sdtMoi).matches()) {
            showWarning("Số điện thoại không hợp lệ (chỉ chứa số, 8-15 ký tự).", tfSoDienThoai);
            return false;
        }

        String ngaySinhStr = tfNgaySinh.getText().trim();
        if (!ngaySinhStr.isEmpty()) {
            try {
                LocalDate ngaySinhMoi = LocalDate.parse(ngaySinhStr, dateFormatter);
                if (ngaySinhMoi.isAfter(LocalDate.now().minusYears(18))) {
                    showWarning("Nhân viên phải đủ 18 tuổi.", tfNgaySinh);
                    return false;
                }
            } catch (DateTimeParseException ex) {
                showWarning("Định dạng Ngày sinh không hợp lệ.\nVui lòng dùng YYYY-MM-DD (ví dụ: 1995-12-30).", tfNgaySinh);
                return false;
            }
        }
        return true;
    }

    // --- Các phương thức trợ giúp (Helper Methods) ---

    private void addFormFieldLabel(JPanel panel, GridBagConstraints gbc, String labelText, int gridY) {
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        panel.add(label, gbc);
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int gridY) {
        addFormFieldLabel(panel, gbc, labelText, gridY);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.ipady = FIELD_VERTICAL_PADDING; // Áp dụng đệm dọc
        field.setFont(VALUE_FONT);
        panel.add(field, gbc);
        gbc.ipady = 0; // Reset đệm cho thành phần tiếp theo
    }

    private JSpinner createLuongSpinner() {
        SpinnerNumberModel luongModel = new SpinnerNumberModel(BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("999999999999.99"), new BigDecimal("500000.00"));
        JSpinner spinner = new JSpinner(luongModel);
        spinner.setFont(VALUE_FONT);

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        currencyFormat.setMinimumFractionDigits(0);
        currencyFormat.setMaximumFractionDigits(2);

        NumberFormatter formatter = new NumberFormatter(currencyFormat);
        formatter.setValueClass(BigDecimal.class);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        JSpinner.NumberEditor editorLuong = (JSpinner.NumberEditor) spinner.getEditor();
        editorLuong.getTextField().setFormatterFactory(new DefaultFormatterFactory(formatter));
        editorLuong.getTextField().setHorizontalAlignment(JTextField.LEFT);
        
        return spinner;
    }
    
    private void showWarning(String message, JComponent componentToFocus) {
        JOptionPane.showMessageDialog(this, message, "Cảnh Báo Nhập Liệu", JOptionPane.WARNING_MESSAGE);
        if (componentToFocus != null) {
            // Đảm bảo focus được yêu cầu sau khi dialog đã đóng
            SwingUtilities.invokeLater(componentToFocus::requestFocusInWindow);
        }
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(backgroundColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(backgroundColor);
            }
        });
        return btn;
    }
}