package view.admin;

import controller.admin.ManageProduct;
import model.SanPham;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageProductView extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTable table;
    private DefaultTableModel tableModel;
    private ManageProduct controller;
    private JTextField tfSearch;
    private JComboBox<String> cbHangSX;

    public ManageProductView() {
        setTitle("Quản lý sản phẩm");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel lblTitle = new JLabel("Danh sách sản phẩm", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        initUI();
        controller = new ManageProduct(this);
        controller.loadSanPhamList();
        loadHangSXComboBox();
    }

    private void initUI() {
        tableModel = new DefaultTableModel(new String[]{
                "Mã SP", "Tên SP", "Màu", "Đơn giá", "Nước SX", "Hãng SX", "Số lượng"
        }, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        tfSearch = new JTextField(20);
        JButton btnTim = createStyledButton("Tìm kiếm");
        btnTim.addActionListener(e -> {
            String keyword = tfSearch.getText().trim();
            if (keyword.isEmpty()) controller.loadSanPhamList();
            else controller.searchSanPhamByTen(keyword);
        });

        cbHangSX = new JComboBox<>();
        JButton btnLoc = createStyledButton("Lọc");
        btnLoc.addActionListener(e -> {
            String hangSX = (String) cbHangSX.getSelectedItem();
            if ("Tất cả".equals(hangSX)) controller.loadSanPhamList();
            else controller.filterSanPhamByHangSX(hangSX);
        });

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Tìm tên SP:"));
        searchPanel.add(tfSearch);
        searchPanel.add(btnTim);
        searchPanel.add(new JLabel("Hãng SX:"));
        searchPanel.add(cbHangSX);
        searchPanel.add(btnLoc);

        JButton btnTaiLai = createStyledButton("Tải lại");
        btnTaiLai.addActionListener(e -> controller.loadSanPhamList());

        JButton btnThem = createStyledButton("Thêm");
        btnThem.addActionListener(e -> showAddForm());

        JButton btnSua = createStyledButton("Sửa");
        btnSua.addActionListener(e -> showEditForm());

        JButton btnXoa = createStyledButton("Xóa");
        btnXoa.addActionListener(e -> deleteSelectedProduct());

        JButton btnBack = createStyledButton("Trở về");
        btnBack.addActionListener(e -> {
            dispose();
            new AdminView().setVisible(true);
        });

        JPanel bottomPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(btnTaiLai);
        bottomPanel.add(btnThem);
        bottomPanel.add(btnSua);
        bottomPanel.add(btnXoa);
        bottomPanel.add(btnBack);

        add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(59, 89, 182));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 50, 100), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        return btn;
    }

    public void updateTable(List<SanPham> list) {
        tableModel.setRowCount(0);
        for (SanPham sp : list) {
            tableModel.addRow(new Object[]{
                    sp.getMaSP(), sp.getTenSP(), sp.getMau(),
                    sp.getDonGia(), sp.getNuocSX(), sp.getHangSX(), sp.getSoLuong()
            });
        }
    }

    private void loadHangSXComboBox() {
        cbHangSX.removeAllItems();
        cbHangSX.addItem("Tất cả");
        List<String> hangSXList = controller.getAllHangSX();
        for (String hsx : hangSXList) cbHangSX.addItem(hsx);
    }

    private void showAddForm() {
        JTextField tfMaSP = new JTextField();
        JTextField tfTen = new JTextField();
        JTextField tfMau = new JTextField();
        JTextField tfDonGia = new JTextField();
        JTextField tfNuocSX = new JTextField();
        JTextField tfHangSX = new JTextField();
        JTextField tfSoLuong = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Mã SP:")); panel.add(tfMaSP);
        panel.add(new JLabel("Tên SP:")); panel.add(tfTen);
        panel.add(new JLabel("Màu:")); panel.add(tfMau);
        panel.add(new JLabel("Đơn giá:")); panel.add(tfDonGia);
        panel.add(new JLabel("Nước SX:")); panel.add(tfNuocSX);
        panel.add(new JLabel("Hãng SX:")); panel.add(tfHangSX);
        panel.add(new JLabel("Số lượng:")); panel.add(tfSoLuong);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm sản phẩm",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                SanPham sp = new SanPham(
                        tfMaSP.getText(),
                        tfTen.getText(),
                        tfMau.getText(),
                        Double.parseDouble(tfDonGia.getText()),
                        tfNuocSX.getText(),
                        tfHangSX.getText(),
                        Integer.parseInt(tfSoLuong.getText())
                );

                if (controller.insertSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công");
                    controller.loadSanPhamList();
                    loadHangSXComboBox();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ");
            }
        }
    }

    private void showEditForm() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn sản phẩm để sửa");
            return;
        }

        String maSP = tableModel.getValueAt(row, 0).toString();
        JTextField tfTen = new JTextField(tableModel.getValueAt(row, 1).toString());
        JTextField tfMau = new JTextField(tableModel.getValueAt(row, 2).toString());
        JTextField tfDonGia = new JTextField(tableModel.getValueAt(row, 3).toString());
        JTextField tfNuocSX = new JTextField(tableModel.getValueAt(row, 4).toString());
        JTextField tfHangSX = new JTextField(tableModel.getValueAt(row, 5).toString());
        JTextField tfSoLuong = new JTextField(tableModel.getValueAt(row, 6).toString());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Tên SP:")); panel.add(tfTen);
        panel.add(new JLabel("Màu:")); panel.add(tfMau);
        panel.add(new JLabel("Đơn giá (VND):")); panel.add(tfDonGia);
        panel.add(new JLabel("Nước SX:")); panel.add(tfNuocSX);
        panel.add(new JLabel("Hãng SX:")); panel.add(tfHangSX);
        panel.add(new JLabel("Số lượng:")); panel.add(tfSoLuong);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa sản phẩm",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                SanPham sp = new SanPham(
                        maSP,
                        tfTen.getText(),
                        tfMau.getText(),
                        Double.parseDouble(tfDonGia.getText()),
                        tfNuocSX.getText(),
                        tfHangSX.getText(),
                        Integer.parseInt(tfSoLuong.getText())
                );

                if (controller.updateSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công");
                    controller.loadSanPhamList();
                    loadHangSXComboBox();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ");
            }
        }
    }

    private void deleteSelectedProduct() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn sản phẩm để xóa");
            return;
        }

        String maSP = tableModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa sản phẩm này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteSanPham(maSP)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công");
                controller.loadSanPhamList();
                loadHangSXComboBox();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại");
            }
        }
    }
}