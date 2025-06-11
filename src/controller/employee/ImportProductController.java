package controller.employee;

import dbConnection.DBConnection;
import model.NhapHangItem;
import query.HoaDonNhapQuery;
import query.SPCuTheQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportProductController {
    
    private static final Logger logger = Logger.getLogger(ImportProductController.class.getName());

    /**
     * Thực hiện nghiệp vụ nhập hàng trong một transaction.
     *
     * @param maNV          Mã nhân viên thực hiện.
     * @param maNCC         Mã nhà cung cấp.
     * @param itemsToImport Danh sách các sản phẩm cần nhập.
     * @return true nếu toàn bộ quá trình thành công, false nếu có lỗi và đã rollback.
     */
    public boolean nhapHang(int maNV, int maNCC, List<NhapHangItem> itemsToImport) {
        // Kiểm tra đầu vào cơ bản
        if (itemsToImport == null || itemsToImport.isEmpty()) {
            logger.warning("Thao tác nhập hàng bị hủy do danh sách sản phẩm rỗng.");
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            // 1. BẮT ĐẦU TRANSACTION
            conn.setAutoCommit(false);
            logger.info("Bắt đầu transaction nhập hàng cho NV: " + maNV);

            // 2. Tạo hóa đơn nhập và lấy về MaHDN
            int maHDN = HoaDonNhapQuery.insertAndGetId(maNV, maNCC, conn);
            logger.info("Đã tạo hóa đơn nhập với MaHDN: " + maHDN);


            // 3. Dùng MaHDN để thêm hàng loạt sản phẩm cụ thể
            SPCuTheQuery.insertBatch(itemsToImport, maHDN, conn);
            logger.info("Đã thêm " + itemsToImport.size() + " loại sản phẩm vào MaHDN: " + maHDN);

            // 4. Nếu mọi thứ thành công, COMMIT TRANSACTION
            conn.commit();
            logger.info("Transaction nhập hàng thành công. Đã commit.");
            return true;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi SQL trong quá trình nhập hàng, đang thực hiện rollback...", e);
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.info("Rollback transaction thành công.");
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Lỗi nghiêm trọng khi thực hiện rollback transaction.", ex);
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Lỗi khi đóng kết nối.", e);
                }
            }
        }
    }
}