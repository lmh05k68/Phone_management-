package view.admin;

import controller.admin.ManageProduct;
import model.SanPham;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ManageProductView extends JPanel {
    private static final long serialVersionUID = 1L;

    // --- Hằng số UI cho style nhất quán ---
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color TITLE_COLOR = new Color(0, 102, 204);
    private static final Color TABLE_HEADER_BG = new Color(32, 136, 203);
    private static final Color PRIMARY_ACTION_COLOR = new Color(0, 123, 255); // Màu cho Tìm, Lọc
    private static final Color SECONDARY_ACTION_COLOR = new Color(108, 117, 125); // Màu cho Tải lại
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);      // Màu cho Thêm
    private static final Color WARNING_COLOR = new Color(255, 193, 7);      // Màu cho Sửa
    private static final Color DANGER_COLOR = new Color(220, 53, 69);       // Màu cho Xóa

    // --- Components ---
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfSearch;
    private JComboBox<String> cbHangSX;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // --- Controller ---
    private final ManageProduct controller;

    public ManageProductView() {
        // Thiết lập layout và border cho JPanel này
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setBackground(BG_COLOR);

        controller = new ManageProduct(this);
        initUI();
        controller.loadProducts();
        loadHangSXComboBox();
    }

    private void initUI() {
        // --- Panel Header (chứa tiêu đề và các hành động trên cùng) ---
        add(createHeaderPanel(), BorderLayout.NORTH);

        // --- Panel Bảng Dữ Liệu ---
        tableModel = new DefaultTableModel(new String[]{
                "Mã SP", "Tên Sản Phẩm", "Màu Sắc", "Giá Niêm Yết", "Nước SX", "Hãng SX", "Tồn Kho"
        }, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 6) return Integer.class;
                if (columnIndex == 3) return BigDecimal.class;
                return String.class;
            }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224)));
        add(scrollPane, BorderLayout.CENTER);

        // --- Panel các nút chức năng ở dưới ---
        add(createBottomButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản Lý Sản Phẩm", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(TITLE_COLOR);
        headerPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel topActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topActionPanel.setOpaque(false);
        
        // --- Cải tiến các nút ở đây ---
        tfSearch = new JTextField(25);
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnTim = createStyledButton("Tìm", PRIMARY_ACTION_COLOR);
        
        cbHangSX = new JComboBox<>();
        cbHangSX.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbHangSX.setPreferredSize(new Dimension(180, btnTim.getPreferredSize().height));
        
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
        
        // Gán sự kiện cho các nút trên cùng
        btnTim.addActionListener(e -> controller.searchByName(tfSearch.getText()));
        btnLoc.addActionListener(e -> controller.filterByBrand((String) cbHangSX.getSelectedItem()));
        btnTaiLai.addActionListener(e -> {
            controller.loadProducts();
            tfSearch.setText("");
            if (cbHangSX.getItemCount() > 0) cbHangSX.setSelectedIndex(0);
        });

        return headerPanel;
    }
    
    private JPanel createBottomButtonPanel() {
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomButtonPanel.setOpaque(false);
        
        JButton btnThem = createStyledButton("Thêm Sản Phẩm", SUCCESS_COLOR);
        JButton btnSua = createStyledButton("Sửa Sản Phẩm", WARNING_COLOR);
        JButton btnXoa = createStyledButton("Xóa Sản Phẩm", DANGER_COLOR);
        
        bottomButtonPanel.add(btnThem);
        bottomButtonPanel.add(btnSua);
        bottomButtonPanel.add(btnXoa);

        // Gán sự kiện cho các nút dưới cùng
        btnThem.addActionListener(e -> showAddForm());
        btnSua.addActionListener(e -> showEditForm());
        btnXoa.addActionListener(e -> deleteSelectedProduct());
        
        return bottomButtonPanel;
    }

    private void styleTable(JTable tbl) {
        tbl.setFillsViewportHeight(true);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tbl.getTableHeader().setOpaque(false);
        tbl.getTableHeader().setBackground(TABLE_HEADER_BG);
        tbl.getTableHeader().setForeground(Color.WHITE);
        tbl.setRowHeight(30);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setShowGrid(true);
        tbl.setGridColor(new Color(224, 224, 224));

        // Renderer và định dạng cột
        tbl.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer());
        TableColumnModel columnModel = tbl.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(80);
        columnModel.getColumn(1).setPreferredWidth(300);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(150);
        columnModel.getColumn(4).setPreferredWidth(120);
        columnModel.getColumn(5).setPreferredWidth(120);
        columnModel.getColumn(6).setPreferredWidth(80);
    }
    
    /**
    * Tối ưu hóa phương thức tạo nút: linh hoạt, có hiệu ứng hover.
    */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);

        // Nút màu vàng chữ đen cho dễ đọc
        if (backgroundColor.equals(WARNING_COLOR)) {
            btn.setForeground(Color.BLACK);
        }

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Dùng padding thay vì setPreferredSize để nút linh hoạt
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Thêm hiệu ứng hover
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

    public void updateTable(List<SanPham> list) {
         tableModel.setRowCount(0);
        if (list != null) {
            for (SanPham sp : list) {
                tableModel.addRow(new Object[]{
                        sp.getMaSP(),
                        sp.getTenSP(),
                        sp.getMau(),
                        sp.getGiaNiemYet(),
                        sp.getNuocSX(),
                        sp.getHangSX(),
                        sp.getSoLuongTon() // Giả sử có phương thức này trong model SanPham
                });
            }
        }
    }

    private void loadHangSXComboBox() {
        cbHangSX.removeAllItems();
        cbHangSX.addItem("Tất cả");
        List<String> hangSXList = controller.getAllBrands();
        if (hangSXList != null) {
            for (String hsx : hangSXList) {
                cbHangSX.addItem(hsx);
            }
        }
    }
    
    // --- Các phương thức xử lý form và thông báo (giữ nguyên logic) ---
    private void showAddForm() {
        JPanel panel = createFormPanel(null);
        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm Sản Phẩm Mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Lấy component một cách an toàn hơn
                Component[] components = panel.getComponents();
                JTextField tfTen = (JTextField) components[1];
                JTextField tfMau = (JTextField) components[3];
                JSpinner spinnerDonGia = (JSpinner) components[5];
                JTextField tfNuocSX = (JTextField) components[7];
                JTextField tfHangSX = (JTextField) components[9];
                
                BigDecimal giaNiemYet = (BigDecimal) spinnerDonGia.getValue();
                controller.addProduct(
                    tfTen.getText().trim(),
                    tfMau.getText().trim(),
                    giaNiemYet,
                    tfNuocSX.getText().trim(),
                    tfHangSX.getText().trim()
                );
            } catch (Exception e) {
                showMessage("Đã xảy ra lỗi khi xử lý dữ liệu: " + e.getMessage(), false);
            }
        }
    }

    private void showEditForm() {
        int selectedRowInView = table.getSelectedRow();
        if (selectedRowInView == -1) {
            showMessage("Vui lòng chọn một sản phẩm từ bảng để sửa.", false);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRowInView);
        SanPham spToEdit = controller.getProductById((int) tableModel.getValueAt(modelRow, 0));
        
        if (spToEdit == null) {
            showMessage("Không tìm thấy thông tin sản phẩm.", false);
            return;
        }

        JPanel panel = createFormPanel(spToEdit);
        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa Thông Tin Sản Phẩm (Mã: " + spToEdit.getMaSP() + ")",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
             try {
                Component[] components = panel.getComponents();
                JTextField tfTen = (JTextField) components[3];
                JTextField tfMau = (JTextField) components[5];
                JSpinner spinnerDonGia = (JSpinner) components[7];
                JTextField tfNuocSX = (JTextField) components[9];
                JTextField tfHangSX = (JTextField) components[11];
                BigDecimal giaNiemYetMoi = (BigDecimal) spinnerDonGia.getValue();
                
                controller.updateProduct(spToEdit.getMaSP(),
                    tfTen.getText().trim(),
                    tfMau.getText().trim(),
                    giaNiemYetMoi,
                    tfNuocSX.getText().trim(),
                    tfHangSX.getText().trim()
                );
            } catch (Exception e) {
                showMessage("Đã xảy ra lỗi khi xử lý dữ liệu: " + e.getMessage(), false);
            }
        }
    }
    
    private JPanel createFormPanel(SanPham sp) {
        // Thay thế GridBagLayout bằng GridLayout để lấy component theo index an toàn hơn
        JPanel formContentPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String tenSP = (sp != null) ? sp.getTenSP() : "";
        String mau = (sp != null) ? sp.getMau() : "";
        BigDecimal gia = (sp != null) ? sp.getGiaNiemYet() : BigDecimal.ZERO;
        String nuocSX = (sp != null) ? sp.getNuocSX() : "";
        String hangSX = (sp != null) ? sp.getHangSX() : "";
        
        JTextField tfTen = new JTextField(tenSP, 20);
        JTextField tfMau = new JTextField(mau, 20);
        JTextField tfNuocSX = new JTextField(nuocSX, 20);
        JTextField tfHangSX = new JTextField(hangSX, 20);
        
        SpinnerNumberModel giaModel = new SpinnerNumberModel(gia, BigDecimal.ZERO, new BigDecimal("9999999999"), new BigDecimal("100000"));
        JSpinner spinnerDonGia = new JSpinner(giaModel);
        JSpinner.NumberEditor editorDonGia = new JSpinner.NumberEditor(spinnerDonGia, "#,##0.##");
        spinnerDonGia.setEditor(editorDonGia);

        if(sp != null) {
            formContentPanel.add(createLabel("Mã SP:"));
            formContentPanel.add(new JLabel(String.valueOf(sp.getMaSP())));
        }
        formContentPanel.add(createLabel("Tên SP*:"));
        formContentPanel.add(tfTen);
        formContentPanel.add(createLabel("Màu:"));
        formContentPanel.add(tfMau);
        formContentPanel.add(createLabel("Giá Niêm Yết*:"));
        formContentPanel.add(spinnerDonGia);
        formContentPanel.add(createLabel("Nước SX:"));
        formContentPanel.add(tfNuocSX);
        formContentPanel.add(createLabel("Hãng SX:"));
        formContentPanel.add(tfHangSX);
        
        return formContentPanel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    private void deleteSelectedProduct() {
        int selectedRowInView = table.getSelectedRow();
        if (selectedRowInView == -1) {
            showMessage("Vui lòng chọn một sản phẩm từ bảng để xóa.", false);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRowInView);
        int maSP = (int) tableModel.getValueAt(modelRow, 0);
        String tenSP = (String) tableModel.getValueAt(modelRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa sản phẩm:\n'" + tenSP + "' (Mã: " + maSP + ") không?",
                "Xác Nhận Xóa Sản Phẩm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteProduct(maSP);
        }
    }
    
    public void showMessage(String message, boolean isSuccess) {
        if (isSuccess) {
            JOptionPane.showMessageDialog(this, message, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class CurrencyRenderer extends javax.swing.table.DefaultTableCellRenderer {
 private static final long serialVersionUID = 1L;
 private static final NumberFormat FORMAT = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));

 @Override
 public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
     if (value instanceof Number) {
         value = FORMAT.format(value);
     }
     setHorizontalAlignment(SwingConstants.RIGHT); 
     return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
 }
}