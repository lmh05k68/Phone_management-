package view.employee;

import model.PhieuBaoHanh;
import query.PhieuBaoHanhQuery; 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;
import java.time.LocalDate;
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
        System.out.println("WARRANTY_MANAGEMENT_VIEW: Khởi tạo.");
        setTitle("Quản Lý Phiếu Bảo Hành");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadData(null); // Tải tất cả dữ liệu ban đầu
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTitle = new JLabel("Quản Lý Phiếu Bảo Hành");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(lblTitle);

        JPanel pnFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnFilters.setBorder(BorderFactory.createTitledBorder("Bộ lọc danh sách phiếu"));
        pnFilters.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        pnFilters.add(new JLabel("Mã Khách Hàng (lọc):"));
        txtMaKHFilter = new JTextField(10);
        pnFilters.add(txtMaKHFilter);

        btnLoc = createStyledButton("Lọc theo Mã KH");
        btnLoc.addActionListener(e -> filterByMaKH());
        pnFilters.add(btnLoc);

        btnClearFilter = createStyledButton("Hiện tất cả");
        btnClearFilter.setBackground(new Color(108, 117, 125));
        btnClearFilter.addActionListener(e -> {
            txtMaKHFilter.setText("");
            currentMaKHFilter = null;
            loadData(null);
        });
        pnFilters.add(btnClearFilter);
        mainPanel.add(pnFilters);
        mainPanel.add(Box.createVerticalStrut(15));

        String[] columns = {"Mã Phiếu (ID)", "Mã KH", "Mã SP", "Ngày Nhận", "Ngày Trả", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
        	private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(15));

        JPanel pnActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnActions.setBorder(BorderFactory.createTitledBorder("Cập nhật trạng thái phiếu đã chọn"));
        pnActions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        pnActions.add(new JLabel("Trạng thái mới:"));
        cboTrangThaiUpdate = new JComboBox<>(new String[]{"Đang xử lý", "Đã sửa xong", "Không thể sửa", "Đã trả khách"});
        cboTrangThaiUpdate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnActions.add(cboTrangThaiUpdate);

        btnCapNhat = createStyledButton("Cập Nhật Phiếu"); // Đổi tên nút
        btnCapNhat.setBackground(new Color(40, 167, 69));
        btnCapNhat.addActionListener(e -> capNhatTrangThaiVaNgayTra()); // Gọi hàm mới
        pnActions.add(btnCapNhat);
        mainPanel.add(pnActions);
        mainPanel.add(Box.createVerticalStrut(10));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        btnTroVe = createStyledButton("Trở về");
        btnTroVe.setBackground(new Color(220, 53, 69));
        btnTroVe.addActionListener(e -> dispose());
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
        columnModel.getColumn(0).setPreferredWidth(100); // Mã Phiếu (ID)
        columnModel.getColumn(1).setPreferredWidth(80);  // Mã KH
        columnModel.getColumn(2).setPreferredWidth(80);  // Mã SP
        columnModel.getColumn(3).setPreferredWidth(120); // Ngày Nhận
        columnModel.getColumn(4).setPreferredWidth(120); // Ngày Trả
        columnModel.getColumn(5).setPreferredWidth(180); // Trạng Thái (rộng hơn chút)
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
            // Gọi các phương thức static từ PhieuBaoHanhQuery
            if (maKHFilter == null) {
                list = PhieuBaoHanhQuery.getAll();
            } else {
                list = PhieuBaoHanhQuery.getByMaKH(maKHFilter); // Truyền int
            }

            if (list != null && !list.isEmpty()) {
                for (PhieuBaoHanh p : list) {
                    model.addRow(new Object[]{
                            p.getIdBH(),        // int
                            p.getMaKH(),        // int
                            p.getMaSP(),        // int
                            p.getNgayNhanSanPham(), // LocalDate
                            p.getNgayTraSanPham(),   // LocalDate (có thể null)
                            p.getTrangThai()    // String
                    });
                }
            } else if (maKHFilter != null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu bảo hành nào cho Mã KH: " + maKHFilter, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) { // Bắt lỗi rộng hơn trong trường hợp Query ném lỗi không mong muốn
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu phiếu bảo hành: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterByMaKH() {
        String text = txtMaKHFilter.getText().trim();
        if (text.isEmpty()) {
            currentMaKHFilter = null; // Reset bộ lọc
            loadData(null);
            return;
        }
        try {
            int maKH = Integer.parseInt(text); // Chuyển sang int
            // Tùy chọn: Kiểm tra xem MaKH có tồn tại trong bảng KhachHang không
            // if (!KhachHangQuery.exists(maKH)) { // Giả sử KhachHangQuery.exists(int)
            //     JOptionPane.showMessageDialog(this, "Mã Khách Hàng " + maKH + " không tồn tại.", "Lỗi Mã KH", JOptionPane.ERROR_MESSAGE);
            //     currentMaKHFilter = null;
            //     loadData(null); // Hiển thị lại tất cả
            //     return;
            // }
            currentMaKHFilter = maKH;
            loadData(currentMaKHFilter);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã Khách Hàng phải là một số nguyên.", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            txtMaKHFilter.requestFocus();
            // Có thể giữ nguyên currentMaKHFilter hoặc reset tùy theo logic mong muốn
        }
    }

    private void capNhatTrangThaiVaNgayTra() { // Đổi tên phương thức cho rõ ràng
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu bảo hành để cập nhật.", "Chưa chọn phiếu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idBH = (Integer) model.getValueAt(selectedRow, 0); // idBH là int
        String trangThaiMoi = (String) cboTrangThaiUpdate.getSelectedItem();

        // Lấy ngày trả hiện tại từ model (có thể là null)
        Object ngayTraHienTaiObj = model.getValueAt(selectedRow, 4);
        LocalDate ngayTraHienTai = null;
        if (ngayTraHienTaiObj instanceof LocalDate) {
            ngayTraHienTai = (LocalDate) ngayTraHienTaiObj;
        }

        LocalDate ngayTraDeCapNhat = ngayTraHienTai; // Mặc định giữ ngày trả cũ

        // Xử lý logic cho ngày trả dựa trên trạng thái mới
        if ("Đã trả khách".equals(trangThaiMoi)) {
            if (ngayTraHienTai == null) { // Nếu chưa có ngày trả, yêu cầu nhập
                String inputNgayTraStr = JOptionPane.showInputDialog(this,
                        "Nhập ngày trả sản phẩm (YYYY-MM-DD):",
                        LocalDate.now().toString()); // Gợi ý ngày hiện tại

                if (inputNgayTraStr == null) { // Người dùng nhấn Cancel
                    return; // Không làm gì cả
                }
                if (inputNgayTraStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ngày trả không được để trống khi trạng thái là 'Đã trả khách'.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    ngayTraDeCapNhat = LocalDate.parse(inputNgayTraStr.trim());
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Định dạng ngày trả không hợp lệ. Vui lòng sử dụng YYYY-MM-DD.", "Lỗi định dạng ngày", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            // Nếu đã có ngày trả, người dùng có thể không cần nhập lại, hoặc bạn có thể thêm lựa chọn cho phép sửa ngày trả.
        } else if (!"Đã sửa xong".equals(trangThaiMoi)) {
            // Nếu trạng thái không phải "Đã trả khách" và cũng không phải "Đã sửa xong"
            // (ví dụ: "Đang xử lý", "Không thể sửa"), thì nên xóa ngày trả (nếu có)
            ngayTraDeCapNhat = null;
        }
        // Nếu trạng thái là "Đã sửa xong", ngày trả có thể được giữ nguyên hoặc để trống chờ đến khi "Đã trả khách"

        String message = String.format("Cập nhật phiếu %d:\nTrạng thái mới: '%s'\nNgày trả mới: %s\n\nBạn có chắc chắn không?",
                                       idBH, trangThaiMoi, (ngayTraDeCapNhat != null ? ngayTraDeCapNhat.toString() : "Chưa có"));
        int confirm = JOptionPane.showConfirmDialog(this, message, "Xác nhận cập nhật", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Gọi phương thức query đã sửa
            boolean success = PhieuBaoHanhQuery.updateTrangThaiAndNgayTra(idBH, trangThaiMoi, ngayTraDeCapNhat);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật phiếu bảo hành thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData(currentMaKHFilter); // Tải lại dữ liệu
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật phiếu bảo hành thất bại. Vui lòng thử lại.", "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
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
             System.err.println("Không thể áp dụng Nimbus Look and Feel: " + e.getMessage());
         }
         SwingUtilities.invokeLater(() -> new WarrantyManagementView().setVisible(true));
     }
}