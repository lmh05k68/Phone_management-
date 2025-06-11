package query;

import dbConnection.DBConnection; 
import model.PhieuBaoHanh;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhieuBaoHanhQuery {
    
    private static final Logger logger = Logger.getLogger(PhieuBaoHanhQuery.class.getName());
    private static PhieuBaoHanh mapResultSetToPhieuBaoHanh(ResultSet rs) throws SQLException {
        LocalDate ngayTra = null;
        Date sqlNgayTra = rs.getDate("ngaytrasanpham");
        if (!rs.wasNull()) {
            ngayTra = sqlNgayTra.toLocalDate();
        }

        PhieuBaoHanh.TrangThaiBaoHanh trangThai = PhieuBaoHanh.TrangThaiBaoHanh.fromString(rs.getString("trangthai"));

        return new PhieuBaoHanh(
                rs.getInt("idbh"),
                rs.getString("maspcuthe"),
                rs.getDate("ngaynhansanpham").toLocalDate(),
                ngayTra,
                rs.getInt("makh"),
                trangThai,
                rs.getInt("mahdx")
        );
    }

    // Lấy TẤT CẢ phiếu bảo hành
    public static List<PhieuBaoHanh> getAll() {
        List<PhieuBaoHanh> list = new ArrayList<>();
        String sql = "SELECT idbh, maspcuthe, ngaynhansanpham, ngaytrasanpham, makh, trangthai, mahdx " +
                     "FROM PhieuBaoHanh ORDER BY ngaynhansanpham DESC, idbh DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToPhieuBaoHanh(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách phiếu bảo hành: " + e.getMessage(), e);
        }
        return list;
    }

    // Lấy phiếu bảo hành THEO MÃ KHÁCH HÀNG
    public static List<PhieuBaoHanh> getByMaKH(int maKH) {
        List<PhieuBaoHanh> list = new ArrayList<>();
        String sql = "SELECT idbh, maspcuthe, ngaynhansanpham, ngaytrasanpham, makh, trangthai, mahdx " +
                     "FROM PhieuBaoHanh WHERE makh = ? ORDER BY ngaynhansanpham DESC, idbh DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maKH);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPhieuBaoHanh(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy phiếu bảo hành theo Mã KH " + maKH + ": " + e.getMessage(), e);
        }
        return list;
    }

    // Cập nhật TRẠNG THÁI và NGÀY TRẢ
    public static boolean updateTrangThaiAndNgayTra(int idBH, PhieuBaoHanh.TrangThaiBaoHanh trangThaiMoi, LocalDate ngayTraMoi) {
        // Trigger `func_auto_update_spct_status_on_warranty` sẽ tự động cập nhật bảng SanPhamCuThe
        // nên ta chỉ cần cập nhật bảng PhieuBaoHanh.
        // Tuy nhiên, trigger đó chỉ chạy khi INSERT. Ta cần một trigger khác cho UPDATE.
        // Giả sử logic cập nhật trạng thái SP cụ thể được xử lý ở đây hoặc trong trigger khác.
        String sql = "UPDATE PhieuBaoHanh SET trangthai = ?, ngaytrasanpham = ? WHERE idbh = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trangThaiMoi.getValue());

            if (ngayTraMoi != null) {
                pstmt.setDate(2, Date.valueOf(ngayTraMoi));
            } else {
                pstmt.setNull(2, Types.DATE);
            }

            pstmt.setInt(3, idBH);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật phiếu bảo hành ID " + idBH + ": " + e.getMessage(), e);
            return false;
        }
    }
 // Thêm phương thức này vào trong class PhieuBaoHanhQuery đã có của bạn
    public static boolean isProductCurrentlyInWarranty(String maSPCuThe) {
        String sql = "SELECT 1 FROM PhieuBaoHanh WHERE maspcuthe = ? AND trangthai != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maSPCuThe);
            pstmt.setString(2, PhieuBaoHanh.TrangThaiBaoHanh.DA_TRA_KHACH.getValue());

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // true nếu có kết quả
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi kiểm tra sản phẩm đang bảo hành: " + maSPCuThe, e);
            // Trong trường hợp lỗi, giả định an toàn là không có để tránh chặn người dùng oan
            return false;
        }
    }
    public static Integer insertPhieuBaoHanhAndGetId(PhieuBaoHanh pbh) throws SQLException {
        // CSDL của bạn định nghĩa MaHDX là NOT NULL, nên không cần kiểm tra null ở đây
        String sql = "INSERT INTO PhieuBaoHanh (maspcuthe, ngaynhansanpham, ngaytrasanpham, makh, trangthai, mahdx) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, pbh.getMaSPCuThe());
            pstmt.setDate(2, Date.valueOf(pbh.getNgayNhanSanPham()));
            if (pbh.getNgayTraSanPham() != null) {
                pstmt.setDate(3, Date.valueOf(pbh.getNgayTraSanPham()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            
            pstmt.setInt(4, pbh.getMaKH());
            pstmt.setString(5, pbh.getTrangThai().getValue()); // Lấy chuỗi từ Enum
            pstmt.setInt(6, pbh.getMaHDX());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Tạo phiếu bảo hành thất bại, không có hàng nào được thêm.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Trả về idbh được tạo
                } else {
                    throw new SQLException("Tạo phiếu bảo hành thành công nhưng không lấy được ID.");
                }
            }
        }
    }}