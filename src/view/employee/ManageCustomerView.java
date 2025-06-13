package view.employee;

import model.KhachHang;
import query.KhachHangQuery;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
public class ManageCustomerView extends JFrame {
    private static final long serialVersionUID = 1L;

    // --- UI Constants ---
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color ALT_ROW_COLOR = new Color(242, 242, 242);

    // --- Components ---
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;

    public ManageCustomerView() {
        setTitle("Quản lý khách hàng có tài khoản");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        reloadData();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Quản Lý Khách Hàng");
        lblTitle.setFont(TITLE_FONT);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(createTopPanel(), BorderLayout.NORTH);
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createTopPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("Tìm kiếm (Tên/SĐT/Mã KH):");
        lblSearch.setFont(LABEL_FONT);
        searchPanel.add(lblSearch);

        txtSearch = new JTextField(30);
        txtSearch.setFont(LABEL_FONT);
        searchPanel.add(txtSearch);

        JButton btnSearch = createStyledButton("Tìm", PRIMARY_COLOR);
        JButton btnReload = createStyledButton("Tải lại", SECONDARY_COLOR);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReload);

        // Action Listeners for top buttons
        btnSearch.addActionListener(e -> searchCustomers());
        txtSearch.addActionListener(e -> searchCustomers()); // Allow search on Enter key
        btnReload.addActionListener(e -> reloadData());

        return searchPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"Mã KH", "Họ Tên", "Số Điện Thoại", "Điểm Tích Lũy"};
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }

            // TỐI ƯU: Khai báo kiểu dữ liệu để sắp xếp chính xác
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 3) {
                    return Integer.class;
                }
                return String.class;
            }
        };

        // TỐI ƯU: Sử dụng JTable tùy chỉnh để có màu xen kẽ
        table = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ALT_ROW_COLOR);
                }
                return c;
            }
        };
        
        styleTable(table);
        return new JScrollPane(table);
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JButton btnBack = createStyledButton("Trở về", DANGER_COLOR);
        btnBack.addActionListener(e -> dispose());
        bottomPanel.add(btnBack);
        return bottomPanel;
    }

    private void styleTable(JTable tbl) {
        tbl.setFont(TABLE_FONT);
        tbl.getTableHeader().setFont(TABLE_HEADER_FONT);
        tbl.getTableHeader().setBackground(new Color(60, 63, 65));
        tbl.getTableHeader().setForeground(Color.WHITE);
        tbl.setRowHeight(32);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoCreateRowSorter(true); // Bật chức năng sắp xếp

        // TỐI ƯU: Áp dụng renderer để căn giữa cho tất cả các cột
        CenteredRenderer centeredRenderer = new CenteredRenderer();
        tbl.setDefaultRenderer(String.class, centeredRenderer);
        tbl.setDefaultRenderer(Integer.class, centeredRenderer);

        TableColumnModel columnModel = tbl.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);
        columnModel.getColumn(1).setPreferredWidth(250);
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(3).setPreferredWidth(120);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setFocusPainted(false);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return btn;
    }

    private void reloadData() {
        txtSearch.setText("");
        loadCustomerData(null); // Tải tất cả khách hàng
    }

    private void searchCustomers() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        loadCustomerData(keyword); // Tìm kiếm theo từ khóa
    }

    /**
     * Tải dữ liệu khách hàng (toàn bộ hoặc tìm kiếm) trong một luồng nền.
     * @param keyword Từ khóa tìm kiếm. Nếu là null, tải tất cả khách hàng.
     */
    private void loadCustomerData(String keyword) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tableModel.setRowCount(0);

        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<KhachHang> doInBackground() {
                if (keyword == null || keyword.isEmpty()) {
                    return KhachHangQuery.getCustomersWithAccounts();
                } else {
                    try {
                        int maKHSearch = Integer.parseInt(keyword);
                        KhachHang kh = KhachHangQuery.getKhachHangById(maKHSearch);
                        return (kh != null) ? List.of(kh) : Collections.emptyList();
                    } catch (NumberFormatException e) {
                        return KhachHangQuery.searchCustomersWithAccounts(keyword);
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    List<KhachHang> customers = get();
                    for (KhachHang kh : customers) {
                        tableModel.addRow(new Object[]{
                                kh.getMaKH(),
                                kh.getHoTen(),
                                kh.getSdtKH(),
                                kh.getSoDiemTichLuy()
                        });
                    }
                    if (customers.isEmpty() && keyword != null) {
                        JOptionPane.showMessageDialog(ManageCustomerView.this,
                                "Không tìm thấy khách hàng nào khớp với: '" + keyword + "'",
                                "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ManageCustomerView.this,
                            "Lỗi khi tải dữ liệu khách hàng.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }
    private static class CenteredRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        public CenteredRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }
}