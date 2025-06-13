package controller.employee;
import model.DoiTra;
import query.DoiTraQuery;
import view.employee.ReturnRequestListView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Cursor;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
public class ReturnRequestListController {
private final ReturnRequestListView view;
private final DateTimeFormatter dateTimeFormatter;
public static final String TRANG_THAI_CHO_XU_LY = "Cho xu ly";
public static final String TRANG_THAI_DA_DUYET = "Da duyet"; // Giữ nguyên không dấu cho nhất quán
public static final String TRANG_THAI_TU_CHOI = "Tu choi";  // Giữ nguyên không dấu cho nhất quán
public ReturnRequestListController(ReturnRequestListView view) {
    this.view = view;
    this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    this.addEventListeners();
    this.loadReturnRequests();
}

private void addEventListeners() {
    this.view.addBackButtonListener(e -> this.view.dispose());
    this.view.addApproveButtonListener(e -> handleUpdateRequest(TRANG_THAI_DA_DUYET));
    this.view.addRejectButtonListener(e -> handleUpdateRequest(TRANG_THAI_TU_CHOI));
}

public void loadReturnRequests() {
    view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    new SwingWorker<List<DoiTra>, Void>() {
        @Override
        protected List<DoiTra> doInBackground() { return DoiTraQuery.getAllDoiTra(); }

        @Override
        protected void done() {
            try {
                List<DoiTra> requestList = get();
                DefaultTableModel model = view.getModel();
                model.setRowCount(0);
                if (requestList != null) {
                    for (DoiTra dt : requestList) {
                        String formattedDate = (dt.getNgayDoiTra() != null) ? dt.getNgayDoiTra().format(dateTimeFormatter) : "N/A";
                        model.addRow(new Object[]{dt.getIdDT(), dt.getMaKH(), dt.getMaSPCuThe(), dt.getMaDonHang(), formattedDate, dt.getLyDo(), dt.getTrangThai()});
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách yêu cầu.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            } finally {
                view.setCursor(Cursor.getDefaultCursor());
            }
        }
    }.execute();
}

private void handleUpdateRequest(String newStatus) {
    String requestIdStr = view.getSelectedRequestId();
    if (requestIdStr == null) {
        JOptionPane.showMessageDialog(view, "Vui lòng chọn một yêu cầu để xử lý.", "Chưa chọn yêu cầu", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String currentStatus = view.getCurrentStatusOfSelectedRequest();
    // Bây giờ phép so sánh này sẽ trả về ĐÚNG
    if (currentStatus == null || !TRANG_THAI_CHO_XU_LY.equalsIgnoreCase(currentStatus.trim())) {
        JOptionPane.showMessageDialog(view, "Yêu cầu này đã được xử lý (Trạng thái: " + currentStatus + ").", "Yêu cầu đã xử lý", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    String productCode = view.getSelectedProductCode();
    if (productCode == null || productCode.isEmpty()) {
         JOptionPane.showMessageDialog(view, "Không thể xác định mã sản phẩm của yêu cầu đã chọn.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String actionVerb = newStatus.equals(TRANG_THAI_DA_DUYET) ? "DUYỆT" : "TỪ CHỐI";
    int confirmation = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn " + actionVerb + " yêu cầu có ID: " + requestIdStr + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);

    if (confirmation == JOptionPane.YES_OPTION) {
        processRequestUpdate(Integer.parseInt(requestIdStr), productCode, newStatus);
    }
}

private void processRequestUpdate(int idDT, String maSPCuThe, String newStatus) {
    view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    new SwingWorker<Boolean, Void>() {
        @Override
        protected Boolean doInBackground() {
            return DoiTraQuery.processReturnRequest(idDT, maSPCuThe, newStatus);
        }

        @Override
        protected void done() {
            try {
                boolean success = get();
                if (success) {
                    JOptionPane.showMessageDialog(view, "Xử lý yêu cầu ID " + idDT + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadReturnRequests(); // Tải lại bảng để thấy trạng thái mới
                } else {
                    JOptionPane.showMessageDialog(view, "Xử lý yêu cầu thất bại. Vui lòng kiểm tra log hệ thống.", "Thất bại", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Đã xảy ra lỗi nghiêm trọng khi xử lý yêu cầu.", "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            } finally {
                view.setCursor(Cursor.getDefaultCursor());
            }
        }
    }.execute();
}
} 