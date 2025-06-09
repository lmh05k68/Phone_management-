package controller.customer;

import model.DoiTra;
import query.DoiTraQuery; // Gọi static
import view.customer.ReturnProductView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate; 
public class ReturnProductController {
    private ReturnProductView view;
    private final int maKHController;
    public ReturnProductController(ReturnProductView view, int maKH) {
        this.view = view;
        this.maKHController = maKH;
        this.view.getBtnGuiYeuCau().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String maSPStr = view.getMaSP().trim();
                    String maDonHangStr = view.getMaDonHang().trim();
                    String lyDo = view.getLyDo().trim();

                    if (maSPStr.isEmpty() || maDonHangStr.isEmpty() || lyDo.isEmpty()) {
                        JOptionPane.showMessageDialog(view, "Vui lòng điền đầy đủ thông tin Mã SP, Mã Đơn Hàng và Lý do.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int maSP = Integer.parseInt(maSPStr);
                    int maDonHang = Integer.parseInt(maDonHangStr);

                    // Tạo đối tượng DoiTra không cần idDT ban đầu
                    // Sử dụng maKHController (int) đã lưu
                    DoiTra doiTra = new DoiTra(maKHController, maSP, maDonHang, LocalDate.now(), lyDo); // Trạng thái mặc định "Chờ xử lý"

                    // Gọi phương thức static từ DoiTraQuery
                    // Giả sử themYeuCauDoiTraAndGetId trả về Integer là idDT mới, hoặc chỉ themYeuCauDoiTra nếu không cần ID
                    Integer idDTGenerated = DoiTraQuery.themYeuCauDoiTraAndGetId(doiTra);

                    if (idDTGenerated != null && idDTGenerated > 0) {
                        JOptionPane.showMessageDialog(view, "Gửi yêu cầu đổi trả thành công! Mã yêu cầu: " + idDTGenerated, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                        view.dispose();
                        // new CustomerView(maKHController).setVisible(true); // Mở lại CustomerView nếu cần
                    } else {
                        JOptionPane.showMessageDialog(view, "Gửi yêu cầu thất bại. Vui lòng kiểm tra lại thông tin (Mã đơn hàng có tồn tại?) hoặc xem log lỗi.", "Thất Bại", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(view, "Mã sản phẩm và Mã đơn hàng phải là số nguyên.", "Lỗi Định Dạng Số", JOptionPane.ERROR_MESSAGE);
                }
                catch (Exception ex) {
                    System.err.println("Lỗi trong ReturnProductController: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(view, "Đã xảy ra lỗi hệ thống: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}