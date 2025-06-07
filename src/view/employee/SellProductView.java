package view.employee;

import controller.employee.SellProduct;
import model.ChiTietHDXuat;
import model.SanPham;
import query.SanPhamQuery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SellProductView extends JFrame {
    private static final long serialVersionUID = 1L;
    private String maNV;
    private JTextField tfMaKH, tfTenKH, tfSdtKH, tfMaHDX, tfMaTichDiem; // << THÊM LẠI tfMaTichDiem
    private JSpinner spinnerSoLuong;
    private JTable tableSanPham, tableHoaDon;
    private DefaultTableModel modelSanPham, modelHoaDon;
    private JLabel lblTongTien;
    private final SellProduct sellProductController = new SellProduct();
    private final SanPhamQuery spQuery = new SanPhamQuery();
    private JButton btnBack;

    public SellProductView(String maNV) {
        this.maNV = maNV;
        System.out.println("SELL_VIEW: Khởi tạo SellProductView cho NV: " + maNV);
        setTitle("Bán hàng - Nhân viên " + maNV);
        setSize(1050, 700); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 15, 10, 15));
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
        JPanel panelTichDiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTichDiem.setBorder(BorderFactory.createTitledBorder("Tích điểm (nếu có Mã KH)"));
        tfMaTichDiem = new JTextField(10); // << KHÔI PHỤC
        panelTichDiem.add(new JLabel("Mã tích điểm*:")); 
        panelTichDiem.add(tfMaTichDiem);
        topInfoPanelContainer.add(panelTichDiem, BorderLayout.SOUTH);
        String[] colsSP = {"Mã SP", "Tên SP", "Màu", "Đơn giá", "Nước SX", "Hãng SX", "Tồn kho"};
        modelSanPham = new DefaultTableModel(colsSP, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableSanPham = new JTable(modelSanPham);
        styleTable(tableSanPham);
        JScrollPane scrollSP = new JScrollPane(tableSanPham);
        scrollSP.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));

        String[] colsHD = {"Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền"};
        modelHoaDon = new DefaultTableModel(colsHD, 0) {
            private static final long serialVersionUID = 1L;
             @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableHoaDon = new JTable(modelHoaDon);
        styleTable(tableHoaDon);
        JScrollPane scrollHD = new JScrollPane(tableHoaDon);
        scrollHD.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));
        JPanel panelControl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        spinnerSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        ((JSpinner.DefaultEditor) spinnerSoLuong.getEditor()).getTextField().setColumns(3);

        JButton btnThem = createStyledButton("Thêm vào HĐ");
        JButton btnXoa = createStyledButton("Xóa khỏi HĐ");
        JButton btnBan = createStyledButton("Tạo Hóa Đơn");

        btnThem.addActionListener(e -> themVaoHoaDon());
        btnXoa.addActionListener(e -> xoaKhoiHoaDon());
        btnBan.addActionListener(e -> banHang());

        panelControl.add(new JLabel("Số lượng:"));
        panelControl.add(spinnerSoLuong);
        panelControl.add(btnThem);
        panelControl.add(btnXoa);
        panelControl.add(btnBan);

        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongTien.setBorder(new EmptyBorder(5,0,5,5));

        btnBack = createStyledButton("Trở về");
        btnBack.addActionListener(e -> {
            System.out.println("SELL_VIEW: Nút 'Trở về' được nhấn.");
            this.dispose();
        });

        JPanel bottomPanelContainer = new JPanel(new BorderLayout());
        bottomPanelContainer.add(lblTongTien, BorderLayout.CENTER);
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.add(btnBack);
        bottomPanelContainer.add(backButtonPanel, BorderLayout.EAST);
        bottomPanelContainer.setBorder(new EmptyBorder(10, 10, 5, 10));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollSP, scrollHD);
        split.setDividerLocation(280);
        split.setResizeWeight(0.4);

        content.add(topInfoPanelContainer, BorderLayout.NORTH); // << SỬA Ở ĐÂY
        content.add(split, BorderLayout.CENTER);
        content.add(panelControl, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
        add(bottomPanelContainer, BorderLayout.SOUTH);

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
        double total = 0;
        for (int i = 0; i < modelHoaDon.getRowCount(); i++) {
            total += (double) modelHoaDon.getValueAt(i, 4);
        }
        lblTongTien.setText(String.format("Tổng tiền: %,.0f VNĐ", total));
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
                modelHoaDon.setValueAt(tongSoLuongMoi * donGia, i, 4);
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
        // ... (Giữ nguyên logic xóa khỏi hóa đơn)
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
        String maTichDiem = tfMaTichDiem.getText().trim(); 
        if (!maKH.isEmpty()) { 
            if (maTichDiem.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Tích Điểm cho khách hàng thân thiết.", "Thiếu thông tin", JOptionPane.ERROR_MESSAGE);
                tfMaTichDiem.requestFocus();
                return;
            }
            if (maTichDiem.length() > 8) { // KIỂM TRA ĐỘ DÀI
                JOptionPane.showMessageDialog(this, "Mã Tích Điểm không được vượt quá 8 ký tự.", "Mã không hợp lệ", JOptionPane.ERROR_MESSAGE);
                tfMaTichDiem.requestFocus();
                return;
            }
            if (tenKH.isEmpty() || sdtKH.isEmpty()){
                 JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên và SĐT cho khách hàng thân thiết.", "Thiếu thông tin KH", JOptionPane.ERROR_MESSAGE);
                return;
            }

        } else { 
            if (!maTichDiem.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Khách lẻ không cần nhập Mã Tích Điểm.", "Thông tin không cần thiết", JOptionPane.INFORMATION_MESSAGE);
                 tfMaTichDiem.setText("");
                 maTichDiem = ""; 
            }
        }


        List<ChiTietHDXuat> danhSachChiTiet = new ArrayList<>();
        for (int i = 0; i < modelHoaDon.getRowCount(); i++) {
            String maSP = modelHoaDon.getValueAt(i, 0).toString();
            int soLuong = (int) modelHoaDon.getValueAt(i, 2);
            double donGia = (double) modelHoaDon.getValueAt(i, 3);
            danhSachChiTiet.add(new ChiTietHDXuat(null, maSP, soLuong, donGia));
        }

        System.out.println("SELL_VIEW: Chuẩn bị gọi controller.banHang với MaHDX=" + maHDX + ", MaNV=" + maNV + ", MaKH=" + (maKH.isEmpty() ? "Khách lẻ" : maKH) + ", MaTichDiem=" + maTichDiem);
        boolean success = sellProductController.banHang(maHDX, maNV, maKH.isEmpty() ? null : maKH, tenKH, sdtKH, maKH.isEmpty() ? null : maTichDiem, danhSachChiTiet);

        if (success) {
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn " + maHDX + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            modelHoaDon.setRowCount(0);
            tfMaHDX.setText("HDX" + System.currentTimeMillis() % 10000);
            tfMaKH.setText("");
            tfTenKH.setText("");
            tfSdtKH.setText("");
            tfMaTichDiem.setText(""); 
            spinnerSoLuong.setValue(1);
            updateTotalAmount();
            loadSanPham();
            System.out.println("SELL_VIEW: Tạo hóa đơn thành công, đã reset form.");
        } else {
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thất bại! Vui lòng kiểm tra lại thông tin, tồn kho và log lỗi.", "Lỗi tạo hóa đơn", JOptionPane.ERROR_MESSAGE);
            System.err.println("SELL_VIEW: Tạo hóa đơn thất bại.");
        }
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