package view.admin;

import query.BangLuongQuery;
import model.BangLuong;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class SalaryReportView extends JPanel {
    private static final long serialVersionUID = 1L;

    // --- UI Constants ---
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color TITLE_COLOR = new Color(0, 102, 204);
    private static final Color HEADER_BG_COLOR = new Color(52, 73, 94);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);

    // --- UI Components ---
    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private JTable salaryTable;
    private DefaultTableModel tableModel;
    private JLabel totalSalaryLabel;
    private JLabel statusLabel;

    private BangLuongQuery bangLuongDAO;

    public SalaryReportView() {
        this.bangLuongDAO = new BangLuongQuery();
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setBackground(BG_COLOR);

        initUI();
        setupListeners();
        setDefaultValuesAndLoadInitialData();
    }

    private void initUI() {
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }
    
    private JLabel createTitlePanel() {
        JLabel titleLabel = new JLabel("BÁO CÁO BẢNG LƯƠNG NHÂN VIÊN");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        return titleLabel;
    }
    
    private JPanel createMainContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(createFilterPanel(), BorderLayout.NORTH);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);
        return contentPanel;
    }
    
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Xem theo thời gian"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Tháng:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        monthComboBox = createMonthComboBox();
        filterPanel.add(monthComboBox, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.insets.left = 15;
        filterPanel.add(new JLabel("Năm:"), gbc);
        gbc.insets.left = 5;

        gbc.gridx = 3; gbc.gridy = 0;
        yearComboBox = createYearComboBox();
        filterPanel.add(yearComboBox, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 1.0;
        filterPanel.add(new JLabel(""), gbc);

        return filterPanel;
    }

    private JComboBox<String> createMonthComboBox() {
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = "Tháng " + (i + 1);
        }
        JComboBox<String> cbo = new JComboBox<>(months);
        styleComboBox(cbo);
        return cbo;
    }

    private JComboBox<Integer> createYearComboBox() {
        Vector<Integer> years = new Vector<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i >= currentYear - 5; i--) {
            years.add(i);
        }
        JComboBox<Integer> cbo = new JComboBox<>(years);
        styleComboBox(cbo);
        return cbo;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"Mã NV", "Tên Nhân Viên", "Lương Cơ Bản", "Tổng Thưởng", "Lương Tháng (Thực Lãnh)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                if (col == 0) return Integer.class;
                if (col == 2 || col == 3 || col == 4) return BigDecimal.class;
                return String.class;
            }
        };
        salaryTable = new JTable(tableModel);
        styleTable();
        return new JScrollPane(salaryTable);
    }
    
    private JPanel createStatusPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        statusLabel = new JLabel("Sẵn sàng.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setBorder(new EmptyBorder(10, 5, 0, 5));
        
        totalSalaryLabel = new JLabel("Tổng quỹ lương: 0 ₫");
        totalSalaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalSalaryLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(totalSalaryLabel, BorderLayout.EAST);
        return bottomPanel;
    }

    // TỐI ƯU & SỬA LỖI: Cập nhật phương thức styleTable
    private void styleTable() {
        salaryTable.setFillsViewportHeight(true);
        salaryTable.setRowHeight(30);
        salaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salaryTable.setGridColor(new Color(220, 220, 220));
        salaryTable.setSelectionBackground(new Color(184, 207, 229));

        salaryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        salaryTable.getTableHeader().setBackground(HEADER_BG_COLOR);
        salaryTable.getTableHeader().setForeground(Color.WHITE);
        salaryTable.getTableHeader().setReorderingAllowed(false);
        
        // --- ÁP DỤNG CĂN CHỈNH ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        CurrencyRenderer currencyRenderer = new CurrencyRenderer(); // Renderer cho tiền tệ (căn phải)

        // Áp dụng renderer cho từng cột
        salaryTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);      // Mã NV -> Căn giữa
        // Cột 1 (Tên Nhân Viên) sẽ dùng renderer mặc định (căn trái)
        salaryTable.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer);   // Lương Cơ Bản -> Căn phải
        salaryTable.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);   // Tổng Thưởng -> Căn phải
        salaryTable.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);   // Lương Tháng -> Căn phải

        // Kích thước cột
        salaryTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        salaryTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        salaryTable.setRowSorter(sorter);
    }

    private void styleComboBox(JComboBox<?> cbo) {
        cbo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbo.setPreferredSize(new Dimension(120, 38));
        cbo.setBackground(Color.WHITE);
        cbo.setUI(new BasicComboBoxUI());
    }
    
    private void setDefaultValuesAndLoadInitialData() {
        LocalDate today = LocalDate.now();
        LocalDate lastMonth = today.minusMonths(1);
        monthComboBox.setSelectedIndex(lastMonth.getMonthValue() - 1);
        yearComboBox.setSelectedItem(lastMonth.getYear());
        loadSalaryData(); 
    }

    private void setupListeners() {
        monthComboBox.addActionListener(e -> loadSalaryData());
        yearComboBox.addActionListener(e -> loadSalaryData());
    }
    
    private void loadSalaryData() {
        if (bangLuongDAO == null) return;
        
        int selectedMonth = monthComboBox.getSelectedIndex() + 1;
        int selectedYear = (int) yearComboBox.getSelectedItem();
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Đang tải dữ liệu cho tháng " + selectedMonth + "/" + selectedYear + "...");

        SwingWorker<List<BangLuong>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BangLuong> doInBackground() throws Exception {
                return bangLuongDAO.getBangLuongTheoThang(selectedMonth, selectedYear);
            }

            @Override
            protected void done() {
                try {
                    List<BangLuong> salaryList = get();
                    updateTable(salaryList);
                    statusLabel.setText("Hiển thị " + salaryList.size() + " kết quả cho tháng " + selectedMonth + "/" + selectedYear + ".");
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("Lỗi khi tải dữ liệu.");
                    JOptionPane.showMessageDialog(SalaryReportView.this, "Lỗi khi tải dữ liệu lương: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }
    
    private void updateTable(List<BangLuong> salaryList) {
        tableModel.setRowCount(0);
        BigDecimal totalSalary = BigDecimal.ZERO;
        
        for (BangLuong bl : salaryList) {
            tableModel.addRow(new Object[]{
                    bl.getMaNV(),
                    bl.getTenNV(),
                    bl.getLuongCoBan(),
                    bl.getTongThuong(),
                    bl.getLuongThucLanh()
            });
            if (bl.getLuongThucLanh() != null) {
                totalSalary = totalSalary.add(bl.getLuongThucLanh());
            }
        }
        
        totalSalaryLabel.setText("Tổng quỹ lương: " + CurrencyRenderer.FORMATTER.format(totalSalary));
    }
    
    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        public static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof BigDecimal || value instanceof Number) {
                setText(FORMATTER.format(value));
            } else {
                setText("0 ₫");
            }

            setHorizontalAlignment(SwingConstants.RIGHT); // Căn phải cho tiền tệ
            return this;
        }
    }
}