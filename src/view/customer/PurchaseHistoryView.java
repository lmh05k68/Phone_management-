package view.customer; 
import model.HoaDonXuat;
import model.ChiTietHDXuat;
import query.HoaDonXuatQuery; 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class PurchaseHistoryView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int maKH; // Sửa thành int
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JTextArea txtChiTietDonHang;
    private List<HoaDonXuat> currentDisplayedOrders;

    public PurchaseHistoryView(int maKH) { // Constructor nhận int
        this.maKH = maKH;
        setTitle("Lịch Sử Mua Hàng - KH: " + maKH);
        setSize(850, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadPurchaseHistory();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTitle = new JLabel("Lịch Sử Mua Hàng Của Bạn", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        String[] columnNames = {"Mã HĐ", "Ngày Lập", "Thành Tiền", "Thuế (%)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        historyTable = new JTable(tableModel);
        styleTable(historyTable);
        JScrollPane tableScrollPane = new JScrollPane(historyTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách đơn hàng"));
        tableScrollPane.setPreferredSize(new Dimension(780, 250));

        txtChiTietDonHang = new JTextArea(12, 40);
        txtChiTietDonHang.setEditable(false);
        txtChiTietDonHang.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane detailScrollPane = new JScrollPane(txtChiTietDonHang);
        detailScrollPane.setBorder(BorderFactory.createTitledBorder("Chi tiết đơn hàng đã chọn"));
        detailScrollPane.setPreferredSize(new Dimension(780, 250));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, detailScrollPane);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(8);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JButton btnBack = new JButton("Trở về");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnBack.setBackground(new Color(76, 175, 80));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.setPreferredSize(new Dimension(120, 40));
        btnBack.addActionListener(e -> {
            dispose();
            // Giả sử CustomerView đã được sửa để nhận int maKH
            new CustomerView(this.maKH).setVisible(true);
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnBack);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        historyTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && historyTable.getSelectedRow() != -1) {
                int selectedRowInView = historyTable.getSelectedRow();
                if (selectedRowInView >= 0 && selectedRowInView < tableModel.getRowCount()) {
                    int modelRow = historyTable.convertRowIndexToModel(selectedRowInView);
                     if (currentDisplayedOrders != null && modelRow < currentDisplayedOrders.size()) {
                        displayOrderDetails(currentDisplayedOrders.get(modelRow));
                    }
                }
            }
        });
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void loadPurchaseHistory() {
        tableModel.setRowCount(0);
        txtChiTietDonHang.setText("");

        // Gọi phương thức static từ HoaDonXuatQuery
        currentDisplayedOrders = HoaDonXuatQuery.getHoaDonByKhachHang(maKH); // Truyền int maKH

        if (currentDisplayedOrders == null || currentDisplayedOrders.isEmpty()) {
            txtChiTietDonHang.setText("Không có lịch sử mua hàng cho khách hàng #" + maKH);
            return;
        }

        for (HoaDonXuat hd : currentDisplayedOrders) {
            tableModel.addRow(new Object[]{
                hd.getMaHDX(),
                getFormattedDate(hd.getNgayLap()), // Truyền LocalDate
                String.format("%,.0f VNĐ", hd.getThanhTien()),
                String.format("%.1f", hd.getMucThue()) // Định dạng thuế nếu cần
            });
        }

        if (!currentDisplayedOrders.isEmpty()) {
            historyTable.setRowSelectionInterval(0, 0);
            // displayOrderDetails(currentDisplayedOrders.get(0)); // Hiển thị chi tiết cho đơn đầu tiên
        }
    }

    private String getFormattedDate(LocalDate localDate) { // Sửa: Nhận LocalDate
        if (localDate == null) {
            return "N/A";
        }
        try {
            // Bạn có thể chọn định dạng khác nếu muốn, ví dụ: DateTimeFormatter.ISO_LOCAL_DATE
            return localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        } catch (Exception e) {
            System.err.println("Lỗi định dạng ngày từ LocalDate: " + e.getMessage());
            return localDate.toString(); // Trả về dạng mặc định nếu có lỗi
        }
    }

    private void displayOrderDetails(HoaDonXuat selectedHD) {
        StringBuilder details = new StringBuilder();
        details.append("Mã hóa đơn: ").append(selectedHD.getMaHDX()).append("\n");
        details.append("Ngày lập: ").append(getFormattedDate(selectedHD.getNgayLap())).append("\n");
        details.append("Thành tiền: ").append(String.format("%,.0f VNĐ", selectedHD.getThanhTien())).append("\n");
        details.append("Thuế: ").append(String.format("%.1f%%", selectedHD.getMucThue())).append("\n");
        details.append("Mã nhân viên: ").append(selectedHD.getMaNV()).append("\n\n");

        details.append("DANH SÁCH SẢN PHẨM:\n");
        details.append("---------------------------------------------------------\n");
        details.append(String.format("%-10s | %-8s | %-15s\n", "Mã SP", "Số Lượng", "Đơn Giá")); // Sửa Đơn Giá Xuất
        details.append("---------------------------------------------------------\n");

        // Cần lấy chi tiết hóa đơn từ DB vì đối tượng HoaDonXuat có thể chưa load chi tiết
        List<ChiTietHDXuat> chiTietList = HoaDonXuatQuery.getChiTietHDXuat(selectedHD.getMaHDX());
        // Hoặc nếu bạn đã setChiTietList cho selectedHD trước đó:
        // List<ChiTietHDXuat> chiTietList = selectedHD.getChiTietList();


        if (chiTietList != null && !chiTietList.isEmpty()) {
            for (ChiTietHDXuat ct : chiTietList) {
                details.append(String.format("%-10s | %-8d | %,.0f VNĐ\n",
                        ct.getMaSP(), ct.getSoLuong(), ct.getDonGiaXuat()));
            }
        } else {
            details.append("Không có chi tiết sản phẩm cho hóa đơn này hoặc chưa tải.\n");
        }

        details.append("---------------------------------------------------------\n");
        txtChiTietDonHang.setText(details.toString());
        txtChiTietDonHang.setCaretPosition(0);
    }

    // Main method để test (tùy chọn)
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         // Cần một maKH (int) hợp lệ để test
    //         PurchaseHistoryView view = new PurchaseHistoryView(1); // Ví dụ MaKH = 1
    //         view.setVisible(true);
    //     });
    // }
}