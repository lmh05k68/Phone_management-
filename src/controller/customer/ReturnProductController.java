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
            String maSPCuTheStr = view.getMaSPCuThe().trim();
            String maDonHangStr = view.getMaDonHang().trim();
            String lyDo = view.getLyDo().trim();
            int maKH = view.getMaKHFromView();

            if (maSPCuTheStr.isEmpty() || maDonHangStr.isEmpty() || lyDo.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng điền đầy đủ thông tin.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int maDonHang = Integer.parseInt(maDonHangStr);

            DoiTra doiTra = new DoiTra(maSPCuTheStr, maKH, maDonHang, LocalDate.now(), lyDo);
            Integer idDTGenerated = DoiTraQuery.themYeuCauDoiTraAndGetId(doiTra);

            if (idDTGenerated != null && idDTGenerated > 0) {
                JOptionPane.showMessageDialog(view, "Gửi yêu cầu đổi trả thành công! Mã yêu cầu: " + idDTGenerated, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                view.dispose();
            } else {
                JOptionPane.showMessageDialog(view, "Gửi yêu cầu thất bại. Vui lòng kiểm tra lại Mã Đơn Hàng.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Mã đơn hàng phải là số nguyên.", "Lỗi Định Dạng Số", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            System.err.println("Lỗi trong ReturnProductController: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Đã xảy ra lỗi hệ thống.", "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}