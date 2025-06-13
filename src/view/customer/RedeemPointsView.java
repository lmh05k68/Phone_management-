package view.customer;

import query.KhachHangQuery;
import javax.swing.*;
import java.awt.*;

public class RedeemPointsView extends JFrame {

    private static final long serialVersionUID = 1L;
    private final Color PRIMARY_ACTION_COLOR = new Color(0, 123, 255);
    private final Color SECONDARY_ACTION_COLOR = new Color(108, 117, 125);
    private final Color SUCCESS_TEXT_COLOR = new Color(40, 167, 69);
    private final Color WARNING_TEXT_COLOR = new Color(220, 53, 69);
    private JLabel lblTitle;
    private JLabel lblCurrentPoints;
    private JLabel lblDiscountResult;
    private JButton btnCheckDiscount;
    private JButton btnBack;
    private final int maKH;
    private int currentPoints = 0;

    public RedeemPointsView(int maKH) {
        this.maKH = maKH;

        initUI(); // Khởi tạo giao diện
        addActions(); // Gán sự kiện cho các nút
        loadCustomerPoints(); 
        pack(); // Tự động điều chỉnh kích thước frame cho vừa với nội dung
        setMinimumSize(new Dimension(500, 380)); // Đặt kích thước tối thiểu
    }
    private void initUI() {
        setTitle("Đổi Điểm Thưởng - Khách Hàng #" + maKH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40)); 
        mainPanel.setBackground(Color.WHITE);
        lblTitle = new JLabel("Đổi Điểm Lấy Ưu Đãi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0)); // Khoảng cách dưới tiêu đề
        mainPanel.add(lblTitle);
        lblCurrentPoints = new JLabel("Đang tải điểm...", SwingConstants.CENTER);
        lblCurrentPoints.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblCurrentPoints.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblCurrentPoints);

        mainPanel.add(Box.createVerticalStrut(20)); 
        btnCheckDiscount = createStyledButton("Xem Mức Giảm Giá Có Thể Nhận", PRIMARY_ACTION_COLOR);
        btnCheckDiscount.setEnabled(false); // Vô hiệu hóa cho đến khi tải xong điểm
        mainPanel.add(btnCheckDiscount);
        
        mainPanel.add(Box.createVerticalStrut(15)); 
        lblDiscountResult = new JLabel(" ", SwingConstants.CENTER); // Bắt đầu với text rỗng
        lblDiscountResult.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblDiscountResult.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblDiscountResult);
        mainPanel.add(Box.createVerticalGlue());
        btnBack = createStyledButton("Trở về", SECONDARY_ACTION_COLOR);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(btnBack);

        setContentPane(mainPanel);
    }
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
        return btn;
    }
    private void addActions() {
        btnCheckDiscount.addActionListener(e -> showDiscountInfo());
        btnBack.addActionListener(e -> dispose());
    }
    private void loadCustomerPoints() {
        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return KhachHangQuery.getSoDiemTichLuy(maKH);
            }

            @Override
            protected void done() {
                try {
                    currentPoints = get();
                    lblCurrentPoints.setText("Điểm tích lũy hiện có: " + currentPoints + " điểm");
                    btnCheckDiscount.setEnabled(true); // Kích hoạt nút sau khi tải xong
                } catch (Exception e) {
                    lblCurrentPoints.setText("Lỗi: Không thể tải điểm thưởng.");
                    lblCurrentPoints.setForeground(WARNING_TEXT_COLOR);
                    btnCheckDiscount.setEnabled(false);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    private int tinhPhanTramGiamTuDiem(int soDiem) {
        if (soDiem < 100) {
            return 0; // Chưa đủ điểm tối thiểu
        }
        return Math.min(soDiem / 100, 20);
    }
    private void showDiscountInfo() {
        int phanTramGiam = tinhPhanTramGiamTuDiem(this.currentPoints);

        if (phanTramGiam <= 0) {
            int diemConThieu = 100 - this.currentPoints;
            lblDiscountResult.setText(String.format("Cần ít nhất 100 điểm để đổi (Còn thiếu %d điểm)", diemConThieu));
            lblDiscountResult.setForeground(WARNING_TEXT_COLOR); // Đặt màu cảnh báo
        } else {
            lblDiscountResult.setText(String.format("Giảm %d%% cho đơn hàng kế tiếp", phanTramGiam));
            lblDiscountResult.setForeground(SUCCESS_TEXT_COLOR); // Đặt màu thành công
        }
    }
}