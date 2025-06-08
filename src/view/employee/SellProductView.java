package view.employee;

import controller.employee.SellProduct;
import model.ChiTietHDXuat;
import model.KhachHang; // Thêm import
import model.SanPham;
import query.KhachHangQuery; // Thêm import
import query.SanPhamQuery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SellProductView extends JFrame {
    private static final long serialVersionUID = 1L;
    private String maNV;
    private JTextField tfMaKH, tfTenKH, tfSdtKH, tfMaHDX, tfMaTichDiem;
    private JSpinner spinnerSoLuong;
    private JTable tableSanPham, tableHoaDon;
    private DefaultTableModel modelSanPham, modelHoaDon;
    private JLabel lblTongTien, lblGiamGia; // Thêm lblGiamGia
    private JButton btnSuDungDiem, btnBack; // Thêm btnSuDungDiem

    private final SellProduct sellProductController = new SellProduct();
    private final SanPhamQuery spQuery = new SanPhamQuery();
    private final KhachHangQuery khachHangQuery = new KhachHangQuery(); // Khởi tạo

    private boolean suDungDiemDangApDung = false;
    private int phanTramGiamHienTai = 0;

    public SellProductView(String maNV) {
        this.maNV = maNV;
        System.out.println("SELL_VIEW: Khởi tạo SellProductView cho NV: " + maNV);
        setTitle("Bán hàng - Nhân viên " + maNV);
        setSize(1050, 750); // Tăng chiều cao một chút
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        tfMaHDX.setText("HDX" + System.currentTimeMillis() % 100000); // Gợi ý mã HDX
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Top Panel: HDX, KH, Tích điểm
        JPanel topInfoPanelContainer = new JPanel(new BorderLayout(10,5));

        JPanel panelHDXAndKH = new JPanel(new GridLayout(2,1,5,5));

        JPanel panelHDX = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelHDX.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));
        tfMaHDX = new JTextField(15);
        panelHDX.add(new JLabel("Mã HDX*:"));
        panelHDX.add(tfMaHDX);
        panelHDXAndKH.add(panelHDX);

        JPanel panelKH = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        tfMaKH = new JTextField(10);
        tfTenKH = new JTextField(15);
        tfSdtKH = new JTextField(10);
        panelKH.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng (nếu có)"));
        panelKH.add(new JLabel("Mã KH:"));
        panelKH.add(tfMaKH);
        panelKH.add(new JLabel("Tên KH:"));
        panelKH.add(tfTenKH);
        panelKH.add(new JLabel("SĐT KH:"));
        panelKH.add(tfSdtKH);
        panelHDXAndKH.add(panelKH);

        topInfoPanelContainer.add(panelHDXAndKH, BorderLayout.CENTER);

        JPanel panelTichDiemVaSuDungDiem = new JPanel(new BorderLayout(10,5));
        panelTichDiemVaSuDungDiem.setBorder(BorderFactory.createTitledBorder("Điểm thưởng (nếu có Mã KH)"));

        JPanel panelTichDiemInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,0));
        tfMaTichDiem = new JTextField(10);
        panelTichDiemInput.add(new JLabel("Mã tích điểm*:"));
        panelTichDiemInput.add(tfMaTichDiem);

        btnSuDungDiem = createStyledButton("Sử dụng điểm thưởng");
        btnSuDungDiem.setEnabled(false); // Ban đầu disable, chỉ enable khi có MaKH
        lblGiamGia = new JLabel("Giảm giá: 0%");
        lblGiamGia.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        JPanel panelSuDungDiemControls = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        panelSuDungDiemControls.add(btnSuDungDiem);
        panelSuDungDiemControls.add(lblGiamGia);

        panelTichDiemVaSuDungDiem.add(panelTichDiemInput, BorderLayout.NORTH);
        panelTichDiemVaSuDungDiem.add(panelSuDungDiemControls, BorderLayout.SOUTH);

        topInfoPanelContainer.add(panelTichDiemVaSuDungDiem, BorderLayout.SOUTH);


        // Tables
        String[] colsSP = {"Mã SP", "Tên SP", "Màu", "Đơn giá", "Nước SX", "Hãng SX", "Tồn kho"};
        modelSanPham = new DefaultTableModel(colsSP, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableSanPham = new JTable(modelSanPham);
        styleTable(tableSanPham);
        JScrollPane scrollSP = new JScrollPane(tableSanPham);
        scrollSP.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));

        String[] colsHD = {"Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền"};
        modelHoaDon = new DefaultTableModel(colsHD, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableHoaDon = new JTable(modelHoaDon);
        styleTable(tableHoaDon);
        JScrollPane scrollHD = new JScrollPane(tableHoaDon);
        scrollHD.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));

        // Control Panel for adding/removing items and creating invoice
        JPanel panelControl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        spinnerSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        ((JSpinner.DefaultEditor) spinnerSoLuong.getEditor()).getTextField().setColumns(3);

        JButton btnThem = createStyledButton("Thêm vào HĐ");
        JButton btnXoa = createStyledButton("Xóa khỏi HĐ");
        JButton btnBan = createStyledButton("Tạo Hóa Đơn");

        panelControl.add(new JLabel("Số lượng:"));
        panelControl.add(spinnerSoLuong);
        panelControl.add(btnThem);
        panelControl.add(btnXoa);
        panelControl.add(btnBan);

        // Bottom Panel: Total amount and Back button
        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongTien.setBorder(new EmptyBorder(5,0,5,5));

        btnBack = createStyledButton("Trở về");
        JPanel bottomPanelContainer = new JPanel(new BorderLayout());
        bottomPanelContainer.add(lblTongTien, BorderLayout.CENTER);
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.add(btnBack);
        bottomPanelContainer.add(backButtonPanel, BorderLayout.EAST);
        bottomPanelContainer.setBorder(new EmptyBorder(10, 10, 5, 10));

        // Split Pane for tables
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollSP, scrollHD);
        split.setDividerLocation(280);
        split.setResizeWeight(0.4);

        // Add components to content panel
        content.add(topInfoPanelContainer, BorderLayout.NORTH);
        content.add(split, BorderLayout.CENTER);
        content.add(panelControl, BorderLayout.SOUTH);

        // Add content and bottom panel to frame
        add(content, BorderLayout.CENTER);
        add(bottomPanelContainer, BorderLayout.SOUTH);

        // Action Listeners
        btnThem.addActionListener(e -> themVaoHoaDon());
        btnXoa.addActionListener(e -> xoaKhoiHoaDon());
        btnBan.addActionListener(e -> banHang());
        btnBack.addActionListener(e -> {
            System.out.println("SELL_VIEW: Nút 'Trở về' được nhấn.");
            this.dispose();
        });
        btnSuDungDiem.addActionListener(e -> toggleSuDungDiem());

        // Document listener for MaKH to enable/disable reward features
        tfMaKH.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { handleMaKHChange(); }
            public void removeUpdate(DocumentEvent e) { handleMaKHChange(); }
            public void insertUpdate(DocumentEvent e) { handleMaKHChange(); }
        });

        loadSanPham();
        System.out.println("SELL_VIEW: initUI hoàn tất.");
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179), 1),
                BorderFactory.createEmptyBorder(7, 15, 7, 15)
        ));
        return btn;
    }

    private void loadSanPham() {
        System.out.println("SELL_VIEW: Bắt đầu tải danh sách sản phẩm.");
        modelSanPham.setRowCount(0);
        List<SanPham> sanPhams = spQuery.getAllSanPham();
        if (sanPhams != null && !sanPhams.isEmpty()) {
            for (SanPham sp : sanPhams) {
                modelSanPham.addRow(new Object[]{
                        sp.getMaSP(), sp.getTenSP(), sp.getMau(), sp.getDonGia(),
                        sp.getNuocSX(), sp.getHangSX(), sp.getSoLuong()
                });
            }
            System.out.println("SELL_VIEW: Đã tải " + sanPhams.size() + " sản phẩm.");
        } else {
            System.out.println("SELL_VIEW: Không có sản phẩm nào để tải hoặc có lỗi.");
        }
    }

    private void updateTotalAmount() {
        double subTotal = 0;
        for (int i = 0; i < modelHoaDon.getRowCount(); i++) {
            subTotal += (double) modelHoaDon.getValueAt(i, 4);
        }

        double finalTotal = subTotal;
        if (suDungDiemDangApDung && phanTramGiamHienTai > 0) {
            finalTotal = subTotal * (1 - (double) phanTramGiamHienTai / 100.0);
        }
        // Thuế 10% được tính trên số tiền sau khi đã giảm giá (nếu có)
        finalTotal = finalTotal * 1.10; // Cộng 10% VAT

        lblTongTien.setText(String.format("Tổng tiền: %,.0f VNĐ", finalTotal));
    }

    private void handleMaKHChange() {
        String maKH = tfMaKH.getText().trim();
        if (maKH.isEmpty()) {
            btnSuDungDiem.setEnabled(false);
            tfMaTichDiem.setEnabled(false); // Chỉ cho nhập mã tích điểm khi có mã KH
            tfMaTichDiem.setText("");
            // Reset trạng thái dùng điểm nếu MaKH bị xóa
            if (suDungDiemDangApDung) {
                suDungDiemDangApDung = false;
                phanTramGiamHienTai = 0;
                btnSuDungDiem.setText("Sử dụng điểm thưởng");
                lblGiamGia.setText("Giảm giá: 0%");
                updateTotalAmount();
            }
        } else {
            btnSuDungDiem.setEnabled(true);
            tfMaTichDiem.setEnabled(true); // Cho phép nhập mã tích điểm
            // Tự động điền thông tin KH nếu có
            KhachHang kh = KhachHangQuery.getKhachHangById(maKH);
            if (kh != null) {
                tfTenKH.setText(kh.getHoTen());
                tfSdtKH.setText(kh.getSdtKH());
            } else {
                // Nếu không tìm thấy KH, có thể xóa hoặc để người dùng tự nhập
                // tfTenKH.setText("");
                // tfSdtKH.setText("");
            }
        }
    }
     private void toggleSuDungDiem() {
        String maKH = tfMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Khách Hàng trước.", "Thiếu Mã KH", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!suDungDiemDangApDung) { // Đang muốn sử dụng điểm
            int diemHienTai = khachHangQuery.getSoDiemTichLuy(maKH);
            if (diemHienTai <= 0) {
                JOptionPane.showMessageDialog(this, "Khách hàng không có điểm thưởng hoặc không đủ điểm.", "Không có điểm", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            phanTramGiamHienTai = khachHangQuery.tinhPhanTramGiamTuDiem(maKH);
            if (phanTramGiamHienTai > 0) {
                suDungDiemDangApDung = true;
                btnSuDungDiem.setText("Hủy dùng điểm");
                btnSuDungDiem.setBackground(new Color(220, 53, 69)); // Màu đỏ cho hủy
                lblGiamGia.setText(String.format("Giảm giá: %d%% (Điểm: %d)", phanTramGiamHienTai, diemHienTai));
                tfMaTichDiem.setEnabled(false); // Không cho nhập mã tích điểm khi đang dùng điểm
                tfMaTichDiem.setText(""); // Xóa mã tích điểm nếu có
                System.out.println("SELL_VIEW: Áp dụng " + phanTramGiamHienTai + "% giảm giá từ điểm.");
            } else {
                JOptionPane.showMessageDialog(this, "Không đủ điểm để được giảm giá.", "Không đủ điểm", JOptionPane.INFORMATION_MESSAGE);
            }
        } else { // Đang muốn hủy sử dụng điểm
            suDungDiemDangApDung = false;
            phanTramGiamHienTai = 0;
            btnSuDungDiem.setText("Sử dụng điểm thưởng");
            btnSuDungDiem.setBackground(new Color(0, 123, 255)); // Màu xanh mặc định
            lblGiamGia.setText("Giảm giá: 0%");
            tfMaTichDiem.setEnabled(true); // Cho phép nhập lại mã tích điểm
            System.out.println("SELL_VIEW: Hủy sử dụng điểm thưởng.");
        }
        updateTotalAmount();
    }


    private void themVaoHoaDon() {
        int selectedRowSP = tableSanPham.getSelectedRow();
        if (selectedRowSP == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm từ danh sách để thêm.", "Chưa chọn sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int soLuongMua = (int) spinnerSoLuong.getValue();
        if (soLuongMua <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0.", "Số lượng không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maSP = modelSanPham.getValueAt(selectedRowSP, 0).toString();
        String tenSP = modelSanPham.getValueAt(selectedRowSP, 1).toString();
        double donGia = (double) modelSanPham.getValueAt(selectedRowSP, 3);
        int tonKhoHienTai = (int) modelSanPham.getValueAt(selectedRowSP, 6);

        for (int i = 0; i < modelHoaDon.getRowCount(); i++) {
            if (modelHoaDon.getValueAt(i, 0).toString().equals(maSP)) {
                int soLuongTrongHDHienTai = (int) modelHoaDon.getValueAt(i, 2);
                int tongSoLuongMoi = soLuongTrongHDHienTai + soLuongMua;
                if (tongSoLuongMoi > tonKhoHienTai) {
                    JOptionPane.showMessageDialog(this, "Số lượng yêu cầu (" + tongSoLuongMoi + ") vượt quá tồn kho (" + tonKhoHienTai + ") cho sản phẩm " + tenSP + ".", "Hết hàng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                modelHoaDon.setValueAt(tongSoLuongMoi, i, 2);
                modelHoaDon.setValueAt(tongSoLuongMoi * donGia, i, 4); // Thành tiền (chưa VAT, chưa giảm giá)
                updateTotalAmount();
                System.out.println("SELL_VIEW: Đã cập nhật số lượng cho SP '" + maSP + "' trong hóa đơn.");
                return;
            }
        }

        if (soLuongMua > tonKhoHienTai) {
            JOptionPane.showMessageDialog(this, "Số lượng yêu cầu (" + soLuongMua + ") vượt quá tồn kho (" + tonKhoHienTai + ") cho sản phẩm " + tenSP + ".", "Hết hàng", JOptionPane.ERROR_MESSAGE);
            return;
        }
        modelHoaDon.addRow(new Object[]{maSP, tenSP, soLuongMua, donGia, soLuongMua * donGia});
        updateTotalAmount();
        System.out.println("SELL_VIEW: Đã thêm SP '" + maSP + "' vào hóa đơn.");
    }

    private void xoaKhoiHoaDon() {
        int selectedRowHD = tableHoaDon.getSelectedRow();
        if (selectedRowHD == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trong hóa đơn để xóa.", "Chưa chọn sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modelHoaDon.removeRow(selectedRowHD);
        updateTotalAmount();
        System.out.println("SELL_VIEW: Đã xóa sản phẩm khỏi hóa đơn.");
    }

    private void banHang() {
        System.out.println("SELL_VIEW: Nút 'Tạo Hóa Đơn' được nhấn.");
        if (modelHoaDon.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Hóa đơn đang trống! Vui lòng thêm sản phẩm.", "Hóa đơn trống", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maHDX = tfMaHDX.getText().trim();
        if (maHDX.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Hóa Đơn!", "Thiếu thông tin", JOptionPane.ERROR_MESSAGE);
            tfMaHDX.requestFocus();
            return;
        }

        String maKH = tfMaKH.getText().trim();
        String tenKH = tfTenKH.getText().trim();
        String sdtKH = tfSdtKH.getText().trim();
        String maTichDiemCungCap = tfMaTichDiem.getText().trim();

        if (!maKH.isEmpty()) { // Nếu có Mã KH
            if (tenKH.isEmpty() || sdtKH.isEmpty()){
                 JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên và SĐT cho khách hàng.", "Thiếu thông tin KH", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Nếu không dùng điểm để giảm giá VÀ mã tích điểm trống -> cảnh báo
            if (!suDungDiemDangApDung && maTichDiemCungCap.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Khách hàng có Mã KH nhưng chưa nhập Mã Tích Điểm.\nĐiểm sẽ không được tích lũy cho hóa đơn này.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                 // Không return, cho phép tạo hóa đơn không tích điểm
            }
             if (!suDungDiemDangApDung && !maTichDiemCungCap.isEmpty() && maTichDiemCungCap.length() > 8) {
                JOptionPane.showMessageDialog(this, "Mã Tích Điểm không được vượt quá 8 ký tự.", "Mã không hợp lệ", JOptionPane.ERROR_MESSAGE);
                tfMaTichDiem.requestFocus();
                return;
            }
        } else { // Khách lẻ
            if (!maTichDiemCungCap.isEmpty() && !suDungDiemDangApDung) { // Khách lẻ không dùng điểm thì ko cần mã tích điểm
                 JOptionPane.showMessageDialog(this, "Khách lẻ không cần nhập Mã Tích Điểm.", "Thông tin không cần thiết", JOptionPane.INFORMATION_MESSAGE);
                 tfMaTichDiem.setText("");
                 maTichDiemCungCap = "";
            }
            if (suDungDiemDangApDung) { // Không thể xảy ra vì btnSuDungDiem bị disable nếu ko có MaKH
                JOptionPane.showMessageDialog(this, "Lỗi logic: Đang cố dùng điểm cho khách lẻ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }


        List<ChiTietHDXuat> danhSachChiTiet = new ArrayList<>();
        for (int i = 0; i < modelHoaDon.getRowCount(); i++) {
            String maSP_CT = modelHoaDon.getValueAt(i, 0).toString();
            int soLuong_CT = (int) modelHoaDon.getValueAt(i, 2);
            double donGia_CT = (double) modelHoaDon.getValueAt(i, 3);
            danhSachChiTiet.add(new ChiTietHDXuat(null, maSP_CT, soLuong_CT, donGia_CT)); // MaHDX sẽ được set trong query
        }

        System.out.println("SELL_VIEW: Chuẩn bị gọi controller.banHang với MaHDX=" + maHDX +
                           ", MaNV=" + maNV +
                           ", MaKH=" + (maKH.isEmpty() ? "Khách lẻ" : maKH) +
                           ", MaTichDiemCungCap=" + maTichDiemCungCap +
                           ", SuDungDiem=" + suDungDiemDangApDung +
                           ", PhanTramGiam=" + phanTramGiamHienTai);

        boolean success = sellProductController.banHang(maHDX, maNV,
                                                      maKH.isEmpty() ? null : maKH,
                                                      tenKH, sdtKH,
                                                      maTichDiemCungCap, // Truyền mã tích điểm KH cung cấp
                                                      danhSachChiTiet,
                                                      suDungDiemDangApDung, // boolean: có dùng điểm không
                                                      phanTramGiamHienTai); // int: % giảm

        if (success) {
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn " + maHDX + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            resetFormBanHang();
        } else {
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thất bại! Vui lòng kiểm tra lại thông tin, tồn kho và log lỗi.", "Lỗi tạo hóa đơn", JOptionPane.ERROR_MESSAGE);
            System.err.println("SELL_VIEW: Tạo hóa đơn thất bại.");
        }
    }

    private void resetFormBanHang() {
        modelHoaDon.setRowCount(0);
        tfMaHDX.setText("HDX" + System.currentTimeMillis() % 100000);
        tfMaKH.setText(""); // Sẽ trigger handleMaKHChange
        tfTenKH.setText("");
        tfSdtKH.setText("");
        tfMaTichDiem.setText("");
        tfMaTichDiem.setEnabled(false); // Do MaKH trống
        spinnerSoLuong.setValue(1);

        suDungDiemDangApDung = false;
        phanTramGiamHienTai = 0;
        btnSuDungDiem.setText("Sử dụng điểm thưởng");
        btnSuDungDiem.setBackground(new Color(0, 123, 255));
        btnSuDungDiem.setEnabled(false); 
        lblGiamGia.setText("Giảm giá: 0%");

        updateTotalAmount(); 
        loadSanPham(); 
        System.out.println("SELL_VIEW: Form bán hàng đã được reset.");
    }


    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Không thể áp dụng Nimbus Look and Feel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> new SellProductView("NV001").setVisible(true));
    }
}