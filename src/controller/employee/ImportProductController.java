package controller.employee;

import model.HoaDonNhap;
import model.ChiTietHDNhap;
import query.HoaDonNhapQuery; 
import query.SanPhamQuery;   
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

            // Bước 1: Tạo đối tượng HoaDonNhap (MaHDN sẽ tự sinh khi insert)
            HoaDonNhap hdn = new HoaDonNhap(LocalDate.now(), maNV, maNCC);

            // Bước 2: Insert HoaDonNhap vào DB và lấy lại MaHDN tự sinh
            // Giả định HoaDonNhapQuery.insertHoaDonNhapAndGetId(hdn, conn) trả về MaHDN (Integer)
            maHDNGenerated = HoaDonNhapQuery.insertHoaDonNhapAndGetId(hdn, conn);

            if (maHDNGenerated == null || maHDNGenerated <= 0) {
                System.err.println("IMPORT_CONTROLLER: Lỗi khi tạo hóa đơn nhập hoặc không lấy được ID. Rollback.");
                conn.rollback();
                return false;
            }
            System.out.println("IMPORT_CONTROLLER: Hóa đơn nhập với MaHDN (tự sinh) " + maHDNGenerated + " đã được tạo.");

            // Bước 3: Gán MaHDN tự sinh cho từng ChiTietHDNhap và insert chúng vào DB
            for (ChiTietHDNhap ct : dsChiTiet) {
                ct.setMaHDN(maHDNGenerated); // Gán MaHDN (int) cho từng chi tiết
            }

            // Giả định HoaDonNhapQuery.insertListChiTietNhap(dsChiTiet, conn) chèn toàn bộ danh sách
            boolean chiTietInserted = HoaDonNhapQuery.insertListChiTietNhap(dsChiTiet, conn);
            if (!chiTietInserted) {
                System.err.println("IMPORT_CONTROLLER: Lỗi khi lưu danh sách chi tiết hóa đơn nhập. Rollback.");
                conn.rollback();
                return false;
            }
            System.out.println("IMPORT_CONTROLLER: Danh sách chi tiết hóa đơn nhập đã được lưu cho MaHDN: " + maHDNGenerated);

            // Bước 4: Cập nhật số lượng tồn kho cho từng sản phẩm
            System.out.println("IMPORT_CONTROLLER: Bắt đầu cập nhật số lượng sản phẩm trong kho.");
            for (ChiTietHDNhap ct : dsChiTiet) {
                // Giả định SanPhamQuery.tangSoLuong(maSP, soLuongNhap, conn)
                boolean soLuongUpdated = SanPhamQuery.tangSoLuong(ct.getMaSP(), ct.getSoLuong(), conn);
                if (!soLuongUpdated) {
                    System.err.println("IMPORT_CONTROLLER: Lỗi khi cập nhật số lượng tồn kho cho SP ID: " + ct.getMaSP() + ". Rollback.");
                    conn.rollback();
                    return false;
                }
            }
            System.out.println("IMPORT_CONTROLLER: Số lượng sản phẩm trong kho đã được cập nhật.");

            conn.commit(); // Hoàn tất transaction thành công
            System.out.println("IMPORT_CONTROLLER: Nhập hàng thành công cho hóa đơn MaHDN: " + maHDNGenerated);
            return true;

        } catch (SQLException e) {
            System.err.println("IMPORT_CONTROLLER: SQLException trong quá trình nhập hàng: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("IMPORT_CONTROLLER: Đang cố gắng rollback do SQLException.");
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("IMPORT_CONTROLLER: Lỗi khi thực hiện rollback: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            return false;
        } catch (Exception e) { // Bắt các lỗi không mong muốn khác
            System.err.println("IMPORT_CONTROLLER: Lỗi không xác định trong quá trình nhập hàng: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("IMPORT_CONTROLLER: Đang cố gắng rollback do Exception không xác định.");
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("IMPORT_CONTROLLER: Lỗi khi thực hiện rollback: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            return false;
        }
        finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Trả lại trạng thái autoCommit
                    conn.close();             // Đóng kết nối
                } catch (SQLException e) {
                    System.err.println("IMPORT_CONTROLLER: Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}