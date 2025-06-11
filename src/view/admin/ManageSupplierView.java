package view.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import controller.admin.ManageSupplierController;
import model.NhaCungCap;
import query.NhaCungCapQuery;

public class ManageSupplierView extends JPanel {
    private static final long serialVersionUID = 1L;

    // --- CONSTANTS FOR UI STYLING ---
    private static final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    private static final Color COLOR_PRIMARY = new Color(0, 102, 204);
    private static final Color COLOR_TABLE_HEADER = new Color(52, 73, 94);
    private static final Color COLOR_SUCCESS = new Color(40, 167, 69);
    private static final Color COLOR_WARNING = new Color(255, 193, 7);
    private static final Color COLOR_DANGER = new Color(220, 53, 69);
    private static final Color COLOR_SECONDARY = new Color(108, 117, 125);
    private static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 26);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 14);

    // --- UI COMPONENTS ---
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbProvinceFilter;
    private JButton btnAdd, btnEdit, btnDelete, btnClear;

    // --- CONTROLLER ---
    private final ManageSupplierController controller;

    public ManageSupplierView() {
        this.controller = new ManageSupplierController(this);
        initUI();
        controller.loadSuppliers();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setBackground(COLOR_BACKGROUND);

        add(createTitlePanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(createTopPanel(), BorderLayout.NORTH);
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        addListeners();
    }
    
    private JLabel createTitlePanel() {
        JLabel lblTitle = new JLabel("Quản Lý Nhà Cung Cấp", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        return lblTitle;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createTitledBorder("Công cụ"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // --- Dòng tìm kiếm và lọc ---
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Tìm kiếm:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        txtSearch = new JTextField(25);
        txtSearch.setPreferredSize(new Dimension(txtSearch.getPreferredSize().width, 35));
        topPanel.add(txtSearch, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        topPanel.add(new JLabel("Địa chỉ:"), gbc);
        
        gbc.gridx = 3; gbc.gridy = 0;
        cbProvinceFilter = new JComboBox<>();
        cbProvinceFilter.setPreferredSize(new Dimension(150, 35));
        topPanel.add(cbProvinceFilter, gbc);
        
        gbc.gridx = 4; gbc.gridy = 0;
        btnClear = createStyledButton("Làm mới", COLOR_SECONDARY);
        topPanel.add(btnClear, gbc);
        
        // --- Thành phần đệm để đẩy các nút chức năng qua phải ---
        gbc.gridx = 5; gbc.gridy = 0;
        gbc.weightx = 1.0; // Hút hết không gian thừa
        topPanel.add(new JLabel(""), gbc);
        gbc.weightx = 0; // Reset
        
        // --- Dòng nút chức năng (Thêm, Sửa, Xóa) ---
        gbc.gridx = 6; gbc.gridy = 0;
        btnAdd = createStyledButton("Thêm", COLOR_SUCCESS);
        topPanel.add(btnAdd, gbc);
        
        gbc.gridx = 7; gbc.gridy = 0;
        btnEdit = createStyledButton("Sửa", COLOR_WARNING);
        topPanel.add(btnEdit, gbc);
        
        gbc.gridx = 8; gbc.gridy = 0;
        btnDelete = createStyledButton("Xóa", COLOR_DANGER);
        topPanel.add(btnDelete, gbc);
        
        updateFilterComboBox(); // Tải dữ liệu cho combobox
        return topPanel;
    }
    
    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(new String[]{"Mã NCC", "Tên Nhà Cung Cấp", "Địa Chỉ", "Số Điện Thoại"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable();
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }
    
    private void styleTable() {
        table.setFillsViewportHeight(true);
        table.setFont(FONT_TABLE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(COLOR_TABLE_HEADER);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 35)); // Kích thước đồng bộ

        // Chữ màu đen cho nút màu vàng, các nút khác chữ màu trắng
        if (bgColor.equals(COLOR_WARNING)) {
            btn.setForeground(Color.BLACK);
        } else {
            btn.setForeground(Color.WHITE);
        }
        return btn;
    }
    
    private void addListeners() {
        Runnable filterAction = () -> {
            String keyword = txtSearch.getText();
            Object selectedItem = cbProvinceFilter.getSelectedItem();
            String province = (selectedItem != null && !selectedItem.equals("Tất cả")) ? selectedItem.toString() : "";
            controller.searchAndFilter(keyword, province);
        };

        txtSearch.addActionListener(e -> filterAction.run());
        cbProvinceFilter.addActionListener(e -> filterAction.run());
        
        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            if (cbProvinceFilter.getItemCount() > 0) cbProvinceFilter.setSelectedIndex(0);
            controller.loadSuppliers();
        });

        btnAdd.addActionListener(e -> showFormDialog(null));
        btnEdit.addActionListener(e -> {
            int selectedRow = getSelectedModelRow();
            if (selectedRow != -1) {
                NhaCungCap ncc = getSupplierFromTableRow(selectedRow);
                showFormDialog(ncc);
            }
        });
        btnDelete.addActionListener(e -> {
            int selectedRow = getSelectedModelRow();
            if (selectedRow != -1) {
                NhaCungCap ncc = getSupplierFromTableRow(selectedRow);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc muốn xóa nhà cung cấp '" + ncc.getTenNCC() + "' không?",
                        "Xác Nhận Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.deleteSupplier(ncc.getMaNCC());
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            boolean isRowSelected = table.getSelectedRow() != -1;
            btnEdit.setEnabled(isRowSelected);
            btnDelete.setEnabled(isRowSelected);
        });
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void showFormDialog(NhaCungCap ncc) {
        boolean isEditing = (ncc != null);
        String title = isEditing ? "Sửa Nhà Cung Cấp" : "Thêm Nhà Cung Cấp";

        JTextField tenField = new JTextField(isEditing ? ncc.getTenNCC() : "", 25);
        JTextField diaChiField = new JTextField(isEditing ? ncc.getDiaChi() : "", 25);
        JTextField sdtField = new JTextField(isEditing ? ncc.getSdtNCC() : "", 25);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        if (isEditing) {
            gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_END;
            panel.add(new JLabel("Mã NCC:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
            panel.add(new JLabel(String.valueOf(ncc.getMaNCC())), gbc);
        }
        int startRow = isEditing ? 1 : 0;
        gbc.gridx = 0; gbc.gridy = startRow; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Tên NCC*:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; panel.add(tenField, gbc);
        gbc.gridx = 0; gbc.gridy = startRow + 1; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Địa chỉ*:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; panel.add(diaChiField, gbc);
        gbc.gridx = 0; gbc.gridy = startRow + 2; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("SĐT*:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; panel.add(sdtField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            if (isEditing) {
                controller.updateSupplier(ncc.getMaNCC(), tenField.getText(), diaChiField.getText(), sdtField.getText());
            } else {
                controller.addSupplier(tenField.getText(), diaChiField.getText(), sdtField.getText());
            }
        }
    }

    public void updateTable(List<NhaCungCap> list) {
        tableModel.setRowCount(0);
        for (NhaCungCap ncc : list) {
            tableModel.addRow(new Object[]{ncc.getMaNCC(), ncc.getTenNCC(), ncc.getDiaChi(), ncc.getSdtNCC()});
        }
    }
    
    public void updateFilterComboBox() {
        List<String> provinces = NhaCungCapQuery.getAll().stream()
                .map(NhaCungCap::getDiaChi)
                .filter(d -> d != null && !d.isEmpty())
                .map(this::extractProvince) // Trích xuất tỉnh/thành phố
                .filter(p -> p != null && !p.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        String selectedItem = (String) cbProvinceFilter.getSelectedItem();
        cbProvinceFilter.removeAllItems();
        cbProvinceFilter.addItem("Tất cả");
        for (String province : provinces) {
            cbProvinceFilter.addItem(province);
        }
        cbProvinceFilter.setSelectedItem(selectedItem); 
    }
    
    // Hàm phụ trợ để trích xuất tên tỉnh/thành phố từ địa chỉ đầy đủ
    private String extractProvince(String fullAddress) {
        String[] parts = fullAddress.split(", ");
        if (parts.length > 0) {
            return parts[parts.length - 1]; // Lấy phần tử cuối cùng
        }
        return fullAddress; // Trả về địa chỉ gốc nếu không phân tách được
    }

    public void showMessage(String message, boolean isSuccess) {
        JOptionPane.showMessageDialog(this, message, isSuccess ? "Thành Công" : "Lỗi", 
                isSuccess ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
    
    private int getSelectedModelRow() {
        int selectedRowInView = table.getSelectedRow();
        if (selectedRowInView == -1) {
            showMessage("Vui lòng chọn một nhà cung cấp từ bảng.", false);
            return -1;
        }
        return table.convertRowIndexToModel(selectedRowInView);
    }
    
    private NhaCungCap getSupplierFromTableRow(int modelRow) {
        int maNCC = (int) tableModel.getValueAt(modelRow, 0);
        String ten = (String) tableModel.getValueAt(modelRow, 1);
        String diaChi = (String) tableModel.getValueAt(modelRow, 2);
        String sdt = (String) tableModel.getValueAt(modelRow, 3);
        return new NhaCungCap(maNCC, ten, diaChi, sdt);
    }
}