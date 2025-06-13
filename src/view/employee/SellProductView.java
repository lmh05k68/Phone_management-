package view.employee;

import controller.employee.SellProduct;
import model.ChiTietDonHang;
import model.KhachHang;
import query.KhachHangQuery;
import query.SPCuTheQuery;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
public class SellProductView extends JFrame {
    private static final long serialVersionUID = 1L;

    // --- CÁC HẰNG SỐ ĐỊNH DẠNG GIAO DIỆN ---
    private static final Color COLOR_PRIMARY = new Color(0, 123, 255);
    private static final Color COLOR_SUCCESS = new Color(40, 167, 69);
    private static final Color COLOR_DANGER = new Color(220, 53, 69);
    private static final Color COLOR_WARNING = new Color(255, 193, 7);
    private static final Color COLOR_TEXT_ON_DARK = Color.WHITE;

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_TOTAL = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_DISCOUNT = new Font("Segoe UI", Font.ITALIC, 14);
    
    private static final BigDecimal VAT_RATE = new BigDecimal("0.10"); // 10% VAT

    // --- THÀNH VIÊN LỚP ---
    private final int maNV;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
    private final SellProduct sellProductController = new SellProduct();

    // --- CÁC THÀNH PHẦN GIAO DIỆN ---
    private JTextField tfMaKH, tfTenKH, tfSdtKH;
    private JTable tableSanPham, tableGioHang;
    private DefaultTableModel modelSanPham, modelGioHang;
    private JLabel lblTongTien, lblGiamGia, lblDiscountInfo;
    private JButton btnToggleSuDungDiem, btnThanhToan;

    // --- TRẠNG THÁI ---
    private KhachHang khachHangHienTai = null;
    private boolean suDungDiemIsActive = false;

    public SellProductView(int maNV) {
        this.maNV = maNV;
        setTitle("Bán hàng - Nhân viên #" + maNV);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        loadAvailableProducts();
    }

    //region UI Initialization & Styling

    private void initUI() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setBackground(Color.WHITE);

        contentPane.add(createTopPanel(), BorderLayout.NORTH);
        contentPane.add(createCenterPanel(), BorderLayout.CENTER);
        contentPane.add(createBottomPanel(), BorderLayout.SOUTH);
        addActionListeners();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(createCustomerInfoPanel());
        topPanel.add(createDiscountPanel());
        return topPanel;
    }

    private JPanel createCustomerInfoPanel() {
        JPanel panelKH = new JPanel(new GridBagLayout());
        panelKH.setBorder(BorderFactory.createTitledBorder(null, "Thông Tin Khách Hàng", TitledBorder.LEADING, TitledBorder.TOP, FONT_TITLE, COLOR_PRIMARY));
        panelKH.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; panelKH.add(new JLabel("Mã KH:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; tfMaKH = new JTextField(15); panelKH.add(tfMaKH, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; JButton btnTimKH = new JButton("Tìm"); styleButton(btnTimKH, COLOR_PRIMARY); panelKH.add(btnTimKH, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelKH.add(new JLabel("Tên KH:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; tfTenKH = new JTextField(); tfTenKH.setEditable(false); tfTenKH.setFont(new Font("Segoe UI", Font.BOLD, 12)); panelKH.add(tfTenKH, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panelKH.add(new JLabel("SĐT KH:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; tfSdtKH = new JTextField(); tfSdtKH.setEditable(false); panelKH.add(tfSdtKH, gbc);

        btnTimKH.addActionListener(e -> timKhachHang());
        tfMaKH.addActionListener(e -> timKhachHang());
        return panelKH;
    }

    private JPanel createDiscountPanel() {
        JPanel panelDiem = new JPanel(new BorderLayout(0, 10));
        panelDiem.setBorder(BorderFactory.createTitledBorder(null, "Ưu Đãi Điểm Thưởng", TitledBorder.LEADING, TitledBorder.TOP, FONT_TITLE, COLOR_SUCCESS));
        panelDiem.setOpaque(false);

        lblGiamGia = new JLabel("Nhập Mã KH để xem ưu đãi.", SwingConstants.CENTER);
        lblGiamGia.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        btnToggleSuDungDiem = new JButton();
        updateDiscountButtonState();

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        buttonContainer.setOpaque(false);
        buttonContainer.add(btnToggleSuDungDiem);

        panelDiem.add(lblGiamGia, BorderLayout.CENTER);
        panelDiem.add(buttonContainer, BorderLayout.SOUTH);
        return panelDiem;
    }

    private JSplitPane createCenterPanel() {
        String[] cols = {"Mã SP Cụ Thể (IMEI)", "Tên Sản Phẩm", "Màu Sắc", "Giá Bán"};
        
        // Tối ưu: Thêm getColumnClass để bảng có thể dùng đúng renderer và sắp xếp
        modelSanPham = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? BigDecimal.class : Object.class;
            }
        };
        tableSanPham = new JTable(modelSanPham);
        styleProductTable(tableSanPham);

        modelGioHang = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? BigDecimal.class : Object.class;
            }
        };
        tableGioHang = new JTable(modelGioHang);
        styleProductTable(tableGioHang);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tableSanPham),
                new JScrollPane(tableGioHang));
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        return splitPane;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton btnThem = new JButton();
        styleButton(btnThem, COLOR_SUCCESS, "/icons/arrow-right.png");
        btnThem.setToolTipText("Thêm vào giỏ hàng");

        JButton btnXoa = new JButton();
        styleButton(btnXoa, COLOR_WARNING, "/icons/arrow-left.png");
        btnXoa.setToolTipText("Xóa khỏi giỏ hàng");

        JPanel controlPanel = new JPanel();
        controlPanel.setOpaque(false);
        controlPanel.add(btnThem);
        controlPanel.add(btnXoa);

        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        paymentPanel.setOpaque(false);
        JLabel lblTotalTitle = new JLabel("Tổng thanh toán:");
        lblTotalTitle.setFont(FONT_TOTAL);

        lblTongTien = new JLabel(currencyFormatter.format(0));
        lblTongTien.setFont(FONT_TOTAL);

        lblDiscountInfo = new JLabel();
        lblDiscountInfo.setFont(FONT_DISCOUNT);
        lblDiscountInfo.setForeground(COLOR_DANGER);

        btnThanhToan = new JButton("TẠO HÓA ĐƠN");
        styleButton(btnThanhToan, COLOR_SUCCESS);

        paymentPanel.add(lblTotalTitle);
        paymentPanel.add(lblDiscountInfo);
        paymentPanel.add(lblTongTien);
        paymentPanel.add(btnThanhToan);

        bottomPanel.add(controlPanel, BorderLayout.CENTER);
        bottomPanel.add(paymentPanel, BorderLayout.EAST);

        btnThem.addActionListener(e -> themVaoGioHang());
        btnXoa.addActionListener(e -> xoaKhoiGioHang());
        return bottomPanel;
    }
    
    /**
     * Áp dụng style chung cho một button.
     */
    private void styleButton(JButton button, Color backgroundColor) {
        styleButton(button, backgroundColor, null);
    }
    
    /**
     * Áp dụng style cho button, có thể kèm icon.
     */
    private void styleButton(JButton button, Color backgroundColor, String iconPath) {
        button.setBackground(backgroundColor);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        // Mặc định chữ trắng trên nền màu
        button.setForeground(COLOR_TEXT_ON_DARK);
    
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(img));
                button.setText(""); 
                button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            } catch (Exception e) {
                System.err.println("Không tìm thấy icon: " + iconPath);
                if (iconPath.contains("right")) button.setText("Thêm");
                if (iconPath.contains("left")) button.setText("Xóa");
            }
        }
    }
    
    /**
     * Áp dụng style và renderer cho các bảng sản phẩm và giỏ hàng.
     */
    private void styleProductTable(JTable table) {
        table.setFont(FONT_TABLE);
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.setRowHeight(28);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateRowSorter(true);

        // Áp dụng renderer mặc định để căn giữa cho các cột văn bản
        table.setDefaultRenderer(Object.class, new CenteredRenderer());
        // Áp dụng renderer riêng cho cột tiền tệ để căn phải và định dạng
        table.setDefaultRenderer(BigDecimal.class, new CurrencyRenderer());
    }

    //endregion

    //region Action Listeners & Logic

    private void addActionListeners() {
        btnToggleSuDungDiem.addActionListener(e -> toggleSuDungDiem());
        btnThanhToan.addActionListener(e -> thanhToan());
    }
    
    private void moveSelectedRows(JTable sourceTable, DefaultTableModel sourceModel, DefaultTableModel destModel) {
        int[] selectedRows = sourceTable.getSelectedRows();
        if (selectedRows.length == 0) return;

        // Chuyển từ dưới lên để không bị lỗi index
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int modelRow = sourceTable.convertRowIndexToModel(selectedRows[i]);
            Object[] rowData = new Object[sourceModel.getColumnCount()];
            for (int col = 0; col < sourceModel.getColumnCount(); col++) {
                rowData[col] = sourceModel.getValueAt(modelRow, col);
            }
            destModel.addRow(rowData);
            sourceModel.removeRow(modelRow);
        }
        updateTotalAmountDisplay();
    }

    private void loadAvailableProducts() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<List<ChiTietDonHang>, Void>() {
            @Override
            protected List<ChiTietDonHang> doInBackground() { return SPCuTheQuery.getProductsForSale(); }

            @Override
            protected void done() {
                try {
                    List<ChiTietDonHang> dsSanPham = get();
                    modelSanPham.setRowCount(0);
                    for (ChiTietDonHang sp : dsSanPham) {
                        modelSanPham.addRow(new Object[]{sp.getMaSPCuThe(), sp.getTenSP(), sp.getMau(), sp.getGiaXuat()});
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(SellProductView.this, "Lỗi khi tải danh sách sản phẩm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }

    private void timKhachHang() {
        String input = tfMaKH.getText().trim();
        if (input.isEmpty()) {
            resetCustomerInfo();
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<KhachHang, Void>() {
            @Override
            protected KhachHang doInBackground() throws Exception {
                khachHangHienTai = null;
                suDungDiemIsActive = false;
                int maKH = Integer.parseInt(input);
                return KhachHangQuery.getKhachHangById(maKH);
            }
            @Override
            protected void done() {
                try {
                    khachHangHienTai = get();
                    if (khachHangHienTai != null) {
                        tfMaKH.setText(String.valueOf(khachHangHienTai.getMaKH()));
                        tfTenKH.setText(khachHangHienTai.getHoTen());
                        tfSdtKH.setText(khachHangHienTai.getSdtKH());
                        int diem = khachHangHienTai.getSoDiemTichLuy();
                        int phanTramGiam = Math.min(diem / 100, 20);
                        if (phanTramGiam > 0) {
                            lblGiamGia.setText(String.format("<html>Có thể giảm <font color='red'>%d%%</font> (Điểm: %d)</html>", phanTramGiam, diem));
                        } else {
                            lblGiamGia.setText(String.format("Không đủ điểm để giảm giá (Điểm: %d)", diem));
                        }
                    } else {
                        lblGiamGia.setText("Không tìm thấy khách hàng.");
                        resetCustomerInfoFields();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SellProductView.this, "Mã khách hàng phải là một số.", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
                    resetCustomerInfo();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SellProductView.this, "Lỗi khi tìm khách hàng.", "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    resetCustomerInfo();
                } finally {
                    updateDiscountButtonState();
                    updateTotalAmountDisplay();
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }

    private void resetCustomerInfo() {
        khachHangHienTai = null;
        suDungDiemIsActive = false;
        lblGiamGia.setText("Nhập Mã KH để xem ưu đãi.");
        resetCustomerInfoFields();
        updateDiscountButtonState();
        updateTotalAmountDisplay();
    }
    
    private void resetCustomerInfoFields() {
        tfTenKH.setText("");
        tfSdtKH.setText("");
    }
    
    private void updateDiscountButtonState() {
        boolean canUsePoints = khachHangHienTai != null && (khachHangHienTai.getSoDiemTichLuy() / 100) > 0;
        btnToggleSuDungDiem.setEnabled(canUsePoints);

        if (suDungDiemIsActive && canUsePoints) {
            btnToggleSuDungDiem.setText("HỦY SỬ DỤNG ĐIỂM");
            styleButton(btnToggleSuDungDiem, COLOR_DANGER);
        } else {
            btnToggleSuDungDiem.setText("Sử dụng điểm thưởng");
            styleButton(btnToggleSuDungDiem, COLOR_PRIMARY);
        }
        // SỬA ĐỔI: Luôn đảm bảo chữ màu trắng
        btnToggleSuDungDiem.setForeground(COLOR_TEXT_ON_DARK);
    }


    private void toggleSuDungDiem() {
        suDungDiemIsActive = !suDungDiemIsActive;
        updateDiscountButtonState();
        updateTotalAmountDisplay();
    }

    private void themVaoGioHang() {
        moveSelectedRows(tableSanPham, modelSanPham, modelGioHang);
    }

    private void xoaKhoiGioHang() {
        moveSelectedRows(tableGioHang, modelGioHang, modelSanPham);
    }

    private BigDecimal calculateSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            subtotal = subtotal.add((BigDecimal) modelGioHang.getValueAt(i, 3));
        }
        return subtotal;
    }

    private BigDecimal calculateDiscountAmount(BigDecimal subtotal) {
        if (suDungDiemIsActive && khachHangHienTai != null) {
            int diem = khachHangHienTai.getSoDiemTichLuy();
            int phanTramGiam = Math.min(diem / 100, 20);
            if (phanTramGiam > 0) {
                BigDecimal discountRate = new BigDecimal(phanTramGiam).divide(new BigDecimal(100));
                return subtotal.multiply(discountRate).setScale(0, RoundingMode.HALF_UP);
            }
        }
        return BigDecimal.ZERO;
    }

    private void updateTotalAmountDisplay() {
        BigDecimal subtotal = calculateSubtotal();
        BigDecimal discountAmount = calculateDiscountAmount(subtotal);
        BigDecimal amountAfterDiscount = subtotal.subtract(discountAmount);
        BigDecimal vatAmount = amountAfterDiscount.multiply(VAT_RATE);
        BigDecimal totalPayable = amountAfterDiscount.add(vatAmount);

        if(discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            lblDiscountInfo.setText(String.format("(Giảm: %s)", currencyFormatter.format(discountAmount)));
        } else {
            lblDiscountInfo.setText("");
        }
        lblTongTien.setText(currencyFormatter.format(totalPayable));
    }

    private void thanhToan() {
        if (modelGioHang.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống! Vui lòng thêm sản phẩm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer maKH = (khachHangHienTai != null) ? khachHangHienTai.getMaKH() : null;
        List<String> dsMaSPCuThe = new ArrayList<>();
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            dsMaSPCuThe.add((String) modelGioHang.getValueAt(i, 0));
        }
        BigDecimal tongTienGoc = calculateSubtotal();

        btnThanhToan.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return sellProductController.banHang(maNV, maKH, dsMaSPCuThe, suDungDiemIsActive, tongTienGoc);
            }

            @Override
            protected void done() {
                try {
                    Integer maHDX = get();
                    if (maHDX != null) {
                        JOptionPane.showMessageDialog(SellProductView.this, "Thanh toán thành công!\nMã hóa đơn mới là: " + maHDX, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        resetForm();
                    } else {
                        loadAvailableProducts();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String message = "Đã xảy ra lỗi trong quá trình thanh toán.";
                    if (e.getCause() != null) {
                        message = e.getCause().getMessage();
                    }
                    JOptionPane.showMessageDialog(SellProductView.this, message, "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnThanhToan.setEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }

    private void resetForm() {
        modelGioHang.setRowCount(0);
        tfMaKH.setText("");
        resetCustomerInfo();
        loadAvailableProducts();
    }
    private static class CenteredRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }
    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof BigDecimal) {
                setText(FORMATTER.format(value));
            }
            setHorizontalAlignment(SwingConstants.RIGHT); // Tiền tệ luôn căn phải
            return this;
        }
    }
}