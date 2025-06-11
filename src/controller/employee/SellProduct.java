package controller.employee;

import dbConnection.DBConnection;
import model.HoaDonXuat;
import query.HoaDonXuatQuery;
import query.KhachHangQuery;
import query.SPCuTheQuery;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class SellProduct {

    /**
     * Xử lý nghiệp vụ bán hàng trong một transaction an toàn.
     * @param maNV Mã nhân viên thực hiện.
     * @param maKH Mã khách hàng (có thể null).
     * @param dsMaSPCuThe Danh sách các sản phẩm trong giỏ hàng.
     * @param suDungDiem True nếu khách hàng chọn dùng điểm để giảm giá.
     * @param tongTienGoc Tổng tiền gốc của các sản phẩm trong giỏ (dùng để tính điểm tích lũy).
     * @return MaHDX của hóa đơn mới nếu thành công, ngược lại trả về null.
     */
    public Integer banHang(int maNV, Integer maKH, List<String> dsMaSPCuThe, boolean suDungDiem, BigDecimal tongTienGoc) {

        if (dsMaSPCuThe == null || dsMaSPCuThe.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Giỏ hàng đang trống. Không thể thanh toán.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION

            // BƯỚC 1: TẠO HÓA ĐƠN MỚI
            // ThanhTien sẽ được trigger tính toán sau khi sản phẩm được gán vào
            BigDecimal mucThue = new BigDecimal("10.00"); // Thuế VAT 10%
            HoaDonXuat hdx = new HoaDonXuat(LocalDate.now(), mucThue, maNV, maKH != null ? maKH : 0);
            Integer maHDXGenerated = HoaDonXuatQuery.insertHoaDonXuatAndGetId(hdx, conn);

            if (maHDXGenerated == null) {
                throw new SQLException("Không thể tạo hóa đơn mới.");
            }

            // BƯỚC 2: GÁN CÁC SẢN PHẨM VÀO HÓA ĐƠN
            // Trigger sẽ tự động cập nhật trạng thái sản phẩm và tổng tiền hóa đơn
            int updatedProducts = SPCuTheQuery.assignProductsToInvoice(dsMaSPCuThe, maHDXGenerated, conn);
            if (updatedProducts != dsMaSPCuThe.size()) {
                throw new SQLException("Một hoặc nhiều sản phẩm đã được bán hoặc không còn tồn tại. Vui lòng làm mới danh sách sản phẩm.");
            }

            // BƯỚC 3: XỬ LÝ ĐIỂM THƯỞNG CHO KHÁCH HÀNG THÂN THIẾT
            if (maKH != null) {
                if (suDungDiem) {
                    // Kịch bản 1: Khách hàng sử dụng điểm
                    int diemHienTai = KhachHangQuery.getSoDiemTichLuy(maKH, conn);
                    int phanTramGiam = Math.min(diemHienTai / 100, 20); // 100 điểm = 1%, tối đa 20%
                    int soDiemCanDung = phanTramGiam * 100;

                    if (soDiemCanDung > 0) {
                        // Ghi nhận giao dịch trừ điểm. Trigger sẽ tự cập nhật tổng điểm của khách.
                        KhachHangQuery.suDungDiemThuong(maKH, maHDXGenerated, soDiemCanDung, conn);
                    }
                } else {
                    // Kịch bản 2: Khách hàng tích điểm cho hóa đơn này
                    // Quy tắc: 10,000 VND = 1 điểm.
                    int diemMoi = tongTienGoc.divide(new BigDecimal("10000"), 0, RoundingMode.DOWN).intValue();
                    if (diemMoi > 0) {
                        // Ghi nhận giao dịch cộng điểm. Trigger sẽ tự cập nhật tổng điểm của khách.
                        KhachHangQuery.themDiemThuong(maKH, maHDXGenerated, diemMoi, conn);
                    }
                }
            }

            // KẾT THÚC TRANSACTION
            conn.commit();
            return maHDXGenerated;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Nếu có lỗi, hủy bỏ tất cả
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(null, "Lỗi khi xử lý giao dịch:\n" + e.getMessage(), "Lỗi Cơ Sở Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Trả lại trạng thái mặc định
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}