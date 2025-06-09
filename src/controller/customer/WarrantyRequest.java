package controller.customer;

import model.PhieuBaoHanh;
import query.PhieuBaoHanhQuery;
import java.time.LocalDate;

public class WarrantyRequest {
    public boolean createWarrantyRequest(int maSP, Integer maHDX, LocalDate ngayNhan, LocalDate ngayTraDuKien, int maKH) {
        System.out.println("WARRANTY_REQUEST_CONTROLLER: Tạo yêu cầu BH. MaKH=" + maKH + ", MaSP=" + maSP +
                           ", MaHDX=" + maHDX + ", NgayNhan=" + ngayNhan + ", NgayTraDuKien=" + ngayTraDuKien);

        if (maSP <= 0 || ngayNhan == null) {
            System.err.println("WARRANTY_REQUEST_CONTROLLER: Thông tin đầu vào không hợp lệ (MaSP hoặc Ngày nhận).");
            return false;
        }
        if (ngayTraDuKien != null && ngayTraDuKien.isBefore(ngayNhan)) {
            System.err.println("WARRANTY_REQUEST_CONTROLLER: Ngày trả dự kiến không thể trước ngày nhận.");
            return false;
        }

        String trangThaiBanDau = "Chờ tiếp nhận";
        PhieuBaoHanh pbh = new PhieuBaoHanh(maSP, ngayNhan, ngayTraDuKien, maKH, trangThaiBanDau, maHDX);

        Integer idBHGenerated = PhieuBaoHanhQuery.insertPhieuBaoHanhAndGetId(pbh);

        if (idBHGenerated != null && idBHGenerated > 0) {
            System.out.println("WARRANTY_REQUEST_CONTROLLER: Yêu cầu bảo hành đã được tạo với IDBH: " + idBHGenerated);
            return true;
        } else {
            System.err.println("WARRANTY_REQUEST_CONTROLLER: Lỗi khi lưu phiếu bảo hành vào cơ sở dữ liệu.");
            return false;
        }
    }
}