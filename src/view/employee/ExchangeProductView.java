package view.employee;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class ExchangeProductView extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTable availableProductsTable;
    private JComboBox<String> cbOldProductStatus;
    private JButton btnConfirm, btnCancel;
    private JLabel lblOldProductInfo, lblNewProductInfo, lblPriceDifference;

    public ExchangeProductView(int idDT, String maSPCuThe_Cu, BigDecimal oldProductPrice) {
        super((Frame) null, "Thực hiện Đổi/Trả - YC #" + idDT, true);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        initUI(maSPCuThe_Cu, oldProductPrice);
        
        btnCancel.addActionListener(e -> dispose());
    }

    private void initUI(String maSPCuThe_Cu, BigDecimal oldProductPrice) {
        // --- Sử dụng JPanel chính với BorderLayout ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // --- Panel thông tin trên cùng, sử dụng GridBagLayout để căn chỉnh ---
        JPanel topInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 14);

        // --- Dòng 1: Thông tin sản phẩm cũ ---
        JLabel lblOldProductTitle = new JLabel("Sản phẩm khách trả:");
        lblOldProductTitle.setFont(labelFont);
        
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
        String oldPriceFormatted = currencyFormatter.format(oldProductPrice);
        lblOldProductInfo = new JLabel(maSPCuThe_Cu + " | Giá trị: " + oldPriceFormatted);
        lblOldProductInfo.setFont(valueFont);

        JLabel lblProcessTitle = new JLabel("Xử lý:");
        lblProcessTitle.setFont(labelFont);
        
        String[] statuses = {"Nhập lại kho", "Hàng lỗi", "Hủy bỏ"};
        cbOldProductStatus = new JComboBox<>(statuses);
        cbOldProductStatus.setFont(valueFont);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_START;
        topInfoPanel.add(lblOldProductTitle, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        topInfoPanel.add(lblOldProductInfo, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        topInfoPanel.add(lblProcessTitle, gbc);
        
        gbc.gridx = 3;
        topInfoPanel.add(cbOldProductStatus, gbc);

        // --- Dòng 2: Thông tin sản phẩm mới ---
        JLabel lblNewProductTitle = new JLabel("Sản phẩm mới:");
        lblNewProductTitle.setFont(labelFont);
        
        lblNewProductInfo = new JLabel("Chưa chọn từ bảng dưới");
        lblNewProductInfo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        
        gbc.gridx = 0; gbc.gridy = 1;
        topInfoPanel.add(lblNewProductTitle, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; // Kéo dài qua 3 cột
        topInfoPanel.add(lblNewProductInfo, gbc);
        
        // --- Dòng 3: Chênh lệch chi phí ---
        JLabel lblPriceDiffTitle = new JLabel("Chênh lệch chi phí:");
        lblPriceDiffTitle.setFont(labelFont);
        
        lblPriceDifference = new JLabel("Vui lòng chọn sản phẩm mới để tính toán");
        lblPriceDifference.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 14));
        
        gbc.gridx = 0; gbc.gridy = 2;
        topInfoPanel.add(lblPriceDiffTitle, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        topInfoPanel.add(lblPriceDifference, gbc);

        mainPanel.add(topInfoPanel, BorderLayout.NORTH);

        // --- Bảng sản phẩm có thể đổi ---
        availableProductsTable = new JTable();
        availableProductsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleTable(availableProductsTable);
        
        JScrollPane scrollPane = new JScrollPane(availableProductsTable);
        scrollPane.setBorder(new TitledBorder("Danh sách sản phẩm có thể đổi"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Panel nút bấm dưới cùng ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnConfirm = createStyledButton("Xác nhận Hoàn tất", new Color(40, 167, 69));
        btnCancel = createStyledButton("Hủy", new Color(108, 117, 125));
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnConfirm);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    // --- Các phương thức helper để style ---
    private void styleTable(JTable tbl) {
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.setRowHeight(28);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return btn;
    }
    
    // --- Các phương thức public để controller tương tác ---
    public void addConfirmButtonListener(ActionListener listener) { btnConfirm.addActionListener(listener); }
    public JTable getAvailableProductsTable() { return availableProductsTable; }
    public JComboBox<String> getCbOldProductStatus() { return cbOldProductStatus; }
    
    public int getIdDT() { 
        return Integer.parseInt(getTitle().replaceAll("[^0-9]", "")); 
    }
    
    public String getMaSPCuTheCu() { 
        String[] parts = lblOldProductInfo.getText().split("\\|");
        return parts[0].trim();
    }
    
    public JLabel getLblNewProductInfo() { return lblNewProductInfo; }
    public JLabel getLblPriceDifference() { return lblPriceDifference; }
}