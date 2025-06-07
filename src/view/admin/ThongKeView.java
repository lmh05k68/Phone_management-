package view.admin;

import model.HoaDonNhap;
import model.HoaDonXuat;
import query.HoaDonNhapQuery;
import query.HoaDonXuatQuery;
import query.ThongKeQuery;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

public class ThongKeView extends JFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<Integer> cboThang, cboNam;
    private JButton btnThongKe, btnTaiLaiDuLieu, btnBack;
    private JLabel lblTongDoanhThu, lblTongChiTieu, lblLoiNhuan;

    private JTable tableHDX;
    private DefaultTableModel modelHDX;
    private JTable tableHDN;
    private DefaultTableModel modelHDN;

    private final DecimalFormat currencyFormat = new DecimalFormat("###,###,##0 VNĐ");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ThongKeQuery thongKeQuery = new ThongKeQuery();
    private final HoaDonXuatQuery hoaDonXuatQuery = new HoaDonXuatQuery();
    private final HoaDonNhapQuery hoaDonNhapQuery = new HoaDonNhapQuery();

    public ThongKeView() {
        // ... (constructor giữ nguyên)
        System.out.println("THONGKE_VIEW: Constructor ThongKeView bắt đầu.");
        setTitle("Thống kê Doanh thu - Chi tiêu - Lợi nhuận");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        if (cboThang.getSelectedItem() != null && cboNam.getSelectedItem() != null) {
            thongKe();
        }
        System.out.println("THONGKE_VIEW: Constructor ThongKeView kết thúc.");
    }

    private void initUI() {
        // ... (phần topPanel giữ nguyên)
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        JLabel lblViewTitle = new JLabel("Thống Kê Kinh Doanh Theo Tháng", SwingConstants.CENTER);
        lblViewTitle.setFont(new Font("Arial", Font.BOLD, 22));
        topPanel.add(lblViewTitle, BorderLayout.NORTH);

        cboThang = new JComboBox<>();
        cboNam = new JComboBox<>();
        btnThongKe = createStyledButton("Xem Thống Kê");
        btnTaiLaiDuLieu = createStyledButton("Tải Lại Dữ Liệu");
        btnBack = createStyledButton("Trở về");

        IntStream.rangeClosed(1, 12).forEach(cboThang::addItem);
        int namHienTai = Year.now().getValue();
        int thangHienTai = LocalDate.now().getMonthValue();
        IntStream.rangeClosed(namHienTai - 5, namHienTai + 1).forEach(cboNam::addItem);
        cboThang.setSelectedItem(thangHienTai);
        cboNam.setSelectedItem(namHienTai);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);
        cboThang.setFont(inputFont);
        cboNam.setFont(inputFont);
        JLabel lblThang = new JLabel("Tháng:"); lblThang.setFont(inputFont);
        JLabel lblNam = new JLabel("Năm:"); lblNam.setFont(inputFont);

        JPanel inputControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        inputControlsPanel.add(lblThang);
        inputControlsPanel.add(cboThang);
        inputControlsPanel.add(lblNam);
        inputControlsPanel.add(cboNam);
        inputControlsPanel.add(btnThongKe);
        inputControlsPanel.add(btnTaiLaiDuLieu);
        inputControlsPanel.add(btnBack);
        topPanel.add(inputControlsPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] columnsHDX = {"Mã HDX", "Ngày Lập", "Thành Tiền (Trước thuế)", "Thuế (%)", "Tổng Cộng (Sau thuế)", "Mã KH"};
        modelHDX = new DefaultTableModel(columnsHDX, 0) { /* ... */ };
        tableHDX = new JTable(modelHDX);
        styleTable(tableHDX, "hdx");
        JScrollPane scrollPaneHDX = new JScrollPane(tableHDX);
        scrollPaneHDX.setBorder(BorderFactory.createTitledBorder("DOANH THU (Hóa Đơn Xuất)"));

        // SỬA CỘT CHO BẢNG HÓA ĐƠN NHẬP
        String[] columnsHDN = {"Mã HDN", "Ngày Nhập", "Mã NCC", "Mã NV"}; // Bỏ cột "Tổng Tiền Nhập"
        modelHDN = new DefaultTableModel(columnsHDN, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableHDN = new JTable(modelHDN);
        styleTable(tableHDN, "hdn_basic"); // Dùng một key khác để style cột
        JScrollPane scrollPaneHDN = new JScrollPane(tableHDN);
        scrollPaneHDN.setBorder(BorderFactory.createTitledBorder("CHI TIÊU (Danh sách Hóa Đơn Nhập)"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPaneHDX, scrollPaneHDN);
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel resultPanel = new JPanel(new GridLayout(3, 1, 0, 8));
        // ... (phần resultPanel giữ nguyên)
        resultPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        Font resultFont = new Font("Arial", Font.BOLD, 18);
        lblTongDoanhThu = new JLabel("Tổng Doanh Thu (Sau thuế): ...", SwingConstants.CENTER);
        lblTongDoanhThu.setFont(resultFont);
        lblTongDoanhThu.setForeground(new Color(0, 100, 0));
        lblTongChiTieu = new JLabel("Tổng Chi Tiêu (HĐ Nhập): ...", SwingConstants.CENTER);
        lblTongChiTieu.setFont(resultFont);
        lblTongChiTieu.setForeground(Color.RED);
        lblLoiNhuan = new JLabel("Lợi Nhuận (Tháng .../...): ...", SwingConstants.CENTER);
        lblLoiNhuan.setFont(resultFont);
        lblLoiNhuan.setForeground(Color.BLUE);
        resultPanel.add(lblTongDoanhThu);
        resultPanel.add(lblTongChiTieu);
        resultPanel.add(lblLoiNhuan);
        mainPanel.add(resultPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // ... (Action Listeners giữ nguyên)
        btnThongKe.addActionListener(e -> {
            System.out.println("THONGKE_VIEW: Nút 'Xem Thống Kê' được nhấn.");
            thongKe();
        });
        btnTaiLaiDuLieu.addActionListener(e -> {
            System.out.println("THONGKE_VIEW: Nút 'Tải Lại Dữ Liệu' được nhấn.");
            taiLaiTatCaDuLieu();
        });
        btnBack.addActionListener(e -> {
            System.out.println("THONGKE_VIEW: Nút 'Trở về' được nhấn.");
            dispose();
        });
    }

    private void styleTable(JTable table, String tableType) {
        // ... (phần style cho hdx giữ nguyên)
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateRowSorter(true);

        TableColumnModel columnModel = table.getColumnModel();
        if ("hdx".equals(tableType) && columnModel.getColumnCount() == 6) {
            columnModel.getColumn(0).setPreferredWidth(80);
            columnModel.getColumn(1).setPreferredWidth(100);
            columnModel.getColumn(2).setPreferredWidth(180);
            columnModel.getColumn(3).setPreferredWidth(80); // Sửa lại cho thuế
            columnModel.getColumn(4).setPreferredWidth(180);
            columnModel.getColumn(5).setPreferredWidth(80);
        } else if ("hdn_basic".equals(tableType) && columnModel.getColumnCount() == 4) { // SỬA KEY VÀ SỐ CỘT
            columnModel.getColumn(0).setPreferredWidth(100);  // Mã HDN
            columnModel.getColumn(1).setPreferredWidth(120); // Ngày Nhập
            columnModel.getColumn(2).setPreferredWidth(100); // Mã NCC
            columnModel.getColumn(3).setPreferredWidth(100);  // Mã NV
        }
    }

    private JButton createStyledButton(String text) {
        // ... (Giữ nguyên)
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
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

    private void thongKe() {
        // ... (phần lấy doanh thu, chi tiêu, tính lợi nhuận giữ nguyên)
        if (cboThang.getSelectedItem() == null || cboNam.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng và năm.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int thang = (int) cboThang.getSelectedItem();
        int nam = (int) cboNam.getSelectedItem();
        System.out.println("THONGKE_VIEW_thongKe: Thống kê cho Tháng: " + thang + ", Năm: " + nam);

        double doanhThu = thongKeQuery.getDoanhThuThang(thang, nam);
        lblTongDoanhThu.setText("Tổng Doanh Thu (Sau thuế): " + currencyFormat.format(doanhThu));
        System.out.println("THONGKE_VIEW_thongKe: Doanh thu = " + doanhThu);

        double chiTieu = thongKeQuery.getChiTieuThang(thang, nam);
        lblTongChiTieu.setText("Tổng Chi Tiêu (HĐ Nhập): " + currencyFormat.format(chiTieu));
        System.out.println("THONGKE_VIEW_thongKe: Chi tiêu = " + chiTieu);

        double loiNhuan = doanhThu - chiTieu;
        lblLoiNhuan.setText("Lợi Nhuận (Tháng " + thang + "/" + nam + "): " + currencyFormat.format(loiNhuan));
        if (loiNhuan >= 0) {
            lblLoiNhuan.setForeground(new Color(0, 128, 0));
        } else {
            lblLoiNhuan.setForeground(Color.RED);
        }
        System.out.println("THONGKE_VIEW_thongKe: Lợi nhuận = " + loiNhuan);

        loadHoaDonXuatTable(thang, nam);
        loadHoaDonNhapTable(thang, nam);
    }

    private void loadHoaDonXuatTable(int thang, int nam) {
        // ... (Giữ nguyên như phiên bản trước)
        System.out.println("THONGKE_VIEW_loadHoaDonXuatTable: Tải HĐX cho Tháng: " + thang + ", Năm: " + nam);
        modelHDX.setRowCount(0);
        List<HoaDonXuat> list = hoaDonXuatQuery.getHoaDonByMonthAndYear(thang, nam);

        if (list != null && !list.isEmpty()) {
            for (HoaDonXuat hdx : list) {
                double thanhTienTruocThue = hdx.getThanhTien();
                double mucThuePhanTram = hdx.getMucThue();
                double tongCongSauThue = thanhTienTruocThue * (1 + mucThuePhanTram / 100.0);
                modelHDX.addRow(new Object[]{
                        hdx.getMaHDX(),
                        (hdx.getNgayLap() != null) ? hdx.getNgayLap().format(dateFormatter) : "N/A",
                        currencyFormat.format(thanhTienTruocThue),
                        String.format("%.2f", mucThuePhanTram),
                        currencyFormat.format(tongCongSauThue),
                        hdx.getMaKH() != null ? hdx.getMaKH() : "Khách lẻ"
                });
            }
            System.out.println("THONGKE_VIEW_loadHoaDonXuatTable: Đã tải " + list.size() + " hóa đơn xuất.");
        } else {
            System.out.println("THONGKE_VIEW_loadHoaDonXuatTable: Không có hóa đơn xuất nào trong tháng/năm đã chọn.");
        }
    }

    // SỬA PHƯƠNG THỨC NÀY
    private void loadHoaDonNhapTable(int thang, int nam) {
        System.out.println("THONGKE_VIEW_loadHoaDonNhapTable: Tai HDN cho Thang: " + thang + ", Nam: " + nam);
        modelHDN.setRowCount(0);
        // Gọi phương thức chỉ lấy thông tin cơ bản của HoaDonNhap
        List<HoaDonNhap> list = hoaDonNhapQuery.getHoaDonNhapByMonthAndYear(thang, nam);

        if (list != null && !list.isEmpty()) {
            for (HoaDonNhap hdn : list) {
                // Nếu bạn muốn hiển thị tổng tiền của TỪNG hóa đơn nhập này,
                // bạn cần gọi thêm một phương thức query ở đây để tính tổng cho hdn.getMaHDN()
                // Ví dụ: double tongTienTungHDN = hoaDonNhapQuery.tinhTongTienChoMotHoaDonNhap(hdn.getMaHDN());
                // Hiện tại, chúng ta chỉ hiển thị thông tin cơ bản
                modelHDN.addRow(new Object[]{
                        hdn.getMaHDN(),
                        (hdn.getNgayNhap() != null) ? hdn.getNgayNhap().format(dateFormatter) : "N/A",
                        // Bỏ cột tổng tiền ở đây, vì model HoaDonNhap đã không còn
                        // currencyFormat.format(hdn.getTongTienNhap()), << BỎ DÒNG NÀY
                        hdn.getMaNCC(),
                        hdn.getMaNV()
                });
            }
            System.out.println("THONGKE_VIEW_loadHoaDonNhapTable: Da tai " + list.size() + " hoa don nhap (thong tin co ban).");
        } else {
            System.out.println("THONGKE_VIEW_loadHoaDonNhapTable: Khong co hoa don nhap nao.");
        }
    }

    private void taiLaiTatCaDuLieu() {
        if (cboThang.getSelectedItem() == null || cboNam.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng và năm trước khi tải lại.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int thang = (int) cboThang.getSelectedItem();
        int nam = (int) cboNam.getSelectedItem();
        loadHoaDonXuatTable(thang, nam);
        loadHoaDonNhapTable(thang, nam);
        lblTongDoanhThu.setText("Tổng Doanh Thu (Sau thuế): ...");
        lblTongChiTieu.setText("Tổng Chi Tiêu (HĐ Nhập): ...");
        lblLoiNhuan.setText("Lợi Nhuận (Tháng " + thang + "/" + nam + "): ...");
        lblLoiNhuan.setForeground(Color.BLUE);
        if (modelHDX.getRowCount() == 0 && modelHDN.getRowCount() == 0) {
             JOptionPane.showMessageDialog(this, "Không có hóa đơn xuất hoặc nhập nào trong tháng " + thang + "/" + nam + " để hiển thị.",
                    "Không có dữ liệu", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}