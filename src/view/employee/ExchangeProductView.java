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

    // <<< CONSTRUCTOR MỚI >>>
    public ExchangeProductView(int idDT, String maSPCuThe_Cu, BigDecimal oldProductPrice) {
        super((Frame) null, "Thực hiện Đổi/Trả - YC #" + idDT, true);
        setSize(900, 650);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel oldProductPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        oldProductPanel.setBorder(new TitledBorder("Sản phẩm khách trả"));
        
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
        String oldPriceFormatted = currencyFormatter.format(oldProductPrice);
        lblOldProductInfo = new JLabel("Mã SP: " + maSPCuThe_Cu + "  |  Giá trị: " + oldPriceFormatted);
        lblOldProductInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        oldProductPanel.add(lblOldProductInfo);
        oldProductPanel.add(new JLabel("   |   Xử lý:"));
        String[] statuses = {"Nhập lại kho", "Hàng lỗi", "Hủy bỏ"};
        cbOldProductStatus = new JComboBox<>(statuses);
        oldProductPanel.add(cbOldProductStatus);
        
        JPanel newProductPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        newProductPanel.setBorder(new TitledBorder("Sản phẩm mới (chọn từ bảng dưới)"));
        lblNewProductInfo = new JLabel("Chưa chọn sản phẩm mới.");
        lblNewProductInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        newProductPanel.add(lblNewProductInfo);
        
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pricePanel.setBorder(new TitledBorder("Chênh lệch chi phí"));
        lblPriceDifference = new JLabel("Vui lòng chọn sản phẩm mới để tính chênh lệch.");
        lblPriceDifference.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pricePanel.add(lblPriceDifference);

        topPanel.add(oldProductPanel);
        topPanel.add(newProductPanel);
        topPanel.add(pricePanel);
        add(topPanel, BorderLayout.NORTH);

        availableProductsTable = new JTable();
        availableProductsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(availableProductsTable);
        scrollPane.setBorder(new TitledBorder("Danh sách sản phẩm có thể đổi"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnConfirm = new JButton("Xác nhận Hoàn tất");
        btnCancel = new JButton("Hủy");
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnConfirm);
        add(buttonPanel, BorderLayout.SOUTH);
        
        btnCancel.addActionListener(e -> dispose());
    }
    
    public void addConfirmButtonListener(ActionListener listener) { btnConfirm.addActionListener(listener); }
    public JTable getAvailableProductsTable() { return availableProductsTable; }
    public JComboBox<String> getCbOldProductStatus() { return cbOldProductStatus; }
    public int getIdDT() { return Integer.parseInt(getTitle().replaceAll("[^0-9]", "")); }
    public String getMaSPCuTheCu() { 
        String text = lblOldProductInfo.getText().split("\\|")[0]; // Lấy phần "Mã SP: IMEI123  "
        return text.replace("Mã SP:", "").trim();
    }
    
    public JLabel getLblNewProductInfo() { return lblNewProductInfo; }
    public JLabel getLblPriceDifference() { return lblPriceDifference; }
}