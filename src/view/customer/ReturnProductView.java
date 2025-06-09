package view.customer; 
import javax.swing.*;
import java.awt.*;
public class ReturnProductView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int maKH; 
    private JTextField txtMaSP, txtMaDonHang, txtLyDo;
    // private JTextField txtMaDoiTra; // Loại bỏ nếu MaDoiTra (idDT) tự sinh
    private JButton btnGuiYeuCau, btnTroVe;

    // Constructor nhận int maKH
    public ReturnProductView(int maKH) { // <<<< SỬA CONSTRUCTOR
        this.maKH = maKH;
        setTitle("Yêu Cầu Đổi/Trả Sản Phẩm - KH: " + maKH);
        // Đặt kích thước, layout, các thành phần UI khác ở đây
        // Ví dụ:
        setSize(500, 350); // Kích thước ví dụ
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI(); // Gọi hàm khởi tạo UI
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 14);
        JLabel lblMaSP = new JLabel("Mã Sản Phẩm*:");
        lblMaSP.setFont(labelFont);
        txtMaSP = new JTextField();
        txtMaSP.setFont(textFont);

        JLabel lblMaDonHang = new JLabel("Mã Đơn Hàng*:");
        lblMaDonHang.setFont(labelFont);
        txtMaDonHang = new JTextField();
        txtMaDonHang.setFont(textFont);

        JLabel lblLyDo = new JLabel("Lý do đổi/trả*:");
        lblLyDo.setFont(labelFont);
        txtLyDo = new JTextField(); // Hoặc JTextArea nếu lý do dài
        txtLyDo.setFont(textFont);


        btnGuiYeuCau = new JButton("Gửi Yêu Cầu");
        styleButton(btnGuiYeuCau, new Color(33, 150, 243));

        btnTroVe = new JButton("Trở Về");
        styleButton(btnTroVe, new Color(108, 117, 125));


        // Layout
        // gbc.gridx = 0; gbc.gridy = 0; panel.add(lblMaDoiTra, gbc);
        // gbc.gridx = 1; panel.add(txtMaDoiTra, gbc);

        gbc.gridx = 0; gbc.gridy = 0; // Bắt đầu từ row 0
        panel.add(lblMaSP, gbc);
        gbc.gridx = 1;
        panel.add(txtMaSP, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblMaDonHang, gbc);
        gbc.gridx = 1;
        panel.add(txtMaDonHang, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblLyDo, gbc);
        gbc.gridx = 1;
        panel.add(txtLyDo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnGuiYeuCau);
        buttonPanel.add(btnTroVe);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        setContentPane(panel);

        // Action listener cho nút Trở về
        btnTroVe.addActionListener(e -> {
            dispose();
            new CustomerView(this.maKH).setVisible(true); // Mở lại CustomerView
        });

        // Nút Gửi Yêu Cầu sẽ được controller gắn listener
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
    }

    // Getters cho controller lấy thông tin từ form
    // public String getMaDoiTra() { return txtMaDoiTra.getText(); } // Loại bỏ
    public String getMaSP() { return txtMaSP.getText(); }
    public String getMaDonHang() { return txtMaDonHang.getText(); }
    public String getLyDo() { return txtLyDo.getText(); }
    public JButton getBtnGuiYeuCau() { return btnGuiYeuCau; }
    public int getMaKHFromView() { return this.maKH;} // Cung cấp MaKH cho controller nếu cần
}