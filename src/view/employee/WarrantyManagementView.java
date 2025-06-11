package view.employee;

import model.PhieuBaoHanh;
import query.PhieuBaoHanhQuery;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WarrantyManagementView extends JFrame {
    private static final long serialVersionUID = 1L;

    // --- CONSTANTS FOR UI STYLING ---
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 26);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_TABLE_CELL = new Font("Segoe UI", Font.PLAIN, 14);

    // --- CLASS MEMBERS ---
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<PhieuBaoHanh.TrangThaiBaoHanh> cboTrangThaiUpdate;
    private JTextField txtMaKHFilter;
    private JButton btnCapNhat, btnLoc, btnClearFilter;
    private Integer currentMaKHFilter = null;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public WarrantyManagementView() {
        setTitle("Quản Lý Phiếu Bảo Hành");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadData(null); // Tải tất cả dữ liệu ban đầu
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        mainPanel.add(createTitlePanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createActionPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
        addEventListeners();
    }

    private JLabel createTitlePanel() {
        JLabel lblTitle = new JLabel("Quản Lý Phiếu Bảo Hành", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(PRIMARY_COLOR);
        return lblTitle;
    }

    private JPanel createFilterPanel() {
        JPanel pnFilters = new JPanel(new GridBagLayout());
        pnFilters.setBorder(BorderFactory.createTitledBorder("Bộ lọc danh sách phiếu"));
        pnFilters.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        pnFilters.add(new JLabel("Mã Khách Hàng:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtMaKHFilter = new JTextField(10);
        pnFilters.add(txtMaKHFilter, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        btnLoc = createStyledButton("Lọc", SUCCESS_COLOR);
        pnFilters.add(btnLoc, gbc);

        gbc.gridx = 3; gbc.gridy = 0;
        btnClearFilter = createStyledButton("Hiển thị tất cả", SECONDARY_COLOR);
        pnFilters.add(btnClearFilter, gbc);

        return pnFilters;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(createFilterPanel(), BorderLayout.NORTH);

        String[] columns = {"ID Phiếu", "Mã KH", "Mã SP Cụ Thể", "Mã Hóa Đơn", "Ngày Nhận", "Ngày Trả", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        return centerPanel;
    }
    
    private JPanel createActionPanel() {
        JPanel pnActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnActions.setBorder(BorderFactory.createTitledBorder("Cập nhật trạng thái phiếu đã chọn"));
        pnActions.setOpaque(false);
        pnActions.add(new JLabel("Trạng thái mới:"));
        
        cboTrangThaiUpdate = new JComboBox<>(PhieuBaoHanh.TrangThaiBaoHanh.values());
        cboTrangThaiUpdate.setFont(FONT_LABEL);
        pnActions.add(cboTrangThaiUpdate);
        
        btnCapNhat = createStyledButton("Cập Nhật Phiếu", SUCCESS_COLOR);
        pnActions.add(btnCapNhat);
        
        return pnActions;
    }
    
    private void addEventListeners() {
        btnLoc.addActionListener(e -> filterByMaKH());
        btnClearFilter.addActionListener(e -> clearFilter());
        btnCapNhat.addActionListener(e -> capNhatTrangThaiVaNgayTra());
        txtMaKHFilter.addActionListener(e -> btnLoc.doClick()); // Bấm Enter trong ô text cũng sẽ lọc
    }

    private void styleTable(JTable tbl) {
        tbl.setFillsViewportHeight(true);
        tbl.setFont(FONT_TABLE_CELL);
        tbl.getTableHeader().setFont(FONT_TABLE_HEADER);
        tbl.setRowHeight(30);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.getTableHeader().setBackground(new Color(220, 220, 220));

        TableColumnModel columnModel = tbl.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);   // ID Phiếu
        columnModel.getColumn(1).setPreferredWidth(80);   // Mã KH
        columnModel.getColumn(2).setPreferredWidth(180);  // Mã SP Cụ Thể
        columnModel.getColumn(3).setPreferredWidth(100);  // Mã Hóa Đơn
        columnModel.getColumn(4).setPreferredWidth(120);  // Ngày Nhận
        columnModel.getColumn(5).setPreferredWidth(120);  // Ngày Trả
        columnModel.getColumn(6).setPreferredWidth(150);  // Trạng Thái
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return btn;
    }

    private void loadData(Integer maKHFilter) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnLoc.setEnabled(false);
        btnClearFilter.setEnabled(false);
        
        new SwingWorker<List<PhieuBaoHanh>, Void>() {
            @Override
            protected List<PhieuBaoHanh> doInBackground() throws Exception {
                if (maKHFilter == null) {
                    return PhieuBaoHanhQuery.getAll();
                } else {
                    return PhieuBaoHanhQuery.getByMaKH(maKHFilter);
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<PhieuBaoHanh> list = get();
                    model.setRowCount(0); // Xóa dữ liệu cũ
                    if (list.isEmpty() && maKHFilter != null) {
                        JOptionPane.showMessageDialog(WarrantyManagementView.this, "Không tìm thấy phiếu bảo hành nào cho Mã KH: " + maKHFilter, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    for (PhieuBaoHanh p : list) {
                        model.addRow(new Object[]{
                                p.getIdBH(),
                                p.getMaKH(),
                                p.getMaSPCuThe(),
                                p.getMaHDX(),
                                p.getNgayNhanSanPham() != null ? p.getNgayNhanSanPham().format(dateFormatter) : "N/A",
                                p.getNgayTraSanPham() != null ? p.getNgayTraSanPham().format(dateFormatter) : "",
                                p.getTrangThai().getValue()
                        });
                    }
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(WarrantyManagementView.this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    btnLoc.setEnabled(true);
                    btnClearFilter.setEnabled(true);
                }
            }
        }.execute();
    }

    private void clearFilter() {
        txtMaKHFilter.setText("");
        currentMaKHFilter = null;
        loadData(null);
    }
    
    private void filterByMaKH() {
        String text = txtMaKHFilter.getText().trim();
        if (text.isEmpty()) {
            clearFilter();
            return;
        }
        try {
            int maKH = Integer.parseInt(text);
            currentMaKHFilter = maKH;
            loadData(currentMaKHFilter);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã Khách Hàng phải là một số nguyên.", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            txtMaKHFilter.requestFocus();
        }
    }

    private void capNhatTrangThaiVaNgayTra() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu bảo hành để cập nhật.", "Chưa chọn phiếu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idBH = (Integer) model.getValueAt(selectedRow, 0);
        PhieuBaoHanh.TrangThaiBaoHanh trangThaiMoi = (PhieuBaoHanh.TrangThaiBaoHanh) cboTrangThaiUpdate.getSelectedItem();
        LocalDate ngayTraDeCapNhat = null;

        if (trangThaiMoi == PhieuBaoHanh.TrangThaiBaoHanh.DA_TRA_KHACH) {
            String inputNgayTraStr = JOptionPane.showInputDialog(this,
                    "Nhập ngày trả sản phẩm (YYYY-MM-DD):",
                    LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)); 

            if (inputNgayTraStr == null) return; 

            if (inputNgayTraStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ngày trả không được để trống khi trạng thái là 'Đã trả khách'.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                ngayTraDeCapNhat = LocalDate.parse(inputNgayTraStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày trả không hợp lệ. Vui lòng sử dụng YYYY-MM-DD.", "Lỗi định dạng ngày", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String message = String.format("Cập nhật phiếu %d:\n- Trạng thái mới: '%s'\n- Ngày trả mới: %s\n\nBạn có chắc chắn không?",
                idBH, trangThaiMoi.getValue(), (ngayTraDeCapNhat != null ? ngayTraDeCapNhat.format(dateFormatter) : "Chưa có"));
        int confirm = JOptionPane.showConfirmDialog(this, message, "Xác nhận cập nhật", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnCapNhat.setEnabled(false);
            
            final LocalDate finalNgayTra = ngayTraDeCapNhat;
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return PhieuBaoHanhQuery.updateTrangThaiAndNgayTra(idBH, trangThaiMoi, finalNgayTra);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(WarrantyManagementView.this, "Cập nhật phiếu bảo hành thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            loadData(currentMaKHFilter);
                        } else {
                            JOptionPane.showMessageDialog(WarrantyManagementView.this, "Cập nhật phiếu bảo hành thất bại.", "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(WarrantyManagementView.this, "Lỗi khi cập nhật: " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                        btnCapNhat.setEnabled(true);
                    }
                }
            }.execute();
        }
    }
}