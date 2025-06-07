package controller.employee;

import model.DoiTra;
import query.DoiTraQuery;
import view.employee.ReturnRequestListView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.List;
// import java.util.TimeZone; // Bỏ comment nếu muốn dùng TimeZone

public class ReturnRequestListController {
    private ReturnRequestListView view;
    private SimpleDateFormat dateFormat;

    public static final String STATUS_APPROVED = "Đã duyệt";
    public static final String STATUS_REJECTED = "Từ chối";
    public static final String STATUS_PENDING = "Chờ xử lý";

    public ReturnRequestListController(ReturnRequestListView view) {
        System.out.println("CONTROLLER: Constructor ReturnRequestListController bắt đầu.");
        this.view = view;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        // this.dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")); // Ví dụ TimeZone

        System.out.println("CONTROLLER: Gọi loadReturnRequests() từ constructor.");
        loadReturnRequests();

        // Gán listeners
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
            requestList = DoiTraQuery.getAllDoiTra();
        } catch (Exception e) {
            System.err.println("CONTROLLER_loadReturnRequests: Lỗi nghiêm trọng khi gọi DoiTraQuery.getAllDoiTra(): " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Không thể tải danh sách yêu cầu đổi trả do lỗi truy vấn CSDL.", "Lỗi tải dữ liệu", JOptionPane.ERROR_MESSAGE);
            return; // Không tiếp tục nếu không lấy được dữ liệu
        }

        System.out.println("CONTROLLER_loadReturnRequests: Số lượng yêu cầu lấy từ Query: " + (requestList != null ? requestList.size() : "null"));

        if (view == null || view.model == null) {
            System.err.println("CONTROLLER_loadReturnRequests: View hoặc view.model là null. Không thể cập nhật bảng.");
            return;
        }
        DefaultTableModel model = view.model;
        model.setRowCount(0); // Xóa dữ liệu cũ

        if (requestList == null || requestList.isEmpty()) {
            System.out.println("CONTROLLER_loadReturnRequests: Không có yêu cầu đổi/trả nào để hiển thị.");
            // Có thể thêm một hàng thông báo vào bảng nếu muốn
            // model.addRow(new Object[]{"Không có dữ liệu", "", "", "", "", "", ""});
        } else {
            for (DoiTra dt : requestList) {
                if (dt == null) {
                    System.err.println("CONTROLLER_loadReturnRequests: Gặp đối tượng DoiTra null trong danh sách.");
                    continue; // Bỏ qua đối tượng null
                }
                try {
                    String formattedDate = "N/A";
                    if (dt.getNgayDoiTra() != null) {
                        formattedDate = dateFormat.format(dt.getNgayDoiTra());
                    }

                    String trangThaiHienThi = dt.getTrangThai();
                    if (trangThaiHienThi == null || trangThaiHienThi.trim().isEmpty()) {
                        trangThaiHienThi = STATUS_PENDING; // Nếu null hoặc rỗng, coi là Chờ xử lý
                    }

                    System.out.println("CONTROLLER_loadReturnRequests: Đang thêm hàng: ID=" + dt.getIdDT() + ", Ngày=" + formattedDate + ", TrạngTháiThực=" + dt.getTrangThai() + ", TrạngTháiHiểnThị=" + trangThaiHienThi);
                    model.addRow(new Object[]{
                            dt.getIdDT(),
                            dt.getMaKH(),
                            dt.getMaSP(),
                            dt.getMaDonHang(),
                            formattedDate,
                            dt.getLyDo(),
                            trangThaiHienThi
                    });
                } catch (Exception e) {
                    System.err.println("CONTROLLER_loadReturnRequests: Lỗi khi xử lý DoiTra ID: " + dt.getIdDT() + " hoặc thêm vào model: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("CONTROLLER_loadReturnRequests: Đã thêm " + model.getRowCount() + " hàng vào table model.");
        }
    }

    private void handleUpdateRequestStatusWrapper(String newStatus) {
        System.out.println("CONTROLLER_handleUpdateRequestStatusWrapper: Bắt đầu xử lý cập nhật trạng thái sang: " + newStatus);
        String requestId = view.getSelectedRequestId();
        if (requestId == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một yêu cầu để " + (newStatus.equals(STATUS_APPROVED) ? "duyệt." : "từ chối."), "Chưa chọn yêu cầu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        System.out.println("CONTROLLER_handleUpdateRequestStatusWrapper: Request ID đã chọn: " + requestId);

        String currentStatus = view.getCurrentStatusOfSelectedRequest();
         System.out.println("CONTROLLER_handleUpdateRequestStatusWrapper: Trạng thái hiện tại từ view: " + currentStatus);

        if (currentStatus == null) {
            // Nếu trạng thái từ view là null (có thể do lỗi nào đó), thử giả định là PENDING nếu hợp lý
            // Hoặc hiển thị lỗi và không cho phép cập nhật
            currentStatus = STATUS_PENDING; // Hoặc hiển thị lỗi
             System.out.println("CONTROLLER_handleUpdateRequestStatusWrapper: Trạng thái hiện tại từ view là null, giả định là: " + currentStatus);
        }
        
        // Kiểm tra xem yêu cầu có thể được cập nhật không
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
            handleUpdateRequestStatus(requestId, newStatus);
        } else {
             System.out.println("CONTROLLER_handleUpdateRequestStatusWrapper: Người dùng hủy hành động.");
        }
    }

    private void handleUpdateRequestStatus(String idDT, String newStatus) {
        System.out.println("CONTROLLER_handleUpdateRequestStatus: Cập nhật ID: " + idDT + " sang trạng thái: " + newStatus);
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