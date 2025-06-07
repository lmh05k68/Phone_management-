package controller.employee;

import model.HoaDonNhap;
import model.ChiTietHDNhap;
import query.HoaDonNhapQuery;
import query.SanPhamQuery;

import java.time.LocalDate;
import java.util.List;

public class ImportProductController {
    private HoaDonNhapQuery hdnQuery = new HoaDonNhapQuery();
    private SanPhamQuery spQuery = new SanPhamQuery();

    public boolean nhapHang(String maHDN, String maNV, String maNCC, List<ChiTietHDNhap> dsChiTiet) {
        if (maHDN == null || maHDN.isEmpty() || maNV == null || maNCC == null || dsChiTiet == null || dsChiTiet.isEmpty()) {
            return false;
        }

        HoaDonNhap hdn = new HoaDonNhap(maHDN, LocalDate.now(), maNV, maNCC);
        boolean inserted = hdnQuery.insertHoaDonNhapWithMa(hdn);  // bạn cần tạo hàm này
        if (!inserted) return false;

        boolean successCT = hdnQuery.insertChiTietNhap(dsChiTiet);
        if (!successCT) return false;

        for (ChiTietHDNhap ct : dsChiTiet) {
            spQuery.tangSoLuong(ct.getMaSP(), ct.getSoLuong());
        }

        return true;
    }
}