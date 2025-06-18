package view.admin;

import controller.admin.QuanLyTraGopController;
import model.TraGop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class QuanLyTraGopView extends JPanel {
    private static final long serialVersionUID = 1L;

    // --- UI Constants ---
    private static final Color BG_COLOR = new Color(248, 249, 250);
    private static final Color TITLE_COLOR = new Color(0, 102, 204);
    private static final Color HEADER_BG_COLOR = new Color(52, 73, 94);
    private static final Color HEADER_FG_COLOR = Color.WHITE;
    private static final Color ALT_ROW_COLOR = new Color(242, 242, 242);
    private static final Color PRIMARY_ACTION_COLOR = new Color(0, 123, 255);
    private static final Color SECONDARY_ACTION_COLOR = new Color(108, 117, 125);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 14);

    // --- Components ---
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearchMaHDX;
    private JComboBox<String> cboStatusFilter;

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
        lblTitle.setFont(FONT_TITLE);
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

        gbc.gridx = 0; gbc.gridy = 0; controlsPanel.add(new JLabel("Tìm theo Mã HĐX:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        txtSearchMaHDX = new JTextField(15);
        txtSearchMaHDX.setPreferredSize(new Dimension(txtSearchMaHDX.getPreferredSize().width, 38));
        controlsPanel.add(txtSearchMaHDX, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        JButton btnSearch = createStyledButton("Tìm", PRIMARY_ACTION_COLOR, HEADER_FG_COLOR);
        controlsPanel.add(btnSearch, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.insets.left = 20;
        controlsPanel.add(new JLabel("Trạng thái thanh toán:"), gbc);
        gbc.insets.left = 5;

        gbc.gridx = 4; gbc.gridy = 0;
        cboStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Đang trả góp", "Đã hoàn thành"});
        styleComboBox(cboStatusFilter);
        controlsPanel.add(cboStatusFilter, gbc);

        gbc.gridx = 5; gbc.gridy = 0;
        JButton btnRefresh = createStyledButton("Làm mới", SECONDARY_ACTION_COLOR, HEADER_FG_COLOR);
        controlsPanel.add(btnRefresh, gbc);

        gbc.gridx = 6; gbc.gridy = 0; gbc.weightx = 1.0;
        controlsPanel.add(new JLabel(""), gbc); // Spacer

        // --- Event Listeners ---
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
        String[] columnNames = {"Mã Phiếu", "Mã HĐX", "Tiền Gốc", "Số Tháng", "Lãi Suất", "Ngày Bắt Đầu", "Trả Hàng Tháng", "Ngày Đáo Hạn", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                 switch(columnIndex) {
                    case 0: case 1: case 3: return Integer.class;
                    case 2: case 4: case 6: return BigDecimal.class;
                    case 5: case 7: return LocalDate.class;
                    case 8: return Boolean.class;
                    default: return Object.class;
                 }
            }
        };
        
        table = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ALT_ROW_COLOR);
                }
                return c;
            }
        };
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return scrollPane;
    }

    private void styleTable() {
        table.setFillsViewportHeight(true);
        table.setFont(FONT_TABLE);
        table.setRowHeight(32);
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(HEADER_BG_COLOR);
        table.getTableHeader().setForeground(HEADER_FG_COLOR);
        table.setAutoCreateRowSorter(true);

        CenteredRenderer centeredRenderer = new CenteredRenderer();
        table.setDefaultRenderer(Integer.class, centeredRenderer);
        table.setDefaultRenderer(String.class, centeredRenderer);
        
        table.setDefaultRenderer(BigDecimal.class, new CurrencyRenderer());
        table.setDefaultRenderer(LocalDate.class, new DateRenderer());
        table.setDefaultRenderer(Boolean.class, new StatusRenderer());
        
        table.getColumnModel().getColumn(4).setCellRenderer(new PercentageRenderer());
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color foregroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(foregroundColor);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        return btn;
    }

    private void styleComboBox(JComboBox<String> cbo) {
        // *** SỬA LỖI 1: Thay thế StyledComboBoxUI bằng cách styling chuẩn hơn ***
        cbo.setFont(FONT_BUTTON);
        cbo.setBackground(Color.WHITE); // Nền trắng cho dễ nhìn
        cbo.setForeground(Color.BLACK);
        cbo.setPreferredSize(new Dimension(180, 38));
        cbo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    public void updateTable(List<TraGop> list) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            if (list != null) {
                for (TraGop p : list) {
                    tableModel.addRow(new Object[]{
                            p.getMaPhieuTG(), p.getMaHDX(), p.getTienGoc(),
                            p.getSoThang(), p.getLaiSuat(), p.getNgayBatDau(),
                            p.getTienTraHangThang(), p.getNgayDaoHan(), p.isDaThanhToan()
                    });
                }
            }
        });
    }

    public void showMessage(String message, boolean isSuccess) {
        JOptionPane.showMessageDialog(this, message, isSuccess ? "Thành Công" : "Lỗi",
                isSuccess ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
    private static class CenteredRenderer extends DefaultTableCellRenderer { /* ... */ }
    private static class CurrencyRenderer extends DefaultTableCellRenderer { /* ... */ }
    private static class PercentageRenderer extends CenteredRenderer { /* ... */ }
    private static class DateRenderer extends CenteredRenderer { /* ... */ }
    private static class StatusRenderer extends CenteredRenderer { /* ... */ }
}