package controller.customer;

import model.DoiTra;
import query.DoiTraQuery;
import view.customer.ReturnProductView;

import javax.swing.*;
import java.time.LocalDate;

public class ReturnProductController {
    private final ReturnProductView view;

    public ReturnProductController(ReturnProductView view) {
        this.view = view;
        this.view.getBtnGuiYeuCau().addActionListener(e -> handleGuiYeuCau());
    }

    private void handleGuiYeuCau() {
        try {
            Integer maDonHang = view.getMaDonHang();
            String maSPCuThe = view.getMaSPCuThe();
            String lyDo = view.getLyDo().trim();
            int maKH = view.getMaKHFromView();

            if (maDonHang == null) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn một đơn hàng.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (maSPCuThe == null || maSPCuThe.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn một sản phẩm cần đổi/trả.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (lyDo.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng điền lý do đổi/trả.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            DoiTra doiTra = new DoiTra(maSPCuThe, maKH, maDonHang, LocalDate.now(), lyDo);
            Integer idDTGenerated = DoiTraQuery.themYeuCauDoiTraAndGetId(doiTra);

            if (idDTGenerated != null && idDTGenerated > 0) {
                JOptionPane.showMessageDialog(view, "Gửi yêu cầu đổi trả thành công! Mã yêu cầu của bạn là: " + idDTGenerated, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                view.dispose();
            } else {
                JOptionPane.showMessageDialog(view, "Gửi yêu cầu thất bại. Có lỗi xảy ra phía hệ thống.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            System.err.println("Lỗi trong ReturnProductController: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Đã xảy ra lỗi hệ thống không xác định.", "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}