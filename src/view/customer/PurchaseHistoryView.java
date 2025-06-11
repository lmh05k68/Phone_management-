package view.customer;
import javax.swing.border.EmptyBorder;
import model.ChiTietDonHang;
import model.HoaDonXuat;
import query.HoaDonXuatQuery;
import query.SPCuTheQuery;

import javax.swing.*;
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

public class PurchaseHistoryView extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- Hằng số UI cho style nhất quán ---
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

            // TỐI ƯU: Định nghĩa kiểu dữ liệu cho từng cột để sắp xếp đúng
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
        
        // SỬA LỖI & TỐI ƯU: Cân đối lại tỷ lệ hiển thị
        splitPane.setResizeWeight(0.45); // Phân bổ không gian khi resize
        splitPane.setDividerLocation(0.45); // Đặt vị trí thanh chia ban đầu ở 45%

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

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setFont(TABLE_FONT);
        table.getTableHeader().setFont(TABLE_HEADER_FONT);
        table.getTableHeader().setBackground(HEADER_BG_COLOR);
        table.getTableHeader().setForeground(HEADER_FG_COLOR);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        
        // TỐI ƯU: Sử dụng renderer để định dạng dữ liệu, giúp sắp xếp đúng
        table.setDefaultRenderer(LocalDate.class, new DateRenderer());
        table.setDefaultRenderer(BigDecimal.class, new CurrencyRenderer());

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(80);
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
                    if (currentDisplayedOrders.isEmpty()) {
                        txtChiTietDonHang.setText("Bạn chưa có lịch sử mua hàng.");
                        return;
                    }

                    for (HoaDonXuat hd : currentDisplayedOrders) {
                        BigDecimal thanhTien = hd.getThanhTien() != null ? hd.getThanhTien() : BigDecimal.ZERO;
                        BigDecimal mucThuePercent = hd.getMucThue() != null ? hd.getMucThue() : BigDecimal.ZERO;
                        BigDecimal motCongThue = BigDecimal.ONE.add(mucThuePercent.divide(new BigDecimal(100)));
                        
                        BigDecimal tienTruocThue = BigDecimal.ZERO;
                        if (motCongThue.compareTo(BigDecimal.ZERO) != 0) {
                             tienTruocThue = thanhTien.divide(motCongThue, 2, RoundingMode.HALF_UP);
                        }
                        
                        // TỐI ƯU: Thêm đối tượng gốc vào model để sắp xếp đúng
                        tableModel.addRow(new Object[]{
                            hd.getMaHDX(),
                            hd.getNgayLap(),
                            tienTruocThue,
                            thanhTien
                        });
                    }

                    if (tableModel.getRowCount() > 0) {
                        historyTable.setRowSelectionInterval(0, 0);
                    }
                } catch (Exception e) {
                    txtChiTietDonHang.setText("Lỗi khi tải lịch sử mua hàng: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void displayOrderDetails(HoaDonXuat selectedHD) {
        if (selectedHD == null) return;
        
        // Chi tiết hóa đơn được tải trong luồng riêng để không làm chậm việc chọn dòng
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
                    txtChiTietDonHang.setCaretPosition(0);
                } catch (Exception e) {
                    txtChiTietDonHang.setText("Lỗi khi tải chi tiết hóa đơn.");
                    e.printStackTrace();
                }
            }
        };
        detailWorker.execute();
    }

    private String formatOrderDetails(HoaDonXuat hd, List<ChiTietDonHang> chiTietList) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        StringBuilder details = new StringBuilder();
        details.append(String.format("CHI TIẾT HÓA ĐƠN SỐ: %d | NGÀY: %s\n",
                hd.getMaHDX(), hd.getNgayLap().format(dateFormat)));
        details.append("=================================================================\n\n");
        details.append(String.format("%-20s | %-30s | %15s\n", "Mã Sản Phẩm", "Tên Sản Phẩm (Màu)", "Giá Bán"));
        details.append("-----------------------------------------------------------------\n");

        BigDecimal tongTienTruocThue = BigDecimal.ZERO;
        if (chiTietList == null || chiTietList.isEmpty()) {
            details.append("Không tìm thấy chi tiết sản phẩm cho hóa đơn này.\n");
        } else {
            for (ChiTietDonHang ct : chiTietList) {
                BigDecimal giaXuat = ct.getGiaXuat() != null ? ct.getGiaXuat() : BigDecimal.ZERO;
                details.append(String.format("%-20s | %-30.30s | %15s\n",
                        ct.getMaSPCuThe(), ct.getTenSP() + " (" + ct.getMau() + ")", currencyFormat.format(giaXuat)));
                tongTienTruocThue = tongTienTruocThue.add(giaXuat);
            }
        }
        
        details.append("-----------------------------------------------------------------\n");
        details.append(String.format("%54s %15s\n", "Tổng tiền hàng:", currencyFormat.format(tongTienTruocThue)));
        
        BigDecimal mucThue = hd.getMucThue() != null ? hd.getMucThue() : BigDecimal.ZERO;
        BigDecimal tienThue = tongTienTruocThue.multiply(mucThue.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
        details.append(String.format("%54s %15s\n", "Thuế VAT (" + mucThue.stripTrailingZeros().toPlainString() + "%):", currencyFormat.format(tienThue)));
        
        details.append("=================================================================\n");
        BigDecimal tongThanhToan = hd.getThanhTien() != null ? hd.getThanhTien() : BigDecimal.ZERO;
        details.append(String.format("%54s %15s\n", "TỔNG THANH TOÁN:", currencyFormat.format(tongThanhToan)));

        return details.toString();
    }

    // --- Các lớp Renderer tùy chỉnh ---
    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof BigDecimal) {
                setText(FORMATTER.format(value));
            }
            setHorizontalAlignment(SwingConstants.RIGHT);
            return this;
        }
    }
    
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
}