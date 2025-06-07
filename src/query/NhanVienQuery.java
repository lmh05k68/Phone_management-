package query;

import dbConnection.DBConnection;
import model.NhanVien;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class NhanVienQuery {

    public boolean insertNhanVienInTransaction(NhanVien nv, Connection conn) throws SQLException {
        // SỬA TÊN CỘT TRONG SQL
        String sql = "INSERT INTO nhanvien (manv, tennv, ngaysinh, luong, sodienthoai) VALUES (?, ?, ?, ?, ?)";
        System.out.println("DEBUG NV_QUERY (insertNhanVienInTransaction): MaNV=" + nv.getMaNV() + ", SoDienThoai= " + nv.getSoDienThoai());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nv.getMaNV());
            stmt.setString(2, nv.getTenNV());
            try {
                LocalDate localDateNgaySinh = LocalDate.parse(nv.getNgaySinh());
                stmt.setDate(3, Date.valueOf(localDateNgaySinh));
            } catch (DateTimeParseException | NullPointerException e) {
                System.err.println("Lỗi định dạng hoặc ngày sinh null khi insert NhanVien: " + nv.getNgaySinh());
                throw new SQLException("Ngày sinh không đúng định dạng YYYY-MM-DD hoặc bị null.", e);
            }
            stmt.setDouble(4, nv.getLuong());
            stmt.setString(5, nv.getSoDienThoai()); // << SỬA GETTER, CỘT ĐÃ ĐÚNG

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean kiemTraMaNVTonTai(String maNV) {
        String sql = "SELECT 1 FROM nhanvien WHERE manv = ?"; // Giả sử tên cột là manv
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maNV);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiemTraMaNVTonTai cho MaNV " + maNV + ": " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    public static List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        // SỬA TÊN CỘT TRONG SQL
        String sql = "SELECT MaNV, TenNV, NgaySinh, Luong, SoDienThoai FROM NhanVien";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Date sqlDate = rs.getDate("NgaySinh");
                String ngaySinhStr = (sqlDate != null) ? sqlDate.toLocalDate().toString() : null;

                list.add(new NhanVien(
                    rs.getString("MaNV"),
                    rs.getString("TenNV"),
                    ngaySinhStr,
                    rs.getDouble("Luong"),
                    rs.getString("SoDienThoai") // << SỬA TÊN CỘT KHI LẤY TỪ RS
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<NhanVien> search(String keyword, String type) {
        List<NhanVien> list = new ArrayList<>();
        String column;
        switch (type) {
            case "Mã NV": column = "MaNV"; break;
            case "SĐT": column = "SoDienThoai"; break; // << SỬA TÊN CỘT
            case "Tên":
            default: column = "TenNV"; break;
        }

        // SỬA TÊN CỘT TRONG SQL
        String sql = "SELECT MaNV, TenNV, NgaySinh, Luong, SoDienThoai FROM NhanVien WHERE " + column + " ILIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date sqlDate = rs.getDate("NgaySinh");
                    String ngaySinhStr = (sqlDate != null) ? sqlDate.toLocalDate().toString() : null;
                    list.add(new NhanVien(
                        rs.getString("MaNV"),
                        rs.getString("TenNV"),
                        ngaySinhStr,
                        rs.getDouble("Luong"),
                        rs.getString("SoDienThoai") // << SỬA TÊN CỘT KHI LẤY TỪ RS
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public static boolean insert(NhanVien nv) {
        // SỬA TÊN CỘT TRONG SQL
        String sql = "INSERT INTO NhanVien (MaNV, TenNV, NgaySinh, Luong, SoDienThoai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nv.getMaNV());
            stmt.setString(2, nv.getTenNV());
            try {
                LocalDate localDateNgaySinh = LocalDate.parse(nv.getNgaySinh());
                stmt.setDate(3, Date.valueOf(localDateNgaySinh));
            } catch (DateTimeParseException | NullPointerException e) {
                System.err.println("Lỗi định dạng hoặc ngày sinh null khi insert nhân viên (static): " + nv.getNgaySinh());
                stmt.setNull(3, Types.DATE);
            }
            stmt.setDouble(4, nv.getLuong());
            stmt.setString(5, nv.getSoDienThoai()); // << SỬA GETTER
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { // Bỏ IllegalArgumentException vì parse Date đã có try-catch
            System.err.println("Lỗi SQL khi insert nhân viên (static): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean update(NhanVien nv) {
        // SỬA TÊN CỘT TRONG SQL
        String sql = "UPDATE NhanVien SET TenNV = ?, NgaySinh = ?, Luong = ?, SoDienThoai = ? WHERE MaNV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nv.getTenNV());
            try {
                LocalDate localDateNgaySinh = LocalDate.parse(nv.getNgaySinh());
                stmt.setDate(2, Date.valueOf(localDateNgaySinh));
            } catch (DateTimeParseException | NullPointerException e) {
                System.err.println("Lỗi định dạng hoặc ngày sinh null khi update nhân viên: " + nv.getNgaySinh());
                stmt.setNull(2, Types.DATE);
            }
            stmt.setDouble(3, nv.getLuong());
            stmt.setString(4, nv.getSoDienThoai()); // << SỬA GETTER
            stmt.setString(5, nv.getMaNV());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { // Bỏ IllegalArgumentException
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maNV);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean exists(String maNV) {
        String sql = "SELECT 1 FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maNV);
            try(ResultSet rs = stmt.executeQuery()){
                 return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiểm tra sự tồn tại của nhân viên MaNV " + maNV + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}