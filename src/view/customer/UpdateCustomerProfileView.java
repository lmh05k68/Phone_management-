package view.customer;

import model.KhachHang;
import query.KhachHangQuery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UpdateCustomerProfileView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int maKH;
    private JTextField tfHoTen, tfSdtKH;
    private JLabel lblMaKHValue, lblSoDiemValue;
    private JButton btnCapNhat, btnHuy;

    // Callback để thông báo cho cửa sổ cha rằng đã có cập nhật
    private Runnable onUpdateSuccess;

    public UpdateCustomerProfileView(int maKH, Runnable onUpdateSuccess) {
        this.maKH = maKH;
        this.onUpdateSuccess = onUpdateSuccess; // Lưu callback

        setTitle("Cập Nhật Thông Tin Cá Nhân - KH: " + maKH);
        setSize(450, 320); // Tăng chiều cao một chút cho đẹp
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initUI();
        loadCustomerData();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Cập Nhật Thông Tin", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); // Tăng khoảng cách dọc
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Mã KH (không cho sửa)
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblMaKHText = new JLabel("Mã Khách Hàng:");
        lblMaKHText.setFont(labelFont);
        formPanel.add(lblMaKHText, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_START;
        lblMaKHValue = new JLabel();
        lblMaKHValue.setFont(valueFont.deriveFont(Font.BOLD));
        formPanel.add(lblMaKHValue, gbc);

        // Họ Tên
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblHoTen = new JLabel("Họ và Tên*:");
        lblHoTen.setFont(labelFont);
        formPanel.add(lblHoTen, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_START;
        tfHoTen = new JTextField(20);
        tfHoTen.setFont(valueFont);
        formPanel.add(tfHoTen, gbc);

        // Số Điện Thoại
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblSdtKH = new JLabel("Số Điện Thoại*:");
        lblSdtKH.setFont(labelFont);
        formPanel.add(lblSdtKH, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.LINE_START;
        tfSdtKH = new JTextField(20);
        tfSdtKH.setFont(valueFont);
        formPanel.add(tfSdtKH, gbc);

        // Số Điểm Tích Lũy (không cho sửa)
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblSoDiemText = new JLabel("Điểm Tích Lũy:");
        lblSoDiemText.setFont(labelFont);
        formPanel.add(lblSoDiemText, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.LINE_START;
        lblSoDiemValue = new JLabel();
        lblSoDiemValue.setFont(valueFont);
        formPanel.add(lblSoDiemValue, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnCapNhat = createStyledButton("Lưu Thay Đổi", new Color(40, 167, 69));
        btnHuy = createStyledButton("Hủy", new Color(108, 117, 125));

        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnHuy);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Action Listeners
        btnCapNhat.addActionListener(e -> updateProfile());
        btnHuy.addActionListener(e -> dispose());
    }

    private void loadCustomerData() {
        KhachHang kh = KhachHangQuery.getKhachHangById(this.maKH);
        if (kh != null) {
            lblMaKHValue.setText(String.valueOf(kh.getMaKH()));
            tfHoTen.setText(kh.getHoTen());
            tfSdtKH.setText(kh.getSdtKH());
            lblSoDiemValue.setText(String.format("%,d điểm", kh.getSoDiemTichLuy()));
        } else {
            JOptionPane.showMessageDialog(this, "Không thể tải thông tin khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void updateProfile() {
        String hoTenMoi = tfHoTen.getText().trim();
        String sdtKHMoi = tfSdtKH.getText().trim();

        if (hoTenMoi.isEmpty() || sdtKHMoi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên và Số điện thoại không được để trống.", "Lỗi Nhập Liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        KhachHang khUpdated = new KhachHang();
        khUpdated.setMaKH(this.maKH);
        khUpdated.setHoTen(hoTenMoi);
        khUpdated.setSdtKH(sdtKHMoi);
        if (KhachHangQuery.capNhatThongTinCoBan(khUpdated)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            if (onUpdateSuccess != null) {
                onUpdateSuccess.run();
            }

            dispose(); // Đóng cửa sổ sau khi thành công
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thất bại.\nCó thể do số điện thoại đã tồn tại.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                new EmptyBorder(8, 20, 8, 20)
        ));
        return btn;
    }
}