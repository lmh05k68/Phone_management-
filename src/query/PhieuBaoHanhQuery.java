package query;

import dbConnection.DBConnection;
import model.PhieuBaoHanh;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhieuBaoHanhQuery {

    // Sửa: dùng String thay vì int
    public List<PhieuBaoHanh> getByMaKH(String maKH) {
        List<PhieuBaoHanh> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuBaoHanh WHERE MaKH = ? ORDER BY NgayNhanSanPham DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maKH); // Sửa từ setInt -> setString

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PhieuBaoHanh pb = new PhieuBaoHanh(
                        rs.getString("idBH"),
                        rs.getString("MaSP"),
                        rs.getString("NgayNhanSanPham"),
                        rs.getString("NgayTraSanPham"),
                        rs.getString("MaKH"),
                        rs.getString("TrangThai")
                    );
                    list.add(pb);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<PhieuBaoHanh> getAllWarrantyRequests() {
        List<PhieuBaoHanh> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuBaoHanh ORDER BY NgayNhanSanPham DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PhieuBaoHanh pb = new PhieuBaoHanh(
                    rs.getString("idBH"),
                    rs.getString("MaSP"),
                    rs.getString("NgayNhanSanPham"),
                    rs.getString("NgayTraSanPham"),
                    rs.getString("MaKH"),
                    rs.getString("TrangThai")
                );
                list.add(pb);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean updateTrangThai(String idBH, String trangThaiMoi) {
        String sql = "UPDATE PhieuBaoHanh SET TrangThai = ? WHERE idBH = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trangThaiMoi);
            stmt.setString(2, idBH);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}