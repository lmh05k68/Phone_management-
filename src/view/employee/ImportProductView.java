package view.employee;

import controller.employee.ImportProductController;
import model.ChiTietHDNhap;
import model.NhaCungCap;
import model.SanPham;
import query.NhaCungCapQuery;
import query.SanPhamQuery;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
// import java.awt.event.ItemEvent; // Cho việc tự động điền đơn giá
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
    private final int maNV;

    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(0, 123, 255);
    private static final Color SUCCESS_BUTTON_COLOR = new Color(40, 167, 69);
    private static final Color SECONDARY_BUTTON_COLOR = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(0, 86, 179);

    public ImportProductView(int maNV) {
        this.maNV = maNV;
        System.out.println("IMPORT_PRODUCT_VIEW: Khoi tao cho NV (Ma: " + maNV + ")");
        setTitle("Nhap Hang");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Quan Ly Nhap Hang", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel topInputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.insets = new Insets(5, 5, 5, 5);
        gbcTop.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNCC = new JLabel("Nha cung cap*:");
        lblNCC.setFont(LABEL_FONT);
        gbcTop.gridx = 0; gbcTop.gridy = 0;
        gbcTop.weightx = 0.1;
        topInputPanel.add(lblNCC, gbcTop);

        cboNCC = new JComboBox<>();
        cboNCC.setFont(INPUT_FONT);
        gbcTop.gridx = 1; gbcTop.gridy = 0;
        gbcTop.weightx = 0.9;
        topInputPanel.add(cboNCC, gbcTop);

        JPanel productSelectionPanel = new JPanel(new BorderLayout(10,10));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Them san pham vao phieu nhap");
        titledBorder.setTitleFont(LABEL_FONT.deriveFont(Font.BOLD));
        productSelectionPanel.setBorder(titledBorder);

        JPanel productFieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcFields = new GridBagConstraints();
        gbcFields.insets = new Insets(5, 5, 5, 5);
        gbcFields.anchor = GridBagConstraints.WEST;

        JLabel lblSanPham = new JLabel("San pham:");
        lblSanPham.setFont(LABEL_FONT);
        gbcFields.gridx = 0; gbcFields.gridy = 0;
        productFieldsPanel.add(lblSanPham, gbcFields);

        cboSanPham = new JComboBox<>();
        cboSanPham.setFont(INPUT_FONT);
        cboSanPham.setPreferredSize(new Dimension(280, cboSanPham.getPreferredSize().height));
        gbcFields.gridx = 1; gbcFields.gridy = 0; gbcFields.gridwidth = 3;
        gbcFields.fill = GridBagConstraints.HORIZONTAL;
        gbcFields.weightx = 1.0;
        productFieldsPanel.add(cboSanPham, gbcFields);

        JLabel lblSoLuong = new JLabel("So luong:");
        lblSoLuong.setFont(LABEL_FONT);
        gbcFields.gridx = 0; gbcFields.gridy = 1;
        gbcFields.gridwidth = 1; gbcFields.fill = GridBagConstraints.NONE; gbcFields.weightx = 0;
        productFieldsPanel.add(lblSoLuong, gbcFields);

        txtSoLuong = new JTextField(7);
        txtSoLuong.setFont(INPUT_FONT);
        gbcFields.gridx = 1; gbcFields.gridy = 1;
        gbcFields.fill = GridBagConstraints.HORIZONTAL; gbcFields.weightx = 0.3;
        productFieldsPanel.add(txtSoLuong, gbcFields);

        JLabel lblDonGia = new JLabel("Don gia nhap:");
        lblDonGia.setFont(LABEL_FONT);
        gbcFields.gridx = 2; gbcFields.gridy = 1;
        gbcFields.fill = GridBagConstraints.NONE; gbcFields.weightx = 0;
        productFieldsPanel.add(lblDonGia, gbcFields);

        txtDonGia = new JTextField(10);
        txtDonGia.setFont(INPUT_FONT);
        gbcFields.gridx = 3; gbcFields.gridy = 1;
        gbcFields.fill = GridBagConstraints.HORIZONTAL; gbcFields.weightx = 0.3;
        productFieldsPanel.add(txtDonGia, gbcFields);

        JButton btnThemSPVaoBang = createStyledButton("Them vao phieu");
        btnThemSPVaoBang.setMargin(new Insets(5, 10, 5, 10));
        gbcFields.gridx = 4; gbcFields.gridy = 1;
        gbcFields.fill = GridBagConstraints.NONE; gbcFields.weightx = 0.1;
        gbcFields.anchor = GridBagConstraints.EAST;
        productFieldsPanel.add(btnThemSPVaoBang, gbcFields);

        productSelectionPanel.add(productFieldsPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Ma SP", "Ten SP", "So luong", "Don gia nhap", "Thanh tien"}, 0){
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
        JButton btnXacNhanNhapHang = createStyledButton("Xac nhan nhap hang");
        btnXacNhanNhapHang.setBackground(SUCCESS_BUTTON_COLOR);
        JButton btnBack = createStyledButton("Tro ve");
        btnBack.setBackground(SECONDARY_BUTTON_COLOR);

        bottomButtonPanel.add(btnXacNhanNhapHang);
        bottomButtonPanel.add(btnBack);
        mainPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        btnThemSPVaoBang.addActionListener(e -> themSanPhamVaoPhieu());
        btnXacNhanNhapHang.addActionListener(e -> xacNhanNhap());
        btnBack.addActionListener(e -> {
            dispose();
            // new EmployeeMenuView(maNV).setVisible(true); // Example
        });

        setContentPane(mainPanel);
    }

    private void styleTable(JTable tbl) {
        tbl.setFillsViewportHeight(true);
        tbl.setFont(INPUT_FONT);
        tbl.getTableHeader().setFont(INPUT_FONT.deriveFont(Font.BOLD));
        tbl.setRowHeight(30);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoCreateRowSorter(true);

        TableColumnModel tcm = tbl.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(80);
        tcm.getColumn(1).setPreferredWidth(250);
        tcm.getColumn(2).setPreferredWidth(100);
        tcm.getColumn(3).setPreferredWidth(120);
        tcm.getColumn(4).setPreferredWidth(120);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setFocusPainted(false);
        btn.setBackground(PRIMARY_BUTTON_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return btn;
    }

    private void loadData() {
        System.out.println("IMPORT_PRODUCT_VIEW: Loading data for ComboBoxes...");
        try {
            List<NhaCungCap> dsNCC = NhaCungCapQuery.getAll();
            cboNCC.removeAllItems();
            if (dsNCC != null && !dsNCC.isEmpty()) {
                for (NhaCungCap ncc : dsNCC) {
                    cboNCC.addItem(ncc);
                }
                cboNCC.setSelectedItem(null);
                System.out.println("IMPORT_PRODUCT_VIEW: Loaded " + dsNCC.size() + " NhaCungCap.");
            } else {
                System.out.println("IMPORT_PRODUCT_VIEW: No NhaCungCap found or error loading.");
                // Placeholder cho NhaCungCap (điều chỉnh constructor nếu cần)
                // Giả sử NhaCungCap có constructor (int maNCC, String tenNCC, String diaChi, String sdt)
                NhaCungCap placeholderNCC = new NhaCungCap(0, "Khong tim thay NCC", "N/A", "N/A");
                // Nếu NhaCungCap chỉ có constructor (int maNCC, String tenNCC)
                // NhaCungCap placeholderNCC = new NhaCungCap(0, "Khong tim thay NCC");
                cboNCC.addItem(placeholderNCC);
            }
        } catch (Exception e) {
            System.err.println("IMPORT_PRODUCT_VIEW: Error loading NhaCungCap: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Loi tai danh sach Nha Cung Cap!", "Loi Du Lieu", JOptionPane.ERROR_MESSAGE);
        }

        try {
            List<SanPham> dsSP = SanPhamQuery.getAll();
            cboSanPham.removeAllItems();
            if (dsSP != null && !dsSP.isEmpty()) {
                for (SanPham sp : dsSP) {
                    cboSanPham.addItem(sp);
                }
                cboSanPham.setSelectedItem(null);
                System.out.println("IMPORT_PRODUCT_VIEW: Loaded " + dsSP.size() + " SanPham.");
            } else {
                System.out.println("IMPORT_PRODUCT_VIEW: No SanPham found or error loading.");
                // SỬA LỖI: Gọi constructor SanPham 7 tham số cho placeholder
                cboSanPham.addItem(new SanPham(
                        0,                         // maSP (int)
                        "Khong tim thay SP",       // tenSP (String)
                        "N/A",                     // mau (String)
                        0.0,                       // donGia (double) - Đây là giá bán trong model SanPham, không phải giá nhập
                        "N/A",                     // nuocSX (String)
                        "N/A",                     // hangSX (String)
                        0                          // soLuong (int) - Số lượng tồn kho
                ));
            }
        } catch (Exception e) {
            System.err.println("IMPORT_PRODUCT_VIEW: Error loading SanPham: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Loi tai danh sach San Pham!", "Loi Du Lieu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themSanPhamVaoPhieu() {
        SanPham spChon = (SanPham) cboSanPham.getSelectedItem();
        if (spChon == null || spChon.getMaSP() == 0) { // Kiểm tra cả placeholder
            JOptionPane.showMessageDialog(this, "Vui long chon mot san pham hop le.", "Chua chon san pham", JOptionPane.WARNING_MESSAGE);
            cboSanPham.requestFocusInWindow();
            return;
        }

        String soLuongStr = txtSoLuong.getText().trim();
        String donGiaStr = txtDonGia.getText().trim();

        if (soLuongStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap So luong.", "Thieu so luong", JOptionPane.WARNING_MESSAGE);
            txtSoLuong.requestFocusInWindow();
            return;
        }
        if (donGiaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap Don gia nhap.", "Thieu don gia", JOptionPane.WARNING_MESSAGE);
            txtDonGia.requestFocusInWindow();
            return;
        }

        int soLuong;
        double donGiaNhap; // Đổi tên biến để rõ ràng hơn

        try {
            soLuong = Integer.parseInt(soLuongStr);
            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(this, "So luong phai lon hon 0.", "So luong khong hop le", JOptionPane.WARNING_MESSAGE);
                txtSoLuong.requestFocusInWindow();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "So luong phai la mot so nguyen hop le.", "Loi dinh dang so luong", JOptionPane.ERROR_MESSAGE);
            txtSoLuong.requestFocusInWindow();
            return;
        }

        try {
            // Loại bỏ dấu phẩy nếu người dùng nhập theo kiểu 1,000,000
            donGiaNhap = Double.parseDouble(donGiaStr.replace(",", ""));
            if (donGiaNhap <= 0) {
                JOptionPane.showMessageDialog(this, "Don gia nhap phai lon hon 0.", "Don gia khong hop le", JOptionPane.WARNING_MESSAGE);
                txtDonGia.requestFocusInWindow();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Don gia nhap phai la mot so hop le.", "Loi dinh dang don gia", JOptionPane.ERROR_MESSAGE);
            txtDonGia.requestFocusInWindow();
            return;
        }

        for (ChiTietHDNhap ctTrongPhieu : dsNhap) {
            if (ctTrongPhieu.getMaSP() == spChon.getMaSP()) {
                JOptionPane.showMessageDialog(this, "San pham '" + spChon.getTenSP() + "' da duoc them vao phieu truoc do.", "San pham da ton tai", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        // MaHDN sẽ được gán bởi controller sau khi HoaDonNhap được tạo
        ChiTietHDNhap chiTietMoi = new ChiTietHDNhap(spChon.getMaSP(), 0, soLuong, donGiaNhap); // MaHDN tạm là 0
        dsNhap.add(chiTietMoi);
        tableModel.addRow(new Object[]{
                spChon.getMaSP(),
                spChon.getTenSP(),
                soLuong,
                String.format("%,.0f", donGiaNhap), // Định dạng đơn giá nhập có dấu phẩy, không có phần thập phân
                String.format("%,.0f", soLuong * donGiaNhap) // Định dạng thành tiền
        });

        txtSoLuong.setText("");
        txtDonGia.setText("");
        cboSanPham.setSelectedItem(null);
        cboSanPham.requestFocusInWindow();
        System.out.println("IMPORT_PRODUCT_VIEW: Da them SP ID " + spChon.getMaSP() + " vao phieu tam.");
    }

    private void xacNhanNhap() {
        if (dsNhap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long them it nhat mot san pham vao phieu nhap.", "Phieu nhap trong", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhaCungCap nccChon = (NhaCungCap) cboNCC.getSelectedItem();
        if (nccChon == null || nccChon.getMaNCC() == 0) { // Kiểm tra cả placeholder
            JOptionPane.showMessageDialog(this, "Vui long chon nha cung cap hop le.", "Thieu Nha Cung Cap", JOptionPane.WARNING_MESSAGE);
            cboNCC.requestFocusInWindow();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Ban co chac chan muon xac nhan nhap hang voi cac san pham da chon khong?",
                "Xac nhan nhap hang",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.NO_OPTION) {
            return;
        }

        try {
            ImportProductController controller = new ImportProductController();
            boolean success = controller.nhapHang(maNV, nccChon.getMaNCC(), dsNhap);

            if (success) {
                JOptionPane.showMessageDialog(this, "Nhap hang thanh cong!", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
                dsNhap.clear();
                tableModel.setRowCount(0);
                txtSoLuong.setText("");
                txtDonGia.setText("");
                cboNCC.setSelectedItem(null);
                cboSanPham.setSelectedItem(null);
                System.out.println("IMPORT_PRODUCT_VIEW: Nhap hang thanh cong, form da duoc reset.");
            } else {
                JOptionPane.showMessageDialog(this, "Nhap hang that bai. Vui long kiem tra lai thong tin.", "Loi nhap hang", JOptionPane.ERROR_MESSAGE);
                System.err.println("IMPORT_PRODUCT_VIEW: Loi tu controller khi xac nhan nhap hang.");
            }
        } catch (Exception e) {
            System.err.println("IMPORT_PRODUCT_VIEW: Exception khi goi controller.nhapHang: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Da xay ra loi khong mong muon khi xu ly yeu cau.", "Loi He Thong", JOptionPane.ERROR_MESSAGE);
        }
    }
}