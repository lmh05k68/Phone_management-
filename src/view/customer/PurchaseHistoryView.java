package view.customer;

import model.ChiTietDonHang;
import model.HoaDonXuat;
import query.HoaDonXuatQuery;
import query.SPCuTheQuery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
public class PurchaseHistoryView extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font DETAIL_FONT = new Font("Monospaced", Font.PLAIN, 14);
    private static final Color HEADER_BG_COLOR = new Color(32, 136, 203);
    private static final Color HEADER_FG_COLOR = Color.WHITE;
    private static final Color BG_COLOR = new Color(245, 245, 245);

    // --- Components & Data ---
    private final int maKH;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JTextArea txtChiTietDonHang;
    private List<HoaDonXuat> currentDisplayedOrders = Collections.emptyList();

    public PurchaseHistoryView(int maKH) {
        this.maKH = maKH;
        setTitle("Lịch Sử Mua Hàng - Khách Hàng #" + maKH);
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadPurchaseHistory();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(BG_COLOR);

        JLabel lblTitle = new JLabel("Lịch Sử Mua Hàng Của Bạn", SwingConstants.CENTER);
        lblTitle.setFont(TITLE_FONT);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        mainPanel.add(createMainContent(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JSplitPane createMainContent() {
        String[] columnNames = {"Mã HĐ", "Ngày Lập", "Tiền Trước Thuế", "Tổng Tiền (Đã có VAT)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class;
                    case 1: return LocalDate.class;
                    case 2:
                    case 3: return BigDecimal.class;
                    default: return Object.class;
                }
            }
        };
        historyTable = new JTable(tableModel);
        styleTable(historyTable);

        JScrollPane tableScrollPane = new JScrollPane(historyTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));

        txtChiTietDonHang = new JTextArea("Chọn một hóa đơn để xem chi tiết...");
        txtChiTietDonHang.setEditable(false);
        txtChiTietDonHang.setFont(DETAIL_FONT);
        txtChiTietDonHang.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane detailScrollPane = new JScrollPane(txtChiTietDonHang);
        detailScrollPane.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn đã chọn"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, detailScrollPane);
        splitPane.setResizeWeight(0.45);

        historyTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && historyTable.getSelectedRow() != -1) {
                int modelRow = historyTable.convertRowIndexToModel(historyTable.getSelectedRow());
                if (modelRow >= 0 && modelRow < currentDisplayedOrders.size()) {
                    displayOrderDetails(currentDisplayedOrders.get(modelRow));
                }
            }
        });
        
        return splitPane;
    }
    
    private JPanel createBottomPanel() {
        JButton btnBack = createStyledButton("Trở về", HEADER_BG_COLOR, HEADER_FG_COLOR);
        btnBack.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnBack);
        return bottomPanel;
    }

    /**
     * Áp dụng style và các renderer tùy chỉnh cho bảng.
     */
    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setFont(TABLE_FONT);
        table.getTableHeader().setFont(TABLE_HEADER_FONT);
        table.getTableHeader().setBackground(HEADER_BG_COLOR);
        table.getTableHeader().setForeground(HEADER_FG_COLOR);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateRowSorter(true); // Cho phép sắp xếp

        // Áp dụng các renderer để định dạng và căn chỉnh
        table.setDefaultRenderer(LocalDate.class, new DateRenderer());
        table.setDefaultRenderer(BigDecimal.class, new CurrencyRenderer());
        
        // TỐI ƯU: Căn giữa cho cột Mã HĐ (Integer)
        table.getColumnModel().getColumn(0).setCellRenderer(new CenteredRenderer());

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(100);
        columnModel.getColumn(1).setPreferredWidth(120);
        columnModel.getColumn(2).setPreferredWidth(180);
        columnModel.getColumn(3).setPreferredWidth(180);
    }
    
    private JButton createStyledButton(String text, Color backgroundColor, Color foregroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(foregroundColor);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 25, 8, 25));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(backgroundColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(backgroundColor);
            }
        });
        return btn;
    }

    /**
     * Tải danh sách hóa đơn của khách hàng từ CSDL trong một luồng nền.
     */
    private void loadPurchaseHistory() {
        tableModel.setRowCount(0);
        txtChiTietDonHang.setText("Đang tải lịch sử mua hàng, vui lòng đợi...");

        SwingWorker<List<HoaDonXuat>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<HoaDonXuat> doInBackground() throws Exception {
                return HoaDonXuatQuery.getHoaDonByKhachHang(maKH);
            }

            @Override
            protected void done() {
                try {
                    currentDisplayedOrders = get();
                    if (currentDisplayedOrders == null || currentDisplayedOrders.isEmpty()) {
                        txtChiTietDonHang.setText("Bạn chưa có lịch sử mua hàng.");
                        return;
                    }

                    for (HoaDonXuat hd : currentDisplayedOrders) {
                        BigDecimal thanhTien = hd.getThanhTien() != null ? hd.getThanhTien() : BigDecimal.ZERO;
                        BigDecimal mucThuePercent = hd.getMucThue() != null ? hd.getMucThue() : BigDecimal.ZERO;
                        
                        // Tối ưu: Tính ngược tiền trước thuế từ tổng tiền và mức thuế.
                        // Đây là cách hiệu quả nhất để hiển thị trên bảng mà không cần truy vấn CSDL thêm cho mỗi dòng.
                        BigDecimal motCongThue = BigDecimal.ONE.add(mucThuePercent.divide(new BigDecimal(100)));
                        
                        BigDecimal tienTruocThue = BigDecimal.ZERO;
                        if (motCongThue.compareTo(BigDecimal.ZERO) != 0) {
                             tienTruocThue = thanhTien.divide(motCongThue, 2, RoundingMode.HALF_UP);
                        }

                        tableModel.addRow(new Object[]{
                            hd.getMaHDX(),
                            hd.getNgayLap(),
                            tienTruocThue,
                            thanhTien
                        });
                    }

                    // Tự động chọn dòng đầu tiên nếu có dữ liệu
                    if (tableModel.getRowCount() > 0) {
                        historyTable.setRowSelectionInterval(0, 0);
                    } else {
                        txtChiTietDonHang.setText("Bạn chưa có lịch sử mua hàng.");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    txtChiTietDonHang.setText("Lỗi khi tải lịch sử mua hàng: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    /**
     * Tải và hiển thị chi tiết của một hóa đơn đã chọn.
     * @param selectedHD Hóa đơn được chọn từ bảng.
     */
    private void displayOrderDetails(HoaDonXuat selectedHD) {
        if (selectedHD == null) return;

        txtChiTietDonHang.setText("Đang tải chi tiết hóa đơn #" + selectedHD.getMaHDX() + "...");
        SwingWorker<String, Void> detailWorker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                List<ChiTietDonHang> chiTietList = SPCuTheQuery.getChiTietDonHangByHDX(selectedHD.getMaHDX());
                return formatOrderDetails(selectedHD, chiTietList);
            }
            
            @Override
            protected void done() {
                try {
                    txtChiTietDonHang.setText(get());
                    txtChiTietDonHang.setCaretPosition(0); // Cuộn lên đầu
                } catch (Exception e) {
                    txtChiTietDonHang.setText("Lỗi khi tải chi tiết hóa đơn: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        detailWorker.execute();
    }

    /**
     * Định dạng chi tiết hóa đơn thành một chuỗi String để hiển thị.
     */
    private String formatOrderDetails(HoaDonXuat hd, List<ChiTietDonHang> chiTietList) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        StringBuilder details = new StringBuilder();
        details.append(String.format(" CHI TIẾT HÓA ĐƠN SỐ: %-8d | NGÀY: %s\n",
                hd.getMaHDX(), hd.getNgayLap().format(dateFormat)));
        details.append("=================================================================\n\n");
        details.append(String.format(" %-18s | %-30s | %15s\n", "Mã Sản Phẩm", "Tên Sản Phẩm (Màu)", "Giá Bán"));
        details.append("-----------------------------------------------------------------\n");

        BigDecimal tongTienTruocThue = BigDecimal.ZERO;
        if (chiTietList == null || chiTietList.isEmpty()) {
            details.append(" Không tìm thấy chi tiết sản phẩm cho hóa đơn này.\n");
        } else {
            // Tính tổng tiền các sản phẩm (chính là tiền trước thuế) từ chi tiết
            for (ChiTietDonHang ct : chiTietList) {
                BigDecimal giaXuat = ct.getGiaXuat() != null ? ct.getGiaXuat() : BigDecimal.ZERO;
                String tenSPdaydu = ct.getTenSP() + " (" + ct.getMau() + ")";
                details.append(String.format(" %-18s | %-30.30s | %15s\n",
                        ct.getMaSPCuThe(), tenSPdaydu, currencyFormat.format(giaXuat)));
                tongTienTruocThue = tongTienTruocThue.add(giaXuat);
            }
        }
        
        details.append("-----------------------------------------------------------------\n");
        details.append(String.format("%54s %15s\n", "Tiền hàng:", currencyFormat.format(tongTienTruocThue)));
        
        BigDecimal mucThuePercent = hd.getMucThue() != null ? hd.getMucThue() : BigDecimal.ZERO;
        BigDecimal tienThue = tongTienTruocThue.multiply(mucThuePercent.divide(new BigDecimal(100)));
        details.append(String.format("%54s %15s\n", "Thuế VAT (" + mucThuePercent.stripTrailingZeros().toPlainString() + "%):", currencyFormat.format(tienThue)));
        
        details.append("=================================================================\n");
        // Luôn hiển thị tổng thanh toán được lưu trong CSDL làm con số cuối cùng
        BigDecimal tongThanhToan = hd.getThanhTien() != null ? hd.getThanhTien() : BigDecimal.ZERO;
        details.append(String.format("%54s %15s\n", "TỔNG THANH TOÁN:", currencyFormat.format(tongThanhToan)));

        return details.toString();
    }
    
    // --- CÁC LỚP RENDERER TÙY CHỈNH ---

    /**
     * Renderer để định dạng giá trị tiền tệ và căn phải.
     */
    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof BigDecimal) {
                setText(FORMATTER.format(value));
            }
            setHorizontalAlignment(SwingConstants.RIGHT); // Tiền tệ luôn căn phải
            return this;
        }
    }
    
    /**
     * Renderer để định dạng ngày tháng và căn giữa.
     */
    private static class DateRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof LocalDate) {
                setText(((LocalDate) value).format(FORMATTER));
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }

    /**
     * Renderer để căn giữa nội dung trong ô.
     */
    private static class CenteredRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }
}