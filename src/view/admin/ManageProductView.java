package view.admin;

import controller.admin.ManageProduct;
import model.SanPham;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
public class ManageProductView extends JPanel {
    private static final long serialVersionUID = 1L;

    // --- UI Constants ---
    private static final Color BG_COLOR = new Color(248, 249, 250);
    private static final Color TITLE_COLOR = new Color(0, 102, 204);
    private static final Color TABLE_HEADER_BG = new Color(52, 73, 94);
    private static final Color ALT_ROW_COLOR = new Color(242, 242, 242);
    private static final Color PRIMARY_ACTION_COLOR = new Color(0, 123, 255);
    private static final Color SECONDARY_ACTION_COLOR = new Color(108, 117, 125);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 14);

    // --- Components ---
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfSearch;
    private JComboBox<String> cbHangSX;
    
    // --- Controller ---
    private final ManageProduct controller;

    public ManageProductView() {
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setBackground(BG_COLOR);

        controller = new ManageProduct(this);
        initUI();
        controller.loadProducts();
        loadHangSXComboBox();
    }

    private void initUI() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản Lý Sản Phẩm", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TITLE_COLOR);
        headerPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel topActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topActionPanel.setOpaque(false);
        
        tfSearch = new JTextField(25);
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton btnTim = createStyledButton("Tìm", PRIMARY_ACTION_COLOR);
        cbHangSX = new JComboBox<>();
        cbHangSX.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbHangSX.setPreferredSize(new Dimension(180, tfSearch.getPreferredSize().height + 10));

        JButton btnLoc = createStyledButton("Lọc", PRIMARY_ACTION_COLOR);
        JButton btnTaiLai = createStyledButton("Tải lại", SECONDARY_ACTION_COLOR);
        
        topActionPanel.add(new JLabel("Tìm theo tên:"));
        topActionPanel.add(tfSearch);
        topActionPanel.add(btnTim);
        topActionPanel.add(Box.createHorizontalStrut(20));
        topActionPanel.add(new JLabel("Lọc theo hãng:"));
        topActionPanel.add(cbHangSX);
        topActionPanel.add(btnLoc);
        topActionPanel.add(btnTaiLai);
        headerPanel.add(topActionPanel, BorderLayout.CENTER);
        
        // Event Listeners
        btnTim.addActionListener(e -> controller.searchByName(tfSearch.getText()));
        tfSearch.addActionListener(e -> btnTim.doClick());
        btnLoc.addActionListener(e -> controller.filterByBrand((String) cbHangSX.getSelectedItem()));
        btnTaiLai.addActionListener(e -> {
            controller.loadProducts();
            tfSearch.setText("");
            if (cbHangSX.getItemCount() > 0) cbHangSX.setSelectedIndex(0);
        });

        return headerPanel;
    }
    
    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(new String[]{"Mã SP", "Tên Sản Phẩm", "Màu Sắc", "Giá Niêm Yết", "Nước SX", "Hãng SX", "Tồn Kho"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 6) return Integer.class;
                if (columnIndex == 3) return BigDecimal.class;
                return String.class;
            }
        };
        
        table = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ALT_ROW_COLOR);
                }
                return c;
            }
        };
        styleTable(table);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224)));
        return scrollPane;
    }
    
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setOpaque(false);
        
        JButton btnThem = createStyledButton("Thêm Sản Phẩm", SUCCESS_COLOR);
        JButton btnSua = createStyledButton("Sửa Sản Phẩm", WARNING_COLOR);
        JButton btnXoa = createStyledButton("Xóa Sản Phẩm", DANGER_COLOR);
        
        actionPanel.add(btnThem);
        actionPanel.add(btnSua);
        actionPanel.add(btnXoa);

        btnThem.addActionListener(e -> showProductForm(null));
        btnSua.addActionListener(e -> showEditForm());
        btnXoa.addActionListener(e -> deleteSelectedProduct());
        
        return actionPanel;
    }
    
    private void styleTable(JTable tbl) {
        tbl.setFillsViewportHeight(true);
        tbl.setFont(FONT_TABLE);
        tbl.getTableHeader().setFont(FONT_TABLE_HEADER);
        tbl.getTableHeader().setBackground(TABLE_HEADER_BG);
        tbl.getTableHeader().setForeground(Color.WHITE);
        tbl.setRowHeight(32);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setShowGrid(true);
        tbl.setGridColor(new Color(224, 224, 224));
        tbl.setAutoCreateRowSorter(true);

        // TỐI ƯU: Thiết lập renderer để căn chỉnh nội dung
        CenteredRenderer centeredRenderer = new CenteredRenderer();
        tbl.setDefaultRenderer(String.class, centeredRenderer);
        tbl.setDefaultRenderer(Integer.class, centeredRenderer);
        tbl.setDefaultRenderer(BigDecimal.class, new CurrencyRenderer());

        TableColumnModel columnModel = tbl.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(80);
        columnModel.getColumn(1).setPreferredWidth(300);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(150);
        columnModel.getColumn(4).setPreferredWidth(120);
        columnModel.getColumn(5).setPreferredWidth(120);
        columnModel.getColumn(6).setPreferredWidth(80);
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        if (backgroundColor.equals(WARNING_COLOR)) btn.setForeground(Color.BLACK);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(backgroundColor.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(backgroundColor); }
        });
        return btn;
    }

    public void updateTable(List<SanPham> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (SanPham sp : list) {
                tableModel.addRow(new Object[]{
                        sp.getMaSP(), sp.getTenSP(), sp.getMau(), sp.getGiaNiemYet(),
                        sp.getNuocSX(), sp.getHangSX(), sp.getSoLuongTon()
                });
            }
        }
    }

    public void loadHangSXComboBox() {
        cbHangSX.removeAllItems();
        cbHangSX.addItem("Tất cả");
        List<String> hangSXList = controller.getAllBrands();
        if (hangSXList != null) {
            hangSXList.forEach(cbHangSX::addItem);
        }
    }
    
    private void showEditForm() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Vui lòng chọn một sản phẩm từ bảng để sửa.", false);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int maSP = (int) tableModel.getValueAt(modelRow, 0);
        SanPham spToEdit = controller.getProductById(maSP);
        
        if (spToEdit == null) {
            showMessage("Không tìm thấy thông tin sản phẩm để sửa.", false);
            return;
        }
        showProductForm(spToEdit);
    }
    
    private void showProductForm(SanPham sp) {
        String title = (sp == null) ? "Thêm Sản Phẩm Mới" : "Sửa Thông Tin Sản Phẩm (Mã: " + sp.getMaSP() + ")";
        
        // --- Tạo các components cho form một cách an toàn ---
        JTextField tfTen = new JTextField(sp != null ? sp.getTenSP() : "", 20);
        JTextField tfMau = new JTextField(sp != null ? sp.getMau() : "", 20);
        JTextField tfNuocSX = new JTextField(sp != null ? sp.getNuocSX() : "", 20);
        JTextField tfHangSX = new JTextField(sp != null ? sp.getHangSX() : "", 20);
        
        BigDecimal initialValue = (sp != null && sp.getGiaNiemYet() != null) ? sp.getGiaNiemYet() : BigDecimal.ZERO;
        SpinnerNumberModel giaModel = new SpinnerNumberModel(initialValue, BigDecimal.ZERO, new BigDecimal("9999999999"), new BigDecimal("100000"));
        JSpinner spinnerDonGia = new JSpinner(giaModel);
        JSpinner.NumberEditor editorDonGia = new JSpinner.NumberEditor(spinnerDonGia, "#,##0");
        spinnerDonGia.setEditor(editorDonGia);

        // --- Xây dựng panel với GridBagLayout để căn chỉnh đẹp ---
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(createLabel("Tên SP*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; panel.add(tfTen, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; panel.add(createLabel("Màu:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; panel.add(tfMau, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; panel.add(createLabel("Giá Niêm Yết*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; panel.add(spinnerDonGia, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; panel.add(createLabel("Nước SX:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; panel.add(tfNuocSX, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; panel.add(createLabel("Hãng SX:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; panel.add(tfHangSX, gbc);

        // --- Hiển thị dialog và xử lý kết quả ---
        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            BigDecimal giaNiemYet = (BigDecimal) spinnerDonGia.getValue();
            if (sp == null) { // Chế độ Thêm
                controller.addProduct(tfTen.getText().trim(), tfMau.getText().trim(), giaNiemYet, tfNuocSX.getText().trim(), tfHangSX.getText().trim());
            } else { // Chế độ Sửa
                controller.updateProduct(sp.getMaSP(), tfTen.getText().trim(), tfMau.getText().trim(), giaNiemYet, tfNuocSX.getText().trim(), tfHangSX.getText().trim());
            }
        }
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    private void deleteSelectedProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Vui lòng chọn một sản phẩm từ bảng để xóa.", false);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int maSP = (int) tableModel.getValueAt(modelRow, 0);
        String tenSP = (String) tableModel.getValueAt(modelRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sản phẩm:\n'" + tenSP + "' (Mã: " + maSP + ") không?",
                "Xác Nhận Xóa Sản Phẩm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteProduct(maSP);
        }
    }
    
    public void showMessage(String message, boolean isSuccess) {
        JOptionPane.showMessageDialog(this, message, isSuccess ? "Thành Công" : "Lỗi",
                isSuccess ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
    
    // --- Custom Table Cell Renderers ---

    private static class CenteredRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        public CenteredRenderer() { setHorizontalAlignment(SwingConstants.CENTER); }
    }

    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final NumberFormat FORMAT = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        public CurrencyRenderer() { setHorizontalAlignment(SwingConstants.RIGHT); }
        @Override
        public void setValue(Object value) {
            setText((value == null) ? FORMAT.format(0) : FORMAT.format(value));
        }
    }
}