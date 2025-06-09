package view.customer;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import controller.customer.WarrantyRequest;
public class WarrantyRequestView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtMaSP, txtNgayNhan, txtNgayTra;
    private final int maKH; 
    public WarrantyRequestView(int maKH) { // Constructor nhận int
        this.maKH = maKH;
        setTitle("Yêu Cầu Bảo Hành Sản Phẩm - KH: " + maKH);
        setSize(480, 280); // Điều chỉnh kích thước
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25)); // Tăng padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Cho phép trường text mở rộng

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Loại bỏ Mã Phiếu BH
        // JLabel lblIdBH = new JLabel("Mã phiếu BH:");
        // lblIdBH.setFont(labelFont);
        // txtIdBH = new JTextField();
        // txtIdBH.setFont(textFont);
        // txtIdBH.setToolTipText("Mã phiếu bảo hành sẽ được tạo tự động");
        // txtIdBH.setEditable(false); // Không cho phép sửa nếu tự sinh

        JLabel lblMaSP = new JLabel("Mã sản phẩm*:");
        lblMaSP.setFont(labelFont);
        txtMaSP = new JTextField();
        txtMaSP.setFont(textFont);

        JLabel lblNgayNhan = new JLabel("Ngày nhận SP (bạn giao):");
        lblNgayNhan.setFont(labelFont);
        txtNgayNhan = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)); // Gợi ý ngày hiện tại
        txtNgayNhan.setFont(textFont);
        txtNgayNhan.setToolTipText("Định dạng YYYY-MM-DD");


        // Ngày trả có thể không cần nhập ở form này, mà do nhân viên cập nhật sau
        // Hoặc nếu nhập, nó chỉ là dự kiến
        JLabel lblNgayTra = new JLabel("Ngày trả (dự kiến):");
        lblNgayTra.setFont(labelFont);
        txtNgayTra = new JTextField(LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE)); // Gợi ý 7 ngày sau
        txtNgayTra.setFont(textFont);
        txtNgayTra.setToolTipText("Định dạng YYYY-MM-DD (có thể để trống)");


        JButton btnGuiYeuCau = createStyledButton("Gửi yêu cầu", new Color(33, 150, 243));
        JButton btnTrangChinh = createStyledButton("Trở về", new Color(108, 117, 125));


        // Layout inputs
        // gbc.gridx = 0; gbc.gridy = 0; panel.add(lblIdBH, gbc);
        // gbc.gridx = 1; panel.add(txtIdBH, gbc);

        gbc.gridx = 0; gbc.gridy = 0; // Bắt đầu từ row 0
        panel.add(lblMaSP, gbc);
        gbc.gridx = 1;
        panel.add(txtMaSP, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblNgayNhan, gbc);
        gbc.gridx = 1;
        panel.add(txtNgayNhan, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblNgayTra, gbc);
        gbc.gridx = 1;
        panel.add(txtNgayTra, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5)); // Căn giữa nút
        buttonPanel.add(btnGuiYeuCau);
        buttonPanel.add(btnTrangChinh);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; // gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE; // Không cho fill
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        setContentPane(panel);

        btnGuiYeuCau.addActionListener(e -> {
            // String idBHStr = txtIdBH.getText().trim(); // Loại bỏ
            String maSPStr = txtMaSP.getText().trim();
            String ngayNhanStr = txtNgayNhan.getText().trim();
            String ngayTraStr = txtNgayTra.getText().trim(); // Có thể trống

            if (maSPStr.isEmpty() || ngayNhanStr.isEmpty()) { // Ngày trả có thể không bắt buộc ở bước này
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã sản phẩm và Ngày nhận sản phẩm.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int maSP = Integer.parseInt(maSPStr);
                LocalDate ngayNhan = LocalDate.parse(ngayNhanStr, DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate ngayTra = null; // Mặc định là null
                if (!ngayTraStr.isEmpty()) {
                    ngayTra = LocalDate.parse(ngayTraStr, DateTimeFormatter.ISO_LOCAL_DATE);
                }

                // Giả sử WarrantyRequestController có phương thức createWarrantyRequest
                // và nó xử lý việc tạo PhieuBaoHanh với idBH tự sinh
                WarrantyRequest controller = new WarrantyRequest(); // Hoặc inject nếu dùng DI
                boolean success = controller.createWarrantyRequest(maSP, ngayNhan, ngayTra, this.maKH);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Gửi yêu cầu bảo hành thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new CustomerView(this.maKH).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Gửi yêu cầu thất bại. Vui lòng kiểm tra lại thông tin hoặc xem log lỗi.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mã sản phẩm phải là một số nguyên.", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ. Vui lòng dùng YYYY-MM-DD.", "Lỗi Định Dạng Ngày", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { // Bắt các lỗi khác từ controller
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnTrangChinh.addActionListener(e -> {
            dispose();
            new CustomerView(this.maKH).setVisible(true); // Truyền int maKH
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