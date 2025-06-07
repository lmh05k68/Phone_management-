package view.admin;

import controller.admin.ManageEmployee;
import model.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
public class ManageEmployeeView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel model;
    private ManageEmployee controller; 
    private JTextField tfSearch;
    private JComboBox<String> cbSearchType;

    public ManageEmployeeView() {
        setTitle("Quản lý nhân viên (Chỉ xem)");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        controller = new ManageEmployee(); 
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        cbSearchType = new JComboBox<>(new String[]{"Tên", "Mã NV", "SĐT"});
        tfSearch = new JTextField(20);
        JButton btnSearch = createStyledButton("Tìm kiếm");
        btnSearch.addActionListener(e -> searchNhanVien());
        
        JButton btnRefresh = createStyledButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());

        topPanel.add(new JLabel("Tìm theo:"));
        topPanel.add(cbSearchType);
        topPanel.add(tfSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnRefresh);
        add(topPanel, BorderLayout.NORTH);
        String[] columns = {"Mã NV", "Tên NV", "Ngày Sinh", "Lương", "SĐT"};
        model = new DefaultTableModel(columns, 0) {
        	private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnBack = createStyledButton("Trở về");
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);
        btnBack.addActionListener(e -> {
            dispose();
        });
        loadData();
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Font nhỏ hơn một chút
        btn.setFocusPainted(false);
        btn.setBackground(new Color(59, 89, 182));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }
    private void loadData() {
        model.setRowCount(0); 
        List<NhanVien> ds = controller.getAllNhanVien(); 
        ds.sort(Comparator.comparing(NhanVien::getMaNV));
        for (NhanVien nv : ds) {
            model.addRow(new Object[]{
                    nv.getMaNV(), nv.getTenNV(), nv.getNgaySinh(),
                    nv.getLuong(), nv.getSoDienThoai()
            });
        }
    }
    private void searchNhanVien() {
        String keyword = tfSearch.getText().trim();
        String type = (String) cbSearchType.getSelectedItem();
        model.setRowCount(0);
        List<NhanVien> ds = controller.searchNhanVien(keyword, type); 
        for (NhanVien nv : ds) {
            model.addRow(new Object[]{
                    nv.getMaNV(), nv.getTenNV(), nv.getNgaySinh(),
                    nv.getLuong(), nv.getSoDienThoai()
            });
        }
    }
}