package view.employee; 
import java.util.ArrayList;
import model.KhachHang;
import query.KhachHangQuery;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

public class ManageCustomerView extends JFrame {
    private static final long serialVersionUID = 1L;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;

    public ManageCustomerView() { 
        System.out.println("MANAGE_CUSTOMER_VIEW: Khởi tạo.");
        setTitle("Quản lý khách hàng có tài khoản");
        setSize(900, 650); 
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
        return btn;
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Quản Lý Khách Hàng (Có Tài Khoản)");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24)); 
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); 
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Tìm kiếm (Tên/SĐT/Mã KH):")); // Thêm tìm theo Mã KH
        txtSearch = new JTextField(30); // Tăng độ rộng
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnSearch = createStyledButton("Tìm");
        JButton btnReload = createStyledButton("Tải lại");
        btnReload.setBackground(new Color(108,117,125));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReload);
        
        JPanel topControlsPanel = new JPanel(new BorderLayout());
        topControlsPanel.setOpaque(false);
        topControlsPanel.add(searchPanel, BorderLayout.CENTER); 
        JPanel tableContainerPanel = new JPanel(new BorderLayout());
        tableContainerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        String[] columns = {"Mã KH", "Họ Tên", "Số Điện Thoại", "Điểm Tích Lũy"};
        tableModel = new DefaultTableModel(columns, 0) {
        	private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        tableContainerPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel centerContentPanel = new JPanel(new BorderLayout(0,10));
        centerContentPanel.setOpaque(false);
        centerContentPanel.add(topControlsPanel, BorderLayout.NORTH); // Search panel ở trên table
        centerContentPanel.add(tableContainerPanel, BorderLayout.CENTER);
        mainPanel.add(centerContentPanel, BorderLayout.CENTER);


        // Bottom Button Panel
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomButtonPanel.setOpaque(false);
        bottomButtonPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        JButton btnBack = createStyledButton("Trở về");
        btnBack.setBackground(new Color(220,53,69)); // Màu đỏ cho nút trở về
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
                JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnReload.addActionListener(e -> {
            System.out.println("MANAGE_CUSTOMER_VIEW: Nút 'Tải lại' được nhấn.");
            reloadData();
        });

        btnBack.addActionListener(e -> {
            System.out.println("MANAGE_CUSTOMER_VIEW: Nút 'Trở về' được nhấn.");
            dispose(); 
        });
    }
    
    private void styleTable(JTable tbl) {
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.setRowHeight(30); // Tăng chiều cao hàng
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);

        TableColumnModel columnModel = tbl.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);  // Mã KH
        columnModel.getColumn(0).setMinWidth(60);
        columnModel.getColumn(1).setPreferredWidth(250); // Họ Tên
        columnModel.getColumn(1).setMinWidth(150);
        columnModel.getColumn(2).setPreferredWidth(150); // SĐT
        columnModel.getColumn(2).setMinWidth(100);
        columnModel.getColumn(3).setPreferredWidth(120); // Điểm
        columnModel.getColumn(3).setMinWidth(100);
    }


    private void loadCustomerData() {
        System.out.println("MANAGE_CUSTOMER_VIEW: Bắt đầu loadCustomerData.");
        List<KhachHang> customers = KhachHangQuery.getCustomersWithAccounts();
        tableModel.setRowCount(0); 
        if (customers != null && !customers.isEmpty()) {
            for (KhachHang kh : customers) {
                tableModel.addRow(new Object[]{
                        kh.getMaKH(), // MaKH là int
                        kh.getHoTen(), 
                        kh.getSdtKH(), 
                        kh.getSoDiemTichLuy()
                });
            }
            System.out.println("MANAGE_CUSTOMER_VIEW: Đã tải " + customers.size() + " khách hàng.");
        } else {
            System.out.println("MANAGE_CUSTOMER_VIEW: Không có khách hàng nào có tài khoản để hiển thị.");
        }
    }

    private void searchCustomers(String keyword) {
        System.out.println("MANAGE_CUSTOMER_VIEW: Bắt đầu searchCustomers với keyword: " + keyword);
        tableModel.setRowCount(0); 
        // KhachHangQuery.searchCustomersWithAccounts cần được cập nhật để có thể tìm theo cả MaKH (int)
        // Hiện tại nó chỉ tìm theo Tên/SĐT (String). Cần logic để phân biệt.
        List<KhachHang> customers;
        try { // Thử parse keyword thành int để tìm theo MaKH
             int maKHSearch = Integer.parseInt(keyword);
             KhachHang kh = KhachHangQuery.getKhachHangById(maKHSearch);
             customers = new ArrayList<>();
             if (kh != null) customers.add(kh);
        } catch (NumberFormatException e) { // Nếu không phải số, tìm theo tên/sđt
             customers = KhachHangQuery.searchCustomersWithAccounts(keyword);
        }

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
    // Main method để test
     public static void main(String[] args) {
         try {
             for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                 if ("Nimbus".equals(info.getName())) {
                     UIManager.setLookAndFeel(info.getClassName());
                     break;
                 }
             }
         } catch (Exception e) {
             System.err.println("Không thể áp dụng Nimbus Look and Feel: " + e.getMessage());
         }
         SwingUtilities.invokeLater(() -> new ManageCustomerView(/* Bỏ maNV nếu không cần */).setVisible(true));
     }
}