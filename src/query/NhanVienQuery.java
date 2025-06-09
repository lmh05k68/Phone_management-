package query;

import dbConnection.DBConnection;
import model.NhanVien;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVienQuery {
    public static Integer insertNhanVienAndGetId(NhanVien nv, Connection conn) throws SQLException {
        String sql = "INSERT INTO nhanvien (tennv, ngaysinh, luong, sodienthoai) VALUES (?, ?, ?, ?)";
        System.out.println("NV_QUERY (insertAndGetId): Chuẩn bị insert NhanVien: TenNV=" + nv.getTenNV());
        ResultSet generatedKeys = null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nv.getTenNV());
            if (nv.getNgaySinh() != null) {
                pstmt.setDate(2, Date.valueOf(nv.getNgaySinh()));
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            pstmt.setDouble(3, nv.getLuong());
            pstmt.setString(4, nv.getSoDienThoai());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                 System.err.println("NV_QUERY (insertAndGetId): Chèn NhanVien thất bại, không có hàng nào được thêm.");
                return null;
            }

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                System.out.println("NV_QUERY (insertAndGetId): NhanVien được chèn thành công với ID: " + id);
                return id;
            } else {
                System.err.println("NV_QUERY (insertAndGetId): Chèn NhanVien thành công nhưng không lấy được ID.");
                return null;
            }
        } finally {
            if(generatedKeys != null) try {generatedKeys.close();} catch (SQLException e) {/* ignored */}
        }
    }

    /**
     * Chèn một nhân viên mới. MaNV là tự sinh.
     * Phương thức này tự quản lý Connection.
     * @param nv Đối tượng NhanVien (không cần set MaNV).
     * @return true nếu chèn thành công, false nếu thất bại.
     */
    public static boolean insert(NhanVien nv) {
        String sql = "INSERT INTO nhanvien (tennv, ngaysinh, luong, sodienthoai) VALUES (?, ?, ?, ?)";
        System.out.println("NV_QUERY (insert): Chuẩn bị insert NhanVien: TenNV=" + nv.getTenNV());
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Không cần RETURN_GENERATED_KEYS nếu chỉ trả về boolean

            pstmt.setString(1, nv.getTenNV());
            if (nv.getNgaySinh() != null) {
                pstmt.setDate(2, Date.valueOf(nv.getNgaySinh()));
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            pstmt.setDouble(3, nv.getLuong());
            pstmt.setString(4, nv.getSoDienThoai());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("NV_QUERY (insert): NhanVien được chèn thành công.");
                return true;
            } else {
                System.err.println("NV_QUERY (insert): Chèn NhanVien thất bại.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("NV_QUERY (insert): Lỗi SQL khi chèn nhân viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public static boolean exists(int maNV) {
        String sql = "SELECT 1 FROM nhanvien WHERE manv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maNV);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("NV_QUERY (exists): Lỗi SQL khi kiểm tra sự tồn tại của nhân viên MaNV " + maNV + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT manv, tennv, ngaysinh, luong, sodienthoai FROM nhanvien ORDER BY tennv ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                LocalDate ngaySinh = null;
                Date sqlDateNgaySinh = rs.getDate("ngaysinh");
                if (sqlDateNgaySinh != null) {
                    ngaySinh = sqlDateNgaySinh.toLocalDate();
                }
                list.add(new NhanVien(
                    rs.getInt("manv"),
                    rs.getString("tennv"),
                    ngaySinh,
                    rs.getDouble("luong"),
                    rs.getString("sodienthoai")
                ));
            }
        } catch (SQLException e) {
            System.err.println("NV_QUERY (getAll): Lỗi SQL khi lấy tất cả nhân viên: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static List<NhanVien> search(String keyword, String type) {
        List<NhanVien> list = new ArrayList<>();
        String column;
        boolean searchById = false;
        switch (type) {
            case "Mã NV":
                column = "manv";
                searchById = true;
                break;
            case "SĐT": column = "sodienthoai"; break;
            case "Tên":
            default: column = "LOWER(tennv)"; // Để tìm kiếm không phân biệt hoa thường
                     keyword = keyword.toLowerCase(); // Chuyển keyword sang chữ thường
                     break;
        }

        String sql = "SELECT manv, tennv, ngaysinh, luong, sodienthoai FROM nhanvien WHERE ";
        if (searchById) {
            sql += column + " = ?";
        } else if (type.equals("SĐT")) {
            sql += column + " LIKE ?"; // Cho phép tìm SĐT chứa keyword
        }
        else { // Tìm tên
            sql += column + " LIKE ?"; // ILIKE cho PostgreSQL, hoặc LOWER() cho các DB khác
        }
        sql += " ORDER BY tennv ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (searchById) {
                try {
                    stmt.setInt(1, Integer.parseInt(keyword));
                } catch (NumberFormatException e) {
                    System.err.println("NV_QUERY (search): Từ khóa cho Mã NV '" + keyword + "' không phải là số.");
                    return list;
                }
            } else {
                stmt.setString(1, "%" + keyword + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                     LocalDate ngaySinh = null;
                    Date sqlDateNgaySinh = rs.getDate("ngaysinh");
                    if (sqlDateNgaySinh != null) {
                        ngaySinh = sqlDateNgaySinh.toLocalDate();
                    }
                    list.add(new NhanVien(
                        rs.getInt("manv"),
                        rs.getString("tennv"),
                        ngaySinh,
                        rs.getDouble("luong"),
                        rs.getString("sodienthoai")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("NV_QUERY (search): Lỗi SQL khi tìm kiếm nhân viên (" + type + ": " + keyword + "): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(NhanVien nv) {
        // MaNV của đối tượng nv đã là int và được set khi đọc từ DB hoặc sau khi insert
        String sql = "UPDATE nhanvien SET tennv = ?, ngaysinh = ?, luong = ?, sodienthoai = ? WHERE manv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nv.getTenNV());
            if (nv.getNgaySinh() != null) {
                stmt.setDate(2, Date.valueOf(nv.getNgaySinh()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setDouble(3, nv.getLuong());
            stmt.setString(4, nv.getSoDienThoai());
            stmt.setInt(5, nv.getMaNV());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("NV_QUERY (update): Lỗi SQL khi cập nhật nhân viên MaNV " + nv.getMaNV() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(int maNV) {
        String sql = "DELETE FROM nhanvien WHERE manv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maNV);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
             System.err.println("NV_QUERY (delete): Lỗi SQL khi xóa nhân viên MaNV " + maNV + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
 // Trong NhanVienQuery.java
    public static NhanVien getNhanVienById(int maNV) {
        String sql = "SELECT manv, tennv, ngaysinh, luong, sodienthoai FROM nhanvien WHERE manv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maNV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDate ngaySinh = null;
                    Date sqlDateNgaySinh = rs.getDate("ngaysinh");
                    if (sqlDateNgaySinh != null) {
                        ngaySinh = sqlDateNgaySinh.toLocalDate();
                    }
                    return new NhanVien(
                        rs.getInt("manv"),
                        rs.getString("tennv"),
                        ngaySinh,
                        rs.getDouble("luong"),
                        rs.getString("sodienthoai")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("NV_QUERY (getNhanVienById): Lỗi SQL cho MaNV " + maNV + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}