package view.customer;

import query.KhachHangQuery;
import javax.swing.*;
import java.awt.*;
// import view.customer.CustomerView; // Đã được gọi trong action listener

public class RedeemPointsView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel lblWelcome, lblCurrentPoints, lblDiscountResult;
    private JButton btnCheckDiscount, btnBack;
    private final int maKH;

    public RedeemPointsView(int maKH) {
        this.maKH = maKH;
        setTitle("Đổi Điểm Thưởng - KH: " + maKH);
        setSize(480, 350); // Tăng chiều cao một chút
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadDiemHienTaiCuaKhachHang();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25)); // Tăng padding

        lblWelcome = new JLabel("Đổi Điểm Lấy Ưu Đãi", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 22)); // Font lớn hơn
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(0,0,20,0)); // Tăng khoảng cách dưới

        lblCurrentPoints = new JLabel("Đang tải điểm...", SwingConstants.CENTER);
        lblCurrentPoints.setFont(new Font("Segoe UI", Font.PLAIN, 17)); // Font lớn hơn
        lblCurrentPoints.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCurrentPoints.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));

        btnCheckDiscount = new JButton("Xem Mức Giảm Giá Có Thể Nhận");
        styleButton(btnCheckDiscount, new Color(33, 150, 243));
        btnCheckDiscount.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCheckDiscount.setEnabled(false); // Ban đầu vô hiệu hóa, bật sau khi tải điểm

        lblDiscountResult = new JLabel(" ", SwingConstants.CENTER); // Để trống ban đầu, tránh nhảy layout
        lblDiscountResult.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblDiscountResult.setForeground(new Color(40, 167, 69));
        lblDiscountResult.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDiscountResult.setBorder(BorderFactory.createEmptyBorder(15,0,20,0)); // Tăng khoảng cách

        btnBack = new JButton("Trở về");
        styleButton(btnBack, new Color(108, 117, 125));
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(lblWelcome);
        mainPanel.add(lblCurrentPoints);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(btnCheckDiscount);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(lblDiscountResult);
        mainPanel.add(Box.createVerticalGlue()); // Đẩy nút Back xuống nếu có không gian
        mainPanel.add(btnBack);

        setContentPane(mainPanel);

        btnCheckDiscount.addActionListener(e -> showDiscountInfo());
        btnBack.addActionListener(e -> {
            dispose();
            new CustomerView(this.maKH).setVisible(true);
        });
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Font lớn hơn chút
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20) // Tăng padding
        ));
        button.setPreferredSize(new Dimension(300, 45)); // Kích thước cố định hơn
        button.setMaximumSize(new Dimension(320, 45));
    }

    private void loadDiemHienTaiCuaKhachHang() {
        System.out.println("REDEEM_VIEW: Đang tải điểm cho MaKH: " + maKH);
        // Thực hiện tải điểm trong một thread khác để không block UI (tùy chọn nếu query nhanh)
        // SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
        //     @Override
        //     protected Integer doInBackground() throws Exception {
        //         return KhachHangQuery.getSoDiemTichLuy(maKH);
        //     }
        //     @Override
        //     protected void done() {
        //         try {
        //             int tongDiem = get();
        //             if (tongDiem == -1) {
        //                 lblCurrentPoints.setText("Lỗi tải điểm hoặc KH không tồn tại.");
        //                 btnCheckDiscount.setEnabled(false);
        //             } else {
        //                 lblCurrentPoints.setText("Điểm tích lũy hiện có: " + tongDiem + " điểm");
        //                 btnCheckDiscount.setEnabled(tongDiem > 0);
        //             }
        //         } catch (Exception e) {
        //             lblCurrentPoints.setText("Không thể tải điểm thưởng.");
        //             btnCheckDiscount.setEnabled(false);
        //             e.printStackTrace();
        //         }
        //     }
        // };
        // worker.execute();

        // Cách đơn giản (nếu query nhanh)
        int tongDiem = KhachHangQuery.getSoDiemTichLuy(this.maKH);
        if (tongDiem == -1) {
             lblCurrentPoints.setText("Lỗi tải điểm hoặc KH không tồn tại.");
             btnCheckDiscount.setEnabled(false);
        } else {
             lblCurrentPoints.setText("Điểm tích lũy hiện có: " + tongDiem + " điểm");
             btnCheckDiscount.setEnabled(tongDiem >= 100); // Chỉ bật nếu đủ 100 điểm (theo logic tinhPhanTramGiam)
        }
    }

    private void showDiscountInfo() {
        System.out.println("REDEEM_VIEW: Kiểm tra mức giảm giá cho MaKH: " + maKH);
        int tongDiem = KhachHangQuery.getSoDiemTichLuy(this.maKH); // Lấy điểm mới nhất
        if (tongDiem == -1) {
             lblDiscountResult.setText("Lỗi khi truy vấn điểm của bạn.");
             return;
        }

        int phanTramGiam = KhachHangQuery.tinhPhanTramGiamTuDiem(this.maKH);

        if (phanTramGiam <= 0) {
            if (tongDiem < 100 && tongDiem >=0) {
                 lblDiscountResult.setText(String.format("Bạn có %d điểm. Cần ít nhất 100 điểm để đổi ưu đãi.", tongDiem));
            } else {
                 lblDiscountResult.setText("Bạn chưa có đủ điểm để đổi ưu đãi.");
            }
        } else {
            lblDiscountResult.setText(String.format("Với %d điểm, bạn có thể được giảm %d%% cho đơn hàng kế tiếp!", tongDiem, phanTramGiam));
        }
    }
}