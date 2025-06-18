package controller.employee;

import model.ChiTietDonHang;
import query.DoiTraQuery;
import query.SPCuTheQuery;
import view.employee.ExchangeProductView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExchangeProductController {
    private final ExchangeProductView view;
    private final ReturnRequestListController parentController;
    private final BigDecimal oldProductPrice; 
    public ExchangeProductController(ExchangeProductView view, ReturnRequestListController parentController, BigDecimal oldProductPrice) {
        this.view = view;
        this.parentController = parentController;
        this.oldProductPrice = oldProductPrice; 
        loadAvailableProducts();
        addEventListeners();
    }

    private void addEventListeners() {
        view.addConfirmButtonListener(e -> handleConfirmExchange());
        view.getAvailableProductsTable().getSelectionModel().addListSelectionListener(this::handleTableSelectionChange);
    }
    
    private void handleTableSelectionChange(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = view.getAvailableProductsTable().getSelectedRow();
            if (selectedRow >= 0) {
                String maSPCuThe = (String) view.getAvailableProductsTable().getValueAt(selectedRow, 0);
                view.getLblNewProductInfo().setText("Mã SP mới: " + maSPCuThe);
                BigDecimal newProductPrice = (BigDecimal) view.getAvailableProductsTable().getValueAt(selectedRow, 3);
                BigDecimal difference = newProductPrice.subtract(oldProductPrice);
                
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
                if (difference.compareTo(BigDecimal.ZERO) >= 0) {
                    view.getLblPriceDifference().setText("Khách hàng cần bù: " + currencyFormatter.format(difference));
                    view.getLblPriceDifference().setForeground(new Color(220, 53, 69)); // Màu đỏ
                } else {
                    view.getLblPriceDifference().setText("Hoàn tiền cho khách: " + currencyFormatter.format(difference.abs()));
                    view.getLblPriceDifference().setForeground(new Color(40, 167, 69)); // Màu xanh
                }
            }
        }
    }

    private void loadAvailableProducts() {
        List<ChiTietDonHang> availableProducts = SPCuTheQuery.getAvailableProductsForExchange();

        String[] columns = {"Mã SP Cụ Thể", "Tên SP", "Màu", "Giá Bán"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
        	private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return BigDecimal.class;
                return String.class;
            }
        };

        if (availableProducts != null) {
            for (ChiTietDonHang sp : availableProducts) {
                model.addRow(new Object[]{sp.getMaSPCuThe(), sp.getTenSP(), sp.getMau(), sp.getGiaXuat()});
            }
        }
        view.getAvailableProductsTable().setModel(model);
    }
    
    private void handleConfirmExchange() {
        int selectedRow = view.getAvailableProductsTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một sản phẩm mới để đổi.", "Chưa chọn sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maSPCuThe_Moi = (String) view.getAvailableProductsTable().getValueAt(selectedRow, 0);
        String maSPCuThe_Cu = view.getMaSPCuTheCu();
        int idDT = view.getIdDT();
        
        String selectedStatus = (String) view.getCbOldProductStatus().getSelectedItem();
        String trangThaiSPCu;
        switch (selectedStatus) {
            case "Nhập lại kho": trangThaiSPCu = "Trong Kho"; break;
            case "Hàng lỗi": trangThaiSPCu = "Loi"; break;
            default: trangThaiSPCu = "Huy bo"; break;
        }
        
        String confirmationMessage = "Xác nhận đổi hàng?\n\n"
            + "- SP cũ (" + maSPCuThe_Cu + ") sẽ được xử lý: " + selectedStatus + "\n"
            + "- SP mới (" + maSPCuThe_Moi + ") sẽ được giao cho khách.\n"
            + "- " + view.getLblPriceDifference().getText() + "\n\n"
            + "Tiếp tục?";
            
        int confirmation = JOptionPane.showConfirmDialog(view, confirmationMessage, "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                boolean success = DoiTraQuery.thucHienDoiTra(idDT, maSPCuThe_Cu, maSPCuThe_Moi, trangThaiSPCu);
                if (success) {
                    JOptionPane.showMessageDialog(view, "Đổi hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    parentController.loadReturnRequests();
                    view.dispose();
                }
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(view, "Đổi hàng thất bại: " + e.getMessage(), "Lỗi Giao Dịch", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}