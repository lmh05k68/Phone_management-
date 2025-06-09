package view.employee;

import model.PhieuBaoHanh;
import query.PhieuBaoHanhQuery; // Đảm bảo model.PhieuBaoHanh có getMaHDX()
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // Thêm nếu chưa có, cho gợi ý ngày
import java.time.format.DateTimeParseException;

public class WarrantyManagementView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> cboTrangThaiUpdate;
    private JTextField txtMaKHFilter;
    private JButton btnCapNhat, btnLoc, btnClearFilter, btnTroVe;
    private Integer currentMaKHFilter = null;

    public WarrantyManagementView() {
        System.out.println("WARRANTY_MANAGEMENT_VIEW: Khoi tao.");
        setTitle("Quan Ly Phieu Bao Hanh");
        setSize(1150, 650); // Tăng chiều rộng để chứa thêm cột
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadData(null); // Tai tat ca du lieu ban dau
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTitle = new JLabel("Quan Ly Phieu Bao Hanh");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(lblTitle);

        JPanel pnFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnFilters.setBorder(BorderFactory.createTitledBorder("Bo loc danh sach phieu"));
        pnFilters.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        pnFilters.add(new JLabel("Ma Khach Hang (loc):"));
        txtMaKHFilter = new JTextField(10);
        pnFilters.add(txtMaKHFilter);

        btnLoc = createStyledButton("Loc theo Ma KH");
        btnLoc.addActionListener(e -> filterByMaKH());
        pnFilters.add(btnLoc);

        btnClearFilter = createStyledButton("Hien tat ca");
        btnClearFilter.setBackground(new Color(108, 117, 125));
        btnClearFilter.addActionListener(e -> {
            txtMaKHFilter.setText("");
            currentMaKHFilter = null;
            loadData(null);
        });
        pnFilters.add(btnClearFilter);
        mainPanel.add(pnFilters);
        mainPanel.add(Box.createVerticalStrut(15));

        // 1. CẬP NHẬT MẢNG COLUMNS
        String[] columns = {"Ma Phieu (ID)", "Ma KH", "Ma SP", "Ma HDX", "Ngay Nhan", "Ngay Tra", "Trang Thai"};
        model = new DefaultTableModel(columns, 0) {
        	private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        styleTable(table); // Gọi styleTable sau khi đã có model và table

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(15));

        JPanel pnActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnActions.setBorder(BorderFactory.createTitledBorder("Cap nhat trang thai phieu da chon"));
        pnActions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        pnActions.add(new JLabel("Trang thai moi:"));
        cboTrangThaiUpdate = new JComboBox<>(new String[]{"Dang xu ly", "Da sua xong", "Khong the sua", "Da tra khach"});
        cboTrangThaiUpdate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnActions.add(cboTrangThaiUpdate);

        btnCapNhat = createStyledButton("Cap Nhat Phieu");
        btnCapNhat.setBackground(new Color(40, 167, 69));
        btnCapNhat.addActionListener(e -> capNhatTrangThaiVaNgayTra());
        pnActions.add(btnCapNhat);
        mainPanel.add(pnActions);
        mainPanel.add(Box.createVerticalStrut(10));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        btnTroVe = createStyledButton("Tro ve");
        btnTroVe.setBackground(new Color(220, 53, 69));
        btnTroVe.addActionListener(e -> dispose()); // Đóng frame hiện tại
        // Ví dụ: nếu muốn quay lại EmployeeMenuView
        // btnTroVe.addActionListener(e -> {
        //     dispose();
        //     new EmployeeMenuView().setVisible(true); // Thay EmployeeMenuView bằng tên view chính của bạn
        // });
        bottomPanel.add(btnTroVe);
        mainPanel.add(bottomPanel);

        setContentPane(mainPanel);
    }

    private void styleTable(JTable tbl) {
        tbl.setFillsViewportHeight(true);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.setRowHeight(28);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);

        TableColumnModel columnModel = tbl.getColumnModel();
        // 2. CẬP NHẬT ĐỘ RỘNG CỘT
        columnModel.getColumn(0).setPreferredWidth(100); // Ma Phieu (ID)
        columnModel.getColumn(1).setPreferredWidth(80);  // Ma KH
        columnModel.getColumn(2).setPreferredWidth(80);  // Ma SP
        columnModel.getColumn(3).setPreferredWidth(80);  // Ma HDX (CỘT MỚI)
        columnModel.getColumn(4).setPreferredWidth(120); // Ngay Nhan (trước là cột 3)
        columnModel.getColumn(5).setPreferredWidth(120); // Ngay Tra (trước là cột 4)
        columnModel.getColumn(6).setPreferredWidth(180); // Trang Thai (trước là cột 5)
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return btn;
    }

    private void loadData(Integer maKHFilter) {
        model.setRowCount(0);
        List<PhieuBaoHanh> list;

        try {
            if (maKHFilter == null) {
                list = PhieuBaoHanhQuery.getAll();
            } else {
                list = PhieuBaoHanhQuery.getByMaKH(maKHFilter);
            }

            if (list != null && !list.isEmpty()) {
                for (PhieuBaoHanh p : list) {
                    // 3. THÊM p.getMaHDX() VÀO addRow
                    // Nếu MaHDX là null, hiển thị chuỗi rỗng hoặc "N/A"
                    Object maHDXDisplay = p.getMaHDX() == null ? "" : p.getMaHDX();
                    model.addRow(new Object[]{
                            p.getIdBH(),
                            p.getMaKH(),
                            p.getMaSP(),
                            maHDXDisplay, // Thêm MaHDX
                            p.getNgayNhanSanPham(),
                            p.getNgayTraSanPham(),
                            p.getTrangThai()
                    });
                }
            } else if (maKHFilter != null) {
                JOptionPane.showMessageDialog(this, "Khong tim thay phieu bao hanh nao cho Ma KH: " + maKHFilter, "Thong bao", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Loi khi tai du lieu phieu bao hanh: " + e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterByMaKH() {
        String text = txtMaKHFilter.getText().trim();
        if (text.isEmpty()) {
            currentMaKHFilter = null;
            loadData(null);
            return;
        }
        try {
            int maKH = Integer.parseInt(text);
            // Optional: Check if MaKH exists
            // if (!KhachHangQuery.exists(maKH)) { // Assuming KhachHangQuery.exists(int)
            //     JOptionPane.showMessageDialog(this, "Ma Khach Hang " + maKH + " khong ton tai.", "Loi Ma KH", JOptionPane.ERROR_MESSAGE);
            //     currentMaKHFilter = null;
            //     loadData(null);
            //     return;
            // }
            currentMaKHFilter = maKH;
            loadData(currentMaKHFilter);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ma Khach Hang phai la mot so nguyen.", "Loi dinh dang", JOptionPane.ERROR_MESSAGE);
            txtMaKHFilter.requestFocus();
        }
    }

    private void capNhatTrangThaiVaNgayTra() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot phieu bao hanh de cap nhat.", "Chua chon phieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Chỉ số các cột đã thay đổi do thêm cột MaHDX
        // Cột 0: idBH
        // Cột 1: MaKH
        // Cột 2: MaSP
        // Cột 3: MaHDX
        // Cột 4: NgayNhan (trước là cột 3)
        // Cột 5: NgayTra (trước là cột 4)
        // Cột 6: TrangThai (trước là cột 5)

        int idBH = (Integer) model.getValueAt(selectedRow, 0);
        String trangThaiMoi = (String) cboTrangThaiUpdate.getSelectedItem();

        Object ngayTraHienTaiObj = model.getValueAt(selectedRow, 5); // Lấy từ cột 5 (NgayTra)
        LocalDate ngayTraHienTai = null;
        if (ngayTraHienTaiObj instanceof LocalDate) {
            ngayTraHienTai = (LocalDate) ngayTraHienTaiObj;
        }

        LocalDate ngayTraDeCapNhat = ngayTraHienTai;

        if ("Da tra khach".equals(trangThaiMoi)) {
            if (ngayTraHienTai == null) {
                String inputNgayTraStr = JOptionPane.showInputDialog(this,
                        "Nhap ngay tra san pham (YYYY-MM-DD):",
                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)); // Gợi ý ngày hiện tại

                if (inputNgayTraStr == null) {
                    return;
                }
                if (inputNgayTraStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ngay tra khong duoc de trong khi trang thai la 'Da tra khach'.", "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    ngayTraDeCapNhat = LocalDate.parse(inputNgayTraStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Dinh dang ngay tra khong hop le. Vui long su dung YYYY-MM-DD.", "Loi dinh dang ngay", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } else if (!"Da sua xong".equals(trangThaiMoi)) {
            ngayTraDeCapNhat = null;
        }

        String message = String.format("Cap nhat phieu %d:\nTrang thai moi: '%s'\nNgay tra moi: %s\n\nBan co chac chan khong?",
                                       idBH, trangThaiMoi, (ngayTraDeCapNhat != null ? ngayTraDeCapNhat.format(DateTimeFormatter.ISO_LOCAL_DATE) : "Chua co"));
        int confirm = JOptionPane.showConfirmDialog(this, message, "Xac nhan cap nhat", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = PhieuBaoHanhQuery.updateTrangThaiAndNgayTra(idBH, trangThaiMoi, ngayTraDeCapNhat);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cap nhat phieu bao hanh thanh cong.", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
                loadData(currentMaKHFilter);
            } else {
                JOptionPane.showMessageDialog(this, "Cap nhat phieu bao hanh that bai. Vui long thu lai.", "Loi cap nhat", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

     public static void main(String[] args) {
         try {
             for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                 if ("Nimbus".equals(info.getName())) {
                     UIManager.setLookAndFeel(info.getClassName());
                     break;
                 }
             }
         } catch (Exception e) {
             System.err.println("Khong the ap dung Nimbus Look and Feel: " + e.getMessage());
         }
         SwingUtilities.invokeLater(() -> new WarrantyManagementView().setVisible(true));
     }
}