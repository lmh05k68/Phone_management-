package view.admin; 
import model.TraGop;
import query.TraGopQuery; 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QuanLyTraGopView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel model;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0 VNĐ"); // Bỏ dấu ### ở đầu để hiển thị số nhỏ hơn đúng

    public QuanLyTraGopView() {
        System.out.println("QLTRAGOP_VIEW: Constructor QuanLyTraGopView bắt đầu.");
        setTitle("Quản Lý Phiếu Trả Góp");
        setSize(950, 500); // Tăng chiều rộng một chút
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Đóng cửa sổ này, không thoát ứng dụng

        initUI();
        loadData(); // Tải dữ liệu khi khởi tạo
        System.out.println("QLTRAGOP_VIEW: Constructor QuanLyTraGopView kết thúc.");
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10,10)); // Thêm khoảng cách
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15)); // Thêm padding

        JLabel lblTitle = new JLabel("Danh Sách Phiếu Trả Góp", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);


        String[] columnNames = {"Mã Phiếu TG", "Mã HĐX", "Tiền Gốc", "Số Tháng", "Lãi Suất", "Ngày Bắt Đầu", "Trạng Thái"};
        model = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp
            }
        };
        table = new JTable(model);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        mainPanel.add(scroll, BorderLayout.CENTER);

        JButton btnRefresh = createStyledButton("Làm mới danh sách");
        btnRefresh.setBackground(new Color(23, 162, 184)); // Màu khác
        btnRefresh.addActionListener(e -> {
            System.out.println("QLTRAGOP_VIEW: Nút 'Làm mới' được nhấn.");
            loadData();
        });

        JButton btnBack = createStyledButton("Trở về");
        btnBack.setBackground(new Color(108, 117, 125)); // Màu xám
        btnBack.addActionListener(e -> {
            System.out.println("QLTRAGOP_VIEW: Nút 'Trở về' được nhấn.");
            dispose();
            // new AdminView().setVisible(true); // Nếu muốn quay lại AdminView
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Tăng padding
        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnBack);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void styleTable(JTable tbl) {
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.setRowHeight(28);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoCreateRowSorter(true); // Cho phép sắp xếp

        TableColumnModel columnModel = tbl.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // Mã Phiếu
        columnModel.getColumn(1).setPreferredWidth(80);  // Mã HDX
        columnModel.getColumn(2).setPreferredWidth(130); // Tiền Gốc
        columnModel.getColumn(3).setPreferredWidth(80);  // Số Tháng
        columnModel.getColumn(4).setPreferredWidth(90);  // Lãi Suất
        columnModel.getColumn(5).setPreferredWidth(120); // Ngày Bắt Đầu
        columnModel.getColumn(6).setPreferredWidth(130); // Trạng Thái
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Font đậm hơn
        btn.setFocusPainted(false);
        // Màu nền sẽ được đặt riêng cho từng nút
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18) // Padding
        ));
        return btn;
    }

    private void loadData() {
        System.out.println("QLTRAGOP_VIEW_loadData: Bắt đầu tải dữ liệu trả góp.");
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            System.out.println("QLTRAGOP_VIEW_loadData: Gọi TraGopQuery.updateTrangThaiTraGopDaHoanThanh().");
            // Gọi phương thức static, không cần tạo instance tgq
            TraGopQuery.updateTrangThaiTraGopDaHoanThanh(); // Cập nhật trạng thái trước khi tải

            System.out.println("QLTRAGOP_VIEW_loadData: Gọi TraGopQuery.getAllPhieuTraGop().");
            List<TraGop> list = TraGopQuery.getAllPhieuTraGop();

            if (list == null || list.isEmpty()) {
                System.out.println("QLTRAGOP_VIEW_loadData: Không có dữ liệu trả góp để hiển thị.");
                // Có thể thêm một dòng thông báo vào bảng
                // model.addRow(new Object[]{"Không có dữ liệu", "-", "-", "-", "-", "-", "-"});
            } else {
                System.out.println("QLTRAGOP_VIEW_loadData: Tìm thấy " + list.size() + " phiếu trả góp.");
                for (TraGop p : list) {
                    LocalDate ngayBatDau = p.getNgayBatDau();
                    String ngayBatDauFormatted = "N/A";
                    if (ngayBatDau != null) {
                        ngayBatDauFormatted = ngayBatDau.format(dateFormatter);
                    }

                    model.addRow(new Object[]{
                            p.getMaPhieuTG(),   // int
                            p.getMaHDX(),       // int
                            currencyFormat.format(p.getTienGoc()), // double
                            p.getSoThang(),     // int
                            String.format("%.2f %%", p.getLaiSuat()), // double
                            ngayBatDauFormatted, // String (đã định dạng)
                            p.isDaThanhToan() ? "Đã hoàn thành" : "Đang trả góp" // boolean
                    });
                }
            }
        } catch (Exception e) { // Bắt lỗi rộng hơn
            System.err.println("QLTRAGOP_VIEW_loadData: Lỗi khi tải dữ liệu trả góp: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể tải dữ liệu phiếu trả góp: " + e.getMessage(), "Lỗi Tải Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main method để test (tùy chọn)
    // public static void main(String[] args) {
    //     try {
    //         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     SwingUtilities.invokeLater(() -> new QuanLyTraGopView().setVisible(true));
    // }
}