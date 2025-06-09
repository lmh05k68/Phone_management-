package view.customer;

import model.HoaDonXuat;
import model.TraGop;
import query.HoaDonXuatQuery; // Gọi static
import query.TraGopQuery;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
public class TraGopKHView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtMaHDX, txtSoThang, txtLaiSuat;
    private JLabel lblTienGoc, lblTienTraHangThang;
    private JButton btnDangKy, btnBack; // Thêm btnBack
    private final int maKH; 
    public TraGopKHView(int maKH) { // Constructor nhận int
        this.maKH = maKH;
        setTitle("Đăng Ký Trả Góp - KH: " + maKH);
        setSize(500, 420); // Điều chỉnh kích thước
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
        gbc.insets = new Insets(8, 8, 8, 8); // Giảm insets một chút
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Cho phép các trường mở rộng

        Font font = new Font("Segoe UI", Font.PLAIN, 15); // Font nhỏ hơn chút
        Font labelFont = new Font("Segoe UI", Font.BOLD, 15);

        // txtMaPhieu = new JTextField(); txtMaPhieu.setFont(font); // Loại bỏ
        txtMaHDX = new JTextField(); txtMaHDX.setFont(font);
        txtSoThang = new JTextField(); txtSoThang.setFont(font);
        txtLaiSuat = new JTextField(); txtLaiSuat.setFont(font);
        lblTienGoc = new JLabel("0 VND"); lblTienGoc.setFont(font); lblTienGoc.setForeground(Color.BLUE);
        lblTienTraHangThang = new JLabel("0 VND"); lblTienTraHangThang.setFont(font); lblTienTraHangThang.setForeground(Color.RED);
        btnDangKy = createStyledButton("Đăng ký");
        btnBack = createStyledButton("Trở về"); // Tạo nút trở về
        btnBack.setBackground(new Color(108, 117, 125)); // Màu xám cho nút trở về


        // addField(panel, gbc, labelFont, "Mã phiếu trả góp:", txtMaPhieu, 0); // Loại bỏ
        addField(panel, gbc, labelFont, "Mã hóa đơn xuất*:", txtMaHDX, 0); // Bắt đầu từ row 0
        addField(panel, gbc, labelFont, "Số tháng trả góp*:", txtSoThang, 1);
        addField(panel, gbc, labelFont, "Lãi suất (%/tháng)*:", txtLaiSuat, 2);
        addField(panel, gbc, labelFont, "Tiền gốc:", lblTienGoc, 3);
        addField(panel, gbc, labelFont, "Trả hàng tháng (dự kiến):", lblTienTraHangThang, 4);

        // Panel cho các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE); // Đồng bộ màu nền
        buttonPanel.add(btnDangKy);
        buttonPanel.add(btnBack);

        gbc.gridx = 0; gbc.gridy = 5; // Đặt lại vị trí cho buttonPanel
        gbc.gridwidth = 2; // Cho buttonPanel chiếm 2 cột
        gbc.anchor = GridBagConstraints.CENTER; // Căn giữa
        gbc.fill = GridBagConstraints.NONE; // Không cho fill ngang nữa
        panel.add(buttonPanel, gbc);

        // Listener để tính toán khi MaHDX, SoThang, LaiSuat thay đổi
        txtMaHDX.getDocument().addUndoableEditListener(e -> tinhToanTienTraGop());
        txtSoThang.getDocument().addUndoableEditListener(e -> tinhToanTienTraGop());
        txtLaiSuat.getDocument().addUndoableEditListener(e -> tinhToanTienTraGop());

        btnDangKy.addActionListener(e -> xuLyDangKy());
        btnBack.addActionListener(e -> {
            dispose();
            // Giả sử CustomerView đã được cập nhật
            new CustomerView(this.maKH).setVisible(true);
        });


        setContentPane(panel);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, Font font, String labelText, JComponent field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel label = new JLabel(labelText); label.setFont(font);
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
        if (field instanceof JTextField) {
            ((JTextField) field).setColumns(15); // Đặt kích thước cột cho JTextField
        }
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

    private void tinhToanTienTraGop() {
        try {
            String maHDXStr = txtMaHDX.getText().trim();
            String soThangStr = txtSoThang.getText().trim();
            String laiSuatStr = txtLaiSuat.getText().trim();

            if (maHDXStr.isEmpty() || soThangStr.isEmpty() || laiSuatStr.isEmpty()) {
                lblTienGoc.setText("0 VND");
                lblTienTraHangThang.setText("0 VND");
                return;
            }

            int maHDX = Integer.parseInt(maHDXStr);
            int soThang = Integer.parseInt(soThangStr);
            double laiSuat = Double.parseDouble(laiSuatStr);

            if (soThang <=0 || laiSuat < 0) {
                 lblTienGoc.setText("0 VND");
                 lblTienTraHangThang.setText("0 VND");
                return;
            }

            HoaDonXuat hdx = HoaDonXuatQuery.getHoaDonById(maHDX); // Gọi static
            if (hdx == null || (hdx.getMaKH() != null && hdx.getMaKH() != this.maKH) ) {
                // Kiểm tra thêm MaKH của hóa đơn có khớp với MaKH đang đăng ký không
                lblTienGoc.setText("HĐ không hợp lệ");
                lblTienTraHangThang.setText("0 VND");
                if (hdx != null && hdx.getMaKH() != null && hdx.getMaKH() != this.maKH) {
                     JOptionPane.showMessageDialog(this, "Mã hóa đơn này không thuộc về bạn!", "Lỗi Hóa Đơn", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }

            double tienGoc = hdx.getThanhTien();
            // Tính lãi đơn giản: (Tiền gốc * Lãi suất hàng tháng) + (Tiền gốc / Số tháng)
            // Hoặc có thể dùng công thức niên kim nếu phức tạp hơn
            double tienLaiHangThang = tienGoc * (laiSuat / 100.0);
            double tienGocHangThang = tienGoc / soThang;
            double tienTraHangThang = tienGocHangThang + tienLaiHangThang;


            lblTienGoc.setText(String.format("%,.0f VND", tienGoc));
            lblTienTraHangThang.setText(String.format("%,.0f VND", tienTraHangThang));

        } catch (NumberFormatException nfe) {
            lblTienGoc.setText("Lỗi định dạng");
            lblTienTraHangThang.setText("0 VND");
        } catch (Exception ex) { // Bắt các lỗi khác nếu có từ query
            lblTienGoc.setText("Lỗi tải HĐ");
            lblTienTraHangThang.setText("0 VND");
            // JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void xuLyDangKy() {
        try {
            // String maPhieu = txtMaPhieu.getText().trim(); // Loại bỏ
            String maHDXStr = txtMaHDX.getText().trim();
            String soThangStr = txtSoThang.getText().trim();
            String laiSuatStr = txtLaiSuat.getText().trim();

            if (maHDXStr.isEmpty() || soThangStr.isEmpty() || laiSuatStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã HĐX, Số tháng và Lãi suất.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int maHDX = Integer.parseInt(maHDXStr);
            int soThang = Integer.parseInt(soThangStr);
            double laiSuat = Double.parseDouble(laiSuatStr);

            if (soThang <= 0) {
                JOptionPane.showMessageDialog(this, "Số tháng trả góp phải lớn hơn 0.", "Số tháng không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (laiSuat < 0) {
                 JOptionPane.showMessageDialog(this, "Lãi suất không được âm.", "Lãi suất không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }


            HoaDonXuat hdx = HoaDonXuatQuery.getHoaDonById(maHDX); // Gọi static
            if (hdx == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn xuất với mã: " + maHDX, "Lỗi Hóa Đơn", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Kiểm tra xem hóa đơn có thuộc khách hàng này không (nếu hóa đơn có MaKH)
            if (hdx.getMaKH() != null && hdx.getMaKH() != this.maKH) {
                 JOptionPane.showMessageDialog(this, "Mã hóa đơn này không thuộc về bạn. Không thể đăng ký trả góp.", "Lỗi Hóa Đơn", JOptionPane.ERROR_MESSAGE);
                return;
            }


            double tienGoc = hdx.getThanhTien(); // Tiền gốc là tổng thành tiền của hóa đơn

            // Tạo đối tượng TraGop không cần MaPhieuTG ban đầu
            TraGop tg = new TraGop(maHDX, soThang, laiSuat, tienGoc, LocalDate.now(), false);

            // Gọi phương thức static từ TraGopQuery
            // Giả sử insertPhieuTraGopAndGetId trả về Integer là maPhieuTG mới, hoặc chỉ insert nếu không cần lấy ID
            Integer maPhieuTGGenerated = TraGopQuery.insertPhieuTraGopAndGetId(tg);

            if (maPhieuTGGenerated != null && maPhieuTGGenerated > 0) {
                JOptionPane.showMessageDialog(this, "Đăng ký trả góp thành công! Mã phiếu trả góp của bạn là: " + maPhieuTGGenerated, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                // Reset form hoặc đóng cửa sổ
                tinhToanTienTraGop(); // Cập nhật lại hiển thị tiền
                // Có thể clear các trường sau khi đăng ký thành công
                // txtMaHDX.setText("");
                // txtSoThang.setText("");
                // txtLaiSuat.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký trả góp thất bại! Có thể do lỗi hệ thống hoặc thông tin không hợp lệ.", "Đăng Ký Thất Bại", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn, số tháng và lãi suất phải là số hợp lệ.", "Lỗi Định Dạng Số", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) { // Bắt các lỗi khác, ví dụ từ query
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý đăng ký: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Main method để test (tùy chọn)
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         // Cần một maKH (int) hợp lệ để test
    //         TraGopKHView view = new TraGopKHView(1); // Ví dụ MaKH = 1
    //         view.setVisible(true);
    //     });
    // }
}