package controller.employee;

import model.DoiTra;
import query.DoiTraQuery;
import query.SPCuTheQuery;
import view.employee.ExchangeProductView;
import view.employee.ReturnRequestListView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Cursor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReturnRequestListController {
    private final ReturnRequestListView view;
    private final DateTimeFormatter dateTimeFormatter;
    private static final String STATUS_PENDING = "Cho xu ly"; 
    private static final String STATUS_APPROVED = "Da phe duyet";
    private static final String STATUS_REJECTED = "Da tu choi";
    public ReturnRequestListController(ReturnRequestListView view) {
        this.view = view;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        addEventListeners();
        loadReturnRequests();
    }

    private void addEventListeners() {
        view.addBackButtonListener(e -> view.dispose());
        view.addApproveButtonListener(e -> handleSimpleStatusUpdate(STATUS_APPROVED, "phê duyệt"));
        view.addRejectButtonListener(e -> handleSimpleStatusUpdate(STATUS_REJECTED, "từ chối"));
        view.addPerformExchangeButtonListener(e -> handlePerformExchange());
    }
    public void loadReturnRequests() {
        view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        new SwingWorker<List<DoiTra>, Void>() {
            @Override
            protected List<DoiTra> doInBackground() {
                return DoiTraQuery.getAllDoiTra();
            }

            @Override
            protected void done() {
                try {
                    List<DoiTra> requestList = get(); // Lấy kết quả từ doInBackground()
                    DefaultTableModel model = view.getModel();
                    model.setRowCount(0); 
                    if (requestList != null) {
                        for (DoiTra dt : requestList) {
                            LocalDate ngayDoiTra = dt.getNgayDoiTra();
                            String formattedDate = (ngayDoiTra != null) ? ngayDoiTra.format(dateTimeFormatter) : "N/A";
                            model.addRow(new Object[]{
                                    dt.getIdDT(), dt.getMaKH(), dt.getMaSPCuThe(), dt.getMaDonHang(),
                                    formattedDate, dt.getLyDo(), dt.getTrangThai()
                            });
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    // Ghi log lỗi để debug nếu cần
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách yêu cầu: " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Luôn đảm bảo trả lại con trỏ chuột bình thường
                    view.setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }

    /**
     * Phương thức chung để xử lý việc "Duyệt" và "Từ chối" - các hành động chỉ cập nhật trạng thái đơn giản.
     * @param newStatus Trạng thái mới để cập nhật.
     * @param actionVerb Động từ mô tả hành động (ví dụ: "phê duyệt", "từ chối") để hiển thị trong hộp thoại.
     */
    private void handleSimpleStatusUpdate(final String newStatus, final String actionVerb) {
        String requestIdStr = view.getSelectedRequestId();
        if (requestIdStr == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một yêu cầu để " + actionVerb + ".", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentStatus = view.getCurrentStatusOfSelectedRequest();
        // Chỉ cho phép hành động nếu trạng thái đang là "Chờ đổi trả"
        if (!STATUS_PENDING.equalsIgnoreCase(currentStatus.trim())) {
            JOptionPane.showMessageDialog(view, "Yêu cầu này đã được xử lý hoặc không ở trạng thái chờ.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn " + actionVerb + " yêu cầu ID: " + requestIdStr + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = DoiTraQuery.capNhatTrangThaiDoiTra(Integer.parseInt(requestIdStr), newStatus);
            if (success) {
                JOptionPane.showMessageDialog(view, "Đã " + actionVerb + " yêu cầu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadReturnRequests(); // Tải lại bảng để cập nhật
            } else {
                JOptionPane.showMessageDialog(view, "Thao tác thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Xử lý việc mở cửa sổ thực hiện đổi/trả sản phẩm.
     */
    private void handlePerformExchange() {
        String requestIdStr = view.getSelectedRequestId();
        if (requestIdStr == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một yêu cầu để thực hiện đổi/trả.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentStatus = view.getCurrentStatusOfSelectedRequest();
        if (!STATUS_APPROVED.equalsIgnoreCase(currentStatus.trim())) {
            JOptionPane.showMessageDialog(view, "Chỉ có thể thực hiện đổi/trả cho các yêu cầu ĐÃ ĐƯỢC PHÊ DUYỆT.", "Thao tác không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idDT = Integer.parseInt(requestIdStr);
        String maSPCuThe_Cu = view.getSelectedProductCode();
        BigDecimal oldProductPrice = SPCuTheQuery.getGiaXuatByMaSPCuThe(maSPCuThe_Cu);
        if (oldProductPrice == null) {
            JOptionPane.showMessageDialog(view,
                "Không thể thực hiện đổi/trả.\n" +
                "Lý do: Không tìm thấy giá trị của sản phẩm cũ (" + maSPCuThe_Cu + ").\n" +
                "Sản phẩm có thể đã được xử lý trước đó.",
                "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ExchangeProductView exchangeView = new ExchangeProductView(idDT, maSPCuThe_Cu, oldProductPrice);
        new ExchangeProductController(exchangeView, this, oldProductPrice);
        exchangeView.setVisible(true);
    }
}