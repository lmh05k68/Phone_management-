package view.admin;

import controller.admin.ManageEmployee;
import model.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ManageEmployeeView extends JPanel {
    private static final long serialVersionUID = 1L;

    // --- CONSTANTS FOR UI STYLING ---
    private static final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    private static final Color COLOR_PRIMARY = new Color(45, 62, 80); // Dark Blue-Gray
    private static final Color COLOR_TABLE_HEADER = new Color(52, 73, 94); // Slightly lighter than primary
    private static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 26);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 14);

    // --- UI COMPONENTS ---
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    // --- CONTROLLER ---
    private final ManageEmployee controller;

    public ManageEmployeeView() {
        this.controller = new ManageEmployee(this);
        initUI();
        controller.loadEmployees();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setBackground(COLOR_BACKGROUND);

        add(createTitlePanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(createSearchPanel(), BorderLayout.NORTH);
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JLabel createTitlePanel() {
        JLabel lblTitle = new JLabel("Danh Sách Nhân Viên", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        return lblTitle;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Công cụ"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Label
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Tìm kiếm (Tên/SĐT):"), gbc);

        // Search TextField
        // *** THAY ĐỔI 1: Giảm kích thước gợi ý và không cho chiếm không gian thừa ***
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 0; // Không cho co giãn
        gbc.fill = GridBagConstraints.NONE;
        txtSearch = new JTextField(25); // Kích thước gợi ý ngắn lại
        txtSearch.setPreferredSize(new Dimension(txtSearch.getPreferredSize().width, 35));
        searchPanel.add(txtSearch, gbc);

        // Search Button
        gbc.gridx = 2; gbc.gridy = 0;
        JButton btnSearch = createStyledButton("Tìm", COLOR_TABLE_HEADER);
        searchPanel.add(btnSearch, gbc);

        // Clear Button
        gbc.gridx = 3; gbc.gridy = 0;
        JButton btnClearSearch = createStyledButton("Làm mới", COLOR_TABLE_HEADER);
        searchPanel.add(btnClearSearch, gbc);

        // *** THAY ĐỔI 2: Thêm thành phần "đệm" để đẩy mọi thứ về bên trái ***
        gbc.gridx = 4; gbc.gridy = 0;
        gbc.weightx = 1.0; // Cho thành phần này chiếm hết không gian thừa
        gbc.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(new JLabel(""), gbc); // Một label rỗng là đủ

        // Action Listeners
        btnSearch.addActionListener(e -> controller.searchEmployees(txtSearch.getText()));
        txtSearch.addActionListener(e -> btnSearch.doClick());
        btnClearSearch.addActionListener(e -> {
            txtSearch.setText("");
            controller.loadEmployees();
        });

        return searchPanel;
    }

    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(new String[]{"Mã NV", "Tên Nhân Viên", "Ngày Sinh", "Lương", "Số Điện Thoại"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                if (columnIndex == 3) return BigDecimal.class;
                return Object.class;
            }
        };
        table = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private void styleTable() {
        table.setFillsViewportHeight(true);
        table.setFont(FONT_TABLE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(COLOR_TABLE_HEADER);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(2).setCellRenderer(new DateRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer());

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35)); // Set kích thước cố định cho các nút
        return button;
    }

    public void updateTable(List<NhanVien> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (NhanVien nv : list) {
                tableModel.addRow(new Object[]{
                        nv.getMaNV(),
                        nv.getTenNV(),
                        nv.getNgaySinh(),
                        nv.getLuong(),
                        nv.getSoDienThoai()
                });
            }
        }
    }

    public void showMessage(String message, boolean isSuccess) {
        JOptionPane.showMessageDialog(
                this,
                message,
                isSuccess ? "Thành Công" : "Lỗi",
                isSuccess ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );
    }

    private static class DateRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof LocalDate) {
                setText(((LocalDate) value).format(FORMATTER));
            } else if (value == null) {
                setText("N/A");
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }

    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number) {
                setText(FORMATTER.format(value));
            } else if (value == null) {
                setText(FORMATTER.format(0));
            }
            setHorizontalAlignment(SwingConstants.RIGHT);
            return this;
        }
    }
}