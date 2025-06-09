package view.customer;

import query.KhachHangQuery; // Gọi phương thức static
import javax.swing.*;
import java.awt.*;
public class RedeemPointsView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel lblWelcome, lblCurrentPoints, lblDiscountResult;
    private JButton btnCheckDiscount, btnBack;
    private final int maKH; 
    public RedeemPointsView(int maKH) { // <<<< THAY ĐỔI: Constructor nhận int
        this.maKH = maKH;
        setTitle("Đổi điểm thưởng - KH: " + maKH); // Hiển thị MaKH (int)
        setSize(450, 320); // Điều chỉnh kích thước nếu cần
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblWelcome = new JLabel("Đổi Điểm Lấy Ưu Đãi", SwingConstants.CENTER); // Sửa tiêu đề
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 20));
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));

        lblCurrentPoints = new JLabel("", SwingConstants.CENTER);
        lblCurrentPoints.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCurrentPoints.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCurrentPoints.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        btnCheckDiscount = new JButton("Xem mức giảm giá có thể nhận");
        styleButton(btnCheckDiscount, new Color(33, 150, 243));
        btnCheckDiscount.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblDiscountResult = new JLabel("", SwingConstants.CENTER);
        lblDiscountResult.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDiscountResult.setForeground(new Color(40, 167, 69));
        lblDiscountResult.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDiscountResult.setBorder(BorderFactory.createEmptyBorder(10,0,15,0));

        btnBack = new JButton("Trở về");
        styleButton(btnBack, new Color(108, 117, 125));
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(lblWelcome);
        mainPanel.add(lblCurrentPoints);
        mainPanel.add(Box.createVerticalStrut(5)); // Thêm khoảng cách nhỏ
        mainPanel.add(btnCheckDiscount);
        mainPanel.add(Box.createVerticalStrut(5)); // Thêm khoảng cách nhỏ
        mainPanel.add(lblDiscountResult);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(btnBack);

        setContentPane(mainPanel);

        loadDiemHienTaiCuaKhachHang();

        btnCheckDiscount.addActionListener(e -> showDiscountInfo());
        btnBack.addActionListener(e -> {
            dispose();
            // Giả sử CustomerView đã được cập nhật để nhận int maKH
            new CustomerView(this.maKH).setVisible(true); // Truyền int maKH
        });
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
        button.setPreferredSize(new Dimension(280, 40)); // Tăng chiều rộng nút
        button.setMaximumSize(new Dimension(300, 40));  // Tăng chiều rộng tối đa
    }

    private void loadDiemHienTaiCuaKhachHang() {
        System.out.println("REDEEM_VIEW: Đang tải điểm cho MaKH: " + maKH); // maKH giờ là int
        try {
            // Gọi phương thức static từ KhachHangQuery, truyền int maKH
            int tongDiem = KhachHangQuery.getSoDiemTichLuy(this.maKH);
            if (tongDiem == -1) { // -1 là giá trị trả về khi có lỗi hoặc không tìm thấy trong KhachHangQuery
                 lblCurrentPoints.setText("Lỗi tải điểm hoặc KH không tồn tại.");
                 btnCheckDiscount.setEnabled(false); // Vô hiệu hóa nút nếu không có điểm
            } else {
                 lblCurrentPoints.setText("Điểm tích lũy hiện có: " + tongDiem + " điểm");
                 btnCheckDiscount.setEnabled(tongDiem > 0); // Chỉ bật nút nếu có điểm
            }
        } catch (Exception e) { // Bắt lỗi rộng hơn nếu có
            lblCurrentPoints.setText("Không thể tải điểm thưởng.");
            btnCheckDiscount.setEnabled(false);
            e.printStackTrace();
        }
    }

    private void showDiscountInfo() {
        System.out.println("REDEEM_VIEW: Kiểm tra mức giảm giá cho MaKH: " + maKH); // maKH giờ là int
        try {
            // Gọi phương thức static từ KhachHangQuery, truyền int maKH
            int phanTramGiam = KhachHangQuery.tinhPhanTramGiamTuDiem(this.maKH);
            int tongDiem = KhachHangQuery.getSoDiemTichLuy(this.maKH); // Lấy lại điểm để hiển thị

            if (phanTramGiam <= 0) {
                lblDiscountResult.setText("Bạn chưa có đủ điểm để đổi ưu đãi (cần ít nhất 100 điểm).");
            } else {
                lblDiscountResult.setText(String.format("Với %d điểm, bạn có thể được giảm %d%% cho đơn hàng kế tiếp!", tongDiem, phanTramGiam));
            }
        } catch (Exception e) { // Bắt lỗi rộng hơn
            lblDiscountResult.setText("Đã xảy ra lỗi khi tính mức giảm giá.");
            e.printStackTrace();
        }
    }

    // Main method để test (tùy chọn)
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         // Cần một maKH (int) hợp lệ để test
    //         RedeemPointsView view = new RedeemPointsView(1); // Ví dụ MaKH = 1
    //         view.setVisible(true);
    //     });
    // }
}