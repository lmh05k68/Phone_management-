package view.customer;

import model.HoaDonXuat;
import model.ChiTietHDXuat;
import query.HoaDonXuatQuery;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class PurchaseHistoryView extends JFrame {
    private static final long serialVersionUID = 1L;
    private String maKH;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private HoaDonXuatQuery hoaDonXuatQuery;
    private JTextArea txtChiTietDonHang;
    private List<HoaDonXuat> currentDisplayedOrders;

    public PurchaseHistoryView(String maKH) {
        this.maKH = maKH;
        this.hoaDonXuatQuery = new HoaDonXuatQuery();

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

        // Tiêu đề
        JLabel lblTitle = new JLabel("Lịch Sử Mua Hàng Của Bạn", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Bảng lịch sử
        String[] columnNames = {"Mã HĐ", "Ngày Lập", "Thành Tiền", "Thuế (%)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            public boolean isCellEditable(int row, int column) { return false; }
        };

        historyTable = new JTable(tableModel);
        styleTable(historyTable);
        JScrollPane tableScrollPane = new JScrollPane(historyTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách đơn hàng"));
        tableScrollPane.setPreferredSize(new Dimension(780, 250));

        // Chi tiết đơn hàng
        txtChiTietDonHang = new JTextArea(12, 40);
        txtChiTietDonHang.setEditable(false);
        txtChiTietDonHang.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane detailScrollPane = new JScrollPane(txtChiTietDonHang);
        detailScrollPane.setBorder(BorderFactory.createTitledBorder("Chi tiết đơn hàng đã chọn"));
        detailScrollPane.setPreferredSize(new Dimension(780, 250));

        // Panel chứa bảng + chi tiết
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, detailScrollPane);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(8);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Nút trở về
        JButton btnBack = new JButton("Trở về");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnBack.setBackground(new Color(76, 175, 80));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.setPreferredSize(new Dimension(120, 40));
        btnBack.addActionListener(e -> {
            dispose();
            new CustomerView(maKH).setVisible(true);
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnBack);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        historyTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && historyTable.getSelectedRow() != -1) {
                int selectedRow = historyTable.convertRowIndexToModel(historyTable.getSelectedRow());
                if (currentDisplayedOrders != null && selectedRow < currentDisplayedOrders.size()) {
                    displayOrderDetails(currentDisplayedOrders.get(selectedRow));
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

        currentDisplayedOrders = hoaDonXuatQuery.getHoaDonByKhachHang(maKH);

        if (currentDisplayedOrders == null || currentDisplayedOrders.isEmpty()) {
            txtChiTietDonHang.setText("Không có lịch sử mua hàng cho khách hàng #" + maKH);
            return;
        }

        for (HoaDonXuat hd : currentDisplayedOrders) {
            tableModel.addRow(new Object[]{
                hd.getMaHDX(),
                getFormattedDate(hd.getNgayLap()),
                String.format("%,.0f VNĐ", hd.getThanhTien()),
                hd.getMucThue()
            });
        }

        if (!currentDisplayedOrders.isEmpty()) {
            historyTable.setRowSelectionInterval(0, 0);
        }
    }

    private String getFormattedDate(Object ngayLapObj) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            if (ngayLapObj instanceof java.util.Date) {
                return dateFormat.format((java.util.Date) ngayLapObj);
            } else if (ngayLapObj instanceof String) {
                return (String) ngayLapObj;
            }
        } catch (Exception e) {
            System.err.println("Lỗi định dạng ngày: " + e.getMessage());
        }
        return "N/A";
    }

    private void displayOrderDetails(HoaDonXuat selectedHD) {
        StringBuilder details = new StringBuilder();
        details.append("Mã hóa đơn: ").append(selectedHD.getMaHDX()).append("\n");
        details.append("Ngày lập: ").append(getFormattedDate(selectedHD.getNgayLap())).append("\n");
        details.append("Thành tiền: ").append(String.format("%,.0f VNĐ", selectedHD.getThanhTien())).append("\n");
        details.append("Thuế: ").append(selectedHD.getMucThue()).append("%\n");
        details.append("Mã nhân viên: ").append(selectedHD.getMaNV()).append("\n\n");

        details.append("DANH SÁCH SẢN PHẨM:\n");
        details.append("---------------------------------------------------------\n");
        details.append(String.format("%-10s | %-8s | %-15s\n", "Mã SP", "Số Lượng", "Đơn Giá Xuất"));
        details.append("---------------------------------------------------------\n");

        List<ChiTietHDXuat> chiTietList = selectedHD.getChiTietList();
        if (chiTietList != null && !chiTietList.isEmpty()) {
            for (ChiTietHDXuat ct : chiTietList) {
                details.append(String.format("%-10s | %-8d | %,.0f VNĐ\n",
                        ct.getMaSP(), ct.getSoLuong(), ct.getDonGiaXuat()));
            }
        } else {
            details.append("Không có chi tiết sản phẩm cho hóa đơn này.\n");
        }

        details.append("---------------------------------------------------------\n");
        txtChiTietDonHang.setText(details.toString());
        txtChiTietDonHang.setCaretPosition(0);
    }
}