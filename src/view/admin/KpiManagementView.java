package view.admin;

import controller.admin.KpiController;
import model.KPI;

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

public class KpiManagementView extends JPanel {
    private static final long serialVersionUID = 1L;

    // --- UI Constants ---
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color TITLE_COLOR = new Color(0, 102, 204);
    private static final Color PRIMARY_ACTION_COLOR = new Color(0, 123, 255);
    private static final Color DANGER_ACTION_COLOR = new Color(220, 53, 69);
    private static final Color HEADER_BG_COLOR = new Color(52, 73, 94);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    // --- UI Components ---
    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private JButton searchButton;
    private JButton updateButton;
    private JTable kpiTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public KpiManagementView() {
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setBackground(BG_COLOR);

        initUI();
        new KpiController(this);
        searchButton.doClick(); // Tải dữ liệu ban đầu
    }

    private void initUI() {
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }

    private JLabel createTitlePanel() {
        JLabel titleLabel = new JLabel("QUẢN LÝ KPI NHÂN VIÊN");
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
        filterPanel.setBorder(BorderFactory.createTitledBorder("Công cụ"));

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

        gbc.gridx = 4; gbc.gridy = 0; gbc.insets.left = 20;
        searchButton = createStyledButton("Xem Kết Quả", PRIMARY_ACTION_COLOR);
        filterPanel.add(searchButton, gbc);

        gbc.gridx = 5; gbc.gridy = 0;
        updateButton = createStyledButton("Tính Lại KPI", DANGER_ACTION_COLOR);
        filterPanel.add(updateButton, gbc);

        // Thành phần đệm để đẩy các nút về bên trái
        gbc.gridx = 6; gbc.gridy = 0; gbc.weightx = 1.0;
        filterPanel.add(new JLabel(""), gbc);

        return filterPanel;
    }
    
    private JComboBox<String> createMonthComboBox() {
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = "Tháng " + (i + 1);
        }
        JComboBox<String> cbo = new JComboBox<>(months);
        cbo.setSelectedIndex(LocalDate.now().getMonthValue() - 1); // Chọn tháng hiện tại
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
        String[] columnNames = {"ID KPI", "Mã NV", "Tên Nhân Viên", "Tháng", "Năm", "Tổng Doanh Số", "Thưởng Mốc", "Thưởng Hạng", "Tổng Thưởng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                if (col == 0 || col == 1 || col == 3 || col == 4) return Integer.class;
                if (col == 5 || col == 6 || col == 7 || col == 8) return BigDecimal.class;
                return String.class;
            }
        };
        kpiTable = new JTable(tableModel);
        styleTable();
        return new JScrollPane(kpiTable);
    }
    
    private JLabel createStatusPanel() {
        statusLabel = new JLabel("Chọn tháng/năm và nhấn 'Xem Kết Quả'.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setBorder(new EmptyBorder(10, 5, 0, 5));
        return statusLabel;
    }

    private void styleTable() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        kpiTable.setFillsViewportHeight(true);
        kpiTable.setRowHeight(30);
        kpiTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        kpiTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        kpiTable.getTableHeader().setBackground(HEADER_BG_COLOR);
        kpiTable.getTableHeader().setForeground(Color.WHITE);
        kpiTable.getTableHeader().setReorderingAllowed(false);

        kpiTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        kpiTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        kpiTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        kpiTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        kpiTable.getColumnModel().getColumn(5).setCellRenderer(new CurrencyRenderer());
        kpiTable.getColumnModel().getColumn(6).setCellRenderer(new CurrencyRenderer());
        kpiTable.getColumnModel().getColumn(7).setCellRenderer(new CurrencyRenderer());
        kpiTable.getColumnModel().getColumn(8).setCellRenderer(new CurrencyRenderer());

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        kpiTable.setRowSorter(sorter);
    }
    
    private void styleComboBox(JComboBox<?> cbo) {
        cbo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbo.setPreferredSize(new Dimension(120, 38));
        cbo.setBackground(Color.WHITE);
        cbo.setUI(new BasicComboBoxUI()); // UI phẳng hơn
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 38));
        return btn;
    }

    // --- Methods for Controller interaction ---
    public int getSelectedMonth() { return monthComboBox.getSelectedIndex() + 1; }
    public int getSelectedYear() { return (int) yearComboBox.getSelectedItem(); }
    public JButton getSearchButton() { return searchButton; }
    public JButton getUpdateButton() { return updateButton; }

    public void updateTable(List<KPI> kpiList) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            if (kpiList != null) {
                for (KPI kpi : kpiList) {
                    tableModel.addRow(new Object[]{
                        kpi.getIdKpi(), kpi.getManv(), kpi.getHoTen(),
                        kpi.getThang(), kpi.getNam(), kpi.getTongDoanhSo(),
                        kpi.getThuongMuc(), kpi.getThuongRank(), kpi.getTongThuong()
                    });
                }
            }
        });
    }

    public void updateStatusLabel(String statusText) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(statusText));
    }

    public int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    public void showMessage(String message, boolean isSuccess) {
        JOptionPane.showMessageDialog(this, message, isSuccess ? "Thành Công" : "Lỗi", isSuccess ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
    
    // --- Custom Table Cell Renderer for Currency ---
    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof BigDecimal) {
                setText(FORMATTER.format(value));
            }
            setHorizontalAlignment(SwingConstants.RIGHT);
            return this;
        }
    }
}