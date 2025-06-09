package controller.employee;

import model.HoaDonNhap;
import model.ChiTietHDNhap;
import query.HoaDonNhapQuery;
import query.SanPhamQuery; // Import SanPhamQuery

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import dbConnection.DBConnection;
import java.sql.Connection;

public class ImportProductController {

    public boolean nhapHang(int maNV, int maNCC, List<ChiTietHDNhap> dsChiTiet) {
        System.out.println("IMPORT_CONTROLLER: Bắt đầu nhập hàng. MaNV=" + maNV + ", MaNCC=" + maNCC +
                           ", Số lượng chi tiết=" + (dsChiTiet != null ? dsChiTiet.size() : 0));

        if (dsChiTiet == null || dsChiTiet.isEmpty()) {
            System.err.println("IMPORT_CONTROLLER: Danh sách chi tiết sản phẩm rỗng. Không thể nhập hàng.");
            return false;
        }

        Connection conn = null;
        Integer maHDNGenerated = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            HoaDonNhap hdn = new HoaDonNhap(LocalDate.now(), maNV, maNCC);
            maHDNGenerated = HoaDonNhapQuery.insertHoaDonNhapAndGetId(hdn, conn);

            if (maHDNGenerated == null || maHDNGenerated <= 0) {
                System.err.println("IMPORT_CONTROLLER: Lỗi khi tạo hóa đơn nhập. Rollback.");
                conn.rollback();
                return false;
            }
            System.out.println("IMPORT_CONTROLLER: Hóa đơn nhập MaHDN " + maHDNGenerated + " đã tạo.");

            for (ChiTietHDNhap ct : dsChiTiet) {
                ct.setMaHDN(maHDNGenerated);
            }

            boolean chiTietInserted = HoaDonNhapQuery.insertListChiTietNhap(dsChiTiet, conn);
            if (!chiTietInserted) {
                System.err.println("IMPORT_CONTROLLER: Lỗi khi lưu chi tiết hóa đơn nhập. Rollback.");
                conn.rollback();
                return false;
            }
            System.out.println("IMPORT_CONTROLLER: Chi tiết hóa đơn nhập đã được lưu cho MaHDN: " + maHDNGenerated);

            // Bước 4: Cập nhật số lượng tồn kho cho từng sản phẩm <<<< THÊM LOGIC NÀY
            System.out.println("IMPORT_CONTROLLER: Bắt đầu cập nhật số lượng sản phẩm trong kho.");
            for (ChiTietHDNhap ct : dsChiTiet) {
                boolean soLuongUpdated = SanPhamQuery.tangSoLuong(ct.getMaSP(), ct.getSoLuong(), conn);
                if (!soLuongUpdated) {
                    // Lý do thất bại có thể là MaSP không tồn tại.
                    // Hoặc nếu tangSoLuong có logic phức tạp hơn.
                    System.err.println("IMPORT_CONTROLLER: Lỗi khi tăng số lượng tồn kho cho SP ID: " + ct.getMaSP() + ". Rollback.");
                    conn.rollback();
                    return false;
                }
            }
            System.out.println("IMPORT_CONTROLLER: Số lượng sản phẩm trong kho đã được cập nhật.");

            conn.commit();
            System.out.println("IMPORT_CONTROLLER: Nhập hàng thành công cho hóa đơn MaHDN: " + maHDNGenerated);
            return true;

        } catch (SQLException e) {
            System.err.println("IMPORT_CONTROLLER: SQLException trong quá trình nhập hàng: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } catch (Exception e) {
            System.err.println("IMPORT_CONTROLLER: Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}