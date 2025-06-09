package controller.employee;
import model.ChiTietHDXuat;
import model.HoaDonXuat;
import query.HoaDonXuatQuery;
import query.KhachHangQuery;
import query.SanPhamQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import dbConnection.DBConnection;

public class SellProduct {
    public Integer banHang(int maNV, Integer maKHInteger, String tenKH, String sdtKH,
                           List<ChiTietHDXuat> dsChiTiet,
                           boolean suDungDiemDeGiamGia, int phanTramGiamTuDiem) {

        System.out.println("SELL_PRODUCT_CONTROLLER: Bắt đầu. MaNV=" + maNV + ", MaKH=" + maKHInteger);
        LogSellDetails(maKHInteger, tenKH, sdtKH, suDungDiemDeGiamGia, phanTramGiamTuDiem); // Sửa Log

        if (dsChiTiet == null || dsChiTiet.isEmpty()) {
            System.err.println("SELL_PRODUCT_CONTROLLER: Danh sách chi tiết sản phẩm rỗng.");
            return null;
        }

        Connection conn = null;
        Integer maHDXGenerated = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Kiểm tra tồn kho
            System.out.println("SELL_PRODUCT_CONTROLLER: Kiểm tra tồn kho...");
            for (ChiTietHDXuat ct : dsChiTiet) {
                // Giả sử getSoLuongTonKho ném SQLException nếu có lỗi DB
                int tonKho = SanPhamQuery.getSoLuongTonKho(ct.getMaSP(), conn);
                if (tonKho < ct.getSoLuong()) {
                    System.err.println("SELL_PRODUCT_CONTROLLER: SP ID " + ct.getMaSP() +
                                       " không đủ tồn kho (cần " + ct.getSoLuong() + ", có " + tonKho + "). Rollback.");
                    conn.rollback();
                    return null;
                }
            }
            System.out.println("SELL_PRODUCT_CONTROLLER: Tất cả sản phẩm đủ tồn kho.");

            // 2. Tính toán tiền
            double tongTienGoc = dsChiTiet.stream().mapToDouble(ct -> ct.getDonGiaXuat() * ct.getSoLuong()).sum();
            double tongTienTruocThue = tongTienGoc;
            if (suDungDiemDeGiamGia && phanTramGiamTuDiem > 0 && maKHInteger != null) {
                double tiLeGiam = (double) phanTramGiamTuDiem / 100.0;
                tongTienTruocThue = tongTienGoc * (1 - tiLeGiam);
                 System.out.println("SELL_PRODUCT_CONTROLLER: Áp dụng giảm giá " + phanTramGiamTuDiem + "%. Tiền sau giảm: " + tongTienTruocThue);
            }
            double thueValue = 0.1; // 10% VAT
            double mucThuePhanTram = thueValue * 100;
            // ThanhTien trong DB là tiền cuối cùng khách trả (đã bao gồm VAT)
            double thanhTienCuoiCung = tongTienTruocThue * (1 + thueValue);
            LogSellCalculation(tongTienGoc, tongTienTruocThue, thanhTienCuoiCung, mucThuePhanTram);

            // 3. Tạo đối tượng HoaDonXuat
            // MaKHInteger đã được xác định hoặc là null từ View
            HoaDonXuat hdx = new HoaDonXuat(LocalDate.now(), thanhTienCuoiCung, mucThuePhanTram, maNV, maKHInteger);
            hdx.setChiTietList(dsChiTiet);

            // 4. Insert Hóa Đơn Xuất, chi tiết, xử lý điểm (nếu có)
            // Loại bỏ maTheTichDiemKhachCungCap khỏi lời gọi
            maHDXGenerated = HoaDonXuatQuery.insertHoaDonXuatFullAndGetId(hdx, tenKH, sdtKH, suDungDiemDeGiamGia, conn);

            if (maHDXGenerated == null || maHDXGenerated <= 0) {
                System.err.println("SELL_PRODUCT_CONTROLLER: Không thể tạo hóa đơn mới hoặc xử lý điểm. Rollback.");
                conn.rollback();
                return null;
            }
            System.out.println("SELL_PRODUCT_CONTROLLER: Hóa đơn MaHDX=" + maHDXGenerated + " và các mục liên quan đã được xử lý.");

            // 5. Cập nhật số lượng tồn kho
            System.out.println("SELL_PRODUCT_CONTROLLER: Cập nhật tồn kho...");
            for (ChiTietHDXuat ct : dsChiTiet) {
                // Gọi hàm giảm số lượng, truyền số lượng bán (dương)
                if (!SanPhamQuery.giamSoLuongKhiBan(ct.getMaSP(), ct.getSoLuong(), conn)) {
                     System.err.println("SELL_PRODUCT_CONTROLLER: Lỗi cập nhật tồn kho cho SP ID: " + ct.getMaSP() + ". Rollback.");
                    conn.rollback();
                    return null;
                }
            }
            System.out.println("SELL_PRODUCT_CONTROLLER: Cập nhật tồn kho thành công.");

            // 6. Reset điểm nếu đã sử dụng
            if (suDungDiemDeGiamGia && maKHInteger != null && phanTramGiamTuDiem > 0) {
                if (!KhachHangQuery.resetDiemTichLuy(maKHInteger, conn)) { // Truyền conn
                    System.err.println("SELL_PRODUCT_CONTROLLER: Lỗi khi reset điểm cho MaKH: " + maKHInteger + ". Rollback.");
                    conn.rollback();
                    return null;
                }
                 System.out.println("SELL_PRODUCT_CONTROLLER: Đã reset điểm tích lũy cho MaKH: " + maKHInteger);
            }

            conn.commit();
            System.out.println("SELL_PRODUCT_CONTROLLER: Quy trình bán hàng cho MaHDX: " + maHDXGenerated + " hoàn tất.");
            return maHDXGenerated;

        } catch (SQLException e) {
            System.err.println("SELL_PRODUCT_CONTROLLER: SQLException: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return null;
        } catch (Exception e) { // Bắt các lỗi không mong muốn khác
             System.err.println("SELL_PRODUCT_CONTROLLER: Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return null;
        }
        finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    private void LogSellDetails(Integer maKH, String tenKH, String sdtKH, boolean suDungDiem, int phanTramGiam) {
        System.out.println("SELL_PRODUCT_CONTROLLER (Input Details): MaKH=" + maKH +
                           ", TenKH=" + tenKH + ", SdtKH=" + sdtKH +
                           ", SuDungDiem=" + suDungDiem + ", PhanTramGiamTuDiem=" + phanTramGiam + "%");
    }

    private void LogSellCalculation(double goc, double sauGiamTruocThue, double cuoiCungSauThue, double thuePT) {
         System.out.println("SELL_PRODUCT_CONTROLLER (Calculation): TongTienGoc=" + goc +
                           ", TongTienSauGiamGiaTruocThue=" + sauGiamTruocThue +
                           ", MucThue=" + thuePT + "%" +
                           ", ThanhTienCuoiCung=" + cuoiCungSauThue);
    }
}