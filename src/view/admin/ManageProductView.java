package view.admin;

import controller.admin.ManageProduct;
import model.SanPham;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setTitle("Quản Lý Sản Phẩm");
        setSize(1050, 700); // Kích thước có thể điều chỉnh
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        controller = new ManageProduct(this);

        initUI();
        controller.loadSanPhamList();
        loadHangSXComboBox();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding chung

        // --- Header Panel (Title + Search/Filter) ---
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10)); // Khoảng cách giữa title và filter

        JLabel lblTitle = new JLabel("Danh Sách Sản Phẩm", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setBorder(new EmptyBorder(5, 0, 10, 0));
        headerPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel topActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Căn giữa các control
        tfSearch = new JTextField(25); // Tăng kích thước một chút
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnTim = createStyledButton("Tìm kiếm tên");
        btnTim.setBackground(new Color(0, 123, 255)); // Màu xanh dương
        btnTim.setForeground(Color.WHITE);

        cbHangSX = new JComboBox<>();
        cbHangSX.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbHangSX.setPreferredSize(new Dimension(200, tfSearch.getPreferredSize().height));
        JButton btnLoc = createStyledButton("Lọc theo hãng");
        btnLoc.setBackground(new Color(0, 123, 255));
        btnLoc.setForeground(Color.WHITE);

        JButton btnTaiLai = createStyledButton("Tải lại DS");
        btnTaiLai.setBackground(new Color(23, 162, 184)); // Màu xanh ngọc
        btnTaiLai.setForeground(Color.WHITE);


        topActionPanel.add(new JLabel("Tìm tên SP:"));
        topActionPanel.add(tfSearch);
        topActionPanel.add(btnTim);
        topActionPanel.add(Box.createHorizontalStrut(20));
        topActionPanel.add(new JLabel("Hãng SX:"));
        topActionPanel.add(cbHangSX);
        topActionPanel.add(btnLoc);
        topActionPanel.add(btnTaiLai);
        headerPanel.add(topActionPanel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        tableModel = new DefaultTableModel(new String[]{
                "Mã SP", "Tên SP", "Màu", "Đơn giá (VNĐ)", "Nước SX", "Hãng SX", "Tồn kho"
        }, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Button Panel ---
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnThem = createStyledButton("Thêm Sản Phẩm");
        btnThem.setBackground(new Color(40, 167, 69)); // Xanh lá
        btnThem.setForeground(Color.WHITE);

        JButton btnSua = createStyledButton("Sửa Sản Phẩm");
        btnSua.setBackground(new Color(255, 193, 7)); // Vàng
        btnSua.setForeground(Color.BLACK); // Chữ đen trên nền vàng

        JButton btnXoa = createStyledButton("Xóa Sản Phẩm");
        btnXoa.setBackground(new Color(220, 53, 69)); // Đỏ
        btnXoa.setForeground(Color.WHITE);

        JButton btnBack = createStyledButton("Trở về Admin");
        btnBack.setBackground(new Color(108, 117, 125)); // Xám
        btnBack.setForeground(Color.WHITE);

        bottomButtonPanel.add(btnThem);
        bottomButtonPanel.add(btnSua);
        bottomButtonPanel.add(btnXoa);
        bottomButtonPanel.add(btnBack);
        mainPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // --- Action Listeners ---
        btnTim.addActionListener(e -> {
            String keyword = tfSearch.getText().trim();
            if (keyword.isEmpty()) {
                controller.loadSanPhamList();
                if(cbHangSX.getItemCount() > 0) cbHangSX.setSelectedIndex(0); // Reset về "Tất cả"
            } else {
                controller.searchSanPhamByTen(keyword);
            }
        });

        btnLoc.addActionListener(e -> {
            String hangSX = (String) cbHangSX.getSelectedItem();
            if (hangSX != null && "Tất cả".equals(hangSX)) {
                controller.loadSanPhamList();
                tfSearch.setText("");
            } else if (hangSX != null) {
                controller.filterSanPhamByHangSX(hangSX);
            }
        });

        btnTaiLai.addActionListener(e -> {
            controller.loadSanPhamList();
            tfSearch.setText("");
             if(cbHangSX.getItemCount() > 0) cbHangSX.setSelectedIndex(0);
        });

        btnThem.addActionListener(e -> showAddForm());
        btnSua.addActionListener(e -> showEditForm());
        btnXoa.addActionListener(e -> deleteSelectedProduct());
        btnBack.addActionListener(e -> {
            dispose();
            new AdminView().setVisible(true); // Tạo mới AdminView khi quay lại
        });
    }

    private void styleTable(JTable tbl) {
        tbl.setFillsViewportHeight(true);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.setRowHeight(28);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoCreateRowSorter(true);

        tbl.getColumnModel().getColumn(0).setPreferredWidth(80);
        tbl.getColumnModel().getColumn(1).setPreferredWidth(250);
        tbl.getColumnModel().getColumn(2).setPreferredWidth(100);
        tbl.getColumnModel().getColumn(3).setPreferredWidth(120);
        tbl.getColumnModel().getColumn(4).setPreferredWidth(100);
        tbl.getColumnModel().getColumn(5).setPreferredWidth(120);
        tbl.getColumnModel().getColumn(6).setPreferredWidth(80);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        // Màu nền và chữ sẽ được đặt riêng cho từng nút
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1), // Viền chung
                new EmptyBorder(10, 25, 10, 25) // Padding
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void updateTable(List<SanPham> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (SanPham sp : list) {
                tableModel.addRow(new Object[]{
                        sp.getMaSP(), sp.getTenSP(), sp.getMau(),
                        sp.getDonGia(), sp.getNuocSX(), sp.getHangSX(), sp.getSoLuong()
                });
            }
        }
    }

    private void loadHangSXComboBox() {
        cbHangSX.removeAllItems();
        cbHangSX.addItem("Tất cả");
        List<String> hangSXList = controller.getAllHangSX();
        if (hangSXList != null) {
            for (String hsx : hangSXList) {
                if (hsx != null && !hsx.trim().isEmpty()) { // Bỏ qua hãng rỗng
                    cbHangSX.addItem(hsx);
                }
            }
        }
    }

    private void showAddForm() {
        JTextField tfTen = new JTextField(20);
        JTextField tfMau = new JTextField(20);
        JTextField tfNuocSX = new JTextField(20);
        JTextField tfHangSX = new JTextField(20);

        JSpinner spinnerSoLuong = new JSpinner(new SpinnerNumberModel(
            Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(9999), Integer.valueOf(1)
        ));
        JSpinner spinnerDonGia = new JSpinner(new SpinnerNumberModel(
            Double.valueOf(0.0), Double.valueOf(0.0), Double.valueOf(1_000_000_000.0), Double.valueOf(1000.0)
        ));
        JSpinner.NumberEditor editorDonGia = new JSpinner.NumberEditor(spinnerDonGia, "#,##0"); // Bỏ .00 nếu muốn số nguyên
        spinnerDonGia.setEditor(editorDonGia);

        // Panel cho form, sử dụng GridBagLayout để căn chỉnh tốt hơn
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Tên SP*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_START; panel.add(tfTen, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Màu:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_START; panel.add(tfMau, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Đơn giá (VNĐ)*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.LINE_START; panel.add(spinnerDonGia, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Nước SX:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.LINE_START; panel.add(tfNuocSX, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Hãng SX:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.LINE_START; panel.add(tfHangSX, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Số lượng tồn*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.LINE_START; panel.add(spinnerSoLuong, gbc);


        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm Sản Phẩm Mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String tenSP = tfTen.getText().trim();
                if (tenSP.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double donGia = ((Number)spinnerDonGia.getValue()).doubleValue();
                int soLuong = ((Number)spinnerSoLuong.getValue()).intValue();

                if (donGia <= 0) {
                     JOptionPane.showMessageDialog(this, "Đơn giá phải lớn hơn 0.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                 if (soLuong < 0) {
                     JOptionPane.showMessageDialog(this, "Số lượng tồn không được âm.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                SanPham sp = new SanPham(
                        tenSP, tfMau.getText().trim(), donGia,
                        tfNuocSX.getText().trim(), tfHangSX.getText().trim(), soLuong
                );

                if (controller.insertSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại. Tên sản phẩm có thể đã tồn tại hoặc lỗi từ CSDL.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void showEditForm() {
        int selectedRowInView = table.getSelectedRow();
        if (selectedRowInView == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm từ bảng để sửa.", "Chưa Chọn Sản Phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRowInView);

        int maSP = (Integer) tableModel.getValueAt(modelRow, 0);
        String tenSPBanDau = tableModel.getValueAt(modelRow, 1).toString();
        String mauBanDau = tableModel.getValueAt(modelRow, 2).toString();
        double donGiaBanDau = (Double) tableModel.getValueAt(modelRow, 3);
        String nuocSXBanDau = tableModel.getValueAt(modelRow, 4).toString();
        String hangSXBanDau = tableModel.getValueAt(modelRow, 5) != null ? tableModel.getValueAt(modelRow, 5).toString() : "";
        int soLuongBanDau = (Integer) tableModel.getValueAt(modelRow, 6);

        JTextField tfTen = new JTextField(tenSPBanDau, 20);
        JTextField tfMau = new JTextField(mauBanDau, 20);
        JTextField tfNuocSX = new JTextField(nuocSXBanDau, 20);
        JTextField tfHangSX = new JTextField(hangSXBanDau, 20);

        JSpinner spinnerSoLuong = new JSpinner(new SpinnerNumberModel(
            Integer.valueOf(soLuongBanDau), Integer.valueOf(0), Integer.valueOf(9999), Integer.valueOf(1)
        ));
        JSpinner spinnerDonGia = new JSpinner(new SpinnerNumberModel(
            Double.valueOf(donGiaBanDau), Double.valueOf(0.0), Double.valueOf(1_000_000_000.0), Double.valueOf(1000.0)
        ));
        JSpinner.NumberEditor editorDonGia = new JSpinner.NumberEditor(spinnerDonGia, "#,##0");
        spinnerDonGia.setEditor(editorDonGia);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Mã SP:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblMaSPValue = new JLabel(String.valueOf(maSP));
        lblMaSPValue.setFont(tfTen.getFont().deriveFont(Font.BOLD));
        panel.add(lblMaSPValue, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Tên SP*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_START; panel.add(tfTen, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Màu:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.LINE_START; panel.add(tfMau, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Đơn giá (VNĐ)*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.LINE_START; panel.add(spinnerDonGia, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Nước SX:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.LINE_START; panel.add(tfNuocSX, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Hãng SX:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.LINE_START; panel.add(tfHangSX, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel("Số lượng tồn*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; gbc.anchor = GridBagConstraints.LINE_START; panel.add(spinnerSoLuong, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa Thông Tin Sản Phẩm (Mã: " + maSP + ")",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String tenSPMoi = tfTen.getText().trim();
                if (tenSPMoi.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double donGiaMoi = ((Number)spinnerDonGia.getValue()).doubleValue();
                int soLuongMoi = ((Number)spinnerSoLuong.getValue()).intValue();

                if (donGiaMoi <= 0 ) {
                     JOptionPane.showMessageDialog(this, "Đơn giá phải lớn hơn 0.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (soLuongMoi < 0) {
                     JOptionPane.showMessageDialog(this, "Số lượng tồn không được âm.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                SanPham sp = new SanPham(
                        maSP, tenSPMoi, tfMau.getText().trim(), donGiaMoi,
                        tfNuocSX.getText().trim(), tfHangSX.getText().trim(), soLuongMoi
                );

                if (controller.updateSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thất bại.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void deleteSelectedProduct() {
        int selectedRowInView = table.getSelectedRow();
        if (selectedRowInView == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm từ bảng để xóa.", "Chưa Chọn Sản Phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRowInView);
        String maSPStr = tableModel.getValueAt(modelRow, 0).toString();
        String tenSP = tableModel.getValueAt(modelRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa sản phẩm:\n'" + tenSP + "' (Mã: " + maSPStr + ") không?",
                "Xác Nhận Xóa Sản Phẩm",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteSanPham(maSPStr)) {
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thất bại. Sản phẩm có thể đang được sử dụng trong các hóa đơn hoặc phiếu nhập.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}