package view.employee;

import controller.employee.ImportProductController;
import model.NhaCungCap;
import model.SanPham;
import model.NhapHangItem;
import query.NhaCungCapQuery;
import query.SanPhamQuery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class ImportProductView extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(0, 123, 255);
    private static final Color SUCCESS_BUTTON_COLOR = new Color(40, 167, 69);
    private static final Color SECONDARY_BUTTON_COLOR = new Color(108, 117, 125);
    private static final Color HEADER_BG_COLOR = new Color(32, 136, 203);

    // --- Components & Data ---
    private JComboBox<NhaCungCap> cboNCC;
    private JComboBox<SanPham> cboSanPham;
    private JTextField txtSoLuong;
    private JTextField txtDonGia;
    private JTable table;
    private DefaultTableModel tableModel;
    private final List<NhapHangItem> dsNhap = new ArrayList<>();
    private final int maNV;

    public ImportProductView(int maNV) {
        this.maNV = maNV;
        setTitle("Quản Lý Nhập Hàng - Nhân viên: " + maNV);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        loadInitialData();
    }

    //region UI Initialization & Styling

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        JLabel lblTitle = new JLabel("Tạo Phiếu Nhập Hàng", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        // Add action listeners
        cboSanPham.addItemListener(this::onProductSelected);
        
        setContentPane(mainPanel);
    }

    private JPanel createCenterPanel() {
        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.setOpaque(false);
        centerContainer.add(createSupplierPanel(), BorderLayout.NORTH);
        centerContainer.add(createProductPanel(), BorderLayout.CENTER);
        return centerContainer;
    }

    private JPanel createSupplierPanel() {
        cboNCC = new JComboBox<>();
        cboNCC.setRenderer(new NhaCungCapRenderer());
        cboNCC.setFont(INPUT_FONT);

        JPanel topInputPanel = new JPanel(new GridBagLayout());
        topInputPanel.setOpaque(false);
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.insets = new Insets(5, 5, 5, 5);
        gbcTop.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNCC = new JLabel("Nhà cung cấp*:");
        lblNCC.setFont(LABEL_FONT.deriveFont(Font.BOLD));
        gbcTop.gridx = 0; gbcTop.gridy = 0; gbcTop.weightx = 0.1;
        topInputPanel.add(lblNCC, gbcTop);

        gbcTop.gridx = 1; gbcTop.gridy = 0; gbcTop.weightx = 0.9;
        topInputPanel.add(cboNCC, gbcTop);
        return topInputPanel;
    }

    private JPanel createProductPanel() {
        JPanel productSelectionPanel = new JPanel(new BorderLayout(10, 10));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Thêm sản phẩm vào phiếu nhập");
        titledBorder.setTitleFont(LABEL_FONT.deriveFont(Font.BOLD));
        productSelectionPanel.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(5, 5, 5, 5)));
        productSelectionPanel.setOpaque(false);

        productSelectionPanel.add(createProductInputFields(), BorderLayout.NORTH);

        // --- Table setup ---
        tableModel = new DefaultTableModel(new String[]{"Mã SP", "Tên Sản Phẩm", "Số lượng", "Đơn giá nhập", "Thành tiền"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }

            // TỐI ƯU: Định nghĩa kiểu dữ liệu cho từng cột để bảng dùng đúng renderer
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: case 2: return Integer.class;
                    case 3: case 4: return BigDecimal.class;
                    default: return String.class;
                }
            }
        };
        table = new JTable(tableModel);
        styleTable(table);
        productSelectionPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        return productSelectionPanel;
    }

    private JPanel createProductInputFields() {
        cboSanPham = new JComboBox<>();
        cboSanPham.setRenderer(new SanPhamRenderer());
        cboSanPham.setFont(INPUT_FONT);

        JPanel productFieldsPanel = new JPanel(new GridBagLayout());
        productFieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; productFieldsPanel.add(new JLabel("Sản phẩm:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        productFieldsPanel.add(cboSanPham, gbc);

        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = 1; productFieldsPanel.add(new JLabel("Số lượng*:"), gbc);
        
        txtSoLuong = new JTextField(7);
        txtSoLuong.setFont(INPUT_FONT);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        productFieldsPanel.add(txtSoLuong, gbc);

        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.insets.left = 15;
        gbc.gridx = 2; gbc.gridy = 1; productFieldsPanel.add(new JLabel("Đơn giá nhập*:"), gbc);
        gbc.insets.left = 5;

        txtDonGia = new JTextField(10);
        txtDonGia.setFont(INPUT_FONT);
        gbc.gridx = 3; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        productFieldsPanel.add(txtDonGia, gbc);

        JButton btnThemSPVaoBang = createStyledButton("Thêm vào phiếu", PRIMARY_BUTTON_COLOR, 150, 35);
        btnThemSPVaoBang.addActionListener(e -> themSanPhamVaoPhieu());
        gbc.gridx = 4; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.1; gbc.anchor = GridBagConstraints.EAST;
        productFieldsPanel.add(btnThemSPVaoBang, gbc);

        return productFieldsPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomButtonPanel.setOpaque(false);
        JButton btnXacNhanNhapHang = createStyledButton("Xác nhận nhập hàng", SUCCESS_BUTTON_COLOR);
        btnXacNhanNhapHang.addActionListener(e -> xacNhanNhap());
        JButton btnBack = createStyledButton("Trở về", SECONDARY_BUTTON_COLOR);
        btnBack.addActionListener(e -> dispose());
        
        bottomButtonPanel.add(btnXacNhanNhapHang);
        bottomButtonPanel.add(btnBack);
        return bottomButtonPanel;
    }

    private void styleTable(JTable tbl) {
        tbl.setFillsViewportHeight(true);
        tbl.setFont(INPUT_FONT);
        tbl.setRowHeight(30);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.setAutoCreateRowSorter(true);

        tbl.getTableHeader().setFont(TABLE_HEADER_FONT);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.getTableHeader().setOpaque(false);
        tbl.getTableHeader().setBackground(HEADER_BG_COLOR);
        tbl.getTableHeader().setForeground(Color.WHITE);
        
        // TỐI ƯU: Áp dụng renderer để căn chỉnh tự động theo kiểu dữ liệu
        tbl.setDefaultRenderer(Object.class, new CenteredRenderer()); // Mặc định căn giữa cho String
        tbl.setDefaultRenderer(Integer.class, new CenteredRenderer()); // Căn giữa cho số lượng
        tbl.setDefaultRenderer(BigDecimal.class, new CurrencyRenderer()); // Căn phải và định dạng cho tiền

        TableColumnModel tcm = tbl.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(80);
        tcm.getColumn(1).setPreferredWidth(300);
        tcm.getColumn(2).setPreferredWidth(100);
        tcm.getColumn(3).setPreferredWidth(120);
        tcm.getColumn(4).setPreferredWidth(150);
    }
    
    private JButton createStyledButton(String text, Color color) {
        return createStyledButton(text, color, 220, 40);
    }

    private JButton createStyledButton(String text, Color color, int width, int height) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setFocusPainted(false);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, height));
        btn.setBorder(BorderFactory.createEmptyBorder());
        return btn;
    }

    //endregion

    //region Data Loading and Logic

    /**
     * Record để chứa dữ liệu ban đầu được tải từ CSDL.
     */
    private record InitialData(List<NhaCungCap> suppliers, List<SanPham> products) {}

    /**
     * Tải dữ liệu cho các JComboBox trong một luồng nền để không làm đơ UI.
     */
    private void loadInitialData() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        new SwingWorker<InitialData, Void>() {
            @Override
            protected InitialData doInBackground() throws Exception {
                List<NhaCungCap> dsNCC = NhaCungCapQuery.getAll();
                List<SanPham> dsSP = SanPhamQuery.getAll();
                return new InitialData(dsNCC, dsSP);
            }

            @Override
            protected void done() {
                try {
                    InitialData data = get();
                    cboNCC.removeAllItems();
                    cboNCC.addItem(new NhaCungCap(0, "-- Chọn nhà cung cấp --", "", ""));
                    for (NhaCungCap ncc : data.suppliers()) { cboNCC.addItem(ncc); }
                    
                    cboSanPham.removeAllItems();
                    cboSanPham.addItem(new SanPham(0, "-- Chọn sản phẩm --", "", null, "", ""));
                    for (SanPham sp : data.products()) { cboSanPham.addItem(sp); }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ImportProductView.this, "Lỗi tải dữ liệu ban đầu!", "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }
    
    /**
     * Xử lý sự kiện khi một sản phẩm được chọn từ JComboBox.
     * Tự động lấy giá nhập cuối cùng của sản phẩm đó.
     */
    private void onProductSelected(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Object item = e.getItem();
            if (item instanceof SanPham selectedProduct && selectedProduct.getMaSP() != 0) {
                new SwingWorker<BigDecimal, Void>() {
                    @Override
                    protected BigDecimal doInBackground() throws Exception {
                        return SanPhamQuery.getLastImportPrice(selectedProduct.getMaSP());
                    }

                    @Override
                    protected void done() {
                        try {
                            BigDecimal lastPrice = get();
                            txtDonGia.setText(lastPrice != null ? lastPrice.toPlainString() : "");
                            txtSoLuong.requestFocusInWindow(); // Tự động focus vào ô số lượng
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            txtDonGia.setText("");
                        }
                    }
                }.execute();
            } else {
                txtDonGia.setText("");
            }
        }
    }

    private void themSanPhamVaoPhieu() {
        SanPham spChon = (SanPham) cboSanPham.getSelectedItem();
        if (spChon == null || spChon.getMaSP() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm hợp lệ.", "Chưa chọn sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            if (soLuong <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là một số nguyên lớn hơn 0.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtSoLuong.requestFocus();
            return;
        }

        BigDecimal donGiaNhap;
        try {
            donGiaNhap = new BigDecimal(txtDonGia.getText().trim().replace(",", ""));
            if (donGiaNhap.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đơn giá nhập phải là một số hợp lệ và lớn hơn 0.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtDonGia.requestFocus();
            return;
        }

        // Kiểm tra sản phẩm đã tồn tại trong phiếu chưa
        if (dsNhap.stream().anyMatch(item -> item.getSanPham().getMaSP() == spChon.getMaSP())) {
            JOptionPane.showMessageDialog(this, "Sản phẩm '" + spChon.getTenSP() + "' đã có trong phiếu.", "Sản phẩm trùng lặp", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dsNhap.add(new NhapHangItem(spChon, soLuong, donGiaNhap));

        // TỐI ƯU: Thêm dữ liệu gốc vào model, renderer sẽ lo việc định dạng
        tableModel.addRow(new Object[]{
                spChon.getMaSP(),
                spChon.getTenSP() + " (" + spChon.getMau() + ")",
                soLuong,
                donGiaNhap,
                donGiaNhap.multiply(new BigDecimal(soLuong))
        });

        // Reset fields for next entry
        cboSanPham.setSelectedIndex(0);
        txtSoLuong.setText("");
        txtDonGia.setText("");
        cboSanPham.requestFocusInWindow();
    }

    private void xacNhanNhap() {
        if (dsNhap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm vào phiếu nhập.", "Phiếu nhập rỗng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhaCungCap nccChon = (NhaCungCap) cboNCC.getSelectedItem();
        if (nccChon == null || nccChon.getMaNCC() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp.", "Thiếu Nhà Cung Cấp", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận tạo phiếu nhập hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        ImportProductController controller = new ImportProductController();
        boolean success = controller.nhapHang(maNV, nccChon.getMaNCC(), dsNhap);

        if (success) {
            JOptionPane.showMessageDialog(this, "Nhập hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Nhập hàng thất bại. Giao dịch đã được hủy bỏ.", "Lỗi nhập hàng", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetForm() {
        dsNhap.clear();
        tableModel.setRowCount(0);
        cboNCC.setSelectedIndex(0);
        cboSanPham.setSelectedIndex(0);
        txtSoLuong.setText("");
        txtDonGia.setText("");
    }
    
    //endregion

    //region Custom Renderers
    
    private static class NhaCungCapRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof NhaCungCap ncc) {
                setText(ncc.getMaNCC() == 0 ? ncc.getTenNCC() : String.format("%d - %s", ncc.getMaNCC(), ncc.getTenNCC()));
            }
            return this;
        }
    }

    private static class SanPhamRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof SanPham sp) {
                setText(sp.getMaSP() == 0 ? sp.getTenSP() : String.format("%s (%s)", sp.getTenSP(), sp.getHangSX()));
            }
            return this;
        }
    }

    /**
     * Renderer để căn giữa nội dung trong ô (dùng cho các cột văn bản, số nguyên).
     */
    private static class CenteredRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        public CenteredRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }
    
    /**
     * Renderer để định dạng giá trị tiền tệ và căn phải.
     */
    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        public CurrencyRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }
        @Override
        public void setValue(Object value) {
            setText((value == null) ? "" : FORMATTER.format(value));
        }
    }
    
    //endregion
}