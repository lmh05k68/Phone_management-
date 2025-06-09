package controller.employee;

import model.DoiTra; // Đảm bảo model DoiTra có các getter tương ứng
import query.DoiTraQuery;
import view.employee.ReturnRequestListView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReturnRequestListController {
    private ReturnRequestListView view;
    private DateTimeFormatter dateTimeFormatter;

    public static final String STATUS_APPROVED = "Da duyet";
    public static final String STATUS_REJECTED = "Tu choi";
    public static final String STATUS_PENDING = "Cho xu ly";
    public ReturnRequestListController(ReturnRequestListView view) {
        System.out.println("CONTROLLER: Constructor ReturnRequestListController bat dau.");
        this.view = view;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("CONTROLLER: Goi loadReturnRequests() tu constructor.");
        loadReturnRequests();

        this.view.addBackButtonListener(e -> {
            System.out.println("CONTROLLER: Nut 'Tro ve' duoc nhan.");
            this.view.dispose();
        });
        this.view.addApproveButtonListener(e -> {
            System.out.println("CONTROLLER: Nut 'Duyet yeu cau' duoc nhan.");
            handleUpdateRequestStatusWrapper(STATUS_APPROVED);
        });
        this.view.addRejectButtonListener(e -> {
            System.out.println("CONTROLLER: Nut 'Tu choi yeu cau' duoc nhan.");
            handleUpdateRequestStatusWrapper(STATUS_REJECTED);
        });
        System.out.println("CONTROLLER: Constructor ReturnRequestListController ket thuc.");
    }

    public void loadReturnRequests() {
        System.out.println("CONTROLLER (loadReturnRequests): Bat dau tai du lieu.");
        List<DoiTra> requestList;
        try {
            requestList = DoiTraQuery.getAllDoiTra();
        } catch (Exception e) {
            System.err.println("CONTROLLER (loadReturnRequests): Loi khi goi DoiTraQuery.getAllDoiTra(): " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Khong the tai danh sach yeu cau doi tra: " + e.getMessage(), "Loi Tai Du Lieu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("CONTROLLER (loadReturnRequests): So luong yeu cau lay tu Query: " + (requestList != null ? requestList.size() : "null"));

        if (view == null || view.getModel() == null) {
            System.err.println("CONTROLLER (loadReturnRequests): View hoac view.model la null. Khong the cap nhat bang.");
            return;
        }
        DefaultTableModel model = view.getModel();
        model.setRowCount(0);

        if (requestList == null || requestList.isEmpty()) {
            System.out.println("CONTROLLER (loadReturnRequests): Khong co yeu cau doi/tra nao de hien thi.");
        } else {
            for (DoiTra dt : requestList) {
                if (dt == null) {
                    System.err.println("CONTROLLER (loadReturnRequests): Gap doi tuong DoiTra null trong danh sach.");
                    continue;
                }
                try {
                    String formattedDate = "N/A";
                    LocalDate ngayDoiTra = dt.getNgayDoiTra();
                    if (ngayDoiTra != null) {
                        formattedDate = ngayDoiTra.format(dateTimeFormatter);
                    }

                    String trangThaiHienThi = dt.getTrangThai();
                    if (trangThaiHienThi == null || trangThaiHienThi.trim().isEmpty()) {
                        trangThaiHienThi = STATUS_PENDING;
                    }

                    model.addRow(new Object[]{
                            dt.getIdDT(),
                            dt.getMaKH(),
                            dt.getMaSP(),
                            dt.getMaDonHang(),
                            formattedDate,
                            dt.getLyDo(),
                            trangThaiHienThi // Đảm bảo giá trị này khớp với STATUS_PENDING nếu là chờ xử lý
                    });
                } catch (Exception e) {
                    System.err.println("CONTROLLER (loadReturnRequests): Loi khi xu ly DoiTra ID: " + dt.getIdDT() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("CONTROLLER (loadReturnRequests): Da them " + model.getRowCount() + " hang vao table model.");
        }
    }

    private void handleUpdateRequestStatusWrapper(String newStatus) {
        System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): Bat dau xu ly cap nhat trang thai sang: '" + newStatus + "'");
        String requestIdStr = view.getSelectedRequestId();
        if (requestIdStr == null) {
            JOptionPane.showMessageDialog(view, "Vui long chon mot yeu cau de " + (newStatus.equals(STATUS_APPROVED) ? "duyet." : "tu choi."), "Chua chon yeu cau", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(requestIdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Ma yeu cau khong hop le: '" + requestIdStr + "'.", "Loi ID", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): Request ID (int) da chon: " + requestId);

        // --- THÊM LOG CHI TIẾT ĐỂ KIỂM TRA currentStatus ---
        String currentStatus = view.getCurrentStatusOfSelectedRequest();
        System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): Trang thai hien tai tu view (RAW): '" + currentStatus + "'");
        if (currentStatus != null) {
            System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): Length of currentStatus: " + currentStatus.length());
            // In ra mã Unicode của từng ký tự để kiểm tra ký tự ẩn/lạ
            System.out.print("CONTROLLER (handleUpdateRequestStatusWrapper): Unicode values of currentStatus: ");
            for (char c : currentStatus.toCharArray()) {
                System.out.print(String.format("\\u%04x", (int) c) + " ");
            }
            System.out.println();
            System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): currentStatus.trim().equalsIgnoreCase(STATUS_PENDING)? " + currentStatus.trim().equalsIgnoreCase(STATUS_PENDING));
        } else {
            System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): currentStatus from view IS NULL.");
        }
        System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): Gia tri cua STATUS_PENDING la: '" + STATUS_PENDING + "'");
        // --- KẾT THÚC LOG CHI TIẾT ---

        // Xử lý trường hợp currentStatus từ view là null hoặc chuỗi rỗng
        // Sử dụng trim() để loại bỏ khoảng trắng thừa trước khi so sánh
        if (currentStatus == null || currentStatus.trim().isEmpty()) {
            currentStatus = STATUS_PENDING; // Gán giá trị mặc định
            System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): currentStatus sau khi xu ly null/empty, duoc gan la: '" + currentStatus + "'");
        } else {
            currentStatus = currentStatus.trim(); // Loại bỏ khoảng trắng thừa từ giá trị lấy từ view
        }


        // Chỉ cho phép cập nhật nếu trạng thái hiện tại là "Chờ xử lý" (sau khi đã trim và xử lý null)
        if (!currentStatus.equalsIgnoreCase(STATUS_PENDING)) {
            System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): DIEU KIEN (!currentStatus.equalsIgnoreCase(STATUS_PENDING)) LA TRUE. Thoat som.");
            System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): So sanh '" + currentStatus + "' voi '" + STATUS_PENDING + "'");
            JOptionPane.showMessageDialog(view, "Yeu cau nay da duoc xu ly (Trang thai: " + currentStatus + "). Khong thể thuc hien hanh dong nay.", "Yeu cau da xu ly", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): Da vuot qua kiem tra trang thai. Chuan bi hien thi confirm dialog.");

        String actionVerb = newStatus.equals(STATUS_APPROVED) ? "DUYET" : "TU CHOI";
        int confirmation = JOptionPane.showConfirmDialog(
                view,
                "Ban co chac muon " + actionVerb + " yeu cau co ID: " + requestId + "?",
                "Xac nhan " + actionVerb.toLowerCase() + " yeu cau",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): Ket qua confirm dialog: " + (confirmation == JOptionPane.YES_OPTION ? "YES" : (confirmation == JOptionPane.NO_OPTION ? "NO" : "CANCEL/CLOSED")));


        if (confirmation == JOptionPane.YES_OPTION) {
            System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): Nguoi dung xac nhan. Goi handleUpdateRequestStatus.");
            handleUpdateRequestStatus(requestId, newStatus);
        } else {
             System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): Nguoi dung huy hanh dong hoac dong dialog.");
        }
        System.out.println("CONTROLLER (handleUpdateRequestStatusWrapper): Ket thuc xu ly.");
    }

    private void handleUpdateRequestStatus(int idDT, String newStatus) {
        System.out.println("CONTROLLER (handleUpdateRequestStatus): Bat dau cap nhat ID: " + idDT + " sang trang thai: '" + newStatus + "'");
        boolean success;
        try {
            success = DoiTraQuery.capNhatTrangThaiDoiTra(idDT, newStatus);
        } catch (Exception e) {
            System.err.println("CONTROLLER (handleUpdateRequestStatus): Loi nghiem trong khi goi DoiTraQuery.capNhatTrangThaiDoiTra cho ID: " + idDT + ": " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Da xay ra loi nghiem trong khi cap nhat trang thai. Vui long thu lai.", "Loi He Thong", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (success) {
            JOptionPane.showMessageDialog(view, "Cap nhat trang thai cho yeu cau ID: " + idDT + " thanh cong!", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("CONTROLLER (handleUpdateRequestStatus): Cap nhat thanh cong. Tai lai danh sach.");
            loadReturnRequests();
        } else {
            JOptionPane.showMessageDialog(view, "Cap nhat trang thai cho yeu cau ID: " + idDT + " that bai. Yeu cau co the khong ton tai hoac da o trang thai nay.", "That bai", JOptionPane.ERROR_MESSAGE);
            System.err.println("CONTROLLER (handleUpdateRequestStatus): Cap nhat that bai cho ID: " + idDT + " (Query tra ve false).");
        }
        System.out.println("CONTROLLER (handleUpdateRequestStatus): Ket thuc cap nhat.");
    }
}