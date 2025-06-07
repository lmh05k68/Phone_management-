package view.customer;

import model.HoaDonXuat;
import model.TraGop;
import query.HoaDonXuatQuery;
import query.TraGopQuery;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class TraGopKHView extends JFrame {
	private static final long serialVersionUID = 1L;
    private JTextField txtMaPhieu, txtMaHDX, txtSoThang, txtLaiSuat;
    private JLabel lblTienGoc, lblTienTraHangThang;
    private JButton btnDangKy;
    private String maKH;

    public TraGopKHView(String maKH) {
        this.maKH = maKH;
        setTitle("Đăng ký trả góp");
        setSize(480, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font font = new Font("Segoe UI", Font.PLAIN, 16);

        txtMaPhieu = new JTextField(); txtMaPhieu.setFont(font);
        txtMaHDX = new JTextField(); txtMaHDX.setFont(font);
        txtSoThang = new JTextField(); txtSoThang.setFont(font);
        txtLaiSuat = new JTextField(); txtLaiSuat.setFont(font);
        lblTienGoc = new JLabel("..."); lblTienGoc.setFont(font);
        lblTienTraHangThang = new JLabel("..."); lblTienTraHangThang.setFont(font);
        btnDangKy = createStyledButton("Đăng ký");

        addField(panel, gbc, font, "Mã phiếu trả góp:", txtMaPhieu, 0);
        addField(panel, gbc, font, "Mã hóa đơn xuất:", txtMaHDX, 1);
        addField(panel, gbc, font, "Số tháng trả góp:", txtSoThang, 2);
        addField(panel, gbc, font, "Lãi suất (%):", txtLaiSuat, 3);
        addField(panel, gbc, font, "Tiền gốc:", lblTienGoc, 4);
        addField(panel, gbc, font, "Trả hàng tháng:", lblTienTraHangThang, 5);

        gbc.gridx = 1; gbc.gridy = 6;
        panel.add(btnDangKy, gbc);

        btnDangKy.addActionListener(e -> xuLyDangKy());

        setContentPane(panel);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, Font font, String labelText, JComponent field, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel label = new JLabel(labelText); label.setFont(font);
        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(33, 150, 243));
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(25, 118, 210)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return btn;
    }

    private void xuLyDangKy() {
        try {
            String maPhieu = txtMaPhieu.getText().trim();
            String maHDX = txtMaHDX.getText().trim();
            int soThang = Integer.parseInt(txtSoThang.getText().trim());
            double laiSuat = Double.parseDouble(txtLaiSuat.getText().trim());

            if (maPhieu.isEmpty() || maHDX.isEmpty()) {
                throw new Exception("Vui lòng nhập đầy đủ thông tin.");
            }

            HoaDonXuat hdx = new HoaDonXuatQuery().getHoaDonById(maHDX);
            if (hdx == null) throw new Exception("Không tìm thấy hóa đơn xuất");

            double tienGoc = hdx.getThanhTien();
            double tienTraHangThang = (tienGoc / soThang) * (1 + laiSuat / 100);

            lblTienGoc.setText(String.format("%,.0f VND", tienGoc));
            lblTienTraHangThang.setText(String.format("%,.0f VND", tienTraHangThang));

            TraGop tg = new TraGop(maPhieu, maHDX, soThang, laiSuat, tienGoc, LocalDate.now(), false);
            boolean success = new TraGopQuery().insertPhieuTraGop(tg);

            if (success) {
                JOptionPane.showMessageDialog(this, "Đăng ký trả góp thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Mã phiếu đã tồn tại hoặc lỗi hệ thống!");
            }

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Số tháng và lãi suất phải là số hợp lệ.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}