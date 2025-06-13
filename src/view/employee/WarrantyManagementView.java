package view.employee;

import model.PhieuBaoHanh;
import query.PhieuBaoHanhQuery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;
public class WarrantyManagementView extends JFrame {
    private static final long serialVersionUID = 1L;

    // --- UI Constants ---
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_TABLE_CELL = new Font("Segoe UI", Font.PLAIN, 14);
    
    private static final Color COLOR_SUCCESS = new Color(40, 167, 69);
    private static final Color COLOR_SECONDARY = new Color(108, 117, 125);
    private static final Color COLOR_PRIMARY = new Color(0, 102, 204);
    private static final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    private static final Color COLOR_HEADER_BG = new Color(73, 80, 87);
    private static final Color COLOR_ALT_ROW = new Color(242, 242, 242);

    // --- Components & Data ---
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<PhieuBaoHanh.TrangThaiBaoHanh> cboTrangThaiUpdate;
    private JTextField txtMaKHFilter;
    private JButton btnCapNhat, btnLoc, btnClearFilter;
    private Integer currentMaKHFilter = null;

    public WarrantyManagementView() {
        setTitle("Quản Lý Phiếu Bảo Hành");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        reloadData();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(COLOR_BACKGROUND);

        mainPanel.add(createTitlePanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createActionPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
        addEventListeners();
    }

    private JLabel createTitlePanel() {
        JLabel lblTitle = new JLabel("Quản Lý Phiếu Bảo Hành", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_PRIMARY);
        return lblTitle;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(createFilterPanel(), BorderLayout.NORTH);
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createFilterPanel() {
        JPanel pnFilters = new JPanel(new GridBagLayout());
        pnFilters.setBorder(BorderFactory.createTitledBorder("Bộ lọc danh sách phiếu"));
        pnFilters.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        pnFilters.add(new JLabel("Mã Khách Hàng:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtMaKHFilter = new JTextField(15);
        pnFilters.add(txtMaKHFilter, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        btnLoc = createStyledButton("Lọc", COLOR_SUCCESS);
        pnFilters.add(btnLoc, gbc);

        gbc.gridx = 3; gbc.gridy = 0;
        btnClearFilter = createStyledButton("Hiển thị tất cả", COLOR_SECONDARY);
        pnFilters.add(btnClearFilter, gbc);
        return pnFilters;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"ID Phiếu", "Mã KH", "Mã SP Cụ Thể", "Mã Hóa Đơn", "Ngày Nhận", "Ngày Trả", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
            
            // TỐI ƯU: Khai báo kiểu dữ liệu để sắp xếp chính xác
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: case 1: case 3: return Integer.class;
                    case 4: case 5: return LocalDate.class;
                    default: return String.class;
                }
            }
        };
        
        table = new JTable(model) {
            private static final long serialVersionUID = 1L;
            // TỐI ƯU: Thêm màu xen kẽ cho các hàng
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_ALT_ROW);
                }
                return c;
            }
        };
        styleTable(table);
        return new JScrollPane(table);
    }
    
    private JPanel createActionPanel() {
        JPanel pnActions = new JPanel(new GridBagLayout());
        pnActions.setBorder(BorderFactory.createTitledBorder("Cập nhật trạng thái phiếu đã chọn"));
        pnActions.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        pnActions.add(new JLabel("Trạng thái mới:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        cboTrangThaiUpdate = new JComboBox<>(PhieuBaoHanh.TrangThaiBaoHanh.values());
        cboTrangThaiUpdate.setFont(FONT_LABEL);
        pnActions.add(cboTrangThaiUpdate, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0; gbc.insets.left = 20;
        btnCapNhat = createStyledButton("Cập Nhật Phiếu", COLOR_SUCCESS);
        pnActions.add(btnCapNhat, gbc);
        
        return pnActions;
    }

    private void addEventListeners() {
        btnLoc.addActionListener(e -> filterByMaKH());
        btnClearFilter.addActionListener(e -> reloadData());
        btnCapNhat.addActionListener(e -> capNhatTrangThaiVaNgayTra());
        txtMaKHFilter.addActionListener(e -> btnLoc.doClick());
    }

    private void styleTable(JTable tbl) {
        tbl.setFillsViewportHeight(true);
        tbl.setFont(FONT_TABLE_CELL);
        tbl.getTableHeader().setFont(FONT_TABLE_HEADER);
        tbl.getTableHeader().setBackground(COLOR_HEADER_BG);
        tbl.getTableHeader().setForeground(Color.WHITE);
        tbl.setRowHeight(32);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoCreateRowSorter(true);

        // Áp dụng renderer để căn giữa và định dạng
        CenteredRenderer centeredRenderer = new CenteredRenderer();
        tbl.setDefaultRenderer(Integer.class, centeredRenderer);
        tbl.setDefaultRenderer(LocalDate.class, new DateRenderer());
        tbl.setDefaultRenderer(String.class, centeredRenderer); // Căn giữa các cột mã
        tbl.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer()); // Renderer đặc biệt cho trạng thái

        TableColumnModel columnModel = tbl.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);
        columnModel.getColumn(1).setPreferredWidth(80);
        columnModel.getColumn(2).setPreferredWidth(180);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(120);
        columnModel.getColumn(5).setPreferredWidth(120);
        columnModel.getColumn(6).setPreferredWidth(150);
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
    
    private void reloadData() {
        txtMaKHFilter.setText("");
        currentMaKHFilter = null;
        loadData(null);
    }
    
    private void filterByMaKH() {
        String text = txtMaKHFilter.getText().trim();
        if (text.isEmpty()) {
            reloadData();
            return;
        }
        try {
            int maKH = Integer.parseInt(text);
            currentMaKHFilter = maKH;
            loadData(currentMaKHFilter);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã Khách Hàng phải là một số nguyên.", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tải dữ liệu phiếu bảo hành (toàn bộ hoặc lọc) trong một luồng nền.
     * @param maKHFilter Mã khách hàng để lọc. Nếu là null, tải tất cả.
     */
    private void loadData(Integer maKHFilter) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnLoc.setEnabled(false);
        btnClearFilter.setEnabled(false);
        
        new SwingWorker<List<PhieuBaoHanh>, Void>() {
            @Override
            protected List<PhieuBaoHanh> doInBackground() throws Exception {
                return maKHFilter == null ? PhieuBaoHanhQuery.getAll() : PhieuBaoHanhQuery.getByMaKH(maKHFilter);
            }
            
            @Override
            protected void done() {
                try {
                    List<PhieuBaoHanh> list = get();
                    model.setRowCount(0);
                    
                    if (list.isEmpty() && maKHFilter != null) {
                        JOptionPane.showMessageDialog(WarrantyManagementView.this, "Không tìm thấy phiếu nào cho Mã KH: " + maKHFilter, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    for (PhieuBaoHanh p : list) {
                        // SỬA LỖI: Truyền thẳng đối tượng LocalDate vào model
                        model.addRow(new Object[]{
                                p.getIdBH(), p.getMaKH(), p.getMaSPCuThe(), p.getMaHDX(),
                                p.getNgayNhanSanPham(), // Truyền LocalDate
                                p.getNgayTraSanPham(),  // Truyền LocalDate (hoặc null)
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

    private void capNhatTrangThaiVaNgayTra() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu bảo hành để cập nhật.", "Chưa chọn phiếu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int idBH = (Integer) model.getValueAt(modelRow, 0);
        PhieuBaoHanh.TrangThaiBaoHanh trangThaiMoi = (PhieuBaoHanh.TrangThaiBaoHanh) cboTrangThaiUpdate.getSelectedItem();
        
        LocalDate ngayTraDeCapNhat = null;

        if (trangThaiMoi == PhieuBaoHanh.TrangThaiBaoHanh.DA_TRA_KHACH) {
            ngayTraDeCapNhat = promptForReturnDate();
            if (ngayTraDeCapNhat == null) return; // Người dùng đã hủy
        }

        String message = String.format("Cập nhật phiếu %d:\n- Trạng thái mới: '%s'\n- Ngày trả mới: %s\n\nBạn có chắc chắn không?",
                idBH, trangThaiMoi.getValue(), (ngayTraDeCapNhat != null ? ngayTraDeCapNhat.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Chưa có"));
        int confirm = JOptionPane.showConfirmDialog(this, message, "Xác nhận cập nhật", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            final LocalDate finalNgayTra = ngayTraDeCapNhat;
            executeUpdate(idBH, trangThaiMoi, finalNgayTra);
        }
    }

    /**
     * Hiển thị hộp thoại để người dùng nhập ngày trả.
     * @return LocalDate nếu người dùng nhập hợp lệ và OK, null nếu hủy.
     */
    private LocalDate promptForReturnDate() {
        try {
            MaskFormatter formatter = new MaskFormatter("##/##/####");
            formatter.setPlaceholderCharacter('_');
            JFormattedTextField dateField = new JFormattedTextField(formatter);
            dateField.setFont(FONT_LABEL);
            dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")));
            
            Object[] message = {"Nhập ngày trả sản phẩm (dd/MM/yyyy):", dateField};
            
            int option = JOptionPane.showConfirmDialog(this, message, "Nhập ngày trả", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                return LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
        } catch (ParseException | DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày trả không hợp lệ. Vui lòng nhập đúng dd/MM/yyyy.", "Lỗi định dạng ngày", JOptionPane.ERROR_MESSAGE);
        }
        return null; // Trả về null nếu có lỗi hoặc người dùng bấm Cancel
    }

    /**
     * Thực thi việc cập nhật trong một luồng nền.
     */
    private void executeUpdate(int idBH, PhieuBaoHanh.TrangThaiBaoHanh trangThaiMoi, LocalDate ngayTra) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnCapNhat.setEnabled(false);
        
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                return PhieuBaoHanhQuery.updateTrangThaiAndNgayTra(idBH, trangThaiMoi, ngayTra);
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(WarrantyManagementView.this, "Cập nhật phiếu thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadData(currentMaKHFilter);
                    } else {
                        JOptionPane.showMessageDialog(WarrantyManagementView.this, "Cập nhật phiếu thất bại.", "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
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
    
    // --- Custom Table Cell Renderers ---

    private static class CenteredRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        public CenteredRenderer() { setHorizontalAlignment(SwingConstants.CENTER); }
    }

    private static class DateRenderer extends CenteredRenderer {
        private static final long serialVersionUID = 1L;
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        @Override
        public void setValue(Object value) {
            setText((value == null) ? "" : ((LocalDate) value).format(formatter));
        }
    }

    private static class StatusCellRenderer extends CenteredRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = (String) value;
            if (status != null) {
                switch (status) {
                    case "Đã trả khách": c.setForeground(new Color(0, 128, 0)); break; // Dark Green
                    case "Đang sửa chữa": c.setForeground(new Color(255, 140, 0)); break; // Dark Orange
                    case "Chờ linh kiện": c.setForeground(new Color(220, 20, 60)); break; // Crimson
                    default: c.setForeground(Color.BLACK); break; // Đang chờ nhận...
                }
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            }
            return c;
        }
    }
}