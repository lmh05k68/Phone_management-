package view.admin;

import controller.admin.ThongKeController;
import model.HoaDonNhap;
import model.HoaDonXuat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class ThongKeView extends JPanel { 
    private static final long serialVersionUID = 1L;

    private JComboBox<Integer> cboThang, cboNam;
    private JLabel lblTongDoanhThu, lblTongChiTieu, lblLoiNhuan;

    private JTable tableHDX;
    private DefaultTableModel modelHDX;
    private JTable tableHDN;
    private DefaultTableModel modelHDN;

    private final ThongKeController controller;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ThongKeView() {
        this.controller = new ThongKeController(this);
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setBackground(new Color(245, 245, 245));

        initUI();
        // Tải dữ liệu cho tháng/năm hiện tại khi khởi động
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();
        cboThang.setSelectedItem(currentMonth);
        cboNam.setSelectedItem(currentYear);
        controller.thongKeTheoThang(currentMonth, currentYear);
    }

    // Phương thức để chạy thử nghiệm
    public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Thống Kê Kinh Doanh");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new ThongKeView());
            frame.setVisible(true);
        });
    }

    private void initUI() {
        // --- Top Panel ---
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);
        
        JLabel lblViewTitle = new JLabel("Thống Kê Kinh Doanh", SwingConstants.CENTER);
        lblViewTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblViewTitle.setForeground(new Color(0, 102, 204));
        topPanel.add(lblViewTitle, BorderLayout.NORTH);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        controlsPanel.setOpaque(false);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        controlsPanel.add(new JLabel("Chọn tháng:"));
        cboThang = new JComboBox<>();
        cboThang.setFont(inputFont);
        IntStream.rangeClosed(1, 12).forEach(cboThang::addItem);

        controlsPanel.add(new JLabel("Chọn năm:"));
        cboNam = new JComboBox<>();
        cboNam.setFont(inputFont);
        int namHienTai = Year.now().getValue();
        IntStream.rangeClosed(namHienTai - 5, namHienTai).forEach(cboNam::addItem);

        JButton btnThongKe = createStyledButton("Xem Thống Kê", new Color(0, 123, 255));
        
        controlsPanel.add(cboThang);
        controlsPanel.add(cboNam);
        controlsPanel.add(btnThongKe);
        topPanel.add(controlsPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // --- Tables Panel ---
        String[] columnsHDX = {"Mã HĐX", "Ngày Lập", "Thành Tiền (Sau thuế)", "Mã KH", "Mã NV"};
        modelHDX = new DefaultTableModel(columnsHDX, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                if (col == 2) return BigDecimal.class; return Object.class;
            }
        };
        tableHDX = new JTable(modelHDX);
        styleTable(tableHDX);
        JScrollPane scrollPaneHDX = new JScrollPane(tableHDX);
        scrollPaneHDX.setBorder(BorderFactory.createTitledBorder("Chi tiết Doanh thu (Hóa Đơn Xuất)"));
        
        String[] columnsHDN = {"Mã HDN", "Ngày Nhập", "Mã NCC", "Mã NV"};
        modelHDN = new DefaultTableModel(columnsHDN, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableHDN = new JTable(modelHDN);
        styleTable(tableHDN);
        JScrollPane scrollPaneHDN = new JScrollPane(tableHDN);
        scrollPaneHDN.setBorder(BorderFactory.createTitledBorder("Chi tiết Chi tiêu (Hóa Đơn Nhập)"));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPaneHDX, scrollPaneHDN);
        splitPane.setResizeWeight(0.55);
        
        add(splitPane, BorderLayout.CENTER);

        // --- Result Panel ---
        JPanel resultPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        resultPanel.setOpaque(false);
        resultPanel.setBorder(new EmptyBorder(15, 0, 10, 0));
        
        lblTongDoanhThu = createResultLabel("Tổng Doanh Thu: 0 VNĐ", new Color(0, 128, 0));
        lblTongChiTieu = createResultLabel("Tổng Chi Tiêu: 0 VNĐ", new Color(204, 0, 0));
        lblLoiNhuan = createResultLabel("Lợi Nhuận: 0 VNĐ", new Color(0, 102, 204));
        
        resultPanel.add(lblTongDoanhThu);
        resultPanel.add(lblTongChiTieu);
        resultPanel.add(lblLoiNhuan);
        
        add(resultPanel, BorderLayout.SOUTH);

        // --- Listeners ---
        btnThongKe.addActionListener(e -> {
            int thang = (int) cboThang.getSelectedItem();
            int nam = (int) cboNam.getSelectedItem();
            controller.thongKeTheoThang(thang, nam);
        });
    }
    
    // --- Public methods for Controller to call ---

    public void updateThongKeTongHop(BigDecimal doanhThu, BigDecimal chiTieu, BigDecimal loiNhuan, int thang, int nam) {
        lblTongDoanhThu.setText("Tổng Doanh Thu: " + currencyFormatter.format(doanhThu));
        lblTongChiTieu.setText("Tổng Chi Tiêu: " + currencyFormatter.format(chiTieu));
        lblLoiNhuan.setText(String.format("Lợi Nhuận (%d/%d): %s", thang, nam, currencyFormatter.format(loiNhuan)));
        lblLoiNhuan.setForeground(loiNhuan.compareTo(BigDecimal.ZERO) >= 0 ? new Color(0, 102, 204) : Color.RED);
    }

    public void updateTableHDX(List<HoaDonXuat> list) {
        modelHDX.setRowCount(0);
        if (list != null) {
            for (HoaDonXuat hdx : list) {
                modelHDX.addRow(new Object[]{
                    hdx.getMaHDX(),
                    hdx.getNgayLap(),
                    hdx.getThanhTien(), // Giá trị đã bao gồm thuế
                    hdx.getMaKH(),
                    hdx.getMaNV()
                });
            }
        }
    }

    public void updateTableHDN(List<HoaDonNhap> list) {
        modelHDN.setRowCount(0);
        if (list != null) {
            for (HoaDonNhap hdn : list) {
                modelHDN.addRow(new Object[]{
                    hdn.getMaHDN(),
                    hdn.getNgayNhap(),
                    hdn.getMaNCC(),
                    hdn.getMaNV()
                });
            }
        }
    }

    // --- Helper methods for UI ---

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Custom renderer cho ngày tháng và tiền tệ
        table.setDefaultRenderer(Object.class, new DateRenderer());
        TableColumnModel tcm = table.getColumnModel();
        if (tcm.getColumnCount() > 2 && table.getModel() == modelHDX) {
             tcm.getColumn(2).setCellRenderer(new CurrencyRenderer());
        }
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private JLabel createResultLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(color);
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, color));
        return label;
    }
    
    // --- Custom TableCellRenderers ---

    private class DateRenderer extends javax.swing.table.DefaultTableCellRenderer {
    	private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof LocalDate) {
                value = ((LocalDate) value).format(dateFormatter);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private class CurrencyRenderer extends javax.swing.table.DefaultTableCellRenderer {
    	private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof BigDecimal) {
                value = currencyFormatter.format(value);
            }
            setHorizontalAlignment(SwingConstants.RIGHT);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}