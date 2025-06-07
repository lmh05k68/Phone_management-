package view.employee;

import controller.employee.ImportProductController;
import model.ChiTietHDNhap;
import model.NhaCungCap;
import model.SanPham;
import query.NhaCungCapQuery;
import query.SanPhamQuery;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ImportProductView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtMaHDN;
    private JComboBox<NhaCungCap> cboNCC;
    private JComboBox<SanPham> cboSanPham;
    private JTextField txtSoLuong, txtDonGia;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<ChiTietHDNhap> dsNhap = new ArrayList<>();
    private String maNV;

    public ImportProductView(String maNV) {
        this.maNV = maNV;
        setTitle("Nhập hàng");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadData();
        setVisible(true);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("Quản lý nhập hàng", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        txtMaHDN = new JTextField();
        cboNCC = new JComboBox<>();
        cboSanPham = new JComboBox<>();
        txtSoLuong = new JTextField();
        txtDonGia = new JTextField();

        inputPanel.add(new JLabel("Mã hóa đơn:"));
        inputPanel.add(txtMaHDN);
        inputPanel.add(new JLabel("Nhà cung cấp:"));
        inputPanel.add(cboNCC);
        inputPanel.add(new JLabel("Sản phẩm:"));
        inputPanel.add(cboSanPham);
        inputPanel.add(new JLabel("Số lượng:"));
        inputPanel.add(txtSoLuong);
        inputPanel.add(new JLabel("Đơn giá:"));
        inputPanel.add(txtDonGia);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã SP", "Tên SP", "Số lượng", "Đơn giá"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        JButton btnThem = createStyledButton("Thêm sản phẩm");
        JButton btnNhap = createStyledButton("Xác nhận nhập hàng");
        buttonPanel.add(btnThem);
        buttonPanel.add(btnNhap);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        btnThem.addActionListener(e -> themSanPham());
        btnNhap.addActionListener(e -> xacNhanNhap());

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JButton btnBack = createStyledButton("Trở về");
        btnBack.setBackground(new Color(76, 175, 80));
        btnBack.addActionListener(e -> {
            dispose();
            new EmployeeView(maNV).setVisible(true);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnBack);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(33, 150, 243));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(25, 118, 210)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return btn;
    }

    private void loadData() {
        List<NhaCungCap> dsNCC = new NhaCungCapQuery().getAllNhaCungCap();
        cboNCC.removeAllItems();
        for (NhaCungCap ncc : dsNCC) {
            cboNCC.addItem(ncc);
        }

        List<SanPham> dsSP = new SanPhamQuery().getAllSanPham();
        cboSanPham.removeAllItems();
        for (SanPham sp : dsSP) {
            cboSanPham.addItem(sp);
        }
    }

    private void themSanPham() {
        SanPham sp = (SanPham) cboSanPham.getSelectedItem();
        if (sp == null) return;

        try {
            int soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            double donGia = Double.parseDouble(txtDonGia.getText().trim());

            for (ChiTietHDNhap ct : dsNhap) {
                if (ct.getMaSP().equals(sp.getMaSP())) {
                    JOptionPane.showMessageDialog(this, "Sản phẩm đã được thêm trước đó.");
                    return;
                }
            }

            ChiTietHDNhap ct = new ChiTietHDNhap(sp.getMaSP(), "", soLuong, donGia);
            dsNhap.add(ct);
            tableModel.addRow(new Object[]{sp.getMaSP(), sp.getTenSP(), soLuong, donGia});

            txtSoLuong.setText("");
            txtDonGia.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng và đơn giá phải là số.");
        }
    }

    private void xacNhanNhap() {
        String maHDN = txtMaHDN.getText().trim();
        if (maHDN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn.");
            return;
        }

        if (dsNhap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm sản phẩm trước khi xác nhận.");
            return;
        }

        NhaCungCap ncc = (NhaCungCap) cboNCC.getSelectedItem();
        if (ncc == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp.");
            return;
        }

        // Gán mã hóa đơn cho từng chi tiết
        for (ChiTietHDNhap ct : dsNhap) {
            ct.setMaHDN(maHDN);
        }

        ImportProductController controller = new ImportProductController();
        boolean success = controller.nhapHang(maHDN, maNV, ncc.getMaNCC(), dsNhap);

        if (success) {
            JOptionPane.showMessageDialog(this, "Nhập hàng thành công!");
            dsNhap.clear();
            tableModel.setRowCount(0);
            txtMaHDN.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Nhập hàng thất bại.");
        }
    }
}