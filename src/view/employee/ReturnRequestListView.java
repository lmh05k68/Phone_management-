package view.employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class ReturnRequestListView extends JFrame {
    private static final long serialVersionUID = 1L;
    public JTable table;
    public DefaultTableModel model;
    private JButton btnBack, btnApprove, btnReject;

    public ReturnRequestListView() {
        System.out.println("VIEW: Constructor ReturnRequestListView bắt đầu.");
        setTitle("Danh sách yêu cầu đổi/trả");
        setSize(880, 520); // Điều chỉnh kích thước nếu cần
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Danh sách Yêu Cầu Đổi/Trả");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Mã YC", "Mã KH", "Mã SP", "Mã ĐH", "Ngày YC", "Lý Do", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        System.out.println("VIEW: DefaultTableModel đã được khởi tạo.");

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Đặt độ rộng cột
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(70);  // Mã YC
        columnModel.getColumn(1).setPreferredWidth(70);  // Mã KH
        columnModel.getColumn(2).setPreferredWidth(70);  // Mã SP
        columnModel.getColumn(3).setPreferredWidth(80);  // Mã ĐH
        columnModel.getColumn(4).setPreferredWidth(140); // Ngày YC (cho cả giờ phút)
        columnModel.getColumn(5).setPreferredWidth(280); // Lý do
        columnModel.getColumn(6).setPreferredWidth(100); // Trạng thái
        System.out.println("VIEW: JTable và TableColumnModel đã được cấu hình.");


        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        System.out.println("VIEW: JScrollPane đã được thêm vào mainPanel.");

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionButtonPanel.setOpaque(false);
        btnApprove = createStyledButton("Duyệt yêu cầu", new Color(40, 167, 69));
        btnReject = createStyledButton("Từ chối yêu cầu", new Color(220, 53, 69));
        actionButtonPanel.add(btnApprove);
        actionButtonPanel.add(btnReject);

        JPanel bottomOuterPanel = new JPanel(new BorderLayout());
        bottomOuterPanel.setOpaque(false);
        bottomOuterPanel.add(actionButtonPanel, BorderLayout.NORTH);

        btnBack = createStyledButton("Trở về", new Color(108, 117, 125));
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        backButtonPanel.setOpaque(false);
        backButtonPanel.add(btnBack);
        bottomOuterPanel.add(backButtonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomOuterPanel, BorderLayout.SOUTH);
        System.out.println("VIEW: Các panel nút đã được thêm vào mainPanel.");

        setContentPane(mainPanel);
        System.out.println("VIEW: Constructor ReturnRequestListView kết thúc.");
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        return btn;
    }

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
            // Cột 0 là "Mã YC" (iddt)
            return model.getValueAt(selectedRow, 0).toString();
        }
        System.out.println("VIEW_getSelectedRequestId: Không có hàng nào được chọn.");
        return null;
    }

    public String getCurrentStatusOfSelectedRequest() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            // Cột 6 là "Trạng Thái"
            Object statusObj = model.getValueAt(selectedRow, 6);
            return statusObj != null ? statusObj.toString() : null;
        }
        System.out.println("VIEW_getCurrentStatusOfSelectedRequest: Không có hàng nào được chọn.");
        return null;
    }
}