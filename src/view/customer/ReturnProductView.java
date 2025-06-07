package view.customer;

import javax.swing.*;
import java.awt.*;

public class ReturnProductView extends JFrame {
    private static final long serialVersionUID = 1L;

    // Thêm JTextField cho Mã Đổi Trả
    private JTextField txtMaDoiTra, txtMaKH, txtMaSP, txtMaDonHang;
    private JTextArea txtLyDo;
    private JButton btnGuiYeuCau, btnBack;
    private String customerId;

    public ReturnProductView(String maKH) {
        this.customerId = maKH;

        setTitle("Yêu cầu đổi/trả sản phẩm");
        setSize(450, 430); // Tăng chiều cao một chút cho trường mới
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Mã Đổi Trả (Mới)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Mã đổi trả:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtMaDoiTra = new JTextField(15);
        txtMaDoiTra.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(txtMaDoiTra, gbc);


        // Mã Khách Hàng
        gbc.gridx = 0;
        gbc.gridy = 1; // Tăng gridy
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Mã khách hàng:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtMaKH = new JTextField(customerId, 15);
        txtMaKH.setEditable(false);
        txtMaKH.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(txtMaKH, gbc);

        // Mã Sản Phẩm
        gbc.gridx = 0;
        gbc.gridy = 2; // Tăng gridy
        gbc.weightx = 0.0;
        panel.add(new JLabel("Mã sản phẩm:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtMaSP = new JTextField(15);
        txtMaSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(txtMaSP, gbc);

        // Mã Đơn Hàng
        gbc.gridx = 0;
        gbc.gridy = 3; // Tăng gridy
        gbc.weightx = 0.0;
        panel.add(new JLabel("Mã đơn hàng:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtMaDonHang = new JTextField(15);
        txtMaDonHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(txtMaDonHang, gbc);

        // Lý do đổi/trả
        gbc.gridx = 0;
        gbc.gridy = 4; // Tăng gridy
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Lý do đổi/trả:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtLyDo = new JTextArea(4, 15);
        txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        JScrollPane scrollPaneLyDo = new JScrollPane(txtLyDo);
        scrollPaneLyDo.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPaneLyDo, gbc);

        // Buttons
        btnGuiYeuCau = new JButton("Gửi yêu cầu");
        btnBack = new JButton("Trở về");
        styleButton(btnGuiYeuCau, new Color(33, 150, 243));
        styleButton(btnBack, new Color(108, 117, 125));


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(btnGuiYeuCau);
        buttonPanel.add(btnBack);

        gbc.gridx = 0;
        gbc.gridy = 5; // Tăng gridy
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        setContentPane(panel);

        btnBack.addActionListener(e -> dispose());
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Getter cho Mã Đổi Trả
    public String getMaDoiTra() {
        return txtMaDoiTra.getText();
    }

    public String getMaKH() {
        return txtMaKH.getText();
    }

    public String getMaSP() {
        return txtMaSP.getText();
    }

    public String getMaDonHang() {
        return txtMaDonHang.getText();
    }

    public String getLyDo() {
        return txtLyDo.getText();
    }

    public JButton getBtnGuiYeuCau() {
        return btnGuiYeuCau;
    }

    public JButton getBtnBack() {
        return btnBack;
    }
}