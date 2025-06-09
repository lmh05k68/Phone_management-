package view.employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class ReturnRequestListView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable table; // Để private, controller không nên truy cập trực tiếp JTable
    private DefaultTableModel model; // Để private, cung cấp getter nếu controller cần
    private JButton btnBack, btnApprove, btnReject;

    public ReturnRequestListView() {
        System.out.println("VIEW: Constructor ReturnRequestListView bắt đầu.");
        setTitle("Danh sách yêu cầu đổi/trả");
        setSize(920, 550); // Tăng kích thước một chút để vừa các cột
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Danh sách Yêu Cầu Đổi/Trả");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        String[] columns = {"Mã YC (ID)", "Mã KH", "Mã SP", "Mã ĐH", "Ngày YC", "Lý Do", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa trực tiếp trên bảng
            }
        };
        System.out.println("VIEW: DefaultTableModel đã được khởi tạo.");

        table = new JTable(model);
        styleTable(table); // Gọi hàm style
        System.out.println("VIEW: JTable và TableColumnModel đã được cấu hình.");


        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        System.out.println("VIEW: JScrollPane đã được thêm vào mainPanel.");
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Căn giữa
        actionButtonPanel.setOpaque(false); // Để màu nền của mainPanel hiển thị
        btnApprove = createStyledButton("Duyệt yêu cầu", new Color(40, 167, 69));
        btnReject = createStyledButton("Từ chối yêu cầu", new Color(220, 53, 69));
        actionButtonPanel.add(btnApprove);
        actionButtonPanel.add(btnReject);
        btnBack = createStyledButton("Trở về", new Color(108, 117, 125));
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Căn phải
        backButtonPanel.setOpaque(false);
        backButtonPanel.add(btnBack);
        JPanel bottomControlsPanel = new JPanel(new BorderLayout());
        bottomControlsPanel.setOpaque(false);
        bottomControlsPanel.add(actionButtonPanel, BorderLayout.CENTER);
        bottomControlsPanel.add(backButtonPanel, BorderLayout.EAST);


        mainPanel.add(bottomControlsPanel, BorderLayout.SOUTH);
        System.out.println("VIEW: Các panel nút đã được thêm vào mainPanel.");

        setContentPane(mainPanel);
        System.out.println("VIEW: Constructor ReturnRequestListView kết thúc.");
    }

    private void styleTable(JTable tbl) {
        tbl.setFillsViewportHeight(true);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.setRowHeight(28);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoCreateRowSorter(true); // Cho phép sắp xếp theo cột

        TableColumnModel columnModel = tbl.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);  // Mã YC (ID)
        columnModel.getColumn(1).setPreferredWidth(70);  // Mã KH
        columnModel.getColumn(2).setPreferredWidth(70);  // Mã SP
        columnModel.getColumn(3).setPreferredWidth(80);  // Mã ĐH
        columnModel.getColumn(4).setPreferredWidth(120); // Ngày YC
        columnModel.getColumn(5).setPreferredWidth(300); // Lý do (cho rộng hơn)
        columnModel.getColumn(6).setPreferredWidth(110); // Trạng thái
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        return btn;
    }

    // Các phương thức để Controller gắn Listener
    public void addApproveButtonListener(ActionListener listener) {
        btnApprove.addActionListener(listener);
    }

    public void addRejectButtonListener(ActionListener listener) {
        btnReject.addActionListener(listener);
    }

    public void addBackButtonListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }
    public String getSelectedRequestId() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Object idObj = model.getValueAt(table.convertRowIndexToModel(selectedRow), 0); // Luôn dùng convertRowIndexToModel
            return idObj != null ? idObj.toString() : null;
        }
        System.out.println("VIEW_getSelectedRequestId: Không có hàng nào được chọn.");
        return null;
    }
    public String getCurrentStatusOfSelectedRequest() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Object statusObj = model.getValueAt(table.convertRowIndexToModel(selectedRow), 6);
            return statusObj != null ? statusObj.toString() : null;
        }
        System.out.println("VIEW_getCurrentStatusOfSelectedRequest: Không có hàng nào được chọn.");
        return null;
    }
    public DefaultTableModel getModel() {
        return model;
    }
}