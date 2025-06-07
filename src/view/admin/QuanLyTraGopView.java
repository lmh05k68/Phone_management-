package view.admin;

import model.TraGop;
import query.TraGopQuery;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.DecimalFormat;
// import java.text.SimpleDateFormat; // Xóa hoặc comment dòng này
import java.time.LocalDate; // Import LocalDate nếu model dùng
import java.time.format.DateTimeFormatter; // << THÊM DÒNG NÀY
import java.util.List;

public class QuanLyTraGopView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel model;
    // private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Xóa hoặc comment
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // << THAY THẾ
    private DecimalFormat currencyFormat = new DecimalFormat("###,###,### VNĐ");

    public QuanLyTraGopView() {
        // ... (phần còn lại của constructor giữ nguyên) ...
        System.out.println("QLTRAGOP_VIEW: Constructor QuanLyTraGopView bắt đầu.");
        setTitle("Quản lý phiếu trả góp");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        String[] columnNames = {"Mã Phiếu", "Mã HDX", "Tiền Gốc", "Số Tháng", "Lãi Suất", "Ngày Bắt Đầu", "Trạng Thái"};
        model = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JButton btnRefresh = createStyledButton("Làm mới");
        btnRefresh.addActionListener(e -> {
            System.out.println("QLTRAGOP_VIEW: Nút 'Làm mới' được nhấn.");
            loadData();
        });

        JButton btnBack = createStyledButton("Trở về");
        btnBack.addActionListener(e -> {
            System.out.println("QLTRAGOP_VIEW: Nút 'Trở về' được nhấn.");
            dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,5));
        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);

        System.out.println("QLTRAGOP_VIEW: Gọi loadData() từ constructor.");
        loadData();
        System.out.println("QLTRAGOP_VIEW: Constructor QuanLyTraGopView kết thúc.");
    }

    private void styleTable(JTable table) {
        // ... (giữ nguyên)
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);
        columnModel.getColumn(1).setPreferredWidth(80);
        columnModel.getColumn(2).setPreferredWidth(120);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(80);
        columnModel.getColumn(5).setPreferredWidth(120);
        columnModel.getColumn(6).setPreferredWidth(120);
    }

    private JButton createStyledButton(String text) {
        // ... (giữ nguyên)
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

    private void loadData() {
        System.out.println("QLTRAGOP_VIEW_loadData: Bắt đầu tải dữ liệu trả góp.");
        model.setRowCount(0);
        TraGopQuery tgq = new TraGopQuery();

        try {
            System.out.println("QLTRAGOP_VIEW_loadData: Gọi tgq.updateTrangThaiTraGop().");
            tgq.updateTrangThaiTraGop();
            System.out.println("QLTRAGOP_VIEW_loadData: Gọi tgq.getAllPhieuTraGop().");
            List<TraGop> list = tgq.getAllPhieuTraGop();

            if (list == null || list.isEmpty()) {
                System.out.println("QLTRAGOP_VIEW_loadData: Không có dữ liệu trả góp để hiển thị.");
            } else {
                System.out.println("QLTRAGOP_VIEW_loadData: Tìm thấy " + list.size() + " phiếu trả góp.");
                for (TraGop p : list) {
                    LocalDate ngayBatDau = p.getNgayBatDau(); // Lấy LocalDate
                    String ngayBatDauFormatted = "N/A";
                    if (ngayBatDau != null) {
                        ngayBatDauFormatted = ngayBatDau.format(dateFormatter); // << SỬ DỤNG DateTimeFormatter
                    }

                    model.addRow(new Object[]{
                            p.getMaPhieuTG(),
                            p.getMaHDX(),
                            currencyFormat.format(p.getTienGoc()),
                            p.getSoThang(),
                            String.format("%.2f %%", p.getLaiSuat()),
                            ngayBatDauFormatted, // Sử dụng chuỗi đã định dạng
                            p.isDaThanhToan() ? "Đã hoàn thành" : "Đang trả góp"
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("QLTRAGOP_VIEW_loadData: Lỗi khi tải dữ liệu trả góp: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu trả góp: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}