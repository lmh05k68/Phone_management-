package view.customer;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import controller.customer.WarrantyRequest;

public class WarrantyRequestView extends JFrame {
    private static final long serialVersionUID = 1L;

    public WarrantyRequestView(String maKH) {
        setTitle("Yêu cầu bảo hành sản phẩm");
        setSize(460, 330);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblIdBH = new JLabel("Mã phiếu BH:");
        JTextField txtIdBH = new JTextField();

        JLabel lblMaSP = new JLabel("Mã sản phẩm:");
        JTextField txtMaSP = new JTextField();

        JLabel lblNgayNhan = new JLabel("Ngày nhận:");
        JTextField txtNgayNhan = new JTextField(LocalDate.now().toString());

        JLabel lblNgayTra = new JLabel("Ngày trả (dự kiến):");
        JTextField txtNgayTra = new JTextField(LocalDate.now().plusDays(7).toString());

        JButton btnGuiYeuCau = new JButton("Gửi yêu cầu");
        JButton btnTrangChinh = new JButton("Trở về");

        // Font & Style
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        lblIdBH.setFont(font);
        lblMaSP.setFont(font);
        lblNgayNhan.setFont(font);
        lblNgayTra.setFont(font);
        txtIdBH.setFont(font);
        txtMaSP.setFont(font);
        txtNgayNhan.setFont(font);
        txtNgayTra.setFont(font);
        btnGuiYeuCau.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTrangChinh.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnGuiYeuCau.setBackground(new Color(33, 150, 243));
        btnGuiYeuCau.setForeground(Color.WHITE);
        btnTrangChinh.setBackground(new Color(255, 152, 0));
        btnTrangChinh.setForeground(Color.WHITE);

        // Layout inputs
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblIdBH, gbc);
        gbc.gridx = 1;
        panel.add(txtIdBH, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblMaSP, gbc);
        gbc.gridx = 1;
        panel.add(txtMaSP, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblNgayNhan, gbc);
        gbc.gridx = 1;
        panel.add(txtNgayNhan, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(lblNgayTra, gbc);
        gbc.gridx = 1;
        panel.add(txtNgayTra, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        buttonPanel.add(btnGuiYeuCau);
        buttonPanel.add(btnTrangChinh);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);

        setContentPane(panel);
        btnGuiYeuCau.addActionListener(e -> {
            String idBH = txtIdBH.getText().trim();
            String maSP = txtMaSP.getText().trim();
            String ngayNhan = txtNgayNhan.getText().trim();
            String ngayTra = txtNgayTra.getText().trim();

            if (idBH.isEmpty() || maSP.isEmpty() || ngayNhan.isEmpty() || ngayTra.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            try {
                LocalDate.parse(ngayNhan);
                LocalDate.parse(ngayTra);

                boolean success = WarrantyRequest.createWarrantyRequest(idBH, maSP, ngayNhan, ngayTra, maKH);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Gửi yêu cầu bảo hành thành công!");
                    dispose();
                    new CustomerView(maKH).setVisible(true); 
                } else {
                    JOptionPane.showMessageDialog(this, "Gửi yêu cầu thất bại. Mã phiếu có thể đã tồn tại.");
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ. Vui lòng dùng yyyy-MM-dd.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        btnTrangChinh.addActionListener(e -> {
            dispose();
            new CustomerView(maKH).setVisible(true);
        });
    }
}