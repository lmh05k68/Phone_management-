// File: controller/employee/SellProduct.java
package controller.employee;

import dbConnection.DBConnection;
import model.HoaDonXuat;
import query.HoaDonXuatQuery;
import query.KhachHangQuery;
import query.SPCuTheQuery;

import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SellProduct {
    private static final Logger logger = Logger.getLogger(SellProduct.class.getName());
    private static final BigDecimal VAT_RATE_PERCENT = new BigDecimal("10.00"); // 10% để lưu vào CSDL
    private static final BigDecimal VAT_RATE_CALC = new BigDecimal("0.10");    // 0.10 để tính toán
    public Integer banHang(int maNV, Integer maKH, List<String> dsMaSPCuThe, boolean suDungDiem, BigDecimal tongTienGoc) {

        if (dsMaSPCuThe == null || dsMaSPCuThe.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Giỏ hàng đang trống. Không thể thanh toán.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Connection conn = null;
        try {
            // --- BẮT ĐẦU TRANSACTION ---
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // --- BƯỚC 1: TÍNH TOÁN GIÁ TRỊ HÓA ĐƠN VÀ ĐIỂM THƯỞNG ---
            BigDecimal tienGiamGia = BigDecimal.ZERO;
            int soDiemCanDung = 0;

            if (maKH != null && suDungDiem) {
                int diemHienTai = KhachHangQuery.getSoDiemTichLuy(maKH, conn);
                int phanTramGiam = Math.min(diemHienTai / 100, 20);
                
                if (phanTramGiam > 0) {
                    soDiemCanDung = phanTramGiam * 100;
                    BigDecimal discountRate = new BigDecimal(phanTramGiam).divide(new BigDecimal(100));
                    tienGiamGia = tongTienGoc.multiply(discountRate).setScale(0, RoundingMode.HALF_UP);
                }
            }

            BigDecimal tienSauGiamGia = tongTienGoc.subtract(tienGiamGia);
            BigDecimal tienThue = tienSauGiamGia.multiply(VAT_RATE_CALC).setScale(0, RoundingMode.HALF_UP);
            BigDecimal thanhTienCuoiCung = tienSauGiamGia.add(tienThue);

            // --- BƯỚC 2: TẠO HÓA ĐƠN XUẤT ---
            HoaDonXuat hdx = new HoaDonXuat();
            hdx.setNgayLap(LocalDate.now());
            hdx.setThanhTien(thanhTienCuoiCung);
            hdx.setMucThue(VAT_RATE_PERCENT);
            hdx.setMaNV(maNV);
            // Gán MaKH, nếu null thì CSDL sẽ tự xử lý (giả sử cột MaKH có thể NULL hoặc có giá trị mặc định cho khách lẻ)
            hdx.setMaKH(maKH != null ? maKH : 0); 


            Integer maHDXGenerated = HoaDonXuatQuery.insertHoaDonXuatAndGetId(hdx, conn);
            if (maHDXGenerated == null) {
                throw new SQLException("Không thể tạo hóa đơn mới trong CSDL.");
            }

            // --- BƯỚC 3: CẬP NHẬT TRẠNG THÁI SẢN PHẨM (KÍCH HOẠT TRIGGER) ---
            // Gọi phương thức sellProducts đã được tối ưu
            SPCuTheQuery.sellProducts(dsMaSPCuThe, maHDXGenerated, conn);

            // --- BƯỚC 4: XỬ LÝ ĐIỂM THƯỞNG ---
            if (maKH != null) {
                if (suDungDiem && soDiemCanDung > 0) {
                    KhachHangQuery.suDungDiemThuong(maKH, maHDXGenerated, soDiemCanDung, conn);
                } else {
                    // Tích điểm dựa trên tổng tiền gốc
                    int diemMoi = tongTienGoc.divide(new BigDecimal("10000"), 0, RoundingMode.DOWN).intValue();
                    if (diemMoi > 0) {
                        KhachHangQuery.themDiemThuong(maKH, maHDXGenerated, diemMoi, conn);
                    }
                }
            }

            // --- KẾT THÚC TRANSACTION ---
            conn.commit(); // Thành công! Lưu tất cả các thay đổi.
            logger.log(Level.INFO, "Giao dịch thành công cho hóa đơn #" + maHDXGenerated);
            return maHDXGenerated;

        } catch (SQLException e) {
            // Đây là nơi xử lý tất cả các lỗi, bao gồm cả lỗi từ TRIGGER
            logger.log(Level.SEVERE, "Lỗi trong quá trình bán hàng. Giao dịch đang được rollback.", e);
            if (conn != null) {
                try {
                    conn.rollback(); // Quan trọng: Hủy bỏ mọi thay đổi nếu có lỗi
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Lỗi nghiêm trọng khi rollback transaction.", ex);
                }
            }

            // Tạo thông báo lỗi thân thiện cho người dùng
            String userMessage;
            if (e.getMessage().contains("đã hết hàng")) {
                // Lỗi từ trigger kiểm tra số lượng
                userMessage = "Thao tác thất bại: " + e.getMessage();
            } else if (e.getMessage().contains("không còn khả dụng")) {
                // Lỗi từ Java kiểm tra số lượng row được update
                 userMessage = "Thao tác thất bại: " + e.getMessage();
            } else {
                // Lỗi chung khác
                userMessage = "Đã xảy ra lỗi không mong muốn khi xử lý giao dịch. Vui lòng thử lại.";
            }

            JOptionPane.showMessageDialog(null, userMessage, "Lỗi Giao Dịch", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Trả lại trạng thái mặc định
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Lỗi khi đóng kết nối.", e);
                }
            }
        }
    }
}