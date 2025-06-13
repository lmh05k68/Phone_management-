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
    private static final BigDecimal VAT_RATE_PERCENT = new BigDecimal("10.00"); // 10%
    private static final BigDecimal VAT_RATE_CALC = new BigDecimal("0.10"); 
    public Integer banHang(int maNV, Integer maKH, List<String> dsMaSPCuThe, boolean suDungDiem, BigDecimal tongTienGoc) {

        if (dsMaSPCuThe == null || dsMaSPCuThe.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Giỏ hàng đang trống. Không thể thanh toán.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); 
            BigDecimal tienGiamGia = BigDecimal.ZERO;
            int soDiemCanDung = 0;
            if (maKH != null && suDungDiem) {
                int diemHienTai = KhachHangQuery.getSoDiemTichLuy(maKH, conn);
                int phanTramGiam = Math.min(diemHienTai / 100, 20); // 100 điểm = 1%, tối đa 20%
                
                if (phanTramGiam > 0) {
                    soDiemCanDung = phanTramGiam * 100;
                    BigDecimal discountRate = new BigDecimal(phanTramGiam).divide(new BigDecimal(100));
                    tienGiamGia = tongTienGoc.multiply(discountRate).setScale(0, RoundingMode.HALF_UP);
                }
            }

            BigDecimal tienSauGiamGia = tongTienGoc.subtract(tienGiamGia);
            BigDecimal tienThue = tienSauGiamGia.multiply(VAT_RATE_CALC).setScale(0, RoundingMode.HALF_UP);
            BigDecimal thanhTienCuoiCung = tienSauGiamGia.add(tienThue);

            // --- BƯỚC 2: TẠO HÓA ĐƠN XUẤT VỚI DỮ LIỆU ĐÃ TÍNH TOÁN ---

            HoaDonXuat hdx = new HoaDonXuat();
            hdx.setNgayLap(LocalDate.now());
            hdx.setThanhTien(thanhTienCuoiCung); // SỬA LỖI QUAN TRỌNG: Gán Thành Tiền đã tính toán
            hdx.setMucThue(VAT_RATE_PERCENT);   // Gán Mức Thuế là 10.00
            hdx.setMaNV(maNV);
            hdx.setMaKH(maKH != null ? maKH : 0); // Giả sử 0 là khách lẻ (hoặc dùng mã KH mặc định)

            Integer maHDXGenerated = HoaDonXuatQuery.insertHoaDonXuatAndGetId(hdx, conn);
            if (maHDXGenerated == null) {
                throw new SQLException("Không thể tạo hóa đơn mới trong CSDL.");
            }

            // --- BƯỚC 3: CẬP NHẬT TRẠNG THÁI SẢN PHẨM ---
            int updatedProducts = SPCuTheQuery.assignProductsToInvoice(dsMaSPCuThe, maHDXGenerated, conn);
            if (updatedProducts != dsMaSPCuThe.size()) {
                // Lỗi này xảy ra khi có người khác đã mua 1 trong các sản phẩm này
                throw new SQLException("Một hoặc nhiều sản phẩm đã được bán hoặc không còn tồn tại. Vui lòng làm mới danh sách sản phẩm.");
            }

            // --- BƯỚC 4: XỬ LÝ ĐIỂM THƯỞNG CHO KHÁCH HÀNG (NẾU CÓ) ---
            if (maKH != null) {
                if (suDungDiem && soDiemCanDung > 0) {
                    // Trừ điểm đã sử dụng
                    KhachHangQuery.suDungDiemThuong(maKH, maHDXGenerated, soDiemCanDung, conn);
                } else {
                    // Cộng điểm tích lũy mới
                    // Tích điểm dựa trên tổng tiền gốc (trước thuế, trước giảm giá)
                    int diemMoi = tongTienGoc.divide(new BigDecimal("10000"), 0, RoundingMode.DOWN).intValue();
                    if (diemMoi > 0) {
                        KhachHangQuery.themDiemThuong(maKH, maHDXGenerated, diemMoi, conn);
                    }
                }
            }

            // --- KẾT THÚC TRANSACTION ---
            conn.commit(); // Lưu tất cả các thay đổi nếu không có lỗi
            return maHDXGenerated;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Nếu có lỗi, hủy bỏ tất cả thay đổi trong transaction
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            // Hiển thị thông báo lỗi thân thiện hơn cho người dùng
            String errorMessage = e.getMessage().contains("đã được bán") ? e.getMessage() : "Lỗi khi xử lý giao dịch. Vui lòng thử lại.";
            JOptionPane.showMessageDialog(null, errorMessage, "Lỗi Cơ Sở Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Trả lại trạng thái mặc định cho connection
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}