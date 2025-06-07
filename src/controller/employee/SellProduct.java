package controller.employee;

import model.ChiTietHDXuat;
import model.HoaDonXuat;
import query.HoaDonXuatQuery;
import query.SanPhamQuery;
// Không cần import DiemThuongQuery hay KhachHangQuery ở đây nữa

import java.time.LocalDate;
import java.util.List;

public class SellProduct {

    private final HoaDonXuatQuery hoaDonXuatQuery = new HoaDonXuatQuery();
    private final SanPhamQuery sanPhamQuery = new SanPhamQuery();

    /**
     * Thực hiện quy trình bán hàng.
     * @param maHDX Mã hóa đơn
     * @param maNV Mã nhân viên
     * @param maKH Mã khách hàng (có thể null)
     * @param tenKH Tên khách hàng (cần cho DiemThuong nếu có maKH)
     * @param sdtKH Số điện thoại KH (cần cho DiemThuong nếu có maKH)
     * @param maTichDiem Mã tích điểm do nhân viên nhập (có thể null nếu maKH là null)
     * @param dsChiTiet Danh sách chi tiết hóa đơn
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean banHang(String maHDX, String maNV, String maKH, String tenKH, String sdtKH, String maTichDiem, List<ChiTietHDXuat> dsChiTiet) {
        System.out.println("SELL_PRODUCT_CONTROLLER: Bắt đầu quy trình bán hàng cho MaHDX: " + maHDX);
        System.out.println("SELL_PRODUCT_CONTROLLER: Tham số đầu vào: MaKH=" + maKH + ", TenKH=" + tenKH + ", SdtKH=" + sdtKH + ", MaTichDiem=" + maTichDiem);


        if (dsChiTiet == null || dsChiTiet.isEmpty()) {
            System.err.println("SELL_PRODUCT_CONTROLLER: Danh sách chi tiết sản phẩm rỗng.");
            return false;
        }

        for (ChiTietHDXuat ct : dsChiTiet) {
            int tonKho = sanPhamQuery.getSoLuongTonKho(ct.getMaSP());
            if (tonKho < ct.getSoLuong()) {
                System.err.println("SELL_PRODUCT_CONTROLLER: Sản phẩm " + ct.getMaSP() + " không đủ tồn kho (Cần: " + ct.getSoLuong() + ", Có: " + tonKho + ").");
                return false;
            }
        }
        System.out.println("SELL_PRODUCT_CONTROLLER: Tất cả sản phẩm đủ tồn kho.");

        double tongTienGoc = dsChiTiet.stream().mapToDouble(ct -> ct.getDonGiaXuat() * ct.getSoLuong()).sum();
        double thueValue = 0.1;
        double mucThuePhanTram = thueValue * 100;
        double thanhTien = tongTienGoc * (1 + thueValue);

        HoaDonXuat hdx = new HoaDonXuat(maHDX, LocalDate.now(), thanhTien, mucThuePhanTram, maNV, maKH);
        hdx.setChiTietList(dsChiTiet);
        // Gán thêm thông tin cần cho việc tạo DiemThuong vào đối tượng HoaDonXuat
        // để HoaDonXuatQuery có thể sử dụng (nếu cần thiết, hoặc truyền riêng)
        // Hiện tại, HoaDonXuatQuery sẽ tự lấy tenKH, sdtKH từ KhachHangQuery nếu có maKH.
        // Mã tích điểm sẽ được truyền riêng.

        System.out.println("SELL_PRODUCT_CONTROLLER: Đã tạo đối tượng HoaDonXuat: MaHDX=" + hdx.getMaHDX() + ", MaKH=" + hdx.getMaKH() + ", ThanhTien=" + hdx.getThanhTien());

        // Gọi HoaDonXuatQuery, truyền thêm maTichDiem, tenKH, sdtKH vào
        String resultMaHDX = hoaDonXuatQuery.insertHoaDonXuat(hdx, maTichDiem, tenKH, sdtKH); // << THAY ĐỔI Ở ĐÂY

        if (resultMaHDX == null) {
            System.err.println("SELL_PRODUCT_CONTROLLER: Không thể tạo hóa đơn mới.");
            return false;
        }
        System.out.println("SELL_PRODUCT_CONTROLLER: Hóa đơn " + resultMaHDX + " đã được tạo thành công.");

        System.out.println("SELL_PRODUCT_CONTROLLER: Bắt đầu cập nhật tồn kho.");
        for (ChiTietHDXuat ct : dsChiTiet) {
            if (!sanPhamQuery.updateSoLuong(ct.getMaSP(), -ct.getSoLuong())) {
                System.err.println("SELL_PRODUCT_CONTROLLER: Lỗi nghiêm trọng - Cập nhật tồn kho thất bại cho sản phẩm: " + ct.getMaSP());
                // Cần cơ chế rollback phức tạp hơn nếu đã commit hóa đơn
                return false;
            }
        }
        System.out.println("SELL_PRODUCT_CONTROLLER: Cập nhật tồn kho thành công.");

        System.out.println("SELL_PRODUCT_CONTROLLER: Quy trình bán hàng cho MaHDX: " + resultMaHDX + " hoàn tất thành công.");
        return true;
    }
}