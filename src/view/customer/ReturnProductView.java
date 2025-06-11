package view.customer;

import javax.swing.*;
import java.awt.*;

public class ReturnProductView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int maKH;
    private JTextField txtMaSPCuThe; 
    private JTextField txtMaDonHang;
    private JTextField txtLyDo;
    private JButton btnGuiYeuCau, btnTroVe;

    public ReturnProductView(int maKH) {
        this.maKH = maKH;
        setTitle("Yêu Cầu Đổi/Trả Sản Phẩm - KH: " + maKH);
        setSize(500, 300); // Điều chỉnh lại kích thước cho phù hợp
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); // Tăng khoảng cách dọc
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 14);

        // SỬA: Đổi tên biến và nội dung Label để chính xác về mặt logic
        JLabel lblMaSPCuThe = new JLabel("Mã SP Cụ Thể*:");
        lblMaSPCuThe.setFont(labelFont);
        txtMaSPCuThe = new JTextField();
        txtMaSPCuThe.setFont(textFont);
        txtMaSPCuThe.setToolTipText("Nhập mã seri hoặc mã định danh duy nhất của sản phẩm trên hóa đơn.");

        JLabel lblMaDonHang = new JLabel("Mã Đơn Hàng*:");
        lblMaDonHang.setFont(labelFont);
        txtMaDonHang = new JTextField();
        txtMaDonHang.setFont(textFont);

        JLabel lblLyDo = new JLabel("Lý do đổi/trả*:");
        lblLyDo.setFont(labelFont);
        txtLyDo = new JTextField();
        txtLyDo.setFont(textFont);

        btnGuiYeuCau = new JButton("Gửi Yêu Cầu");
        styleButton(btnGuiYeuCau, new Color(40, 167, 69)); // Màu xanh lá cho hành động chính

        btnTroVe = new JButton("Trở Về");
        styleButton(btnTroVe, new Color(108, 117, 125)); // Màu xám cho hành động phụ

        // --- Sắp xếp các thành phần ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; // Label chiếm ít không gian hơn
        panel.add(lblMaSPCuThe, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; // Field chiếm nhiều hơn
        panel.add(txtMaSPCuThe, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblMaDonHang, gbc);
        gbc.gridx = 1;
        panel.add(txtMaDonHang, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblLyDo, gbc);
        gbc.gridx = 1;
        panel.add(txtLyDo, gbc);

        // Panel cho các nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(btnGuiYeuCau);
        buttonPanel.add(btnTroVe);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5); // Tăng khoảng cách phía trên nút
        panel.add(buttonPanel, gbc);

        setContentPane(panel);
        btnTroVe.addActionListener(e -> dispose());
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }
    public String getMaSPCuThe() { return txtMaSPCuThe.getText(); }
    public String getMaDonHang() { return txtMaDonHang.getText(); }
    public String getLyDo() { return txtLyDo.getText(); }
    public JButton getBtnGuiYeuCau() { return btnGuiYeuCau; }
    public int getMaKHFromView() { return this.maKH; }
}