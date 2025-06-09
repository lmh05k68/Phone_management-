package controller.employee;
import javax.swing.JOptionPane;
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
        LogSellDetails(maKHInteger, tenKH, sdtKH, suDungDiemDeGiamGia, phanTramGiamTuDiem);

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
                int tonKho = SanPhamQuery.getSoLuongTonKho(ct.getMaSP(), conn); // Sử dụng connection của transaction
                if (tonKho < ct.getSoLuong()) {
                    System.err.println("SELL_PRODUCT_CONTROLLER: SP ID " + ct.getMaSP() +
                                       " không đủ tồn kho (cần " + ct.getSoLuong() + ", có " + tonKho + "). Rollback.");
                    conn.rollback();
                    JOptionPane.showMessageDialog(null, "Sản phẩm " + ct.getMaSP() + " không đủ số lượng tồn kho!", "Lỗi Tồn Kho", JOptionPane.ERROR_MESSAGE);
                    return null; // Trả về null để View biết thất bại
                }
            }
            System.out.println("SELL_PRODUCT_CONTROLLER: Tất cả sản phẩm đủ tồn kho.");

            // 2. Tính toán tiền (giữ nguyên logic này)
            double tongTienGoc = dsChiTiet.stream().mapToDouble(ct -> ct.getDonGiaXuat() * ct.getSoLuong()).sum();
            double tongTienTruocThue = tongTienGoc;
            if (suDungDiemDeGiamGia && phanTramGiamTuDiem > 0 && maKHInteger != null) {
                double tiLeGiam = (double) phanTramGiamTuDiem / 100.0;
                tongTienTruocThue = tongTienGoc * (1 - tiLeGiam);
            }
            double thueValue = 0.1;
            double mucThuePhanTram = thueValue * 100;
            double thanhTienCuoiCung = tongTienTruocThue * (1 + thueValue);
            LogSellCalculation(tongTienGoc, tongTienTruocThue, thanhTienCuoiCung, mucThuePhanTram);

            HoaDonXuat hdx = new HoaDonXuat(LocalDate.now(), thanhTienCuoiCung, mucThuePhanTram, maNV, maKHInteger);
            hdx.setChiTietList(dsChiTiet);

            // 3. Insert Hóa Đơn Xuất, chi tiết, xử lý điểm
            maHDXGenerated = HoaDonXuatQuery.insertHoaDonXuatFullAndGetId(hdx, tenKH, sdtKH, suDungDiemDeGiamGia, conn);

            if (maHDXGenerated == null || maHDXGenerated <= 0) {
                System.err.println("SELL_PRODUCT_CONTROLLER: Không thể tạo hóa đơn mới hoặc xử lý điểm. Rollback.");
                conn.rollback();
                return null;
            }
            System.out.println("SELL_PRODUCT_CONTROLLER: Hóa đơn MaHDX=" + maHDXGenerated + " đã được xử lý.");

            // 4. Cập nhật (giảm) số lượng tồn kho <<<< SỬA GỌI HÀM
            System.out.println("SELL_PRODUCT_CONTROLLER: Cập nhật tồn kho...");
            for (ChiTietHDXuat ct : dsChiTiet) {
                // Gọi hàm giamSoLuongKhiBan, truyền số lượng bán (dương)
                if (!SanPhamQuery.giamSoLuongKhiBan(ct.getMaSP(), ct.getSoLuong(), conn)) {
                     System.err.println("SELL_PRODUCT_CONTROLLER: Lỗi cập nhật (giảm) tồn kho cho SP ID: " + ct.getMaSP() + ". Rollback.");
                     // Lỗi này ở đây có thể do MaSP không tồn tại (dù đã kiểm tra ở trên) hoặc một lỗi DB khác.
                     // Việc không đủ hàng đã được kiểm tra ở bước 1.
                    conn.rollback();
                    return null;
                }
            }
            System.out.println("SELL_PRODUCT_CONTROLLER: Cập nhật tồn kho thành công.");

            // 5. Reset điểm nếu đã sử dụng
            if (suDungDiemDeGiamGia && maKHInteger != null && phanTramGiamTuDiem > 0) {
                if (!KhachHangQuery.resetDiemTichLuy(maKHInteger, conn)) {
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
            JOptionPane.showMessageDialog(null, "Lỗi cơ sở dữ liệu: " + e.getMessage(), "Lỗi Bán Hàng", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Exception e) {
             System.err.println("SELL_PRODUCT_CONTROLLER: Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            JOptionPane.showMessageDialog(null, "Lỗi không xác định: " + e.getMessage(), "Lỗi Bán Hàng", JOptionPane.ERROR_MESSAGE);
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