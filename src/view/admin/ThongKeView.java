package view.admin;

import controller.admin.ThongKeController;
import model.HoaDonNhap;
import model.HoaDonXuat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
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

    // --- UI Constants ---
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_TABLE_CELL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_RESULT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Color COLOR_TITLE = new Color(0, 102, 204);
    private static final Color COLOR_PRIMARY_BUTTON = new Color(0, 123, 255);

    // --- Components ---
    private JComboBox<Integer> cboThang, cboNam;
    private JLabel lblTongDoanhThu, lblTongChiTieu, lblLoiNhuan;
    private JTable tableHDX;
    private DefaultTableModel modelHDX;
    private JTable tableHDN;
    private DefaultTableModel modelHDN;

    // --- Helpers ---
    private final ThongKeController controller;

    public ThongKeView() {
        this.controller = new ThongKeController(this);
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setBackground(new Color(245, 245, 245));

        initUI();
        loadInitialData();
    }

    private void loadInitialData() {
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();
        cboThang.setSelectedItem(currentMonth);
        cboNam.setSelectedItem(currentYear);
        controller.thongKeTheoThang(currentMonth, currentYear);
    }

    private void initUI() {
        add(createTopPanel(), BorderLayout.NORTH);
        add(createTablesPanel(), BorderLayout.CENTER);
        add(createResultPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);

        JLabel lblViewTitle = new JLabel("THỐNG KÊ KINH DOANH", SwingConstants.CENTER);
        lblViewTitle.setFont(FONT_TITLE);
        lblViewTitle.setForeground(COLOR_TITLE);
        topPanel.add(lblViewTitle, BorderLayout.NORTH);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        controlsPanel.setOpaque(false);

        controlsPanel.add(new JLabel("Chọn tháng:")).setFont(FONT_LABEL);
        cboThang = new JComboBox<>();
        cboThang.setFont(FONT_LABEL);
        IntStream.rangeClosed(1, 12).forEach(cboThang::addItem);

        controlsPanel.add(new JLabel("Chọn năm:")).setFont(FONT_LABEL);
        cboNam = new JComboBox<>();
        cboNam.setFont(FONT_LABEL);
        int namHienTai = Year.now().getValue();
        IntStream.rangeClosed(namHienTai - 5, namHienTai).forEach(cboNam::addItem);

        JButton btnThongKe = createStyledButton("Xem Thống Kê", COLOR_PRIMARY_BUTTON);

        controlsPanel.add(cboThang);
        controlsPanel.add(cboNam);
        controlsPanel.add(btnThongKe);

        btnThongKe.addActionListener(e -> {
            int thang = (int) cboThang.getSelectedItem();
            int nam = (int) cboNam.getSelectedItem();
            controller.thongKeTheoThang(thang, nam);
        });

        topPanel.add(controlsPanel, BorderLayout.CENTER);
        return topPanel;
    }

    private JSplitPane createTablesPanel() {
        String[] columnsHDX = {"Mã HĐX", "Ngày Lập", "Thành Tiền (Sau thuế)", "Mã KH", "Mã NV"};
        modelHDX = new DefaultTableModel(columnsHDX, 0) {
        	private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
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
        splitPane.setOpaque(false);

        return splitPane;
    }

    private JPanel createResultPanel() {
        JPanel resultPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        resultPanel.setOpaque(false);
        resultPanel.setBorder(new EmptyBorder(15, 0, 10, 0));

        lblTongDoanhThu = createResultLabel("Tổng Doanh Thu: 0 ₫", new Color(0, 128, 0));
        lblTongChiTieu = createResultLabel("Tổng Chi Tiêu: 0 ₫", new Color(204, 0, 0));
        lblLoiNhuan = createResultLabel("Lợi Nhuận: 0 ₫", COLOR_TITLE);

        resultPanel.add(lblTongDoanhThu);
        resultPanel.add(lblTongChiTieu);
        resultPanel.add(lblLoiNhuan);

        return resultPanel;
    }

    public void updateThongKeTongHop(BigDecimal doanhThu, BigDecimal chiTieu, BigDecimal loiNhuan, int thang, int nam) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        lblTongDoanhThu.setText("Tổng Doanh Thu: " + currencyFormatter.format(doanhThu));
        lblTongChiTieu.setText("Tổng Chi Tiêu: " + currencyFormatter.format(chiTieu));
        lblLoiNhuan.setText(String.format("Lợi Nhuận (%d/%d): %s", thang, nam, currencyFormatter.format(loiNhuan)));
        lblLoiNhuan.setForeground(loiNhuan.compareTo(BigDecimal.ZERO) >= 0 ? COLOR_TITLE : Color.RED);
    }

    public void updateTableHDX(List<HoaDonXuat> list) {
        modelHDX.setRowCount(0);
        if (list != null) {
            for (HoaDonXuat hdx : list) {
                modelHDX.addRow(new Object[]{
                    hdx.getMaHDX(), hdx.getNgayLap(), hdx.getThanhTien(),
                    hdx.getMaKH(), hdx.getMaNV()
                });
            }
        }
    }

    public void updateTableHDN(List<HoaDonNhap> list) {
        modelHDN.setRowCount(0);
        if (list != null) {
            for (HoaDonNhap hdn : list) {
                modelHDN.addRow(new Object[]{
                    hdn.getMaHDN(), hdn.getNgayNhap(), hdn.getMaNCC(), hdn.getMaNV()
                });
            }
        }
    }

    // TỐI ƯU: Phương thức styleTable sử dụng một renderer "thông minh"
    private void styleTable(JTable table) {
        table.setFont(FONT_TABLE_CELL);
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(new Color(220, 220, 220));

        // Tạo renderer một lần và tái sử dụng
        SmartCellRenderer smartRenderer = new SmartCellRenderer();

        // Áp dụng renderer "thông minh" cho TẤT CẢ các cột
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(smartRenderer);
        }
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createResultLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(FONT_RESULT);
        label.setForeground(color);
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, color));
        return label;
    } class SmartCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value == null) {
                setText("");
                setHorizontalAlignment(SwingConstants.CENTER);
                return cell;
            }
            if (value instanceof BigDecimal || value instanceof Double || value instanceof Float) {
                setText(currencyFormatter.format(value));
                setHorizontalAlignment(SwingConstants.RIGHT); // Căn phải cho dễ so sánh
            } else if (value instanceof LocalDate) {
                setText(((LocalDate) value).format(dateFormatter));
                setHorizontalAlignment(SwingConstants.CENTER);
            } else if (value instanceof Integer || value instanceof Long) {
                // SỐ NGUYÊN (CÁC CỘT MÃ) -> KHÔNG CÓ "₫"
                setText(value.toString());
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setText(value.toString());
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            return cell;
        }
    }
}