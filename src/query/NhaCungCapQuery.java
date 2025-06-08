package query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.NhaCungCap;
import dbConnection.DBConnection;

public class NhaCungCapQuery {
    public List<NhaCungCap> getAllNhaCungCap() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap(
                    rs.getString("MaNCC"),
                    rs.getString("TenNCC"),
                    rs.getString("DiaChi"),
                    rs.getString("SdtNCC")
                );
                list.add(ncc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public boolean insertNhaCungCap(NhaCungCap ncc) {
        String sql = "INSERT INTO NhaCungCap (MaNCC, TenNCC, DiaChi, SdtNCC) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ncc.getMaNCC());
            stmt.setString(2, ncc.getTenNCC());
            stmt.setString(3, ncc.getDiaChi());
            stmt.setString(4, ncc.getSdtNCC());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateNhaCungCap(NhaCungCap ncc) {
        String sql = "UPDATE NhaCungCap SET TenNCC = ?, DiaChi = ?, SdtNCC = ? WHERE MaNCC = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ncc.getTenNCC());
            stmt.setString(2, ncc.getDiaChi());
            stmt.setString(3, ncc.getSdtNCC());
            stmt.setString(4, ncc.getMaNCC());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteNhaCungCap(String maNCC) {
        String sql = "DELETE FROM NhaCungCap WHERE MaNCC = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maNCC);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<NhaCungCap> searchNhaCungCap(String keyword) {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap WHERE TenNCC LIKE ? OR SdtNCC LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new NhaCungCap(
                        rs.getString("MaNCC"),
                        rs.getString("TenNCC"),
                        rs.getString("DiaChi"),
                        rs.getString("SdtNCC")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public boolean isMaNCCExists(String maNCC) {
        String sql = "SELECT COUNT(*) FROM nha_cung_cap WHERE ma_ncc = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maNCC);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<String> getDistinctProvinces() {
        List<String> provinces = new ArrayList<>();
        String sql = "SELECT DISTINCT DiaChi FROM NhaCungCap";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                provinces.add(rs.getString("DiaChi"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return provinces;
    }

    public List<String> getDistinctPhonePrefixes() {
        List<String> prefixes = new ArrayList<>();
        String sql = "SELECT DISTINCT LEFT(SdtNCC, 3) AS prefix FROM NhaCungCap";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                prefixes.add(rs.getString("prefix"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prefixes;
    }
}