package query;

import dbConnection.DBConnection;
import model.DoiTra;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoiTraQuery {
    private static final Logger logger = Logger.getLogger(DoiTraQuery.class.getName());
    private static boolean kiemTraMaDonHangTonTai(int maDonHang, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM hoadonxuat WHERE mahdx = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maDonHang);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    public static Integer themYeuCauDoiTraAndGetId(DoiTra dt) {
        String sql = "INSERT INTO doitra (maspcuthe, makh, mahdx, ngaydoitra, lydo, trangthai) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            if (!kiemTraMaDonHangTonTai(dt.getMaDonHang(), conn)) {
                conn.rollback();
                return null;
            }
            stmt.setString(1, dt.getMaSPCuThe());
            stmt.setInt(2, dt.getMaKH());
            stmt.setInt(3, dt.getMaDonHang());
            stmt.setDate(4, Date.valueOf(dt.getNgayDoiTra()));
            stmt.setString(5, dt.getLyDo());
            stmt.setString(6, dt.getTrangThai());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return null;
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    conn.commit();
                    return generatedId;
                } else {
                    conn.rollback();
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi SQL khi thêm yêu cầu đổi/trả.", e);
            return null;
        }
    }

    // *** SỬA ĐỔI 1: Cập nhật getAllDoiTra để lấy cột mới ***
    public static List<DoiTra> getAllDoiTra() {
        List<DoiTra> list = new ArrayList<>();
        // Thêm cột "maspdoi" vào câu lệnh SELECT
        String sql = "SELECT iddt, maspcuthe, makh, mahdx, ngaydoitra, lydo, trangthai, maspdoi FROM doitra ORDER BY ngaydoitra DESC, iddt DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new DoiTra(
                        rs.getInt("iddt"),
                        rs.getString("maspcuthe"),
                        rs.getInt("makh"),
                        rs.getInt("mahdx"),
                        rs.getDate("ngaydoitra").toLocalDate(),
                        rs.getString("lydo"),
                        rs.getString("trangthai"),
                        rs.getString("maspdoi") // Lấy giá trị từ cột mới
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách đổi trả.", e);
        }
        return list;
    }

    // Phương thức này giữ nguyên
    public static boolean capNhatTrangThaiDoiTra(int idDT, String trangThaiMoi) {
        String sql = "UPDATE doitra SET trangthai = ? WHERE iddt = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThaiMoi);
            stmt.setInt(2, idDT);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật trạng thái đổi trả cho ID " + idDT, e);
            return false;
        }
    }


    // *** SỬA ĐỔI 2: Cập nhật thucHienDoiTra để ghi vào cột mới ***
    public static boolean thucHienDoiTra(int idDT, String maSPCuThe_Cu, String maSPCuThe_Moi, String trangThaiSPCu) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Bước 1: Cập nhật sản phẩm cũ (khách trả) - Đã sửa lỗi từ lần trước
            String sqlUpdateOld = "UPDATE SanPhamCuThe SET TrangThai = ?, MaHDX = NULL WHERE MaSPCuThe = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateOld)) {
                stmt.setString(1, trangThaiSPCu);
                stmt.setString(2, maSPCuThe_Cu);
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Không thể cập nhật sản phẩm cũ '" + maSPCuThe_Cu + "'. Sản phẩm không tồn tại.");
                }
            }

            // Bước 2: Cập nhật sản phẩm mới (đưa cho khách)
            String sqlUpdateNew = "UPDATE SanPhamCuThe SET TrangThai = 'Da Ban', MaHDX = (SELECT MaHDX FROM DoiTra WHERE idDT = ?) WHERE MaSPCuThe = ? AND TrangThai = 'Trong Kho'";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateNew)) {
                stmt.setInt(1, idDT);
                stmt.setString(2, maSPCuThe_Moi);
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Sản phẩm mới '" + maSPCuThe_Moi + "' không có sẵn trong kho hoặc đã được bán.");
                }
            }

            // Bước 3: Cập nhật trạng thái và cột maspdoi của yêu cầu đổi trả
            String sqlUpdateDoiTra = "UPDATE doitra SET trangthai = 'Da hoan tat', maspdoi = ? WHERE iddt = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateDoiTra)) {
                 stmt.setString(1, maSPCuThe_Moi); // Gán mã sản phẩm mới
                 stmt.setInt(2, idDT);
                 if (stmt.executeUpdate() == 0) {
                     throw new SQLException("Không thể cập nhật trạng thái yêu cầu đổi trả.");
                 }
            }

            conn.commit();
            logger.log(Level.INFO, "Giao dịch đổi hàng cho ID " + idDT + " thành công.");
            return true;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi giao dịch đổi hàng. Đang rollback... Lỗi: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { logger.log(Level.SEVERE, "Lỗi khi rollback", ex); }
            }
            throw new RuntimeException(e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { logger.log(Level.SEVERE, "Lỗi khi đóng kết nối", ex); }
            }
        }
    }
}