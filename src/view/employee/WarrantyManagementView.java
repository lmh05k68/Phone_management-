package view.employee;

import model.PhieuBaoHanh;
import query.PhieuBaoHanhQuery;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class WarrantyManagementView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> cboTrangThaiUpdate;
    private JTextField txtMaKHFilter;
    private JButton btnCapNhat, btnLoc, btnClearFilter, btnTroVe;
    private PhieuBaoHanhQuery query;
    private String currentMaKHFilter = null;  // ✅ Sửa thành String
    private String maNV;

    public WarrantyManagementView(String maNV) {
        this.maNV = maNV;

        setTitle("Quản Lý Bảo Hành");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        query = new PhieuBaoHanhQuery();

        initUI();
        loadData(null);
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

        // Bộ lọc
        JPanel pnFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnFilters.setBorder(BorderFactory.createTitledBorder("Bộ lọc danh sách"));
        pnFilters.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        pnFilters.add(new JLabel("Mã KH:"));
        txtMaKHFilter = new JTextField(10);
        pnFilters.add(txtMaKHFilter);

        btnLoc = createStyledButton("Lọc theo Mã KH");
        btnLoc.addActionListener(e -> filterByMaKH());
        pnFilters.add(btnLoc);

        btnClearFilter = createStyledButton("Xóa Bộ Lọc");
        btnClearFilter.setBackground(new Color(100, 100, 100));
        btnClearFilter.addActionListener(e -> {
            txtMaKHFilter.setText("");
            currentMaKHFilter = null;
            loadData(null);
        });
        pnFilters.add(btnClearFilter);
        mainPanel.add(pnFilters);
        mainPanel.add(Box.createVerticalStrut(15));

        // Bảng
        String[] columns = {"Mã Phiếu", "Mã KH", "Mã SP", "Ngày Nhận", "Ngày Trả", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(850, 300));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(15));

        // Cập nhật trạng thái
        JPanel pnActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnActions.setBorder(BorderFactory.createTitledBorder("Cập nhật trạng thái phiếu đã chọn"));
        pnActions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        pnActions.add(new JLabel("Trạng thái mới:"));
        cboTrangThaiUpdate = new JComboBox<>(new String[]{"Đang xử lý", "Đã bảo hành", "Từ chối", "Đã trả khách"});
        cboTrangThaiUpdate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnActions.add(cboTrangThaiUpdate);

        btnCapNhat = createStyledButton("Cập Nhật Trạng Thái");
        btnCapNhat.setBackground(new Color(0, 128, 0));
        btnCapNhat.addActionListener(e -> capNhatTrangThai());
        pnActions.add(btnCapNhat);

        // Nút Trở về
        btnTroVe = createStyledButton("Trở về");
        btnTroVe.setBackground(new Color(128, 0, 0));
        btnTroVe.addActionListener(e -> {
            new EmployeeView(maNV).setVisible(true);
            dispose();
        });
        pnActions.add(btnTroVe);

        mainPanel.add(pnActions);
        setContentPane(mainPanel);
    }

    private void styleTable(JTable tbl) {
        tbl.setFillsViewportHeight(true);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.setRowHeight(25);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(59, 89, 182));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 50, 100), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return btn;
    }

    // ✅ Sửa đổi: dùng String thay vì Integer
    private void loadData(String maKHFilter) {
        model.setRowCount(0);
        List<PhieuBaoHanh> list;

        try {
            list = (maKHFilter == null || maKHFilter.isEmpty())
                    ? query.getAllWarrantyRequests()
                    : query.getByMaKH(maKHFilter);

            if (list != null && !list.isEmpty()) {
                for (PhieuBaoHanh p : list) {
                    model.addRow(new Object[]{
                            p.getIdBH(),
                            p.getMaKH(),
                            p.getMaSP(),
                            p.getNgayNhanSanPham(),
                            p.getNgayTraSanPham(),
                            p.getTrangThai()
                    });
                }
            } else if (maKHFilter != null && !maKHFilter.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu bảo hành nào cho Mã KH: " + maKHFilter, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterByMaKH() {
        String text = txtMaKHFilter.getText().trim();
        if (text.isEmpty()) {
            loadData(null);
            currentMaKHFilter = null;
            return;
        }

        currentMaKHFilter = text;
        loadData(currentMaKHFilter);
    }

    private void capNhatTrangThai() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu để cập nhật.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idBH = model.getValueAt(selectedRow, 0).toString();
        String trangThaiMoi = (String) cboTrangThaiUpdate.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn cập nhật trạng thái phiếu " + idBH + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = query.updateTrangThai(idBH, trangThaiMoi);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData(currentMaKHFilter);
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}