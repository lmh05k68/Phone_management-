package view.customer;

import model.HoaDonXuat;
import model.TraGop;
import query.HoaDonXuatQuery;
import query.TraGopQuery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class TraGopKHView extends JFrame {
    private static final long serialVersionUID = 1L;

    // --- UI Constants ---
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 16);

    // --- UI Components ---
    private JTextField txtMaHDX, txtSoThang, txtLaiSuat;
    private JLabel lblTienGoc, lblTienTraHangThang;
    private JButton btnDangKy;

    private final int maKH;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
    private HoaDonXuat currentInvoice = null;

    public TraGopKHView(int maKH) {
        this.maKH = maKH;
        setTitle("Đăng Ký Trả Góp - Khách Hàng #" + maKH);
        setSize(600, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel("Đăng Ký Phiếu Trả Góp", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets.bottom = 20;
        panel.add(lblTitle, gbc);
        gbc.insets.bottom = 8;

        txtMaHDX = new JTextField(15);
        txtSoThang = new JTextField(15);
        txtLaiSuat = new JTextField(15);
        lblTienGoc = new JLabel(currencyFormatter.format(0));
        lblTienTraHangThang = new JLabel(currencyFormatter.format(0));

        styleTextField(txtMaHDX);
        styleTextField(txtSoThang);
        styleTextField(txtLaiSuat);
        styleResultLabel(lblTienGoc, PRIMARY_COLOR, Font.BOLD);
        styleResultLabel(lblTienTraHangThang, ERROR_COLOR, Font.BOLD);

        int row = 1;
        addField(panel, gbc, "Mã hóa đơn xuất*:", txtMaHDX, row++);
        addField(panel, gbc, "Số tháng trả góp*:", txtSoThang, row++);
        addField(panel, gbc, "Lãi suất (%/năm)*:", txtLaiSuat, row++);
        addField(panel, gbc, "Tiền gốc:", lblTienGoc, row++);
        addField(panel, gbc, "Trả hàng tháng (dự kiến):", lblTienTraHangThang, row++);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        panel.add(createButtonPanel(), gbc);

        addEventListeners();
        setContentPane(panel);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.3; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7; gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(field, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);

        btnDangKy = createStyledButton("Đăng Ký Trả Góp", SUCCESS_COLOR);
        JButton btnBack = createStyledButton("Trở Về", SECONDARY_COLOR);

        buttonPanel.add(btnDangKy);
        buttonPanel.add(btnBack);

        btnBack.addActionListener(e -> dispose());
        return buttonPanel;
    }

    private void addEventListeners() {
        // *** SỬA LỖI 2: Triển khai DocumentListener một cách đầy đủ ***
        DocumentListener recalculateListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateMonthlyPaymentDisplay();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateMonthlyPaymentDisplay();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateMonthlyPaymentDisplay();
            }
        };
        
        txtMaHDX.getDocument().addDocumentListener(recalculateListener);
        txtSoThang.getDocument().addDocumentListener(recalculateListener);
        txtLaiSuat.getDocument().addDocumentListener(recalculateListener);

        btnDangKy.addActionListener(e -> xuLyDangKy());
    }
    
    // --- Các phương thức Styling giữ nguyên ---
    private void styleTextField(JTextField tf) {
        tf.setFont(FONT_INPUT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(7, 10, 7, 10)
        ));
    }
    private void styleResultLabel(JLabel label, Color color, int style) {
        label.setFont(FONT_INPUT.deriveFont(style));
        label.setForeground(color);
    }
    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        return btn;
    }

    // --- Logic Methods ---

    private void updateMonthlyPaymentDisplay() {
        String maHDXStr = txtMaHDX.getText().trim();
        String soThangStr = txtSoThang.getText().trim();
        String laiSuatStr = txtLaiSuat.getText().trim();

        // Kiểm tra và fetch hóa đơn
        try {
            int maHDX = Integer.parseInt(maHDXStr);
            // Chỉ fetch lại nếu MaHDX thay đổi
            if (currentInvoice == null || currentInvoice.getMaHDX() != maHDX) {
                fetchInvoiceDetails(maHDX);
                return; // Chờ fetch xong, fetch sẽ gọi lại hàm này
            }
        } catch (NumberFormatException e) {
            currentInvoice = null;
            styleResultLabel(lblTienGoc, ERROR_COLOR, Font.ITALIC);
            lblTienGoc.setText(maHDXStr.isEmpty() ? currencyFormatter.format(0) : "Mã HĐ phải là số");
            lblTienTraHangThang.setText(currencyFormatter.format(0));
            return;
        }

        // Nếu HĐ không hợp lệ, dừng lại
        if (currentInvoice == null) {
            return;
        }
        
        // Cập nhật tiền gốc
        lblTienGoc.setText(currencyFormatter.format(currentInvoice.getThanhTien()));

        // Kiểm tra các ô còn lại để tính toán
        if (soThangStr.isEmpty() || laiSuatStr.isEmpty()) {
            lblTienTraHangThang.setText(currencyFormatter.format(0));
            return;
        }

        try {
            int soThang = Integer.parseInt(soThangStr);
            BigDecimal laiSuatNam = new BigDecimal(laiSuatStr);

            if (soThang <= 0 || laiSuatNam.compareTo(BigDecimal.ZERO) < 0) {
                lblTienTraHangThang.setText(currencyFormatter.format(0));
                return;
            }

            BigDecimal tienGoc = currentInvoice.getThanhTien();
            BigDecimal tienTraHangThang = TraGopQuery.tinhTraGopHangThang(tienGoc, laiSuatNam, soThang);
            
            if(tienTraHangThang != null) {
                lblTienTraHangThang.setText(currencyFormatter.format(tienTraHangThang));
            } else {
                lblTienTraHangThang.setText("Lỗi tính toán");
            }
        } catch (NumberFormatException e) {
            lblTienTraHangThang.setText("Lỗi định dạng số");
        }
    }

    private void fetchInvoiceDetails(int maHDX) {
        // Tạm thời vô hiệu hóa invoice hiện tại
        currentInvoice = null; 
        
        new SwingWorker<HoaDonXuat, Void>() {
            @Override
            protected HoaDonXuat doInBackground() {
                return HoaDonXuatQuery.getHoaDonById(maHDX);
            }

            @Override
            protected void done() {
                try {
                    HoaDonXuat hdx = get();
                    if (hdx != null && hdx.getMaKH() == TraGopKHView.this.maKH) {
                        currentInvoice = hdx;
                        styleResultLabel(lblTienGoc, PRIMARY_COLOR, Font.BOLD);
                    } else {
                        styleResultLabel(lblTienGoc, ERROR_COLOR, Font.ITALIC);
                        lblTienGoc.setText("HĐ không hợp lệ");
                    }
                } catch (Exception e) {
                    styleResultLabel(lblTienGoc, ERROR_COLOR, Font.ITALIC);
                    lblTienGoc.setText("Lỗi tải HĐ");
                    e.printStackTrace();
                } finally {
                    // Gọi lại update display để tính toán với invoice mới (hoặc null)
                    updateMonthlyPaymentDisplay();
                }
            }
        }.execute();
    }

    private void xuLyDangKy() {
        if (currentInvoice == null) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không hợp lệ hoặc không thuộc về bạn.", "Lỗi Hóa Đơn", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String soThangStr = txtSoThang.getText().trim();
        String laiSuatStr = txtLaiSuat.getText().trim();
        if (soThangStr.isEmpty() || laiSuatStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Số tháng và Lãi suất.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int soThang;
        BigDecimal laiSuat;
        try {
            soThang = Integer.parseInt(soThangStr);
            laiSuat = new BigDecimal(laiSuatStr);
            if (soThang <= 0 || laiSuat.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(this, "Số tháng và Lãi suất phải là số hợp lệ, lớn hơn hoặc bằng 0.", "Lỗi Định Dạng Số", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        btnDangKy.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                BigDecimal tienGoc = currentInvoice.getThanhTien();
                if (tienGoc == null || tienGoc.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new Exception("Hóa đơn này chưa có thành tiền, không thể trả góp.");
                }
                
                TraGop tg = new TraGop(currentInvoice.getMaHDX(), soThang, laiSuat, tienGoc, LocalDate.now(), false);
                return TraGopQuery.insertPhieuTraGopAndGetId(tg);
            }

            @Override
            protected void done() {
                try {
                    Integer generatedId = get();
                    if (generatedId != null) {
                        JOptionPane.showMessageDialog(TraGopKHView.this, "Đăng ký trả góp thành công!\nMã phiếu trả góp của bạn là: " + generatedId, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                        resetForm();
                    } else {
                        JOptionPane.showMessageDialog(TraGopKHView.this, "Đăng ký thất bại!\nCó thể hóa đơn này đã được đăng ký trả góp.", "Đăng Ký Thất Bại", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    Throwable cause = (e instanceof ExecutionException) ? e.getCause() : e;
                    JOptionPane.showMessageDialog(TraGopKHView.this, "Lỗi khi xử lý đăng ký: " + cause.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
                    cause.printStackTrace();
                } finally {
                    btnDangKy.setEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }
    
    private void resetForm() {
        txtMaHDX.setText("");
        txtSoThang.setText("");
        txtLaiSuat.setText("");
    }
}