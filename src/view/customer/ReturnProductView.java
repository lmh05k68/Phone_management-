package view.customer;

import model.ChiTietDonHang;
import model.HoaDonXuat; 
import query.HoaDonXuatQuery;
import query.SPCuTheQuery;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReturnProductView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int maKH;
    private JComboBox<HoaDonXuat> cmbMaDonHang;
    private JComboBox<ChiTietDonHang> cmbMaSPCuThe;
    private JTextField txtLyDo;
    private JButton btnGuiYeuCau, btnTroVe;

    private final String ORDER_PLACEHOLDER = "--- Chọn đơn hàng ---";
    private final String PRODUCT_PLACEHOLDER = "--- Chọn sản phẩm ---";


    public ReturnProductView(int maKH) {
        this.maKH = maKH;
        setTitle("Yêu Cầu Đổi/Trả Sản Phẩm - KH: " + maKH);
        setSize(550, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        loadOrderComboBox();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel lblMaDonHang = new JLabel("Chọn Đơn Hàng*:");
        lblMaDonHang.setFont(labelFont);
        cmbMaDonHang = new JComboBox<>();
        cmbMaDonHang.setFont(textFont);

        JLabel lblMaSPCuThe = new JLabel("Chọn Sản Phẩm*:");
        lblMaSPCuThe.setFont(labelFont);
        cmbMaSPCuThe = new JComboBox<>();
        cmbMaSPCuThe.setFont(textFont);
        cmbMaSPCuThe.setEnabled(false);

        JLabel lblLyDo = new JLabel("Lý do đổi/trả*:");
        lblLyDo.setFont(labelFont);
        txtLyDo = new JTextField();
        txtLyDo.setFont(textFont);

        btnGuiYeuCau = new JButton("Gửi Yêu Cầu");
        styleButton(btnGuiYeuCau, new Color(40, 167, 69));

        btnTroVe = new JButton("Trở Về");
        styleButton(btnTroVe, new Color(108, 117, 125));

        // Sắp xếp các thành phần
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(lblMaDonHang, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(cmbMaDonHang, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblMaSPCuThe, gbc);
        gbc.gridx = 1;
        panel.add(cmbMaSPCuThe, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblLyDo, gbc);
        gbc.gridx = 1;
        panel.add(txtLyDo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(btnGuiYeuCau);
        buttonPanel.add(btnTroVe);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(20, 5, 5, 5);
        panel.add(buttonPanel, gbc);

        setContentPane(panel);

        cmbMaDonHang.addActionListener(e -> handleOrderSelection());
        btnTroVe.addActionListener(e -> dispose());
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }

    private void loadOrderComboBox() {
        cmbMaDonHang.removeAllItems();
        cmbMaDonHang.addItem(null); // Mục null cho placeholder
        cmbMaDonHang.setRenderer(new DefaultListCellRenderer() {
        	private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) setText(ORDER_PLACEHOLDER);
                return this;
            }
        });
        List<HoaDonXuat> orders = HoaDonXuatQuery.getHoaDonByKhachHang(this.maKH);
        if (orders.isEmpty()) {
            cmbMaDonHang.setEnabled(false);
            btnGuiYeuCau.setEnabled(false);
             cmbMaDonHang.setRenderer(new DefaultListCellRenderer() {
            	 private static final long serialVersionUID = 1L;
                 @Override
                 public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                     super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                     setText("Bạn chưa có đơn hàng nào");
                     return this;
                 }
            });
        } else {
            orders.forEach(cmbMaDonHang::addItem);
        }
    }

    private void handleOrderSelection() {
        HoaDonXuat selectedOrder = (HoaDonXuat) cmbMaDonHang.getSelectedItem();
        cmbMaSPCuThe.removeAllItems();
        cmbMaSPCuThe.addItem(null);
        cmbMaSPCuThe.setRenderer(new DefaultListCellRenderer() {
        	private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) setText(PRODUCT_PLACEHOLDER);
                return this;
            }
        });

        if (selectedOrder != null) {
            // Lấy mã đơn hàng từ đối tượng HoaDonXuat
            List<ChiTietDonHang> products = SPCuTheQuery.getChiTietDonHangByHDXAndKH(selectedOrder.getMaHDX(), this.maKH);
            products.forEach(cmbMaSPCuThe::addItem);
            cmbMaSPCuThe.setEnabled(!products.isEmpty());
        } else {
            cmbMaSPCuThe.setEnabled(false);
        }
    }

    // --- Getters được cập nhật để làm việc với các Model trực tiếp ---
    public Integer getMaDonHang() {
        HoaDonXuat selected = (HoaDonXuat) cmbMaDonHang.getSelectedItem();
        return (selected != null) ? selected.getMaHDX() : null;
    }

    public String getMaSPCuThe() {
        ChiTietDonHang selected = (ChiTietDonHang) cmbMaSPCuThe.getSelectedItem();
        return (selected != null) ? selected.getMaSPCuThe() : null;
    }

    public String getLyDo() { return txtLyDo.getText(); }
    public JButton getBtnGuiYeuCau() { return btnGuiYeuCau; }
    public int getMaKHFromView() { return this.maKH; }
}