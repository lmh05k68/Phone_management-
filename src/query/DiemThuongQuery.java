package query;

import model.DiemThuong;
import dbConnection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class DiemThuongQuery {
    public static void ghiNhanTichDiem(int maKH, int maHDX, int soDiemTichDuoc, Connection conn) throws SQLException {
        if (soDiemTichDuoc <= 0) return; 
        String sql = "INSERT INTO DiemThuong (MaKH, MaHDX, SoDiem) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            stmt.setInt(2, maHDX);
            stmt.setInt(3, soDiemTichDuoc); // Ghi nhận số điểm dương
            stmt.executeUpdate();
        }
    }
    public static void ghiNhanSuDungDiem(int maKH, int maHDX, int soDiemSuDung, Connection conn) throws SQLException {
        if (soDiemSuDung <= 0) return;

        String sql = "INSERT INTO DiemThuong (MaKH, MaHDX, SoDiem) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            stmt.setInt(2, maHDX);
            stmt.setInt(3, -soDiemSuDung); // <<< Ghi nhận số điểm ÂM
            stmt.executeUpdate();
        }
    }
    public static List<DiemThuong> getLichSuGiaoDichDiem(int maKH) {
        List<DiemThuong> list = new ArrayList<>();
        String sql = "SELECT maTichDiem, MaKH, MaHDX, SoDiem, NgayTichLuy " +
                     "FROM DiemThuong WHERE MaKH = ? ORDER BY NgayTichLuy DESC, maTichDiem DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DiemThuong dt = new DiemThuong(
                        rs.getInt("maTichDiem"),
                        rs.getInt("MaKH"),
                        rs.getInt("MaHDX"),
                        rs.getInt("SoDiem"),
                        rs.getDate("NgayTichLuy").toLocalDate()
                    );
                    list.add(dt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử điểm thưởng cho MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}