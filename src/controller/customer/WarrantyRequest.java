package controller.customer;

import dbConnection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WarrantyRequest {
    public static boolean createWarrantyRequest(String idBH, String maSP, String ngayNhan, String ngayTra, String maKH) {
        if (isIdBHExists(idBH)) {
            return false; 
        }

        String sql = "INSERT INTO PhieuBaoHanh (idBH, MaSP, NgayNhanSanPham, NgayTraSanPham, MaKH, TrangThai) " +
                     "VALUES (?, ?, ?, ?, ?, 'Đang xử lý')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idBH);
            ps.setString(2, maSP);
            ps.setDate(3, java.sql.Date.valueOf(ngayNhan));
            ps.setDate(4, java.sql.Date.valueOf(ngayTra));
            ps.setString(5, maKH);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isIdBHExists(String idBH) {
        String sql = "SELECT 1 FROM PhieuBaoHanh WHERE idBH = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idBH);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
}