package controller.admin;

import query.KpiQuery;
import model.KPI;
import view.admin.KpiManagementView;

import javax.swing.*;
import java.awt.Cursor;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class KpiController {
    
    private final KpiManagementView view;

    public KpiController(KpiManagementView view) {
        this.view = view;
        initController();
    }

    private void initController() {
        view.getSearchButton().addActionListener(e -> searchKpi());
        view.getUpdateButton().addActionListener(e -> finalizeKpi());
    }

    /**
     * Tải/Tìm kiếm KPI dựa trên tháng và năm đã chọn từ JComboBox.
     */
    public void searchKpi() {
        // *** SỬA LỖI TẠI ĐÂY: Dùng phương thức getter mới từ View ***
        int month = view.getSelectedMonth();
        int year = view.getSelectedYear();
        
        view.updateStatusLabel(String.format("Đang tải dữ liệu cho tháng %02d/%d...", month, year));
        view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        view.getSearchButton().setEnabled(false);
        view.getUpdateButton().setEnabled(false);

        SwingWorker<List<KPI>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<KPI> doInBackground() throws Exception {
                // Giả sử phương thức này tồn tại trong KpiQuery
                return KpiQuery.getKpiByMonthYear(month, year);
            }

            @Override
            protected void done() {
                try {
                    List<KPI> results = get();
                    view.updateTable(results);
                    view.updateStatusLabel(String.format("Hiển thị %d kết quả cho tháng %02d/%d.", results.size(), month, year));
                } catch (InterruptedException | ExecutionException e) {
                    view.updateStatusLabel("Lỗi khi tải dữ liệu.");
                    view.showMessage("Lỗi khi truy vấn CSDL: " + e.getMessage(), false);
                    e.printStackTrace();
                } finally {
                    view.setCursor(Cursor.getDefaultCursor());
                    view.getSearchButton().setEnabled(true);
                    view.getUpdateButton().setEnabled(true);
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Thực hiện chốt và tính toán thưởng xếp hạng KPI cho tháng/năm đã chọn.
     */
    private void finalizeKpi() {
        // *** SỬA LỖI TẠI ĐÂY: Dùng phương thức getter mới từ View ***
        int month = view.getSelectedMonth();
        int year = view.getSelectedYear();
        
        int choice = view.showConfirmDialog(
            "Bạn có chắc chắn muốn chốt và tính thưởng xếp hạng KPI cho tháng " + month + "/" + year + "?\n" +
            "Hành động này sẽ ghi đè thưởng hạng cũ (nếu có).",
            "Xác nhận chốt KPI"
        );

        if (choice == JOptionPane.YES_OPTION) {
            view.updateStatusLabel(String.format("Đang thực hiện chốt KPI cho tháng %02d/%d...", month, year));
            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            view.getSearchButton().setEnabled(false);
            view.getUpdateButton().setEnabled(false);
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    // Giả sử phương thức này tồn tại trong KpiQuery
                    return KpiQuery.finalizeKpiRank(month, year);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            view.showMessage("Chốt KPI thành công! Đang tải lại dữ liệu...", true);
                            // Không cần kích hoạt lại nút ở đây, vì searchKpi() sẽ làm điều đó
                            searchKpi(); // Tự động tải lại dữ liệu sau khi chốt thành công
                        } else {
                            view.showMessage("Chốt KPI thất bại. Vui lòng kiểm tra log để biết chi tiết.", false);
                            // Nếu thất bại, phải kích hoạt lại nút
                            view.setCursor(Cursor.getDefaultCursor());
                            view.getSearchButton().setEnabled(true);
                            view.getUpdateButton().setEnabled(true);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        view.updateStatusLabel("Lỗi khi chốt KPI.");
                        view.showMessage("Lỗi khi thực thi tác vụ chốt KPI: " + e.getMessage(), false);
                        e.printStackTrace();
                        // Nếu có lỗi, cũng phải kích hoạt lại nút
                        view.setCursor(Cursor.getDefaultCursor());
                        view.getSearchButton().setEnabled(true);
                        view.getUpdateButton().setEnabled(true);
                    }
                }
            };
            worker.execute();
        }
    }
}