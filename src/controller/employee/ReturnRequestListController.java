package controller.employee;

import model.DoiTra;
import query.DoiTraQuery;
import view.employee.ReturnRequestListView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter; 
import java.util.List;

public class ReturnRequestListController {
    private ReturnRequestListView view;
    private DateTimeFormatter dateTimeFormatter; 
    public static final String STATUS_APPROVED = "Đã duyệt";
    public static final String STATUS_REJECTED = "Từ chối";
    public static final String STATUS_PENDING = "Chờ xử lý";

    public ReturnRequestListController(ReturnRequestListView view) {
        System.out.println("CONTROLLER: Constructor ReturnRequestListController bắt đầu.");
        this.view = view;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Chỉ ngày, không cần giờ

        System.out.println("CONTROLLER: Gọi loadReturnRequests() từ constructor.");
        loadReturnRequests();

        this.view.addBackButtonListener(e -> {
            System.out.println("CONTROLLER: Nút 'Trở về' được nhấn.");
            this.view.dispose();
        });
        this.view.addApproveButtonListener(e -> {
            System.out.println("CONTROLLER: Nút 'Duyệt yêu cầu' được nhấn.");
            handleUpdateRequestStatusWrapper(STATUS_APPROVED);
        });
        this.view.addRejectButtonListener(e -> {
            System.out.println("CONTROLLER: Nút 'Từ chối yêu cầu' được nhấn.");
            handleUpdateRequestStatusWrapper(STATUS_REJECTED);
        });
        System.out.println("CONTROLLER: Constructor ReturnRequestListController kết thúc.");
    }

    public void loadReturnRequests() {
        System.out.println("CONTROLLER_loadReturnRequests: Bắt đầu tải dữ liệu.");
        List<DoiTra> requestList;
        try {
            requestList = DoiTraQuery.getAllDoiTra(); // Gọi phương thức static
        } catch (Exception e) {
            System.err.println("CONTROLLER_loadReturnRequests: Lỗi nghiêm trọng khi gọi DoiTraQuery.getAllDoiTra(): " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Không thể tải danh sách yêu cầu đổi trả do lỗi truy vấn CSDL.", "Lỗi tải dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("CONTROLLER_loadReturnRequests: Số lượng yêu cầu lấy từ Query: " + (requestList != null ? requestList.size() : "null"));

        if (view == null || view.getModel() == null) { // Giả sử View có getModel()
            System.err.println("CONTROLLER_loadReturnRequests: View hoặc view.model là null. Không thể cập nhật bảng.");
            return;
        }
        DefaultTableModel model = view.getModel(); // Giả sử View có getModel()
        model.setRowCount(0);

        if (requestList == null || requestList.isEmpty()) {
            System.out.println("CONTROLLER_loadReturnRequests: Không có yêu cầu đổi/trả nào để hiển thị.");
        } else {
            for (DoiTra dt : requestList) {
                if (dt == null) {
                    System.err.println("CONTROLLER_loadReturnRequests: Gặp đối tượng DoiTra null trong danh sách.");
                    continue;
                }
                try {
                    String formattedDate = "N/A";
                    if (dt.getNgayDoiTra() != null) { // NgayDoiTra là LocalDate
                        formattedDate = dt.getNgayDoiTra().format(dateTimeFormatter);
                    }

                    String trangThaiHienThi = dt.getTrangThai();
                    if (trangThaiHienThi == null || trangThaiHienThi.trim().isEmpty()) {
                        trangThaiHienThi = STATUS_PENDING;
                    }

                    model.addRow(new Object[]{
                            dt.getIdDT(),        // int
                            dt.getMaKH(),        // int
                            dt.getMaSP(),        // int
                            dt.getMaDonHang(),   // int
                            formattedDate,
                            dt.getLyDo(),
                            trangThaiHienThi
                    });
                } catch (Exception e) {
                    System.err.println("CONTROLLER_loadReturnRequests: Lỗi khi xử lý DoiTra ID: " + dt.getIdDT() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("CONTROLLER_loadReturnRequests: Đã thêm " + model.getRowCount() + " hàng vào table model.");
        }
    }

    private void handleUpdateRequestStatusWrapper(String newStatus) {
        System.out.println("CONTROLLER_handleUpdateRequestStatusWrapper: Bắt đầu xử lý cập nhật trạng thái sang: " + newStatus);
        String requestIdStr = view.getSelectedRequestId(); // View trả về String ID
        if (requestIdStr == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một yêu cầu để " + (newStatus.equals(STATUS_APPROVED) ? "duyệt." : "từ chối."), "Chưa chọn yêu cầu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(requestIdStr); // Chuyển ID sang int
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Mã yêu cầu không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("CONTROLLER_handleUpdateRequestStatusWrapper: Request ID (int) đã chọn: " + requestId);

        String currentStatus = view.getCurrentStatusOfSelectedRequest();
        System.out.println("CONTROLLER_handleUpdateRequestStatusWrapper: Trạng thái hiện tại từ view: " + currentStatus);

        if (currentStatus == null) {
            currentStatus = STATUS_PENDING;
            System.out.println("CONTROLLER_handleUpdateRequestStatusWrapper: Trạng thái hiện tại từ view là null, giả định là: " + currentStatus);
        }

        if (!currentStatus.equalsIgnoreCase(STATUS_PENDING)) {
            JOptionPane.showMessageDialog(view, "Yêu cầu này đã được xử lý (Trạng thái: " + currentStatus + "). Không thể thay đổi.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc muốn " + (newStatus.equals(STATUS_APPROVED) ? "DUYỆT" : "TỪ CHỐI") + " yêu cầu ID: " + requestId + "?",
                "Xác nhận hành động",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            System.out.println("CONTROLLER_handleUpdateRequestStatusWrapper: Người dùng xác nhận. Gọi handleUpdateRequestStatus.");
            handleUpdateRequestStatus(requestId, newStatus); // Truyền int requestId
        } else {
             System.out.println("CONTROLLER_handleUpdateRequestStatusWrapper: Người dùng hủy hành động.");
        }
    }

    // Sửa: nhận int idDT
    private void handleUpdateRequestStatus(int idDT, String newStatus) {
        System.out.println("CONTROLLER_handleUpdateRequestStatus: Cập nhật ID: " + idDT + " sang trạng thái: " + newStatus);
        // Gọi phương thức static từ DoiTraQuery, truyền int idDT
        boolean success = DoiTraQuery.capNhatTrangThaiDoiTra(idDT, newStatus);
        if (success) {
            JOptionPane.showMessageDialog(view, "Cập nhật trạng thái cho yêu cầu ID: " + idDT + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("CONTROLLER_handleUpdateRequestStatus: Cập nhật thành công. Tải lại danh sách.");
            loadReturnRequests();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật trạng thái cho yêu cầu ID: " + idDT + " thất bại.", "Thất bại", JOptionPane.ERROR_MESSAGE);
             System.err.println("CONTROLLER_handleUpdateRequestStatus: Cập nhật thất bại cho ID: " + idDT);
        }
    }
}