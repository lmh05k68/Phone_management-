package view.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import model.NhaCungCap;
import query.NhaCungCapQuery;
public class ManageSupplierView extends JPanel { // Nên là JPanel nếu bạn muốn nhúng vào JFrame khác
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbProvinceFilter, cbPhonePrefixFilter;
    private JButton btnSortAZ, btnSearch, btnAdd, btnEdit, btnDelete, btnClearFilter; 
    public ManageSupplierView() {
        setLayout(new BorderLayout(10, 10)); // Thêm khoảng cách cho BorderLayout
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding cho toàn bộ panel
        initUI();
        loadData(); // Tải dữ liệu ban đầu
        addListeners();
        loadFilterComboBoxes(); // Tải dữ liệu cho ComboBox lọc
    }

    // Phương thức này hữu ích nếu bạn muốn hiển thị JPanel này trong một JFrame riêng biệt để test
    public void showInFrame() {
        JFrame frame = new JFrame("Quản Lý Nhà Cung Cấp");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(950, 650); // Tăng kích thước một chút
        frame.setLocationRelativeTo(null);
        frame.setContentPane(this); // Đặt JPanel này làm content pane
        frame.setVisible(true);
    }

    private void initUI() {
        // Panel chính chứa tất cả, thay vì container riêng
        // setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Đã set BorderLayout ở constructor

        JLabel lblTitle = new JLabel("Quản Lý Nhà Cung Cấp");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24)); // Font lớn hơn
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER); // Căn giữa title
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); // Padding dưới title
        add(lblTitle, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterSearchPanel = new JPanel(new GridBagLayout());
        filterSearchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm & Lọc"));
        GridBagConstraints gbcFilter = new GridBagConstraints();
        gbcFilter.insets = new Insets(5, 5, 5, 5);
        gbcFilter.fill = GridBagConstraints.HORIZONTAL;

        txtSearch = new JTextField(20);
        btnSearch = createStyledButton("Tìm theo từ khóa", new Color(0, 123, 255));
        cbProvinceFilter = new JComboBox<>();
        cbProvinceFilter.setPreferredSize(new Dimension(150, txtSearch.getPreferredSize().height));
        cbPhonePrefixFilter = new JComboBox<>();
        cbPhonePrefixFilter.setPreferredSize(new Dimension(150, txtSearch.getPreferredSize().height));
        btnClearFilter = createStyledButton("Xóa lọc", new Color(108,117,125));


        gbcFilter.gridx = 0; gbcFilter.gridy = 0; filterSearchPanel.add(new JLabel("Từ khóa tìm (Tên/SĐT/Địa chỉ):"), gbcFilter);
        gbcFilter.gridx = 1; gbcFilter.gridy = 0; gbcFilter.gridwidth = 2; filterSearchPanel.add(txtSearch, gbcFilter);
        gbcFilter.gridx = 3; gbcFilter.gridy = 0; gbcFilter.gridwidth = 1; filterSearchPanel.add(btnSearch, gbcFilter);

        gbcFilter.gridx = 0; gbcFilter.gridy = 1; filterSearchPanel.add(new JLabel("Lọc theo Địa chỉ:"), gbcFilter);
        gbcFilter.gridx = 1; gbcFilter.gridy = 1; filterSearchPanel.add(cbProvinceFilter, gbcFilter);
        gbcFilter.gridx = 2; gbcFilter.gridy = 1; filterSearchPanel.add(new JLabel("Lọc theo Đầu số:"), gbcFilter);
        gbcFilter.gridx = 3; gbcFilter.gridy = 1; filterSearchPanel.add(cbPhonePrefixFilter, gbcFilter);
        gbcFilter.gridx = 4; gbcFilter.gridy = 1; filterSearchPanel.add(btnClearFilter, gbcFilter);


        // Panel cho các nút Thêm, Sửa, Xóa, Sắp xếp
        JPanel crudButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Căn trái
        btnAdd = createStyledButton("Thêm NCC", new Color(40, 167, 69));
        btnEdit = createStyledButton("Sửa NCC", new Color(255, 193, 7));
         btnEdit.setForeground(Color.BLACK); // Chữ đen trên nền vàng
        btnDelete = createStyledButton("Xóa NCC", new Color(220, 53, 69));
        btnSortAZ = createStyledButton("Sắp xếp A-Z (Tên)", new Color(23, 162, 184));

        crudButtonPanel.add(btnAdd);
        crudButtonPanel.add(btnEdit);
        crudButtonPanel.add(btnDelete);
        crudButtonPanel.add(btnSortAZ);
        JPanel topControlsPanel = new JPanel();
        topControlsPanel.setLayout(new BoxLayout(topControlsPanel, BoxLayout.Y_AXIS));
        topControlsPanel.add(filterSearchPanel);
        topControlsPanel.add(crudButtonPanel);

        add(topControlsPanel, BorderLayout.NORTH);
        String[] columnNames = {"Mã NCC", "Tên Nhà Cung Cấp", "Địa Chỉ", "Số Điện Thoại"};
        tableModel = new DefaultTableModel(columnNames, 0) {
        	private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table); // Áp dụng style
        JScrollPane scrollPane = new JScrollPane(table);
        // Không cần setBorder cho scrollPane ở đây nếu topControlsPanel đã có TitledBorder

        add(scrollPane, BorderLayout.CENTER); // Đặt bảng ở giữa
    }
    private void styleTable(JTable tbl) {
        tbl.setFillsViewportHeight(true);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.setRowHeight(28);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoCreateRowSorter(true); // Cho phép sắp xếp

        tbl.getColumnModel().getColumn(0).setPreferredWidth(80);  // Mã NCC
        tbl.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên NCC
        tbl.getColumnModel().getColumn(2).setPreferredWidth(300); // Địa chỉ
        tbl.getColumnModel().getColumn(3).setPreferredWidth(120); // SĐT
    }


    private void loadData() {
        List<NhaCungCap> list = NhaCungCapQuery.getAll(); // Gọi static
        updateTable(list);
    }

    private void updateTable(List<NhaCungCap> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (NhaCungCap ncc : list) {
                tableModel.addRow(new Object[]{
                        ncc.getMaNCC(), // int
                        ncc.getTenNCC(),
                        ncc.getDiaChi(),
                        ncc.getSdtNCC()
                });
            }
        }
    }


    private void addListeners() {
        btnSearch.addActionListener(e -> performSearchAndFilter());
        cbProvinceFilter.addActionListener(e -> performSearchAndFilter()); // Lọc ngay khi chọn
        cbPhonePrefixFilter.addActionListener(e -> performSearchAndFilter());// Lọc ngay khi chọn
        btnClearFilter.addActionListener(e -> {
            txtSearch.setText("");
            cbProvinceFilter.setSelectedIndex(0); // Chọn "Tất cả"
            cbPhonePrefixFilter.setSelectedIndex(0); // Chọn "Tất cả"
            loadData(); // Tải lại toàn bộ danh sách
        });


        btnSortAZ.addActionListener(e -> sortTableByName());
        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteSelectedSupplier());
    }

    private void performSearchAndFilter() {
        String keyword = txtSearch.getText().trim();
        String selectedProvince = cbProvinceFilter.getSelectedItem() != null ? cbProvinceFilter.getSelectedItem().toString() : "Tất cả địa chỉ";
        String selectedPrefix = cbPhonePrefixFilter.getSelectedItem() != null ? cbPhonePrefixFilter.getSelectedItem().toString() : "Tất cả đầu số";

        List<NhaCungCap> list = NhaCungCapQuery.searchNhaCungCap(keyword); // Query đã tìm theo nhiều trường

        if (!"Tất cả địa chỉ".equals(selectedProvince)) {
            list.removeIf(ncc -> ncc.getDiaChi() == null || !ncc.getDiaChi().contains(selectedProvince));
        }

        if (!"Tất cả đầu số".equals(selectedPrefix)) {
            list.removeIf(ncc -> ncc.getSdtNCC() == null || !ncc.getSdtNCC().startsWith(selectedPrefix));
        }
        updateTable(list);
    }

    private void sortTableByName() {
        // Lấy dữ liệu hiện tại từ bảng (có thể đã được lọc)
        List<NhaCungCap> currentListInTable = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            currentListInTable.add(new NhaCungCap(
                (Integer)tableModel.getValueAt(i, 0), // MaNCC là int
                tableModel.getValueAt(i, 1).toString(),
                tableModel.getValueAt(i, 2).toString(),
                tableModel.getValueAt(i, 3).toString()
            ));
        }
        currentListInTable.sort(Comparator.comparing(NhaCungCap::getTenNCC, String.CASE_INSENSITIVE_ORDER));
        updateTable(currentListInTable);
    }

    private void loadFilterComboBoxes() {
        // Load địa chỉ
        cbProvinceFilter.removeAllItems();
        cbProvinceFilter.addItem("Tất cả địa chỉ");
        List<String> provinces = NhaCungCapQuery.getDistinctProvinces();
        if (provinces != null) {
            for (String province : provinces) {
                cbProvinceFilter.addItem(province);
            }
        }
        // Load đầu số
        cbPhonePrefixFilter.removeAllItems();
        cbPhonePrefixFilter.addItem("Tất cả đầu số");
        List<String> prefixes = NhaCungCapQuery.getDistinctPhonePrefixes();
        if (prefixes != null) {
            for (String prefix : prefixes) {
                cbPhonePrefixFilter.addItem(prefix);
            }
        }
    }


    private void showAddDialog() {
        // Loại bỏ maField vì MaNCC tự sinh
        JTextField tenField = new JTextField();
        JTextField diaChiField = new JTextField();
        JTextField sdtField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5)); // 2 cột
        panel.add(new JLabel("Tên NCC*:")); panel.add(tenField);
        panel.add(new JLabel("Địa chỉ*:")); panel.add(diaChiField);
        panel.add(new JLabel("SĐT*:")); panel.add(sdtField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Thêm Nhà Cung Cấp Mới",
                                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String ten = tenField.getText().trim();
            String diaChi = diaChiField.getText().trim();
            String sdt = sdtField.getText().trim();

            if (ten.isEmpty() || diaChi.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin (Tên, Địa chỉ, SĐT).", "Thiếu Thông Tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Không cần kiểm tra isMaNCCExists vì MaNCC tự sinh

            // Sử dụng constructor không có MaNCC
            NhaCungCap nccMoi = new NhaCungCap(ten, diaChi, sdt);
            Integer generatedId = NhaCungCapQuery.insertNhaCungCapAndGetId(nccMoi); // Gọi static

            if (generatedId != null && generatedId > 0) {
                JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thành công! Mã NCC mới: " + generatedId, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadData(); // Tải lại toàn bộ dữ liệu
                loadFilterComboBoxes(); // Tải lại combobox vì có thể có địa chỉ/đầu số mới
            } else {
                JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thất bại. Có thể do lỗi hệ thống.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int selectedRowInView = table.getSelectedRow();
        if (selectedRowInView == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhà cung cấp từ bảng để sửa.", "Chưa Chọn NCC", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRowInView);

        // Lấy MaNCC (int) từ model
        int maNCC = (Integer) tableModel.getValueAt(modelRow, 0);
        String tenHienTai = tableModel.getValueAt(modelRow, 1).toString();
        String diaChiHienTai = tableModel.getValueAt(modelRow, 2).toString();
        String sdtHienTai = tableModel.getValueAt(modelRow, 3).toString();

        JTextField tenField = new JTextField(tenHienTai);
        JTextField diaChiField = new JTextField(diaChiHienTai);
        JTextField sdtField = new JTextField(sdtHienTai);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Mã NCC:"));
        JLabel lblMaNCCValue = new JLabel(String.valueOf(maNCC));
        lblMaNCCValue.setFont(tenField.getFont().deriveFont(Font.BOLD));
        panel.add(lblMaNCCValue); // Hiển thị Mã NCC, không cho sửa

        panel.add(new JLabel("Tên NCC*:")); panel.add(tenField);
        panel.add(new JLabel("Địa chỉ*:")); panel.add(diaChiField);
        panel.add(new JLabel("SĐT*:")); panel.add(sdtField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Sửa Thông Tin Nhà Cung Cấp (Mã: " + maNCC + ")",
                                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String ten = tenField.getText().trim();
            String diaChi = diaChiField.getText().trim();
            String sdt = sdtField.getText().trim();

            if (ten.isEmpty() || diaChi.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin (Tên, Địa chỉ, SĐT).", "Thiếu Thông Tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tạo đối tượng NhaCungCap với MaNCC (int) đã có
            NhaCungCap nccCapNhat = new NhaCungCap(maNCC, ten, diaChi, sdt);
            boolean success = NhaCungCapQuery.updateNhaCungCap(nccCapNhat); // Gọi static

            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhà cung cấp thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                loadFilterComboBoxes();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedSupplier() {
        int selectedRowInView = table.getSelectedRow();
        if (selectedRowInView == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhà cung cấp từ bảng để xóa.", "Chưa Chọn NCC", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRowInView);
        // Lấy MaNCC (int)
        int maNCC = (Integer) tableModel.getValueAt(modelRow, 0);
        String tenNCC = tableModel.getValueAt(modelRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa nhà cung cấp '" + tenNCC + "' (Mã: " + maNCC + ") không?",
                "Xác Nhận Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = NhaCungCapQuery.deleteNhaCungCap(maNCC); // Gọi static, truyền int
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                loadFilterComboBoxes();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại. Nhà cung cấp có thể đang được sử dụng trong hóa đơn nhập.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Font đậm hơn
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15) // Padding
        ));
        return btn;
    }

    // Main method để test (tùy chọn)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManageSupplierView viewPanel = new ManageSupplierView();
            viewPanel.showInFrame(); // Gọi phương thức để hiển thị trong JFrame
        });
    }
}