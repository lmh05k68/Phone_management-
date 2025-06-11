package controller.admin;

import model.HoaDonNhap;
import model.HoaDonXuat;
import query.HoaDonNhapQuery;
import query.HoaDonXuatQuery;
import query.ThongKeQuery;
import view.admin.ThongKeView;

import java.math.BigDecimal;
import java.util.List;

public class ThongKeController {
    private final ThongKeView view;

    public ThongKeController(ThongKeView view) {
        this.view = view;
    }

    public void thongKeTheoThang(int thang, int nam) {
        BigDecimal doanhThu = ThongKeQuery.getDoanhThuThang(thang, nam);
        BigDecimal chiTieu = ThongKeQuery.getChiTieuThang(thang, nam);
        BigDecimal loiNhuan = doanhThu.subtract(chiTieu);
        List<HoaDonXuat> dsHDX = HoaDonXuatQuery.getHoaDonByMonthAndYear(thang, nam);
        List<HoaDonNhap> dsHDN = HoaDonNhapQuery.getHoaDonNhapByMonthAndYear(thang, nam);
        view.updateThongKeTongHop(doanhThu, chiTieu, loiNhuan, thang, nam);
        view.updateTableHDX(dsHDX);
        view.updateTableHDN(dsHDN);
    }
}