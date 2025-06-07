package view.employee;

import model.KhachHang;
import query.KhachHangQuery; // Đảm bảo import đúng

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel; // Import để tùy chỉnh cột
import java.awt.*;
import java.util.List;

public class ManageCustomerView extends JFrame {
    private static final long serialVersionUID = 1L;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private String maNV;
    public ManageCustomerView(String maNV) {
        this.maNV = maNV;
        System.out.println("MANAGE_CUSTOMER_VIEW: Khởi tạo cho NV: " + maNV);
        setTitle("Quản lý khách hàng có tài khoản");
        setSize(850, 600); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        initUI();
        loadCustomerData(); 
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
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        // btn.setAlignmentX(Component.CENTER_ALIGNMENT); // Không cần nếu panel cha là FlowLayout
        return btn;
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Dùng BorderLayout
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Quản Lý Khách Hàng (Có Tài Khoản)");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Căn trái
        searchPanel.setOpaque(false);
        // searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Không cần với FlowLayout

        searchPanel.add(new JLabel("Tìm kiếm (Tên/SĐT):"));
        txtSearch = new JTextField(25); // Tăng độ rộng trường tìm kiếm
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnSearch = createStyledButton("Tìm"); // Tên nút ngắn gọn
        JButton btnReload = createStyledButton("Tải lại"); // Nút tải lại danh sách

        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReload);
        
        // Gộp searchPanel vào một panel khác để kiểm soát vị trí tốt hơn nếu cần
        JPanel topControlsPanel = new JPanel(new BorderLayout());
        topControlsPanel.setOpaque(false);
        topControlsPanel.add(searchPanel, BorderLayout.WEST);
        mainPanel.add(topControlsPanel, BorderLayout.CENTER); // Đặt search panel ở vị trí khác nếu cần


        // Table Panel (sẽ được đặt trong Center của mainPanel sau khi searchPanel được điều chỉnh)
        JPanel tableContainerPanel = new JPanel(new BorderLayout());
        tableContainerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Khoảng cách trên cho bảng
        String[] columns = {"Mã KH", "Họ Tên", "Số Điện Thoại", "Điểm Tích Lũy"};
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        styleTable(table); // Áp dụng style
        JScrollPane scrollPane = new JScrollPane(table);
        tableContainerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Cấu trúc lại mainPanel
        JPanel centerContentPanel = new JPanel(new BorderLayout(0,10));
        centerContentPanel.setOpaque(false);
        centerContentPanel.add(topControlsPanel, BorderLayout.NORTH);
        centerContentPanel.add(tableContainerPanel, BorderLayout.CENTER);
        mainPanel.add(centerContentPanel, BorderLayout.CENTER);


        // Bottom Button Panel
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Căn phải
        bottomButtonPanel.setOpaque(false);
        bottomButtonPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        JButton btnBack = createStyledButton("Trở về");
        bottomButtonPanel.add(btnBack);
        mainPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Action Listeners
        btnSearch.addActionListener(e -> {
            System.out.println("MANAGE_CUSTOMER_VIEW: Nút 'Tìm' được nhấn.");
            String keyword = txtSearch.getText().trim();
            if (!keyword.isEmpty()) {
                searchCustomers(keyword);
            } else {
                // Nếu keyword rỗng, có thể hiển thị thông báo hoặc tải lại tất cả
                JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                // Hoặc: reloadData();
            }
        });

        btnReload.addActionListener(e -> {
            System.out.println("MANAGE_CUSTOMER_VIEW: Nút 'Tải lại' được nhấn.");
            reloadData();
        });

        btnBack.addActionListener(e -> {
            System.out.println("MANAGE_CUSTOMER_VIEW: Nút 'Trở về' được nhấn.");
            // new EmployeeView(maNV).setVisible(true); // Chỉ tạo mới nếu EmployeeView đã bị dispose
            dispose(); // Đóng cửa sổ hiện tại
        });
    }
    
    private void styleTable(JTable tbl) {
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.setRowHeight(28);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);

        TableColumnModel columnModel = tbl.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);  // Mã KH
        columnModel.getColumn(1).setPreferredWidth(200); // Họ Tên
        columnModel.getColumn(2).setPreferredWidth(120); // SĐT
        columnModel.getColumn(3).setPreferredWidth(120); // Điểm
    }


    private void loadCustomerData() {
        System.out.println("MANAGE_CUSTOMER_VIEW: Bắt đầu loadCustomerData.");
        // SỬA Ở ĐÂY: Gọi phương thức static trực tiếp qua tên lớp
        List<KhachHang> customers = KhachHangQuery.getCustomersWithAccounts();
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        if (customers != null && !customers.isEmpty()) {
            for (KhachHang kh : customers) {
                tableModel.addRow(new Object[]{
                        kh.getMaKH(), kh.getHoTen(), kh.getSdtKH(), kh.getSoDiemTichLuy()
                });
            }
            System.out.println("MANAGE_CUSTOMER_VIEW: Đã tải " + customers.size() + " khách hàng.");
        } else {
            System.out.println("MANAGE_CUSTOMER_VIEW: Không có khách hàng nào có tài khoản để hiển thị.");
            // Có thể hiển thị thông báo trên bảng
            // tableModel.addRow(new Object[]{"Không có dữ liệu", "", "", ""});
        }
    }

    private void searchCustomers(String keyword) {
        System.out.println("MANAGE_CUSTOMER_VIEW: Bắt đầu searchCustomers với keyword: " + keyword);
        tableModel.setRowCount(0); 
        List<KhachHang> customers = KhachHangQuery.searchCustomersWithAccounts(keyword);
        if (customers != null && !customers.isEmpty()) {
            for (KhachHang kh : customers) {
                tableModel.addRow(new Object[]{
                        kh.getMaKH(), kh.getHoTen(), kh.getSdtKH(), kh.getSoDiemTichLuy()
                });
            }
            System.out.println("MANAGE_CUSTOMER_VIEW: Tìm thấy " + customers.size() + " khách hàng.");
        } else {
            System.out.println("MANAGE_CUSTOMER_VIEW: Không tìm thấy khách hàng nào khớp với từ khóa.");
             JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng nào khớp với: '" + keyword + "'", "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void reloadData() {
        System.out.println("MANAGE_CUSTOMER_VIEW: Bắt đầu reloadData.");
        txtSearch.setText(""); 
        loadCustomerData();
    }
}