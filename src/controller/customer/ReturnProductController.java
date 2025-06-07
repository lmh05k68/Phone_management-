package controller.customer;

import model.DoiTra;
import query.DoiTraQuery;
import view.customer.ReturnProductView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date; // Sử dụng java.util.Date

public class ReturnProductController {
    private ReturnProductView view;

    public ReturnProductController(ReturnProductView view) {
        this.view = view;

        this.view.getBtnGuiYeuCau().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String maDoiTra = view.getMaDoiTra().trim(); // Lấy mã đổi trả từ View
                    String maKH = view.getMaKH().trim();
                    String maSP = view.getMaSP().trim();
                    String maDonHang = view.getMaDonHang().trim();
                    String lyDo = view.getLyDo().trim();

                    System.out.println("DEBUG Controller: maDoiTra từ View: '" + maDoiTra + "'");

                    if (maDoiTra.isEmpty() || maKH.isEmpty() || maSP.isEmpty() || maDonHang.isEmpty() || lyDo.isEmpty()) {
                        JOptionPane.showMessageDialog(view, "Vui lòng điền đầy đủ thông tin, bao gồm cả Mã Đổi Trả.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Truyền maDoiTra vào constructor của DoiTra
                    DoiTra doiTra = new DoiTra(maDoiTra, maKH, maSP, maDonHang, new Date(), lyDo);
                    System.out.println("DEBUG Controller: idDT trong đối tượng DoiTra sau khi khởi tạo: '" + doiTra.getIdDT() + "'");

                    boolean success = DoiTraQuery.themYeuCauDoiTra(doiTra);

                    if (success) {
                        JOptionPane.showMessageDialog(view, "Gửi yêu cầu đổi trả thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        view.dispose();
                    } else {
                        // Thông báo lỗi cụ thể hơn có thể đã được DoiTraQuery in ra console
                        JOptionPane.showMessageDialog(view, "Gửi yêu cầu thất bại. Vui lòng kiểm tra lại thông tin hoặc xem log lỗi.", "Thất bại", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                    System.err.println("Lỗi trong ReturnProductController: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(view, "Đã xảy ra lỗi hệ thống: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}