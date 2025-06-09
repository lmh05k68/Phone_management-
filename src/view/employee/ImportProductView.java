package view.employee;

import controller.employee.ImportProductController;
import model.ChiTietHDNhap;
import model.NhaCungCap;
import model.SanPham;
import query.NhaCungCapQuery;
import query.SanPhamQuery; // Đảm bảo tên phương thức gọi khớp

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ImportProductView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<NhaCungCap> cboNCC;
    private JComboBox<SanPham> cboSanPham;
    private JTextField txtSoLuong, txtDonGia;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<ChiTietHDNhap> dsNhap = new ArrayList<>();
    private final int maNV; // Nên là final nếu không thay đổi sau khi khởi tạo

    public ImportProductView(int maNV) {
        this.maNV = maNV;
        System.out.println("IMPORT_PRODUCT_VIEW: Khởi tạo cho NV (Mã: " + maNV + ")");
        setTitle("Nhập Hàng");
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Quản Lý Nhập Hàng", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel topInputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.insets = new Insets(5, 5, 5, 5);
        gbcTop.fill = GridBagConstraints.HORIZONTAL;

        gbcTop.gridx = 0; gbcTop.gridy = 0;
        gbcTop.weightx = 0.2;
        topInputPanel.add(new JLabel("Nhà cung cấp*:"), gbcTop);
        gbcTop.gridx = 1; gbcTop.gridy = 0;
        gbcTop.weightx = 0.8;
        cboNCC = new JComboBox<>();
        cboNCC.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Thêm font
        topInputPanel.add(cboNCC, gbcTop);

        JPanel productSelectionPanel = new JPanel(new BorderLayout(10,10));
        productSelectionPanel.setBorder(BorderFactory.createTitledBorder("Thêm sản phẩm vào phiếu nhập"));

        JPanel productFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        cboSanPham = new JComboBox<>();
        cboSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Thêm font
        cboSanPham.setPreferredSize(new Dimension(250, cboSanPham.getPreferredSize().height)); // Tăng chiều rộng
        txtSoLuong = new JTextField(5);
        txtSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDonGia = new JTextField(8);
        txtDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnThemSPVaoBang = createStyledButton("Thêm vào phiếu");
        btnThemSPVaoBang.setMargin(new Insets(5,10,5,10));

        productFieldsPanel.add(new JLabel("Sản phẩm:"));
        productFieldsPanel.add(cboSanPham);
        productFieldsPanel.add(new JLabel("Số lượng:"));
        productFieldsPanel.add(txtSoLuong);
        productFieldsPanel.add(new JLabel("Đơn giá nhập:"));
        productFieldsPanel.add(txtDonGia);
        productFieldsPanel.add(btnThemSPVaoBang);

        productSelectionPanel.add(productFieldsPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã SP", "Tên SP", "Số lượng", "Đơn giá nhập", "Thành tiền"}, 0){
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        productSelectionPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel centerContainer = new JPanel(new BorderLayout(10,10));
        centerContainer.add(topInputPanel, BorderLayout.NORTH);
        centerContainer.add(productSelectionPanel, BorderLayout.CENTER);
        mainPanel.add(centerContainer, BorderLayout.CENTER);

        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnXacNhanNhapHang = createStyledButton("Xác nhận nhập hàng");
        btnXacNhanNhapHang.setBackground(new Color(40, 167, 69)); // Màu xanh lá
        JButton btnBack = createStyledButton("Trở về");
        btnBack.setBackground(new Color(108, 117, 125));

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
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.setRowHeight(28); // Tăng chiều cao hàng
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoCreateRowSorter(true); // Cho phép sắp xếp
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return btn;
    }

    private void loadData() {
        System.out.println("IMPORT_PRODUCT_VIEW: Loading data for ComboBoxes...");
        List<NhaCungCap> dsNCC = NhaCungCapQuery.getAll(); // Gọi phương thức static
        cboNCC.removeAllItems();
        if (dsNCC != null && !dsNCC.isEmpty()) {
            for (NhaCungCap ncc : dsNCC) {
                cboNCC.addItem(ncc); // JComboBox sẽ gọi toString() của NhaCungCap
            }
            System.out.println("IMPORT_PRODUCT_VIEW: Loaded " + dsNCC.size() + " NhaCungCap.");
        } else {
            System.out.println("IMPORT_PRODUCT_VIEW: No NhaCungCap found or error loading.");
        }

        // Gọi SanPhamQuery.getAll() hoặc một phương thức tương tự
        List<SanPham> dsSP = SanPhamQuery.getAll(); // Đảm bảo đây là phương thức đúng
        cboSanPham.removeAllItems();
        if (dsSP != null && !dsSP.isEmpty()) {
            for (SanPham sp : dsSP) {
                cboSanPham.addItem(sp); // JComboBox sẽ gọi toString() của SanPham
            }
             System.out.println("IMPORT_PRODUCT_VIEW: Loaded " + dsSP.size() + " SanPham.");
        } else {
            System.out.println("IMPORT_PRODUCT_VIEW: No SanPham found or error loading.");
        }
    }

    private void themSanPhamVaoPhieu() {
        SanPham spChon = (SanPham) cboSanPham.getSelectedItem();
        if (spChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm.", "Chưa chọn sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String soLuongStr = txtSoLuong.getText().trim();
        String donGiaStr = txtDonGia.getText().trim();

        if (soLuongStr.isEmpty() || donGiaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Số lượng và Đơn giá nhập.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int soLuong = Integer.parseInt(soLuongStr);
            double donGia = Double.parseDouble(donGiaStr);

            if (soLuong <= 0 || donGia <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng và Đơn giá nhập phải lớn hơn 0.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra sản phẩm đã có trong danh sách dsNhap chưa
            for (ChiTietHDNhap ctTrongPhieu : dsNhap) {
                if (ctTrongPhieu.getMaSP() == spChon.getMaSP()) {
                    JOptionPane.showMessageDialog(this, "Sản phẩm '" + spChon.getTenSP() + "' đã được thêm vào phiếu trước đó.", "Sản phẩm đã tồn tại", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            // MaHDN sẽ được gán bởi controller sau khi HoaDonNhap được tạo
            ChiTietHDNhap chiTietMoi = new ChiTietHDNhap(spChon.getMaSP(), 0, soLuong, donGia); // MaHDN tạm là 0
            dsNhap.add(chiTietMoi);
            tableModel.addRow(new Object[]{spChon.getMaSP(), spChon.getTenSP(), soLuong, donGia, soLuong * donGia});

            // Xóa nội dung các trường input sau khi thêm thành công
            txtSoLuong.setText("");
            txtDonGia.setText("");
            cboSanPham.setSelectedIndex(-1); // Bỏ chọn sản phẩm hiện tại (hoặc focus)
            cboSanPham.requestFocus(); // Focus lại vào combobox sản phẩm
            System.out.println("IMPORT_PRODUCT_VIEW: Đã thêm SP ID " + spChon.getMaSP() + " vào phiếu tạm.");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng và Đơn giá nhập phải là số hợp lệ.", "Lỗi định dạng số", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xacNhanNhap() {
        if (dsNhap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm vào phiếu nhập.", "Phiếu nhập trống", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhaCungCap nccChon = (NhaCungCap) cboNCC.getSelectedItem();
        if (nccChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp.", "Thiếu Nhà Cung Cấp", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Xác nhận lại với người dùng
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xác nhận nhập hàng với các sản phẩm đã chọn không?",
                "Xác nhận nhập hàng",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.NO_OPTION) {
            return;
        }


        ImportProductController controller = new ImportProductController();
        boolean success = controller.nhapHang(maNV, nccChon.getMaNCC(), dsNhap);

        if (success) {
            JOptionPane.showMessageDialog(this, "Nhập hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dsNhap.clear(); // Xóa danh sách tạm
            tableModel.setRowCount(0); 
             System.out.println("IMPORT_PRODUCT_VIEW: Nhập hàng thành công, form đã được reset.");
        } else {
            JOptionPane.showMessageDialog(this, "Nhập hàng thất bại. Vui lòng kiểm tra lại thông tin và log lỗi từ console.", "Lỗi nhập hàng", JOptionPane.ERROR_MESSAGE);
            System.err.println("IMPORT_PRODUCT_VIEW: Lỗi từ controller khi xác nhận nhập hàng.");
        }
    }
}