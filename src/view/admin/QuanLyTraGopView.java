package view.admin;

import controller.admin.QuanLyTraGopController;
import model.TraGop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class QuanLyTraGopView extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color TITLE_COLOR = new Color(0, 102, 204);
    private static final Color HEADER_BG_COLOR = new Color(52, 73, 94);
    private static final Color HEADER_FG_COLOR = Color.WHITE;
    private static final Color PRIMARY_ACTION_COLOR = new Color(0, 123, 255);
    private static final Color SECONDARY_ACTION_COLOR = new Color(108, 117, 125);

    // --- Components ---
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearchMaHDX;
    private JComboBox<String> cboStatusFilter;

    // --- Controller ---
    private final QuanLyTraGopController controller;

    public QuanLyTraGopView() {
        this.controller = new QuanLyTraGopController(this);
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setBackground(BG_COLOR);
        initUI();
        controller.loadInstallments();
    }

    private void initUI() {
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    private JLabel createTitlePanel() {
        JLabel lblTitle = new JLabel("Danh Sách Phiếu Trả Góp", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(TITLE_COLOR);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        return lblTitle;
    }

    private JPanel createMainContentPanel() {
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setOpaque(false);
        mainContentPanel.add(createControlsPanel(), BorderLayout.NORTH);
        mainContentPanel.add(createTablePanel(), BorderLayout.CENTER);
        return mainContentPanel;
    }

    private JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setOpaque(false);
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Công cụ lọc và tìm kiếm"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        controlsPanel.add(new JLabel("Tìm theo Mã HĐX:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        txtSearchMaHDX = new JTextField(15);
        txtSearchMaHDX.setPreferredSize(new Dimension(txtSearchMaHDX.getPreferredSize().width, 38));
        controlsPanel.add(txtSearchMaHDX, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        JButton btnSearch = createStyledButton("Tìm", PRIMARY_ACTION_COLOR, HEADER_FG_COLOR);
        controlsPanel.add(btnSearch, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.insets.left = 20;
        controlsPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.insets.left = 5;

        gbc.gridx = 4; gbc.gridy = 0;
        cboStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Đang trả góp", "Đã hoàn thành"});
        styleComboBox(cboStatusFilter);
        controlsPanel.add(cboStatusFilter, gbc);

        gbc.gridx = 5; gbc.gridy = 0;
        JButton btnRefresh = createStyledButton("Làm mới", SECONDARY_ACTION_COLOR, HEADER_FG_COLOR);
        controlsPanel.add(btnRefresh, gbc);

        gbc.gridx = 6; gbc.gridy = 0; gbc.weightx = 1.0;
        controlsPanel.add(new JLabel(""), gbc);

        // --- Gắn sự kiện ---
        Runnable searchAction = () -> controller.searchByInvoiceId(txtSearchMaHDX.getText());
        txtSearchMaHDX.addActionListener(e -> searchAction.run());
        btnSearch.addActionListener(e -> searchAction.run());
        cboStatusFilter.addActionListener(e -> controller.filterByStatus((String) cboStatusFilter.getSelectedItem()));
        btnRefresh.addActionListener(e -> {
            txtSearchMaHDX.setText("");
            cboStatusFilter.setSelectedIndex(0);
            controller.loadInstallments();
        });

        return controlsPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"Mã Phiếu", "Mã HĐX", "Tiền Gốc", "Số Tháng", "Lãi Suất", "Ngày Bắt Đầu", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                 switch(columnIndex) {
                    case 0: case 1: case 3: return Integer.class;
                    case 2: case 4: return BigDecimal.class;
                    case 5: return LocalDate.class;
                    case 6: return Boolean.class;
                    default: return Object.class;
                 }
            }
        };
        table = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return scrollPane;
    }

    private void styleTable() {
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(HEADER_BG_COLOR);
        table.getTableHeader().setForeground(HEADER_FG_COLOR);
        table.getColumnModel().getColumn(2).setCellRenderer(new CurrencyRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new PercentageRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new DateRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color foregroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(foregroundColor);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // *** SỬA LỖI TẠI ĐÂY: Dùng border để tạo padding, không dùng setPreferredSize ***
        // Điều này đảm bảo nút sẽ tự động có chiều rộng đủ để chứa chữ
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));

        return btn;
    }

    private void styleComboBox(JComboBox<String> cbo) {
        cbo.setUI(new StyledComboBoxUI());
        cbo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cbo.setBackground(HEADER_BG_COLOR);
        cbo.setForeground(HEADER_FG_COLOR);
        cbo.setPreferredSize(new Dimension(160, 38));
    }

    public void updateTable(List<TraGop> list) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            if (list != null) {
                for (TraGop p : list) {
                    tableModel.addRow(new Object[]{
                            p.getMaPhieuTG(), p.getMaHDX(), p.getTienGoc(),
                            p.getSoThang(), p.getLaiSuat(), p.getNgayBatDau(), p.isDaThanhToan()
                    });
                }
            }
        });
    }

    public void showMessage(String message, boolean isSuccess) {
        JOptionPane.showMessageDialog(this, message, isSuccess ? "Thành Công" : "Lỗi",
                isSuccess ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
    private static class CurrencyRenderer extends javax.swing.table.DefaultTableCellRenderer {
    	private static final long serialVersionUID = 1L;
        private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number) setText(FORMATTER.format(value));
            setHorizontalAlignment(SwingConstants.RIGHT); return this;
        }
    }
    private static class PercentageRenderer extends javax.swing.table.DefaultTableCellRenderer {
    	private static final long serialVersionUID = 1L;
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof BigDecimal) setText(String.format("%.2f %%", ((BigDecimal) value).doubleValue()));
            setHorizontalAlignment(SwingConstants.CENTER); return this;
        }
    }
    private static class DateRenderer extends javax.swing.table.DefaultTableCellRenderer {
    	private static final long serialVersionUID = 1L;
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof LocalDate) setText(((LocalDate) value).format(FORMATTER));
            setHorizontalAlignment(SwingConstants.CENTER); return this;
        }
    }
    private static class StatusRenderer extends javax.swing.table.DefaultTableCellRenderer {
    	private static final long serialVersionUID = 1L;
        private final Color ongoingColor = new Color(204, 102, 0);
        private final Color completedColor = new Color(0, 128, 0);
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Boolean) {
                boolean isCompleted = (Boolean) value;
                setText(isCompleted ? "Đã hoàn thành" : "Đang trả góp");
                c.setForeground(isCompleted ? completedColor : ongoingColor);
                setFont(c.getFont().deriveFont(Font.BOLD));
            }
            setHorizontalAlignment(SwingConstants.CENTER); return c;
        }
    }
    private static class StyledComboBoxUI extends BasicComboBoxUI {
        @Override protected JButton createArrowButton() {
            return new javax.swing.plaf.basic.BasicArrowButton(SwingConstants.SOUTH, HEADER_BG_COLOR, HEADER_BG_COLOR, TITLE_COLOR, HEADER_BG_COLOR);
        }
    }
}