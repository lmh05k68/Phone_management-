package query;

import dbConnection.DBConnection;
import model.TraGop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TraGopQuery {

    public boolean insertPhieuTraGop(TraGop p) {
        String sql = "INSERT INTO PhieuTraGop (MaPhieuTG, MaHDX, SoThang, LaiSuat, TienGoc, NgayBatDau, DaThanhToan) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getMaPhieuTG());
            stmt.setString(2, p.getMaHDX());
            stmt.setInt(3, p.getSoThang());
            stmt.setDouble(4, p.getLaiSuat());
            stmt.setDouble(5, p.getTienGoc());
            stmt.setDate(6, Date.valueOf(p.getNgayBatDau()));
            stmt.setBoolean(7, p.isDaThanhToan());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TraGop> getAllPhieuTraGop() {
        List<TraGop> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuTraGop ORDER BY NgayBatDau DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TraGop p = new TraGop(
                        rs.getString("MaPhieuTG"),
                        rs.getString("MaHDX"),
                        rs.getInt("SoThang"),
                        rs.getDouble("LaiSuat"),
                        rs.getDouble("TienGoc"),
                        rs.getDate("NgayBatDau").toLocalDate(),
                        rs.getBoolean("DaThanhToan")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void updateTrangThaiTraGop() {
        String sql = "UPDATE PhieuTraGop SET DaThanhToan = TRUE " +
                "WHERE DaThanhToan = FALSE AND NgayBatDau + INTERVAL '1 month' * SoThang <= CURRENT_DATE";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}