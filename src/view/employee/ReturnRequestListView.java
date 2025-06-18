package view.employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class ReturnRequestListView extends JFrame {
	private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnBack, btnApprove, btnReject, btnPerformExchange;

    public ReturnRequestListView() {
        setTitle("Danh sách yêu cầu đổi/trả");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Danh sách Yêu Cầu Đổi/Trả");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Dùng font Segoe UI cho đồng bộ
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Mã YC", "Mã KH", "Mã SP Cụ Thể", "Mã ĐH", "Ngày YC", "Lý Do", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
        	private static final long serialVersionUID = 1L;};
        table = new JTable(model);
        styleTable(table);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5)); // Tăng khoảng cách giữa các nút
        btnApprove = createStyledButton("Duyệt", new Color(40, 167, 69));
        btnReject = createStyledButton("Từ chối", new Color(220, 53, 69));
        btnPerformExchange = createStyledButton("Thực hiện Đổi/Trả", new Color(23, 162, 184));

        actionButtonPanel.add(btnApprove);
        actionButtonPanel.add(btnReject);
        actionButtonPanel.add(btnPerformExchange);

        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        btnBack = createStyledButton("Trở về", new Color(108, 117, 125));
        backButtonPanel.add(btnBack);

        JPanel bottomControlsPanel = new JPanel(new BorderLayout());
        bottomControlsPanel.add(actionButtonPanel, BorderLayout.CENTER);
        bottomControlsPanel.add(backButtonPanel, BorderLayout.EAST);
        mainPanel.add(bottomControlsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void styleTable(JTable tbl) {
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.setRowHeight(28);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Căn chỉnh độ rộng cột
        TableColumnModel columnModel = tbl.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(60);  // Mã YC
        columnModel.getColumn(1).setMaxWidth(60);  // Mã KH
        columnModel.getColumn(2).setPreferredWidth(150); // Mã SP
        columnModel.getColumn(3).setMaxWidth(60);  // Mã ĐH
        columnModel.getColumn(4).setPreferredWidth(100); // Ngày
        columnModel.getColumn(5).setPreferredWidth(350); // Lý do
        columnModel.getColumn(6).setPreferredWidth(120); // Trạng thái
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false); // Bỏ viền khi focus
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return btn;
    }
    public void addApproveButtonListener(ActionListener listener) { btnApprove.addActionListener(listener); }
    public void addRejectButtonListener(ActionListener listener) { btnReject.addActionListener(listener); }
    public void addBackButtonListener(ActionListener listener) { btnBack.addActionListener(listener); }
    public void addPerformExchangeButtonListener(ActionListener listener) { btnPerformExchange.addActionListener(listener); }
    public String getSelectedRequestId() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return null;
        return model.getValueAt(selectedRow, 0).toString();
    }

    public String getCurrentStatusOfSelectedRequest() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return null;
        return model.getValueAt(selectedRow, 6).toString(); // Cột 6 là Trạng Thái
    }

    public String getSelectedProductCode() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return null;
        return model.getValueAt(selectedRow, 2).toString(); // Cột 2 là Mã SP Cụ Thể
    }
    
    public DefaultTableModel getModel() { return model; }
}