package controller.admin;

import model.TraGop;
import query.TraGopQuery;
import view.admin.QuanLyTraGopView;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QuanLyTraGopController {
    private final QuanLyTraGopView view;

    public QuanLyTraGopController(QuanLyTraGopView view) {
        this.view = view;
    }
    public void loadInstallments() {
        new SwingWorker<List<TraGop>, Void>() {
            @Override
            protected List<TraGop> doInBackground() {
                TraGopQuery.updateCompletedStatus();
                return TraGopQuery.getFiltered(null, null);
            }
            @Override
            protected void done() {
                try {
                    view.updateTable(get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    view.showMessage("Lỗi khi tải dữ liệu trả góp.", false);
                }
            }
        }.execute();
    }

    /**
     * Lọc danh sách theo trạng thái đã chọn.
     */
    public void filterByStatus(String status) {
        Boolean isCompleted;
        switch (status) {
            case "Đã hoàn thành":
                isCompleted = true;
                break;
            case "Đang trả góp":
                isCompleted = false;
                break;
            default: // "Tất cả"
                isCompleted = null;
                break;
        }

        new SwingWorker<List<TraGop>, Void>() {
            @Override
            protected List<TraGop> doInBackground() {
                return TraGopQuery.getFiltered(null, isCompleted);
            }
            @Override
            protected void done() {
                try {
                    view.updateTable(get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    view.showMessage("Lỗi khi lọc dữ liệu.", false);
                }
            }
        }.execute();
    }

    /**
     * Tìm kiếm phiếu trả góp theo Mã Hóa Đơn.
     */
    public void searchByInvoiceId(String maHDXStr) {
        if (maHDXStr == null || maHDXStr.trim().isEmpty()) {
            loadInstallments(); // Nếu trống thì tải lại tất cả
            return;
        }
        
        try {
            int maHDX = Integer.parseInt(maHDXStr.trim());
            new SwingWorker<List<TraGop>, Void>() {
                @Override
                protected List<TraGop> doInBackground() {
                    // Gọi truy vấn trực tiếp với MaHDX
                    return TraGopQuery.getFiltered(maHDX, null);
                }
                @Override
                protected void done() {
                    try {
                        List<TraGop> result = get();
                        view.updateTable(result);
                        if (result.isEmpty()) {
                            view.showMessage("Không tìm thấy phiếu trả góp nào cho Mã HĐX: " + maHDX, true);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        view.showMessage("Lỗi khi tìm kiếm.", false);
                    }
                }
            }.execute();

        } catch (NumberFormatException e) {
            view.showMessage("Mã hóa đơn phải là một số.", false);
        }
    }
}