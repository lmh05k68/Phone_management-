package view.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import model.NhaCungCap;
import query.NhaCungCapQuery;

public class ManageSupplierView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbProvinceFilter, cbPhonePrefixFilter;
    private JButton btnSortAZ, btnSearch;
    private NhaCungCapQuery nccDAO = new NhaCungCapQuery();

    public ManageSupplierView() {
        setLayout(new BorderLayout());
        initUI();
        loadData();
        addListeners();
    }

    public void showInFrame() {
        JFrame frame = new JFrame("Quản lý nhà cung cấp");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(this);
        frame.setVisible(true);
    }

    private void initUI() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        add(container, BorderLayout.CENTER);

        JLabel lblTitle = new JLabel("Quản lý nhà cung cấp");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        container.add(lblTitle);

        // Filter Panel
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridLayout(2, 3, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm & Lọc"));

        txtSearch = new JTextField();
        btnSearch = createStyledButton("Tìm", new Color(0, 123, 255));
        btnSortAZ = createStyledButton("Sắp xếp A-Z", new Color(40, 167, 69));

        cbProvinceFilter = new JComboBox<>();
        cbProvinceFilter.addItem("Tất cả địa chỉ");
        for (String province : nccDAO.getDistinctProvinces()) {
            cbProvinceFilter.addItem(province);
        }

        cbPhonePrefixFilter = new JComboBox<>();
        cbPhonePrefixFilter.addItem("Tất cả đầu số");
        for (String prefix : nccDAO.getDistinctPhonePrefixes()) {
            cbPhonePrefixFilter.addItem(prefix);
        }

        filterPanel.add(new JLabel("Từ khóa:"));
        filterPanel.add(new JLabel("Địa chỉ:"));
        filterPanel.add(new JLabel("Đầu số điện thoại:"));
        filterPanel.add(txtSearch);
        filterPanel.add(cbProvinceFilter);
        filterPanel.add(cbPhonePrefixFilter);

        container.add(filterPanel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.add(btnSearch);
        btnPanel.add(btnSortAZ);
        container.add(btnPanel);

        // Table
        String[] columnNames = {"Mã NCC", "Tên NCC", "Địa chỉ", "SĐT"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách nhà cung cấp"));

        container.add(scrollPane);
    }

    private void loadData() {
        List<NhaCungCap> list = nccDAO.getAllNhaCungCap();
        tableModel.setRowCount(0);
        for (NhaCungCap ncc : list) {
            tableModel.addRow(new Object[]{ncc.getMaNCC(), ncc.getTenNCC(), ncc.getDiaChi(), ncc.getSdtNCC()});
        }
    }

    private void addListeners() {
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            String selectedProvince = (String) cbProvinceFilter.getSelectedItem();
            String selectedPrefix = (String) cbPhonePrefixFilter.getSelectedItem();
            searchAndFilter(keyword, selectedProvince, selectedPrefix);
        });

        btnSortAZ.addActionListener(e -> sortTableByName());
    }

    private void searchAndFilter(String keyword, String province, String prefix) {
        List<NhaCungCap> list = nccDAO.searchNhaCungCap(keyword);

        if (!province.equals("Tất cả tỉnh/thành")) {
            list.removeIf(ncc -> !ncc.getDiaChi().contains(province));
        }

        if (!prefix.equals("Tất cả đầu số")) {
            list.removeIf(ncc -> !ncc.getSdtNCC().startsWith(prefix));
        }

        tableModel.setRowCount(0);
        for (NhaCungCap ncc : list) {
            tableModel.addRow(new Object[]{ncc.getMaNCC(), ncc.getTenNCC(), ncc.getDiaChi(), ncc.getSdtNCC()});
        }
    }

    private void sortTableByName() {
        List<NhaCungCap> list = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            list.add(new NhaCungCap(
                tableModel.getValueAt(i, 0).toString(),
                tableModel.getValueAt(i, 1).toString(),
                tableModel.getValueAt(i, 2).toString(),
                tableModel.getValueAt(i, 3).toString()
            ));
        }
        list.sort(Comparator.comparing(NhaCungCap::getTenNCC));
        tableModel.setRowCount(0);
        for (NhaCungCap ncc : list) {
            tableModel.addRow(new Object[]{ncc.getMaNCC(), ncc.getTenNCC(), ncc.getDiaChi(), ncc.getSdtNCC()});
        }
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        return btn;
    }
}