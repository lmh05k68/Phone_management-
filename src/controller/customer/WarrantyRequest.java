package controller.customer;

import model.PhieuBaoHanh;
import model.SPCuThe;
import query.PhieuBaoHanhQuery;
import query.SPCuTheQuery;
import java.sql.SQLException;
import java.time.LocalDate;

public class WarrantyRequest {

    /**
     * Xử lý logic tạo một yêu cầu bảo hành mới.
     * @throws WarrantyRequestException nếu có lỗi nghiệp vụ xảy ra.
     * @throws RuntimeException nếu có lỗi hệ thống không mong muốn (ví dụ: lỗi SQL).
     */
    public void createWarrantyRequest(String maSPCuThe, Integer maHDX, int maKH) throws WarrantyRequestException {
        // Ngày nhận sản phẩm luôn là ngày hiện tại khi khách hàng tạo yêu cầu
        LocalDate ngayNhan = LocalDate.now();
        
        // 1. Validation đầu vào
        validateInputs(maSPCuThe, maHDX, ngayNhan);

        // 2. Validation nghiệp vụ
        SPCuThe sp = SPCuTheQuery.getById(maSPCuThe);
        if (sp == null) {
            throw new WarrantyRequestException("Không tìm thấy sản phẩm với mã: " + maSPCuThe);
        }
        
        if (sp.getMaHDX() == null || sp.getMaHDX() != maHDX.intValue()) {
            throw new WarrantyRequestException("Sản phẩm này không thuộc hóa đơn đã cung cấp.");
        }
        
        if (sp.getTrangThai() != SPCuThe.TrangThaiSP.DA_BAN) {
            throw new WarrantyRequestException("Chỉ có thể bảo hành sản phẩm có trạng thái 'Đã Bán'.");
        }
        
        if (PhieuBaoHanhQuery.isProductCurrentlyInWarranty(maSPCuThe)) {
            throw new WarrantyRequestException("Sản phẩm này hiện đang trong quá trình bảo hành.");
        }

        // 3. Tạo đối tượng và thực thi
        PhieuBaoHanh.TrangThaiBaoHanh trangThaiBanDau = PhieuBaoHanh.TrangThaiBaoHanh.DANG_XU_LY;
        PhieuBaoHanh pbh = new PhieuBaoHanh(maSPCuThe, ngayNhan, null, maKH, trangThaiBanDau, maHDX);

        try {
            // SỬA LỖI: Gọi đúng phương thức đã được định nghĩa
            Integer idBHGenerated = PhieuBaoHanhQuery.insertPhieuBaoHanhAndGetId(pbh);
            if (idBHGenerated == null || idBHGenerated <= 0) {
                throw new RuntimeException("Lỗi hệ thống: Không thể tạo phiếu bảo hành trong CSDL.");
            }
            // Trigger trong CSDL sẽ tự động cập nhật trạng thái của SanPhamCuThe thành 'Dang Bao Hanh'
        } catch (SQLException e) {
            // Chuyển SQLException thành một lỗi Runtime để View không cần biết về SQL
            throw new RuntimeException("Lỗi cơ sở dữ liệu khi tạo phiếu bảo hành: " + e.getMessage(), e);
        }
    }

    private void validateInputs(String maSPCuThe, Integer maHDX, LocalDate ngayNhan) throws WarrantyRequestException {
        if (maSPCuThe == null || maSPCuThe.trim().isEmpty()) {
            throw new WarrantyRequestException("Mã sản phẩm cụ thể không được để trống.");
        }
        if (maHDX == null || maHDX <= 0) {
            throw new WarrantyRequestException("Mã hóa đơn không hợp lệ.");
        }
        if (ngayNhan.isAfter(LocalDate.now())) {
            throw new WarrantyRequestException("Ngày nhận không thể là một ngày trong tương lai.");
        }
    }
}