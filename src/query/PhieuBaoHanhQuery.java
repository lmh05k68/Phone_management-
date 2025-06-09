package query;

import dbConnection.DBConnection;
import model.PhieuBaoHanh;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhieuBaoHanhQuery {

    public static Integer insertPhieuBaoHanhAndGetId(PhieuBaoHanh pbh) {
        // Câu lệnh SQL đã đúng, bao gồm MaHDX
        String sql = "INSERT INTO PhieuBaoHanh (MaSP, NgayNhanSanPham, NgayTraSanPham, MaKH, TrangThai, MaHDX) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        ResultSet generatedKeys = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pbh.getMaSP());
            stmt.setDate(2, Date.valueOf(pbh.getNgayNhanSanPham()));
            if (pbh.getNgayTraSanPham() != null) {
                stmt.setDate(3, Date.valueOf(pbh.getNgayTraSanPham()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            stmt.setInt(4, pbh.getMaKH());
            stmt.setString(5, pbh.getTrangThai());

            // Lấy maHDX từ đối tượng pbh và set cho PreparedStatement
            if (pbh.getMaHDX() != null) {
                stmt.setInt(6, pbh.getMaHDX());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("PhieuBaoHanhQuery (insertAndGetId): Chèn phiếu bảo hành thất bại.");
                return null;
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                System.out.println("PhieuBaoHanhQuery (insertAndGetId): Phiếu BH được chèn với IDBH: " + id);
                return id;
            } else {
                System.err.println("PhieuBaoHanhQuery (insertAndGetId): Chèn thành công nhưng không lấy được ID.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("PhieuBaoHanhQuery (insertAndGetId): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
             if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException ex) { /* ignored */ }
        }
    }

    public static List<PhieuBaoHanh> getByMaKH(int maKH) {
        List<PhieuBaoHanh> list = new ArrayList<>();
        // Câu lệnh SQL đã đúng, bao gồm mahdx
        String sql = "SELECT idbh, masp, ngaynhansanpham, ngaytrasanpham, makh, trangthai, mahdx " +
                     "FROM PhieuBaoHanh WHERE makh = ? ORDER BY ngaynhansanpham DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate ngayTra = null;
                    Date sqlDateTra = rs.getDate("ngaytrasanpham");
                    if (sqlDateTra != null) {
                        ngayTra = sqlDateTra.toLocalDate();
                    }
                    Integer maHDX = rs.getObject("mahdx") == null ? null : rs.getInt("mahdx");

                    // SỬA LỖI: Gọi constructor đúng của PhieuBaoHanh
                    PhieuBaoHanh pb = new PhieuBaoHanh(
                        rs.getInt("idbh"),
                        rs.getInt("masp"),
                        rs.getDate("ngaynhansanpham").toLocalDate(),
                        ngayTra,
                        rs.getInt("makh"),
                        rs.getString("trangthai"),
                        maHDX // Truyền maHDX
                    );
                    list.add(pb);
                }
            }
        } catch (SQLException e) {
            System.err.println("PhieuBaoHanhQuery (getByMaKH): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static List<PhieuBaoHanh> getAll() {
        List<PhieuBaoHanh> list = new ArrayList<>();
        // Câu lệnh SQL đã đúng, bao gồm mahdx
        String sql = "SELECT idbh, masp, ngaynhansanpham, ngaytrasanpham, makh, trangthai, mahdx " +
                     "FROM PhieuBaoHanh ORDER BY ngaynhansanpham DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                 LocalDate ngayTra = null;
                 Date sqlDateTra = rs.getDate("ngaytrasanpham");
                 if (sqlDateTra != null) {
                     ngayTra = sqlDateTra.toLocalDate();
                 }
                 Integer maHDX = rs.getObject("mahdx") == null ? null : rs.getInt("mahdx");

                // SỬA LỖI: Gọi constructor đúng của PhieuBaoHanh
                PhieuBaoHanh pb = new PhieuBaoHanh(
                    rs.getInt("idbh"),
                    rs.getInt("masp"),
                    rs.getDate("ngaynhansanpham").toLocalDate(),
                    ngayTra,
                    rs.getInt("makh"),
                    rs.getString("trangthai"),
                    maHDX // Truyền maHDX
                );
                list.add(pb);
            }
        } catch (SQLException e) {
            System.err.println("PhieuBaoHanhQuery (getAll): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updateTrangThaiAndNgayTra(int idBH, String trangThaiMoi, LocalDate ngayTraSanPhamMoi) {
        String sql = "UPDATE PhieuBaoHanh SET trangthai = ?, ngaytrasanpham = ? WHERE idbh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThaiMoi);
            if (ngayTraSanPhamMoi != null) {
                stmt.setDate(2, Date.valueOf(ngayTraSanPhamMoi));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setInt(3, idBH);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("PhieuBaoHanhQuery (updateTrangThaiAndNgayTra): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static PhieuBaoHanh getByIdBH(int idBH) {
        // Câu lệnh SQL đã đúng, bao gồm mahdx
        String sql = "SELECT idbh, masp, ngaynhansanpham, ngaytrasanpham, makh, trangthai, mahdx " +
                     "FROM PhieuBaoHanh WHERE idbh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDate ngayTra = null;
                    Date sqlDateTra = rs.getDate("ngaytrasanpham");
                    if (sqlDateTra != null) {
                        ngayTra = sqlDateTra.toLocalDate();
                    }
                    Integer maHDX = rs.getObject("mahdx") == null ? null : rs.getInt("mahdx");
                    return new PhieuBaoHanh(
                        rs.getInt("idbh"),
                        rs.getInt("masp"),
                        rs.getDate("ngaynhansanpham").toLocalDate(),
                        ngayTra,
                        rs.getInt("makh"),
                        rs.getString("trangthai"),
                        maHDX 
                    );
                }
            }
        } catch (SQLException e) {
             System.err.println("PhieuBaoHanhQuery (getByIdBH): Lỗi SQL cho IDBH " + idBH + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}