package view.admin;

import model.HoaDonNhap;
import model.HoaDonXuat;
import query.HoaDonNhapQuery; // Các phương thức trong đây được gọi static
import query.HoaDonXuatQuery; // Các phương thức trong đây được gọi static
import query.ThongKeQuery;    // Các phương thức trong đây được gọi static

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

    // SỬA LỖI: Bỏ các instance của Query class nếu các phương thức đều là static
    // private final ThongKeQuery thongKeQuery = new ThongKeQuery();
    // private final HoaDonXuatQuery hoaDonXuatQuery = new HoaDonXuatQuery();
    // private final HoaDonNhapQuery hoaDonNhapQuery = new HoaDonNhapQuery();

    public ThongKeView() {
        System.out.println("THONGKE_VIEW: Constructor ThongKeView bat dau.");
        setTitle("Thong ke Doanh thu - Chi tieu - Loi nhuan");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        if (cboThang.getSelectedItem() != null && cboNam.getSelectedItem() != null) {
            thongKe();
        }
        System.out.println("THONGKE_VIEW: Constructor ThongKeView ket thuc.");
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        JLabel lblViewTitle = new JLabel("Thong Ke Kinh Doanh Theo Thang", SwingConstants.CENTER);
        lblViewTitle.setFont(new Font("Arial", Font.BOLD, 22));
        topPanel.add(lblViewTitle, BorderLayout.NORTH);

        JPanel inputControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel lblThang = new JLabel("Thang:"); lblThang.setFont(inputFont);
        cboThang = new JComboBox<>();
        cboThang.setFont(inputFont);
        IntStream.rangeClosed(1, 12).forEach(cboThang::addItem);

        JLabel lblNam = new JLabel("Nam:"); lblNam.setFont(inputFont);
        cboNam = new JComboBox<>();
        cboNam.setFont(inputFont);
        int namHienTai = Year.now().getValue();
        IntStream.rangeClosed(namHienTai - 5, namHienTai + 1).forEach(cboNam::addItem);

        cboThang.setSelectedItem(LocalDate.now().getMonthValue());
        cboNam.setSelectedItem(namHienTai);

        btnThongKe = createStyledButton("Xem Thong Ke");
        btnTaiLaiDuLieu = createStyledButton("Tai Lai Du Lieu");
        btnTaiLaiDuLieu.setBackground(new Color(23, 162, 184));
        btnBack = createStyledButton("Tro ve");
        btnBack.setBackground(new Color(108, 117, 125));

        inputControlsPanel.add(lblThang);
        inputControlsPanel.add(cboThang);
        inputControlsPanel.add(lblNam);
        inputControlsPanel.add(cboNam);
        inputControlsPanel.add(btnThongKe);
        inputControlsPanel.add(btnTaiLaiDuLieu);
        inputControlsPanel.add(btnBack);
        topPanel.add(inputControlsPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] columnsHDX = {"Ma HDX", "Ngay Lap", "Thanh Tien (Truoc thue)", "Thue (%)", "Tong Cong (Sau thue)", "Ma KH"};
        modelHDX = new DefaultTableModel(columnsHDX, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableHDX = new JTable(modelHDX);
        styleTable(tableHDX, "hdx");
        JScrollPane scrollPaneHDX = new JScrollPane(tableHDX);
        scrollPaneHDX.setBorder(BorderFactory.createTitledBorder("DOANH THU (Hoa Don Xuat)"));

        String[] columnsHDN = {"Ma HDN", "Ngay Nhap", "Ma NCC", "Ma NV"};
        modelHDN = new DefaultTableModel(columnsHDN, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableHDN = new JTable(modelHDN);
        styleTable(tableHDN, "hdn_basic");
        JScrollPane scrollPaneHDN = new JScrollPane(tableHDN);
        scrollPaneHDN.setBorder(BorderFactory.createTitledBorder("CHI TIEU (Danh sach Hoa Don Nhap)"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPaneHDX, scrollPaneHDN);
        splitPane.setResizeWeight(0.55);
        splitPane.setOneTouchExpandable(true);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel resultPanel = new JPanel(new GridLayout(3, 1, 0, 8));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        Font resultFont = new Font("Arial", Font.BOLD, 18);
        lblTongDoanhThu = new JLabel("Tong Doanh Thu (Sau thue): ...", SwingConstants.CENTER);
        lblTongDoanhThu.setFont(resultFont);
        lblTongDoanhThu.setForeground(new Color(0, 100, 0));
        lblTongChiTieu = new JLabel("Tong Chi Tieu (HD Nhap): ...", SwingConstants.CENTER);
        lblTongChiTieu.setFont(resultFont);
        lblTongChiTieu.setForeground(Color.RED.darker());
        lblLoiNhuan = new JLabel("Loi Nhuan (Thang .../...): ...", SwingConstants.CENTER);
        lblLoiNhuan.setFont(resultFont);
        lblLoiNhuan.setForeground(Color.BLUE.darker());
        resultPanel.add(lblTongDoanhThu);
        resultPanel.add(lblTongChiTieu);
        resultPanel.add(lblLoiNhuan);
        mainPanel.add(resultPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        btnThongKe.addActionListener(e -> {
            System.out.println("THONGKE_VIEW: Nut 'Xem Thong Ke' duoc nhan.");
            thongKe();
        });
        btnTaiLaiDuLieu.addActionListener(e -> {
            System.out.println("THONGKE_VIEW: Nut 'Tai Lai Du Lieu' duoc nhan.");
            taiLaiTatCaDuLieu();
        });
        btnBack.addActionListener(e -> {
            System.out.println("THONGKE_VIEW: Nut 'Tro ve' duoc nhan.");
            dispose();
        });
    }

    private void styleTable(JTable table, String tableType) {
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
            columnModel.getColumn(3).setPreferredWidth(80);
            columnModel.getColumn(4).setPreferredWidth(180);
            columnModel.getColumn(5).setPreferredWidth(80);
        } else if ("hdn_basic".equals(tableType) && columnModel.getColumnCount() == 4) {
            columnModel.getColumn(0).setPreferredWidth(100);
            columnModel.getColumn(1).setPreferredWidth(120);
            columnModel.getColumn(2).setPreferredWidth(100);
            columnModel.getColumn(3).setPreferredWidth(100);
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
                BorderFactory.createEmptyBorder(7, 15, 7, 15)
        ));
        return btn;
    }

    private void thongKe() {
        if (cboThang.getSelectedItem() == null || cboNam.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon thang va nam.", "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int thang = (int) cboThang.getSelectedItem();
        int nam = (int) cboNam.getSelectedItem();
        System.out.println("THONGKE_VIEW (thongKe): Thong ke cho Thang: " + thang + ", Nam: " + nam);

        // SỬA LỖI: Gọi phương thức static trực tiếp từ tên lớp Query
        double doanhThu = ThongKeQuery.getDoanhThuThang(thang, nam);
        lblTongDoanhThu.setText("Tong Doanh Thu (Sau thue): " + currencyFormat.format(doanhThu));
        System.out.println("THONGKE_VIEW (thongKe): Doanh thu = " + doanhThu);

        double chiTieu = ThongKeQuery.getChiTieuThang(thang, nam);
        lblTongChiTieu.setText("Tong Chi Tieu (HD Nhap): " + currencyFormat.format(chiTieu));
        System.out.println("THONGKE_VIEW (thongKe): Chi tieu = " + chiTieu);

        double loiNhuan = doanhThu - chiTieu;
        lblLoiNhuan.setText("Loi Nhuan (Thang " + thang + "/" + nam + "): " + currencyFormat.format(loiNhuan));
        if (loiNhuan >= 0) {
            lblLoiNhuan.setForeground(new Color(0, 128, 0));
        } else {
            lblLoiNhuan.setForeground(Color.RED.darker());
        }
        System.out.println("THONGKE_VIEW (thongKe): Loi nhuan = " + loiNhuan);

        loadHoaDonXuatTable(thang, nam);
        loadHoaDonNhapTable(thang, nam);
    }

    private void loadHoaDonXuatTable(int thang, int nam) {
        System.out.println("THONGKE_VIEW (loadHoaDonXuatTable): Tai HĐX cho Thang: " + thang + ", Nam: " + nam);
        modelHDX.setRowCount(0);
        // SỬA LỖI: Gọi phương thức static trực tiếp từ tên lớp Query
        List<HoaDonXuat> list = HoaDonXuatQuery.getHoaDonByMonthAndYear(thang, nam);

        if (list != null && !list.isEmpty()) {
            for (HoaDonXuat hdx : list) {
                double thanhTienTruocThue = hdx.getThanhTien();
                double mucThuePhanTram = hdx.getMucThue();
                double tongCongSauThue = thanhTienTruocThue * (1 + (mucThuePhanTram / 100.0));

                modelHDX.addRow(new Object[]{
                        hdx.getMaHDX(),
                        (hdx.getNgayLap() != null) ? hdx.getNgayLap().format(dateFormatter) : "N/A",
                        currencyFormat.format(thanhTienTruocThue),
                        String.format("%.2f%%", mucThuePhanTram),
                        currencyFormat.format(tongCongSauThue),
                        hdx.getMaKH() != null ? hdx.getMaKH() : "Khach le"
                });
            }
            System.out.println("THONGKE_VIEW (loadHoaDonXuatTable): Da tai " + list.size() + " hoa don xuat.");
        } else {
            System.out.println("THONGKE_VIEW (loadHoaDonXuatTable): Khong co hoa don xuat nao trong thang/nam da chon.");
        }
    }

    private void loadHoaDonNhapTable(int thang, int nam) {
        System.out.println("THONGKE_VIEW (loadHoaDonNhapTable): Tai HDN cho Thang: " + thang + ", Nam: " + nam);
        modelHDN.setRowCount(0);
        // SỬA LỖI: Gọi phương thức static trực tiếp từ tên lớp Query
        List<HoaDonNhap> list = HoaDonNhapQuery.getHoaDonNhapByMonthAndYear(thang, nam);

        if (list != null && !list.isEmpty()) {
            for (HoaDonNhap hdn : list) {
                modelHDN.addRow(new Object[]{
                        hdn.getMaHDN(),
                        (hdn.getNgayNhap() != null) ? hdn.getNgayNhap().format(dateFormatter) : "N/A", // Giả sử model có getNgayNhap()
                        hdn.getMaNCC(),
                        hdn.getMaNV()
                });
            }
            System.out.println("THONGKE_VIEW (loadHoaDonNhapTable): Da tai " + list.size() + " hoa don nhap.");
        } else {
            System.out.println("THONGKE_VIEW (loadHoaDonNhapTable): Khong co hoa don nhap nao trong thang/nam da chon.");
        }
    }

    private void taiLaiTatCaDuLieu() {
        if (cboThang.getSelectedItem() == null || cboNam.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon thang va nam truoc khi tai lai.", "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        thongKe();

        if (modelHDX.getRowCount() == 0 && modelHDN.getRowCount() == 0) {
             JOptionPane.showMessageDialog(this, "Khong co hoa don xuat hoac nhap nao trong thang " +
                            cboThang.getSelectedItem() + "/" + cboNam.getSelectedItem() + " de hien thi.",
                    "Khong co du lieu", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}