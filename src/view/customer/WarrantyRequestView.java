package view.customer;

import controller.customer.WarrantyRequest;
import controller.customer.WarrantyRequestException;
import model.ChiTietDonHang;
import query.SPCuTheQuery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Cửa sổ giao diện cho phép khách hàng tạo yêu cầu bảo hành cho một sản phẩm đã mua.
 */
public class WarrantyRequestView extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- Hằng số UI ---
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color CANCEL_COLOR = new Color(108, 117, 125);
    private static final int FIELD_VERTICAL_PADDING = 8; // Đệm dọc để tăng chiều cao ô nhập

    // --- Components & Data ---
    private final int maKH;
    private JTextField txtMaHDX;
    private JComboBox<ChiTietDonHang> cbSanPham;
    private JButton btnTimSanPham;
    private JButton btnGuiYeuCau;

    public WarrantyRequestView(int maKH) {
        this.maKH = maKH;
        setTitle("Yêu Cầu Bảo Hành Sản Phẩm - KH: " + maKH);
        setSize(650, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel("Tạo Yêu Cầu Bảo Hành", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Dòng 1: Nhập Mã Hóa Đơn ---
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0.3; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblMaHDX = new JLabel("1. Nhập Mã Hóa Đơn*:");
        lblMaHDX.setFont(LABEL_FONT);
        formPanel.add(lblMaHDX, gbc);
        
        txtMaHDX = new JTextField(20);
        txtMaHDX.setFont(VALUE_FONT);
        gbc.gridx = 1; gbc.weightx = 0.5; gbc.anchor = GridBagConstraints.LINE_START;
        // SỬA: Thêm đệm dọc để tăng chiều cao
        gbc.ipady = FIELD_VERTICAL_PADDING; 
        formPanel.add(txtMaHDX, gbc);
        gbc.ipady = 0; // Reset lại cho các component khác

        btnTimSanPham = createStyledButton("Tìm Sản Phẩm", PRIMARY_COLOR, Color.WHITE);
        gbc.gridx = 2; gbc.weightx = 0.2;
        formPanel.add(btnTimSanPham, gbc);

        // --- Dòng 2: Chọn Sản Phẩm ---
        gbc.gridy = 1;
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblChonSP = new JLabel("2. Chọn Sản Phẩm*:");
        lblChonSP.setFont(LABEL_FONT);
        formPanel.add(lblChonSP, gbc);
        
        cbSanPham = new JComboBox<>();
        cbSanPham.setFont(VALUE_FONT);
        cbSanPham.setEnabled(false); 
        cbSanPham.setRenderer(new ChiTietDonHangRenderer()); // Dùng một lớp Renderer riêng cho gọn
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.LINE_START;
        gbc.ipady = FIELD_VERTICAL_PADDING; // Áp dụng cả cho ComboBox
        formPanel.add(cbSanPham, gbc);
        gbc.ipady = 0;

        // Gán sự kiện
        btnTimSanPham.addActionListener(e -> timSanPham());

        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnGuiYeuCau = createStyledButton("Gửi Yêu Cầu", SUCCESS_COLOR, Color.WHITE);
        JButton btnHuy = createStyledButton("Hủy", CANCEL_COLOR, Color.WHITE);
        btnGuiYeuCau.setEnabled(false);
        
        buttonPanel.add(btnGuiYeuCau);
        buttonPanel.add(btnHuy);
        
        // Gán sự kiện
        btnGuiYeuCau.addActionListener(e -> guiYeuCau());
        btnHuy.addActionListener(e -> dispose());
        
        return buttonPanel;
    }

    /**
     * TỐI ƯU: Tìm sản phẩm trong một luồng nền để không treo giao diện.
     */
    private void timSanPham() {
        String maHDXStr = txtMaHDX.getText().trim();
        if (maHDXStr.isEmpty()) {
            showError("Vui lòng nhập mã hóa đơn.", txtMaHDX);
            return;
        }

        try {
            final int maHDX = Integer.parseInt(maHDXStr);
            btnTimSanPham.setEnabled(false); // Vô hiệu hóa nút trong khi tìm
            cbSanPham.removeAllItems();
            cbSanPham.setEnabled(false);
            btnGuiYeuCau.setEnabled(false);

            SwingWorker<List<ChiTietDonHang>, Void> worker = new SwingWorker<>() {
                @Override
                protected List<ChiTietDonHang> doInBackground() throws Exception {
                    return SPCuTheQuery.getChiTietDonHangByHDXAndKH(maHDX, maKH);
                }

                @Override
                protected void done() {
                    try {
                        List<ChiTietDonHang> sanPhamList = get();
                        if (sanPhamList.isEmpty()) {
                            showError("Không tìm thấy sản phẩm nào cho hóa đơn này hoặc hóa đơn không thuộc về bạn.", txtMaHDX);
                        } else {
                            for (ChiTietDonHang sp : sanPhamList) {
                                cbSanPham.addItem(sp);
                            }
                            cbSanPham.setEnabled(true);
                            btnGuiYeuCau.setEnabled(true);
                            JOptionPane.showMessageDialog(WarrantyRequestView.this, "Đã tìm thấy " + sanPhamList.size() + " sản phẩm. Vui lòng chọn sản phẩm cần bảo hành.", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception e) {
                        showError("Lỗi khi tìm kiếm sản phẩm: " + e.getMessage(), txtMaHDX);
                        e.printStackTrace();
                    } finally {
                        btnTimSanPham.setEnabled(true); // Kích hoạt lại nút sau khi xong
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            showError("Mã hóa đơn phải là một số nguyên.", txtMaHDX);
        }
    }

    private void guiYeuCau() {
        Object selectedItem = cbSanPham.getSelectedItem();
        if (!(selectedItem instanceof ChiTietDonHang)) {
            showError("Vui lòng chọn một sản phẩm hợp lệ từ danh sách.", cbSanPham);
            return;
        }
        
        ChiTietDonHang sanPhamDuocChon = (ChiTietDonHang) selectedItem;
        String maSPCuTheStr = sanPhamDuocChon.getMaSPCuThe();
        int maHDX = Integer.parseInt(txtMaHDX.getText().trim());

        try {
            WarrantyRequest controller = new WarrantyRequest();
            controller.createWarrantyRequest(maSPCuTheStr, maHDX, this.maKH);

            JOptionPane.showMessageDialog(this,
                    "Gửi yêu cầu bảo hành thành công!\nNhân viên sẽ sớm liên hệ với bạn.",
                    "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (WarrantyRequestException ex) {
            showError(ex.getMessage(), null);
        } catch (Exception ex) {
            showError("Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.", null);
            ex.printStackTrace();
        }
    }
    
    private void showError(String message, Component componentToFocus) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
        if (componentToFocus != null) {
            SwingUtilities.invokeLater(componentToFocus::requestFocusInWindow);
        }
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color foregroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(foregroundColor);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        return btn;
    }
    
    /**
     * Lớp Renderer tùy chỉnh để hiển thị thông tin ChiTietDonHang trong JComboBox.
     */
    private static class ChiTietDonHangRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ChiTietDonHang) {
                ChiTietDonHang item = (ChiTietDonHang) value;
                setText(String.format("%s (%s)", item.getTenSP(), item.getMaSPCuThe()));
            } else if (value == null && index == -1) {
                // Hiển thị gợi ý khi chưa có item nào được chọn
                setText(" -- Chọn sản phẩm -- ");
            }
            return this;
        }
    }
}