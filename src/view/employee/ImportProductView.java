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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ImportProductView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<NhaCungCap> cboNCC;
    private JComboBox<SanPham> cboSanPham;
    private JTextField txtSoLuong;
    private JTextField txtDonGia;
    private JTable table;
    private DefaultTableModel tableModel;
    private final List<NhapHangItem> dsNhap = new ArrayList<>();
    private final int maNV;

    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(0, 123, 255);
    private static final Color SUCCESS_BUTTON_COLOR = new Color(40, 167, 69);
    private static final Color SECONDARY_BUTTON_COLOR = new Color(108, 117, 125);
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));

    public ImportProductView(int maNV) {
        this.maNV = maNV;
        setTitle("Quản Lý Nhập Hàng - Nhân viên: " + maNV);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        JLabel lblTitle = new JLabel("Tạo Phiếu Nhập Hàng", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        cboNCC = new JComboBox<>();
        cboNCC.setRenderer(new NhaCungCapRenderer());
        cboSanPham = new JComboBox<>();
        cboSanPham.setRenderer(new SanPhamRenderer());
        // *** THAY ĐỔI 1: Thêm listener để tự động lấy giá ***
        cboSanPham.addItemListener(this::onProductSelected);

        JPanel topInputPanel = new JPanel(new GridBagLayout());
        topInputPanel.setOpaque(false);
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.insets = new Insets(5, 5, 5, 5);
        gbcTop.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblNCC = new JLabel("Nhà cung cấp*:");
        lblNCC.setFont(LABEL_FONT.deriveFont(Font.BOLD));
        gbcTop.gridx = 0; gbcTop.gridy = 0; gbcTop.weightx = 0.1;
        topInputPanel.add(lblNCC, gbcTop);
        cboNCC.setFont(INPUT_FONT);
        gbcTop.gridx = 1; gbcTop.gridy = 0; gbcTop.weightx = 0.9;
        topInputPanel.add(cboNCC, gbcTop);

        JPanel productSelectionPanel = new JPanel(new BorderLayout(10, 10));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Thêm sản phẩm vào phiếu nhập");
        titledBorder.setTitleFont(LABEL_FONT.deriveFont(Font.BOLD));
        productSelectionPanel.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(5, 5, 5, 5)));
        productSelectionPanel.setOpaque(false);

        JPanel productFieldsPanel = new JPanel(new GridBagLayout());
        productFieldsPanel.setOpaque(false);
        GridBagConstraints gbcFields = new GridBagConstraints();
        gbcFields.insets = new Insets(8, 5, 8, 5);
        gbcFields.anchor = GridBagConstraints.WEST;

        JLabel lblSanPham = new JLabel("Sản phẩm:");
        lblSanPham.setFont(LABEL_FONT);
        gbcFields.gridx = 0; gbcFields.gridy = 0;
        productFieldsPanel.add(lblSanPham, gbcFields);

        cboSanPham.setFont(INPUT_FONT);
        gbcFields.gridx = 1; gbcFields.gridy = 0; gbcFields.gridwidth = 4;
        gbcFields.fill = GridBagConstraints.HORIZONTAL; gbcFields.weightx = 1.0;
        productFieldsPanel.add(cboSanPham, gbcFields);

        JLabel lblSoLuong = new JLabel("Số lượng*:");
        lblSoLuong.setFont(LABEL_FONT);
        gbcFields.gridx = 0; gbcFields.gridy = 1; gbcFields.gridwidth = 1;
        gbcFields.fill = GridBagConstraints.NONE; gbcFields.weightx = 0;
        productFieldsPanel.add(lblSoLuong, gbcFields);

        txtSoLuong = new JTextField(7);
        txtSoLuong.setFont(INPUT_FONT);
        gbcFields.gridx = 1; gbcFields.gridy = 1;
        gbcFields.fill = GridBagConstraints.HORIZONTAL; gbcFields.weightx = 0.3;
        productFieldsPanel.add(txtSoLuong, gbcFields);

        JLabel lblDonGia = new JLabel("Đơn giá nhập*:");
        lblDonGia.setFont(LABEL_FONT);
        gbcFields.gridx = 2; gbcFields.gridy = 1;
        gbcFields.fill = GridBagConstraints.NONE; gbcFields.weightx = 0;
        gbcFields.insets.left = 15;
        productFieldsPanel.add(lblDonGia, gbcFields);
        gbcFields.insets.left = 5;

        txtDonGia = new JTextField(10);
        txtDonGia.setFont(INPUT_FONT);
        gbcFields.gridx = 3; gbcFields.gridy = 1;
        gbcFields.fill = GridBagConstraints.HORIZONTAL; gbcFields.weightx = 0.3;
        productFieldsPanel.add(txtDonGia, gbcFields);

        JButton btnThemSPVaoBang = createStyledButton("Thêm vào phiếu", PRIMARY_BUTTON_COLOR);
        btnThemSPVaoBang.setMargin(new Insets(5, 10, 5, 10));
        gbcFields.gridx = 4; gbcFields.gridy = 1; gbcFields.fill = GridBagConstraints.NONE;
        gbcFields.weightx = 0.1; gbcFields.anchor = GridBagConstraints.EAST;
        productFieldsPanel.add(btnThemSPVaoBang, gbcFields);

        productSelectionPanel.add(productFieldsPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã SP", "Tên Sản Phẩm", "Số lượng", "Đơn giá nhập", "Thành tiền"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        productSelectionPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.setOpaque(false);
        centerContainer.add(topInputPanel, BorderLayout.NORTH);
        centerContainer.add(productSelectionPanel, BorderLayout.CENTER);
        mainPanel.add(centerContainer, BorderLayout.CENTER);

        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomButtonPanel.setOpaque(false);
        JButton btnXacNhanNhapHang = createStyledButton("Xác nhận nhập hàng", SUCCESS_BUTTON_COLOR);
        JButton btnBack = createStyledButton("Trở về", SECONDARY_BUTTON_COLOR);

        bottomButtonPanel.add(btnXacNhanNhapHang);
        bottomButtonPanel.add(btnBack);
        mainPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        btnThemSPVaoBang.addActionListener(e -> themSanPhamVaoPhieu());
        btnXacNhanNhapHang.addActionListener(e -> xacNhanNhap());
        btnBack.addActionListener(e -> dispose());
        setContentPane(mainPanel);
    }

    private void styleTable(JTable tbl) {
        tbl.setFillsViewportHeight(true);
        tbl.setFont(INPUT_FONT);
        tbl.getTableHeader().setFont(INPUT_FONT.deriveFont(Font.BOLD));
        tbl.setRowHeight(30);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.getTableHeader().setOpaque(false);
        tbl.getTableHeader().setBackground(new Color(32, 136, 203));
        tbl.getTableHeader().setForeground(Color.WHITE);

        TableColumnModel tcm = tbl.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(80);
        tcm.getColumn(1).setPreferredWidth(300);
        tcm.getColumn(2).setPreferredWidth(100);
        tcm.getColumn(3).setPreferredWidth(120);
        tcm.getColumn(4).setPreferredWidth(150);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setFocusPainted(false);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 40));
        btn.setBorder(BorderFactory.createEmptyBorder());
        return btn;
    }

    private void loadData() {
        // ... (phương thức này giữ nguyên, không thay đổi)
        try {
            List<NhaCungCap> dsNCC = NhaCungCapQuery.getAll();
            cboNCC.removeAllItems();
            cboNCC.addItem(new NhaCungCap(0, "-- Chọn nhà cung cấp --", "", ""));
            for (NhaCungCap ncc : dsNCC) { cboNCC.addItem(ncc); }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách Nhà Cung Cấp!", "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }

        try {
            List<SanPham> dsSP = SanPhamQuery.getAll();
            cboSanPham.removeAllItems();
            cboSanPham.addItem(new SanPham(0, "-- Chọn sản phẩm --", "", null, "", ""));
            for (SanPham sp : dsSP) { cboSanPham.addItem(sp); }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách Sản Phẩm!", "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void onProductSelected(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Object item = e.getItem();
            if (item instanceof SanPham && ((SanPham) item).getMaSP() != 0) {
                SanPham selectedProduct = (SanPham) item;

                new SwingWorker<BigDecimal, Void>() {
                    @Override
                    protected BigDecimal doInBackground() throws Exception {
                        return SanPhamQuery.getLastImportPrice(selectedProduct.getMaSP());
                    }

                    @Override
                    protected void done() {
                        try {
                            BigDecimal lastPrice = get();
                            if (lastPrice != null) {
                                // toPlainString() để hiển thị số thuần, không có ký tự khoa học
                                txtDonGia.setText(lastPrice.toPlainString());
                            } else {
                                // Nếu sản phẩm chưa từng được nhập, xóa trống ô đơn giá
                                txtDonGia.setText("");
                            }
                            // Chuyển con trỏ tới ô số lượng để tiện nhập liệu
                            txtSoLuong.requestFocusInWindow();
                        } catch (InterruptedException | ExecutionException ex) {
                            ex.printStackTrace();
                            txtDonGia.setText(""); // Xóa nếu có lỗi
                        }
                    }
                }.execute();
            } else {
                // Nếu người dùng chọn mục "-- Chọn...", xóa trống ô đơn giá
                txtDonGia.setText("");
            }
        }
    }

    private void themSanPhamVaoPhieu() {
        // ... (phương thức này giữ nguyên, không thay đổi)
        SanPham spChon = (SanPham) cboSanPham.getSelectedItem();
        if (spChon == null || spChon.getMaSP() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm hợp lệ.", "Chưa chọn sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int soLuong;
        BigDecimal donGiaNhap;

        try {
            soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            if (soLuong <= 0) throw new NumberFormatException("Số lượng phải lớn hơn 0");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là một số nguyên lớn hơn 0.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtSoLuong.requestFocus();
            return;
        }

        try {
            donGiaNhap = new BigDecimal(txtDonGia.getText().trim().replace(",", ""));
            if (donGiaNhap.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException("Đơn giá phải lớn hơn 0");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đơn giá nhập phải là một số hợp lệ và lớn hơn 0.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtDonGia.requestFocus();
            return;
        }

        for (NhapHangItem ct : dsNhap) {
            if (ct.getSanPham().getMaSP() == spChon.getMaSP()) {
                JOptionPane.showMessageDialog(this, "Sản phẩm '" + spChon.getTenSP() + "' đã có trong phiếu.", "Sản phẩm trùng lặp", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        NhapHangItem itemMoi = new NhapHangItem(spChon, soLuong, donGiaNhap);
        dsNhap.add(itemMoi);

        BigDecimal thanhTien = donGiaNhap.multiply(new BigDecimal(soLuong));
        tableModel.addRow(new Object[]{
                spChon.getMaSP(),
                spChon.getTenSP(),
                soLuong,
                currencyFormatter.format(donGiaNhap),
                currencyFormatter.format(thanhTien)
        });

        cboSanPham.setSelectedIndex(0);
        txtSoLuong.setText("");
        txtDonGia.setText("");
        cboSanPham.requestFocusInWindow();
    }

    private void xacNhanNhap() {
        // ... (phương thức này giữ nguyên, không thay đổi)
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
            dsNhap.clear();
            tableModel.setRowCount(0);
            cboNCC.setSelectedIndex(0);
            cboSanPham.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, "Nhập hàng thất bại. Giao dịch đã được hủy bỏ. Vui lòng kiểm tra log để biết chi tiết.", "Lỗi nhập hàng", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class NhaCungCapRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof NhaCungCap) {
                NhaCungCap ncc = (NhaCungCap) value;
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
            if (value instanceof SanPham) {
                SanPham sp = (SanPham) value;
                setText(sp.getMaSP() == 0 ? sp.getTenSP() : String.format("%s (%s)", sp.getTenSP(), sp.getHangSX()));
            }
            return this;
        }
    }
}