package query;

import model.DiemThuong;
import dbConnection.DBConnection;

import java.sql.Connection;
import java.sql.Date; 
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
public class DiemThuongQuery {
    public boolean insertDiemThuong(DiemThuong dt) {
        System.out.println("DEBUG DIEMTHUONG_QUERY: insertDiemThuong - MaTichDiem = '" + dt.getMaTichDiem() + "', MaKH = '" + dt.getMaKH() + "', SoDiem = " + dt.getSoDiem());
        if (dt.getMaTichDiem() == null || dt.getMaTichDiem().trim().isEmpty()) {
            System.err.println("Lỗi: MaTichDiem không được để trống.");
            return false;
        }
        String sql = "INSERT INTO diemthuong (matichdiem, makh, tenkh, madonhang, sodiem, ngaytichluy, sdtkh) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dt.getMaTichDiem());
            stmt.setString(2, dt.getMaKH());
            stmt.setString(3, dt.getTenKH());
            stmt.setString(4, dt.getMaDonHang());
            stmt.setInt(5, dt.getSoDiem());
            stmt.setDate(6, Date.valueOf(dt.getNgayTichLuy())); // Chuyển từ LocalDate sang java.sql.Date
            stmt.setString(7, dt.getSdtKH());

            boolean result = stmt.executeUpdate() > 0;
            System.out.println("DEBUG DIEMTHUONG_QUERY: insertDiemThuong - executeUpdate result = " + result);
            return result;

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi insert DiemThuong: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public int layTongDiemDaTichLuyTuBangDiemThuong(String maKH) {
        String sql = "SELECT COALESCE(SUM(sodiem), 0) FROM diemthuong WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maKH);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi layTongDiemDaTichLuyTuBangDiemThuong cho MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}