package controller.employee;

import model.ChiTietHDXuat;
import model.HoaDonXuat;
import query.HoaDonXuatQuery;
import query.KhachHangQuery; // Thêm import
import query.SanPhamQuery;

import java.time.LocalDate;
import java.util.List;

public class SellProduct {

    private final HoaDonXuatQuery hoaDonXuatQuery = new HoaDonXuatQuery();
    private final SanPhamQuery sanPhamQuery = new SanPhamQuery();
    private final KhachHangQuery khachHangQuery = new KhachHangQuery(); // Khởi tạo

    public boolean banHang(String maHDX, String maNV, String maKH, String tenKH, String sdtKH,
                           String maTichDiemKhachCungCap, List<ChiTietHDXuat> dsChiTiet,
                           boolean suDungDiemDeGiamGia, int phanTramGiamTuDiem) { // Thêm tham số
        System.out.println("SELL_PRODUCT_CONTROLLER: Bắt đầu quy trình bán hàng cho MaHDX: " + maHDX);
        System.out.println("SELL_PRODUCT_CONTROLLER: Tham số: MaKH=" + maKH + ", TenKH=" + tenKH + ", SdtKH=" + sdtKH +
                           ", MaTichDiemCungCap=" + maTichDiemKhachCungCap +
                           ", SuDungDiem=" + suDungDiemDeGiamGia + ", PhanTramGiamTuDiem=" + phanTramGiamTuDiem + "%");

        if (dsChiTiet == null || dsChiTiet.isEmpty()) {
            System.err.println("SELL_PRODUCT_CONTROLLER: Danh sách chi tiết sản phẩm rỗng.");
            return false;
        }

        // 1. Kiểm tra tồn kho
        for (ChiTietHDXuat ct : dsChiTiet) {
            int tonKho = sanPhamQuery.getSoLuongTonKho(ct.getMaSP());
            if (tonKho < ct.getSoLuong()) {
                System.err.println("SELL_PRODUCT_CONTROLLER: Sản phẩm " + ct.getMaSP() +
                                   " không đủ tồn kho (Cần: " + ct.getSoLuong() + ", Có: " + tonKho + ").");
                return false;
            }
        }
        System.out.println("SELL_PRODUCT_CONTROLLER: Tất cả sản phẩm đủ tồn kho.");

        // 2. Tính toán tổng tiền
        double tongTienGoc = dsChiTiet.stream().mapToDouble(ct -> ct.getDonGiaXuat() * ct.getSoLuong()).sum();
        System.out.println("SELL_PRODUCT_CONTROLLER: Tổng tiền gốc (trước giảm giá, trước thuế): " + tongTienGoc);

        double tongTienSauGiamGia = tongTienGoc;
        if (suDungDiemDeGiamGia && phanTramGiamTuDiem > 0) {
            double tiLeGiam = (double) phanTramGiamTuDiem / 100.0;
            tongTienSauGiamGia = tongTienGoc * (1 - tiLeGiam);
            System.out.println("SELL_PRODUCT_CONTROLLER: Áp dụng giảm " + phanTramGiamTuDiem + "%. Tổng tiền sau giảm giá: " + tongTienSauGiamGia);
        }

        double thueValue = 0.1; // 10% VAT
        double mucThuePhanTram = thueValue * 100;
        double thanhTienCuoiCung = tongTienSauGiamGia * (1 + thueValue);
        System.out.println("SELL_PRODUCT_CONTROLLER: Thành tiền cuối cùng (sau giảm giá, sau thuế): " + thanhTienCuoiCung);


        // 3. Tạo đối tượng Hóa Đơn Xuất
        HoaDonXuat hdx = new HoaDonXuat(maHDX, LocalDate.now(), thanhTienCuoiCung, mucThuePhanTram, maNV, maKH);
        hdx.setChiTietList(dsChiTiet);
        System.out.println("SELL_PRODUCT_CONTROLLER: Đã tạo đối tượng HoaDonXuat: MaHDX=" + hdx.getMaHDX() +
                           ", MaKH=" + hdx.getMaKH() + ", ThanhTien=" + hdx.getThanhTien());

        // 4. Insert Hóa Đơn Xuất (và chi tiết, và có thể tích điểm nếu không dùng điểm)
        // Nếu suDungDiemDeGiamGia là true, maTichDiemThucTeSeSuDung sẽ là null để không tích điểm
        // Ngược lại, sẽ dùng maTichDiemKhachCungCap để tích điểm (nếu KH cung cấp)
        String maTichDiemThucTeSeSuDung = suDungDiemDeGiamGia ? null : maTichDiemKhachCungCap;

        System.out.println("SELL_PRODUCT_CONTROLLER: MaTichDiem sẽ gửi cho HoaDonXuatQuery: " + maTichDiemThucTeSeSuDung);

        String resultMaHDX = hoaDonXuatQuery.insertHoaDonXuat(hdx, maTichDiemThucTeSeSuDung, tenKH, sdtKH);

        if (resultMaHDX == null) {
            System.err.println("SELL_PRODUCT_CONTROLLER: Không thể tạo hóa đơn mới (insertHoaDonXuat trả về null).");
            return false;
        }
        System.out.println("SELL_PRODUCT_CONTROLLER: Hóa đơn " + resultMaHDX + " đã được tạo thành công.");
        System.out.println("SELL_PRODUCT_CONTROLLER: Bắt đầu cập nhật tồn kho.");
        for (ChiTietHDXuat ct : dsChiTiet) {
            if (!sanPhamQuery.updateSoLuong(ct.getMaSP(), -ct.getSoLuong())) {
                System.err.println("SELL_PRODUCT_CONTROLLER: Lỗi nghiêm trọng - Cập nhật tồn kho thất bại cho sản phẩm: " + ct.getMaSP() + ". Cần xử lý rollback hoặc thông báo.");
                return false;
            }
        }
        System.out.println("SELL_PRODUCT_CONTROLLER: Cập nhật tồn kho thành công.");
        if (suDungDiemDeGiamGia && maKH != null && !maKH.isEmpty()) {
            if (khachHangQuery.resetDiemTichLuy(maKH)) {
                System.out.println("SELL_PRODUCT_CONTROLLER: Đã reset điểm tích lũy cho MaKH: " + maKH);
            } else {
                System.err.println("SELL_PRODUCT_CONTROLLER: Lỗi khi reset điểm tích lũy cho MaKH: " + maKH + ". Hóa đơn đã tạo, tồn kho đã cập nhật.");
            }
        }

        System.out.println("SELL_PRODUCT_CONTROLLER: Quy trình bán hàng cho MaHDX: " + resultMaHDX + " hoàn tất thành công.");
        return true;
    }
}