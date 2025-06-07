package query;

import dbConnection.DBConnection;
import model.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamQuery {

	public List<SanPham> getAllSanPham() {
        List<SanPham> dsSP = new ArrayList<>();
        String sql = "SELECT * FROM SanPham ORDER BY MaSP"; // Sắp xếp để giao diện ổn định

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SanPham sp = new SanPham(
                        rs.getString("MaSP"),
                        rs.getString("TenSP"),
                        rs.getString("Mau"),
                        rs.getDouble("DonGia"),
                        rs.getString("NuocSX"),
                        rs.getString("HangSX"),
                        rs.getInt("SoLuong")
                );
                dsSP.add(sp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dsSP;
    }

    public List<SanPham> searchSanPhamByTen(String keyword) {
        List<SanPham> dsSP = new ArrayList<>();
        String sql = "SELECT * FROM SanPham WHERE TenSP LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                SanPham sp = new SanPham(
                    rs.getString("MaSP"),
                    rs.getString("TenSP"),
                    rs.getString("Mau"),
                    rs.getDouble("DonGia"),
                    rs.getString("NuocSX"),
                    rs.getString("HangSX"),
                    rs.getInt("SoLuong")
                );
                dsSP.add(sp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dsSP;
    }

    public List<SanPham> filterSanPhamByHangSX(String hangSX) {
        List<SanPham> dsSP = new ArrayList<>();
        String sql = "SELECT * FROM SanPham WHERE HangSX = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hangSX);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                SanPham sp = new SanPham(
                    rs.getString("MaSP"),
                    rs.getString("TenSP"),
                    rs.getString("Mau"),
                    rs.getDouble("DonGia"),
                    rs.getString("NuocSX"),
                    rs.getString("HangSX"),
                    rs.getInt("SoLuong")
                );
                dsSP.add(sp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dsSP;
    }

    public int getSoLuongTonKho(String maSP) {
        String sql = "SELECT SoLuong FROM SanPham WHERE MaSP = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maSP);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("SoLuong");
            } else {
                System.err.println("Không tìm thấy sản phẩm với mã: " + maSP);
                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean updateSoLuong(String maSP, int soLuongBan) {
        // Kiểm tra tồn kho trước khi cập nhật
        int tonKho = getSoLuongTonKho(maSP);
        if (tonKho < soLuongBan) {
            System.err.println("Không đủ tồn kho để bán. Tồn kho hiện tại: " + tonKho + ", cần bán: " + soLuongBan);
            return false;
        }

        String sql = "UPDATE SanPham SET SoLuong = SoLuong - ? WHERE MaSP = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, soLuongBan);
            stmt.setString(2, maSP);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean tangSoLuong(String maSP, int soLuongTangThem) {
        String sql = "UPDATE SanPham SET SoLuong = SoLuong + ? WHERE MaSP = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, soLuongTangThem);
            stmt.setString(2, maSP);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSanPham(SanPham sp) {
        String sql = "UPDATE SanPham SET TenSP = ?, Mau = ?, DonGia = ?, NuocSX = ?, HangSX = ?, SoLuong = ? WHERE MaSP = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sp.getTenSP());
            stmt.setString(2, sp.getMau());
            stmt.setDouble(3, sp.getDonGia());
            stmt.setString(4, sp.getNuocSX());
            stmt.setString(5, sp.getHangSX());
            stmt.setInt(6, sp.getSoLuong());
            stmt.setString(7, sp.getMaSP());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSanPham(String maSP) {
        String sql = "DELETE FROM SanPham WHERE MaSP = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maSP);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertSanPham(SanPham sp) {
        String sql = "INSERT INTO SanPham (MaSP, TenSP, Mau, DonGia, NuocSX, HangSX, SoLuong) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sp.getMaSP());
            stmt.setString(2, sp.getTenSP());
            stmt.setString(3, sp.getMau());
            stmt.setDouble(4, sp.getDonGia());
            stmt.setString(5, sp.getNuocSX());
            stmt.setString(6, sp.getHangSX());
            stmt.setInt(7, sp.getSoLuong());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAllHangSX() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT HangSX FROM SanPham ORDER BY HangSX ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("HangSX"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<SanPham> getAllSanPhamOrderedByMaSP() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM SanPham ORDER BY MaSP ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SanPham sp = new SanPham(
                        rs.getString("MaSP"),
                        rs.getString("TenSP"),
                        rs.getString("Mau"),
                        rs.getDouble("DonGia"),
                        rs.getString("NuocSX"),
                        rs.getString("HangSX"),
                        rs.getInt("SoLuong")
                );
                list.add(sp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<SanPham> getAll() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM SanPham";
        try (Connection conn = DBConnection .getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                SanPham sp = new SanPham(
                    rs.getString("MaSP"),
                    rs.getString("TenSP"),
                    rs.getString("Mau"),
                    rs.getDouble("DonGia"),
                    rs.getString("NuocSX"),
                    rs.getString("HangSX"),
                    rs.getInt("SoLuong")
                );
                list.add(sp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}