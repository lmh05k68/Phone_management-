package view.customer;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import controller.customer.WarrantyRequest; 
public class WarrantyRequestView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtMaSP, txtMaHDX, txtNgayNhan, txtNgayTra; // Thêm txtMaHDX
    private final int maKH;

    public WarrantyRequestView(int maKH) {
        this.maKH = maKH;
        setTitle("Yêu Cầu Bảo Hành Sản Phẩm - KH: " + maKH);
        setSize(480, 350); // Tăng chiều cao để chứa thêm trường
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 14);

        int currentRow = 0;

        // Mã sản phẩm
        JLabel lblMaSP = new JLabel("Mã sản phẩm*:");
        lblMaSP.setFont(labelFont);
        txtMaSP = new JTextField();
        txtMaSP.setFont(textFont);
        gbc.gridx = 0; gbc.gridy = currentRow; panel.add(lblMaSP, gbc);
        gbc.gridx = 1; panel.add(txtMaSP, gbc);
        currentRow++;

        // Mã hóa đơn xuất (MỚI)
        JLabel lblMaHDX = new JLabel("Mã hóa đơn xuất (nếu có):");
        lblMaHDX.setFont(labelFont);
        txtMaHDX = new JTextField(); // Khởi tạo txtMaHDX
        txtMaHDX.setFont(textFont);
        txtMaHDX.setToolTipText("Nhập mã hóa đơn mua hàng (nếu có)");
        gbc.gridx = 0; gbc.gridy = currentRow; panel.add(lblMaHDX, gbc);
        gbc.gridx = 1; panel.add(txtMaHDX, gbc);
        currentRow++;

        // Ngày nhận
        JLabel lblNgayNhan = new JLabel("Ngày nhận SP (bạn giao)*:");
        lblNgayNhan.setFont(labelFont);
        txtNgayNhan = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        txtNgayNhan.setFont(textFont);
        txtNgayNhan.setToolTipText("Định dạng YYYY-MM-DD");
        gbc.gridx = 0; gbc.gridy = currentRow; panel.add(lblNgayNhan, gbc);
        gbc.gridx = 1; panel.add(txtNgayNhan, gbc);
        currentRow++;

        // Ngày trả
        JLabel lblNgayTra = new JLabel("Ngày trả (dự kiến):");
        lblNgayTra.setFont(labelFont);
        txtNgayTra = new JTextField(LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE));
        txtNgayTra.setFont(textFont);
        txtNgayTra.setToolTipText("Định dạng YYYY-MM-DD (có thể để trống)");
        gbc.gridx = 0; gbc.gridy = currentRow; panel.add(lblNgayTra, gbc);
        gbc.gridx = 1; panel.add(txtNgayTra, gbc);
        currentRow++;


        JButton btnGuiYeuCau = createStyledButton("Gửi yêu cầu", new Color(33, 150, 243));
        JButton btnTrangChinh = createStyledButton("Trở về", new Color(108, 117, 125));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.add(btnGuiYeuCau);
        buttonPanel.add(btnTrangChinh);

        gbc.gridx = 0; gbc.gridy = currentRow; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        setContentPane(panel);

        btnGuiYeuCau.addActionListener(e -> {
            String maSPStr = txtMaSP.getText().trim();
            String maHDXStr = txtMaHDX.getText().trim(); // Lấy giá trị từ txtMaHDX
            String ngayNhanStr = txtNgayNhan.getText().trim();
            String ngayTraStr = txtNgayTra.getText().trim();

            if (maSPStr.isEmpty() || ngayNhanStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã sản phẩm và Ngày nhận sản phẩm.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int maSP = Integer.parseInt(maSPStr);
                Integer maHDX = null; // Mặc định là null nếu không nhập
                if (!maHDXStr.isEmpty()) {
                    try {
                        maHDX = Integer.parseInt(maHDXStr);
                        if (maHDX <= 0) {
                            JOptionPane.showMessageDialog(this, "Mã hóa đơn xuất phải là một số nguyên dương (nếu nhập).", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Mã hóa đơn xuất phải là một số nguyên (nếu nhập).", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                LocalDate ngayNhan = LocalDate.parse(ngayNhanStr, DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate ngayTra = null;
                if (!ngayTraStr.isEmpty()) {
                    ngayTra = LocalDate.parse(ngayTraStr, DateTimeFormatter.ISO_LOCAL_DATE);
                }

                WarrantyRequest controller = new WarrantyRequest();
                // SỬA LỖI: Truyền maHDX vào phương thức createWarrantyRequest
                boolean success = controller.createWarrantyRequest(maSP, maHDX, ngayNhan, ngayTra, this.maKH);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Gửi yêu cầu bảo hành thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    // Nếu bạn có CustomerView và muốn mở nó:
                    // new CustomerView(this.maKH).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Gửi yêu cầu thất bại. Vui lòng kiểm tra lại thông tin hoặc xem log lỗi.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) { // Dành cho lỗi parse MaSP
                JOptionPane.showMessageDialog(this, "Mã sản phẩm phải là một số nguyên.", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ. Vui lòng dùng YYYY-MM-DD.", "Lỗi Định Dạng Ngày", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnTrangChinh.addActionListener(e -> {
            dispose();
        });
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
}