package view.employee;

import controller.employee.SellProduct;
import model.ChiTietHDXuat;
import model.KhachHang;
import model.SanPham;
import query.KhachHangQuery;
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
    private int maNV;
    private JTextField tfMaKH, tfTenKH, tfSdtKH; 
    private JSpinner spinnerSoLuong;
    private JTable tableSanPham, tableHoaDon;
    private DefaultTableModel modelSanPham, modelHoaDon;
    private JLabel lblTongTien, lblGiamGia;
    private JButton btnSuDungDiem, btnBack;

    private final SellProduct sellProductController = new SellProduct();
    // Các lớp Query nên được gọi thông qua phương thức static, không cần tạo instance ở đây
    // private final SanPhamQuery spQuery = new SanPhamQuery();
    // private final KhachHangQuery khachHangQuery = new KhachHangQuery();

    private boolean suDungDiemDangApDung = false;
    private int phanTramGiamHienTai = 0;

    public SellProductView(int maNV) {
        this.maNV = maNV;
        System.out.println("SELL_VIEW: Khởi tạo SellProductView cho NV (Mã: " + maNV + ")");
        setTitle("Bán hàng - Nhân viên (Mã: " + maNV + ")");
        setSize(1100, 750); // Điều chỉnh kích thước nếu cần
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        // tfMaHDX.setText("HDX" + System.currentTimeMillis() % 100000); // Loại bỏ vì MaHDX tự sinh
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Top Panel: KH, Tích điểm
        JPanel topInfoPanelContainer = new JPanel(new GridLayout(1, 2, 15, 0));

        // Panel trái: Thông tin KH
        JPanel panelKH = new JPanel(new GridLayout(0, 2, 5, 5));
        tfMaKH = new JTextField(10);
        tfTenKH = new JTextField(15);
        tfSdtKH = new JTextField(10);
        panelKH.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng (nếu có)"));
        panelKH.add(new JLabel("Mã KH:")); panelKH.add(tfMaKH);
        panelKH.add(new JLabel("Tên KH:")); panelKH.add(tfTenKH);
        panelKH.add(new JLabel("SĐT KH:")); panelKH.add(tfSdtKH);
        topInfoPanelContainer.add(panelKH); // Panel KH giờ chiếm phần bên trái

        // Panel phải: Sử dụng điểm
        JPanel rightTopPanel = new JPanel();
        rightTopPanel.setLayout(new BoxLayout(rightTopPanel, BoxLayout.Y_AXIS));
        rightTopPanel.setBorder(BorderFactory.createTitledBorder("Điểm thưởng (nếu có Mã KH)"));

        // Loại bỏ tfMaTichDiem
        // JPanel panelTichDiemInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,5));
        // tfMaTichDiem = new JTextField(10);
        // tfMaTichDiem.setEnabled(false);
        // panelTichDiemInput.add(new JLabel("Mã tích điểm*:")); // Không cần nữa
        // panelTichDiemInput.add(tfMaTichDiem);
        // rightTopPanel.add(panelTichDiemInput);
        // rightTopPanel.add(Box.createVerticalStrut(5));

        btnSuDungDiem = createStyledButton("Sử dụng điểm thưởng");
        btnSuDungDiem.setEnabled(false);
        lblGiamGia = new JLabel("Giảm giá: 0%");
        lblGiamGia.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        JPanel panelSuDungDiemControls = new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));
        panelSuDungDiemControls.add(btnSuDungDiem);
        panelSuDungDiemControls.add(lblGiamGia);
        rightTopPanel.add(panelSuDungDiemControls);
        topInfoPanelContainer.add(rightTopPanel);


        // Tables
        String[] colsSP = {"Mã SP", "Tên SP", "Màu", "Đơn giá", "Nước SX", "Hãng SX", "Tồn kho"};
        modelSanPham = new DefaultTableModel(colsSP, 0) {
        	private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableSanPham = new JTable(modelSanPham);
        styleTable(tableSanPham);
        JScrollPane scrollSP = new JScrollPane(tableSanPham);
        scrollSP.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));
        scrollSP.setPreferredSize(new Dimension(scrollSP.getPreferredSize().width, 250));


        String[] colsHD = {"Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền"};
        modelHoaDon = new DefaultTableModel(colsHD, 0) {
        	private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableHoaDon = new JTable(modelHoaDon);
        styleTable(tableHoaDon);
        JScrollPane scrollHD = new JScrollPane(tableHoaDon);
        scrollHD.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));

        JPanel panelControl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        spinnerSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        ((JSpinner.DefaultEditor) spinnerSoLuong.getEditor()).getTextField().setColumns(4);

        JButton btnThem = createStyledButton("Thêm vào HĐ");
        JButton btnXoa = createStyledButton("Xóa khỏi HĐ");
        JButton btnBan = createStyledButton("Tạo Hóa Đơn");
        btnBan.setBackground(new Color(40, 167, 69));

        panelControl.add(new JLabel("Số lượng:"));
        panelControl.add(spinnerSoLuong);
        panelControl.add(btnThem);
        panelControl.add(btnXoa);
        panelControl.add(btnBan);

        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongTien.setBorder(new EmptyBorder(5,0,5,10));

        btnBack = createStyledButton("Trở về");
        btnBack.setBackground(new Color(108, 117, 125));
        JPanel bottomPanelContainer = new JPanel(new BorderLayout());
        bottomPanelContainer.add(lblTongTien, BorderLayout.CENTER);
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.add(btnBack);
        bottomPanelContainer.add(backButtonPanel, BorderLayout.EAST);
        bottomPanelContainer.setBorder(new EmptyBorder(10, 10, 5, 10));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollSP, scrollHD);
        split.setDividerLocation(300);
        split.setResizeWeight(0.45);

        content.add(topInfoPanelContainer, BorderLayout.NORTH);
        content.add(split, BorderLayout.CENTER);
        content.add(panelControl, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
        add(bottomPanelContainer, BorderLayout.SOUTH);

        btnThem.addActionListener(e -> themVaoHoaDon());
        btnXoa.addActionListener(e -> xoaKhoiHoaDon());
        btnBan.addActionListener(e -> banHang());
        btnBack.addActionListener(e -> {
            System.out.println("SELL_VIEW: Nút 'Trở về' được nhấn.");
            this.dispose();
        });
        btnSuDungDiem.addActionListener(e -> toggleSuDungDiem());

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
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
         if (table == tableSanPham) {
             table.getColumnModel().getColumn(0).setPreferredWidth(60);
             table.getColumnModel().getColumn(1).setPreferredWidth(180);
             table.getColumnModel().getColumn(6).setPreferredWidth(70);
         } else if (table == tableHoaDon) {
             table.getColumnModel().getColumn(0).setPreferredWidth(60);
             table.getColumnModel().getColumn(1).setPreferredWidth(180);
         }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179), 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        return btn;
    }

    private void loadSanPham() {
        System.out.println("SELL_VIEW: Bắt đầu tải danh sách sản phẩm.");
        modelSanPham.setRowCount(0);
        // Gọi phương thức static từ SanPhamQuery
        List<SanPham> sanPhams = SanPhamQuery.getAllSanPhamActiving();
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

        double finalTotalTruocThue = subTotal;
        if (suDungDiemDangApDung && phanTramGiamHienTai > 0) {
            finalTotalTruocThue = subTotal * (1 - (double) phanTramGiamHienTai / 100.0);
        }
        double tienThue = finalTotalTruocThue * 0.10; // 10% VAT
        double finalTotalSauThue = finalTotalTruocThue + tienThue;

        lblTongTien.setText(String.format("Tổng tiền thanh toán: %,.0f VNĐ", finalTotalSauThue));
    }

    private void handleMaKHChange() {
        String maKHStr = tfMaKH.getText().trim();
        if (maKHStr.isEmpty()) {
            btnSuDungDiem.setEnabled(false);
            // tfMaTichDiem.setEnabled(false); // Loại bỏ
            // tfMaTichDiem.setText("");      // Loại bỏ
            tfTenKH.setText("");
            tfSdtKH.setText("");

            if (suDungDiemDangApDung) {
                suDungDiemDangApDung = false;
                phanTramGiamHienTai = 0;
                btnSuDungDiem.setText("Sử dụng điểm thưởng");
                btnSuDungDiem.setBackground(new Color(0, 123, 255));
                lblGiamGia.setText("Giảm giá: 0%");
                updateTotalAmount();
            }
        } else {
            try {
                int maKH = Integer.parseInt(maKHStr);
                btnSuDungDiem.setEnabled(true);
                // tfMaTichDiem.setEnabled(true); // Loại bỏ
                KhachHang kh = KhachHangQuery.getKhachHangById(maKH);
                if (kh != null) {
                    tfTenKH.setText(kh.getHoTen());
                    tfSdtKH.setText(kh.getSdtKH());
                } else {
                    tfTenKH.setText("");
                    tfSdtKH.setText("");
                    // Không cần JOptionPane ở đây, để người dùng tiếp tục nhập hoặc tạo KH mới khi bán
                }
            } catch (NumberFormatException e) {
                btnSuDungDiem.setEnabled(false);
                // tfMaTichDiem.setEnabled(false); // Loại bỏ
                tfTenKH.setText("");
                tfSdtKH.setText("");
                if (suDungDiemDangApDung) {
                     suDungDiemDangApDung = false;
                     phanTramGiamHienTai = 0;
                     btnSuDungDiem.setText("Sử dụng điểm thưởng");
                     lblGiamGia.setText("Giảm giá: 0%");
                     updateTotalAmount();
                }
            }
        }
    }

     private void toggleSuDungDiem() {
        String maKHStr = tfMaKH.getText().trim();
        if (maKHStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Khách Hàng trước.", "Thiếu Mã KH", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
             int maKH = Integer.parseInt(maKHStr);
             if (!KhachHangQuery.exists(maKH)) { // Kiểm tra KH có tồn tại không
                 JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với mã: " + maKHStr, "Lỗi Mã KH", JOptionPane.ERROR_MESSAGE);
                 return;
             }

             if (!suDungDiemDangApDung) {
                 int diemHienTai = KhachHangQuery.getSoDiemTichLuy(maKH);
                 if (diemHienTai <= 0) {
                     JOptionPane.showMessageDialog(this, "Khách hàng không có điểm thưởng hoặc không đủ điểm.", "Không có điểm", JOptionPane.INFORMATION_MESSAGE);
                     return;
                 }
                 phanTramGiamHienTai = KhachHangQuery.tinhPhanTramGiamTuDiem(maKH);
                 if (phanTramGiamHienTai > 0) {
                     suDungDiemDangApDung = true;
                     btnSuDungDiem.setText("Hủy dùng điểm");
                     btnSuDungDiem.setBackground(new Color(220, 53, 69));
                     lblGiamGia.setText(String.format("Giảm giá: %d%% (Điểm: %d)", phanTramGiamHienTai, diemHienTai));
                     // tfMaTichDiem.setEnabled(false); // Loại bỏ
                     // tfMaTichDiem.setText("");      // Loại bỏ
                     System.out.println("SELL_VIEW: Áp dụng " + phanTramGiamHienTai + "% giảm giá từ điểm.");
                 } else {
                     JOptionPane.showMessageDialog(this, "Không đủ điểm để được giảm giá (tối thiểu 100 điểm cho 1%).", "Không đủ điểm", JOptionPane.INFORMATION_MESSAGE);
                 }
             } else {
                 suDungDiemDangApDung = false;
                 phanTramGiamHienTai = 0;
                 btnSuDungDiem.setText("Sử dụng điểm thưởng");
                 btnSuDungDiem.setBackground(new Color(0, 123, 255));
                 lblGiamGia.setText("Giảm giá: 0%");
                 // tfMaTichDiem.setEnabled(true); // Loại bỏ - không còn tfMaTichDiem
                 System.out.println("SELL_VIEW: Hủy sử dụng điểm thưởng.");
             }
             updateTotalAmount();
        } catch (NumberFormatException e){
             JOptionPane.showMessageDialog(this, "Mã khách hàng không hợp lệ.", "Lỗi Mã KH", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void themVaoHoaDon() {
        int selectedRowSP = tableSanPham.getSelectedRow();
        if (selectedRowSP == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm từ danh sách để thêm.", "Chưa chọn sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int soLuongMua = (Integer) spinnerSoLuong.getValue();
        if (soLuongMua <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0.", "Số lượng không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int maSP = (Integer) modelSanPham.getValueAt(selectedRowSP, 0);
        String tenSP = modelSanPham.getValueAt(selectedRowSP, 1).toString();
        double donGia = (Double) modelSanPham.getValueAt(selectedRowSP, 3);
        int tonKhoHienTai = (Integer) modelSanPham.getValueAt(selectedRowSP, 6);

        for (int i = 0; i < modelHoaDon.getRowCount(); i++) {
            if ((Integer)modelHoaDon.getValueAt(i, 0) == maSP) {
                int soLuongTrongHDHienTai = (Integer) modelHoaDon.getValueAt(i, 2);
                int tongSoLuongMoi = soLuongTrongHDHienTai + soLuongMua;
                if (tongSoLuongMoi > tonKhoHienTai) {
                    JOptionPane.showMessageDialog(this, "Số lượng yêu cầu (" + tongSoLuongMoi + ") vượt quá tồn kho (" + tonKhoHienTai + ") cho sản phẩm " + tenSP + ".", "Hết hàng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                modelHoaDon.setValueAt(tongSoLuongMoi, i, 2);
                modelHoaDon.setValueAt(tongSoLuongMoi * donGia, i, 4);
                updateTotalAmount();
                System.out.println("SELL_VIEW: Đã cập nhật số lượng cho SP ID '" + maSP + "' trong hóa đơn.");
                return;
            }
        }

        if (soLuongMua > tonKhoHienTai) {
            JOptionPane.showMessageDialog(this, "Số lượng yêu cầu (" + soLuongMua + ") vượt quá tồn kho (" + tonKhoHienTai + ") cho sản phẩm " + tenSP + ".", "Hết hàng", JOptionPane.ERROR_MESSAGE);
            return;
        }
        modelHoaDon.addRow(new Object[]{maSP, tenSP, soLuongMua, donGia, soLuongMua * donGia});
        updateTotalAmount();
        System.out.println("SELL_VIEW: Đã thêm SP ID '" + maSP + "' vào hóa đơn.");
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

        String maKHStr = tfMaKH.getText().trim();
        String tenKH = tfTenKH.getText().trim();
        String sdtKH = tfSdtKH.getText().trim();
        Integer maKHInteger = null;

        if (!maKHStr.isEmpty()) {
            try {
                maKHInteger = Integer.parseInt(maKHStr);
                 // Kiểm tra xem KH có tồn tại không nếu có mã
                if (!KhachHangQuery.exists(maKHInteger)) {
                    // Nếu không tồn tại và có tên + SĐT -> sẽ tạo KH mới trong controller
                    // Nếu không tồn tại và không có tên + SĐT -> lỗi
                    if (tenKH.isEmpty() || sdtKH.isEmpty()) {
                         JOptionPane.showMessageDialog(this, "Mã KH không tồn tại và thiếu thông tin Tên/SĐT để tạo KH mới.", "Lỗi Khách Hàng", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                     System.out.println("SELL_VIEW: MaKH " + maKHInteger + " không tồn tại, sẽ thử tạo KH mới nếu có Tên và SĐT.");
                } else {
                     KhachHang khExisting = KhachHangQuery.getKhachHangById(maKHInteger);
                     if (khExisting != null) {
                         tenKH = khExisting.getHoTen(); // Dùng thông tin từ DB cho chắc
                         sdtKH = khExisting.getSdtKH();
                         tfTenKH.setText(tenKH); // Cập nhật lại UI
                         tfSdtKH.setText(sdtKH);
                     }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã Khách Hàng không hợp lệ.", "Lỗi Mã KH", JOptionPane.ERROR_MESSAGE);
                tfMaKH.requestFocus();
                return;
            }
        } else { // Khách vãng lai, không có MaKH
            if (tenKH.isEmpty() || sdtKH.isEmpty()) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "Bạn chưa nhập Mã KH. Thông tin Tên và SĐT cũng trống.\n" +
                        "Hóa đơn sẽ được tạo cho khách vãng lai (không có thông tin KH cụ thể).\n" +
                        "Bạn có muốn tiếp tục không?",
                        "Xác nhận khách vãng lai", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (choice == JOptionPane.NO_OPTION) return;
                tenKH = "Khách vãng lai"; // Hoặc để trống
                sdtKH = "";
            }
        }


        List<ChiTietHDXuat> danhSachChiTiet = new ArrayList<>();
        for (int i = 0; i < modelHoaDon.getRowCount(); i++) {
            int maSP_CT = (Integer) modelHoaDon.getValueAt(i, 0);
            int soLuong_CT = (Integer) modelHoaDon.getValueAt(i, 2);
            double donGia_CT = (Double) modelHoaDon.getValueAt(i, 3);
            danhSachChiTiet.add(new ChiTietHDXuat(maSP_CT, soLuong_CT, donGia_CT));
        }

        System.out.println("SELL_VIEW: Chuẩn bị gọi controller.banHang..." );
        // Gọi controller, không còn maTheTichDiemKhachCungCap
        Integer maHDXGenerated = sellProductController.banHang(
                maNV,
                maKHInteger,
                tenKH, sdtKH,
                danhSachChiTiet,
                suDungDiemDangApDung,
                phanTramGiamHienTai);

        if (maHDXGenerated != null && maHDXGenerated > 0) {
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thành công! Mã HĐX: " + maHDXGenerated, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            resetFormBanHang();
        } else {
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thất bại! Vui lòng kiểm tra lại thông tin, tồn kho và log lỗi.", "Lỗi tạo hóa đơn", JOptionPane.ERROR_MESSAGE);
            System.err.println("SELL_VIEW: Tạo hóa đơn thất bại.");
        }
    }

    private void resetFormBanHang() {
        modelHoaDon.setRowCount(0);
        // tfMaHDX.setText("HDX" + System.currentTimeMillis() % 100000); // Loại bỏ
        tfMaKH.setText("");
        // tfTenKH, tfSdtKH, tfMaTichDiem sẽ tự động xóa/disable bởi handleMaKHChange

        spinnerSoLuong.setValue(1);

        suDungDiemDangApDung = false;
        phanTramGiamHienTai = 0;
        btnSuDungDiem.setText("Sử dụng điểm thưởng");
        btnSuDungDiem.setBackground(new Color(0, 123, 255)); // Reset màu nút
        btnSuDungDiem.setEnabled(false);
        lblGiamGia.setText("Giảm giá: 0%");

        updateTotalAmount();
        loadSanPham();
        System.out.println("SELL_VIEW: Form bán hàng đã được reset.");
    }
}