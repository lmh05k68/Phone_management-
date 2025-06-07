package view.customer;

// import query.DiemThuongQuery; // Sẽ dùng KhachHangQuery để lấy điểm tổng kết
import query.KhachHangQuery; // << THAY ĐỔI
import javax.swing.*;
import java.awt.*;

public class RedeemPointsView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel lblWelcome, lblCurrentPoints, lblDiscountResult;
    private JButton btnCheckDiscount, btnBack;
    private String maKH;
    private KhachHangQuery khachHangQuery; // << THÊM

    public RedeemPointsView(String maKH) {
        this.maKH = maKH;
        this.khachHangQuery = new KhachHangQuery(); // << KHỞI TẠO
        setTitle("Đổi điểm thưởng");
        setSize(450, 300); // Tăng chiều cao một chút
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Sử dụng JPanel với BoxLayout để căn chỉnh tốt hơn
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        lblWelcome = new JLabel("Đổi điểm lấy ưu đãi", SwingConstants.CENTER);
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
        lblDiscountResult.setFont(new Font("Segoe UI", Font.BOLD, 16)); // In đậm kết quả
        lblDiscountResult.setForeground(new Color(40, 167, 69)); // Màu xanh cho kết quả
        lblDiscountResult.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDiscountResult.setBorder(BorderFactory.createEmptyBorder(10,0,15,0));


        btnBack = new JButton("Trở về");
        styleButton(btnBack, new Color(108, 117, 125)); // Màu xám
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(lblWelcome);
        mainPanel.add(lblCurrentPoints);
        mainPanel.add(btnCheckDiscount);
        mainPanel.add(lblDiscountResult);
        mainPanel.add(Box.createVerticalStrut(10)); // Khoảng cách
        mainPanel.add(btnBack);
        
        setContentPane(mainPanel);

        loadDiemHienTaiCuaKhachHang();

        btnCheckDiscount.addActionListener(e -> showDiscountInfo());
        btnBack.addActionListener(e -> {
            dispose();
            // Giả sử CustomerView tồn tại và có constructor (String maKH)
            // new CustomerView(maKH).setVisible(true);
        });

        // setVisible(true); // Nên gọi setVisible từ nơi gọi View này
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
        // Đặt kích thước cố định hoặc min/max để các nút trông đều hơn nếu cần
        button.setPreferredSize(new Dimension(250, 40));
        button.setMaximumSize(new Dimension(280, 40));
    }


    private void loadDiemHienTaiCuaKhachHang() {
        System.out.println("REDEEM_VIEW: Đang tải điểm cho MaKH: " + maKH);
        try {
            int tongDiem = khachHangQuery.getSoDiemTichLuy(maKH); // << SỬ DỤNG PHƯƠNG THỨC MỚI
            if (tongDiem == -1) { // Lỗi hoặc không tìm thấy KH
                 lblCurrentPoints.setText("Lỗi tải điểm hoặc KH không tồn tại.");
            } else {
                 lblCurrentPoints.setText("Điểm tích lũy hiện có: " + tongDiem + " điểm");
            }
        } catch (Exception e) {
            lblCurrentPoints.setText("Không thể tải điểm thưởng.");
            e.printStackTrace();
        }
    }

    private void showDiscountInfo() {
        System.out.println("REDEEM_VIEW: Kiểm tra mức giảm giá cho MaKH: " + maKH);
        try {
            int tongDiem = khachHangQuery.getSoDiemTichLuy(maKH); // << SỬ DỤNG PHƯƠNG THỨC MỚI
            if (tongDiem == -1) {
                 lblDiscountResult.setText("Lỗi tải điểm hoặc KH không tồn tại.");
                 return;
            }
            if (tongDiem <= 0) {
                lblDiscountResult.setText("Bạn chưa có điểm để đổi ưu đãi.");
                return;
            }

            // Logic đổi điểm ví dụ: 1 điểm = 1% giảm giá, tối đa 20%
            int phanTramGiam = Math.min(tongDiem, 20); // Giới hạn % giảm giá
            // Hoặc 100 điểm = 1% giảm, tối đa 20%
            // int phanTramGiam = Math.min(tongDiem / 100, 20);


            lblDiscountResult.setText("Bạn có thể được giảm " + phanTramGiam + "% cho đơn hàng kế tiếp!");

        } catch (Exception e) {
            lblDiscountResult.setText("Đã xảy ra lỗi khi tính mức giảm giá.");
            e.printStackTrace();
        }
    }
}