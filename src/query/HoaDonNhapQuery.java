package query;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.time.LocalDate;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.ArrayList;
import dbConnection.DBConnection; 
import model.HoaDonNhap;
import model.ChiTietHDNhap;

public class HoaDonNhapQuery {
    public static Integer insertHoaDonNhapAndGetId(HoaDonNhap hdn, Connection conn) throws SQLException {
        String sql = "INSERT INTO hoadonnhap (ngaynhap, manv, mancc) VALUES (?, ?, ?)";
        System.out.println("HOADONNHAP_QUERY (insertAndGetId): Chuẩn bị insert HĐN. MaNV=" + hdn.getMaNV() + ", MaNCC=" + hdn.getMaNCC());

        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (hdn.getNgayNhap() != null) {
                stmt.setDate(1, java.sql.Date.valueOf(hdn.getNgayNhap()));
            } else {
                stmt.setNull(1, Types.DATE); // Hoặc giá trị mặc định nếu DB cho phép và có logic
            }
            if (hdn.getMaNV() > 0) { 
                 stmt.setInt(2, hdn.getMaNV());
            } else {
                 stmt.setNull(2, Types.INTEGER);
            }
            if (hdn.getMaNCC() > 0) {
                stmt.setInt(3, hdn.getMaNCC());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("HOADONNHAP_QUERY (insertAndGetId): Chèn HĐN thất bại, không có hàng nào được thêm.");
                return null;
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                System.out.println("HOADONNHAP_QUERY (insertAndGetId): HĐN được chèn thành công với MaHDN (tự sinh): " + generatedId);
                return generatedId;
            } else {
                System.err.println("HOADONNHAP_QUERY (insertAndGetId): Chèn HĐN thành công nhưng không lấy được ID tự sinh.");
                throw new SQLException("Không thể lấy ID tự sinh cho Hóa Đơn Nhập.");
            }
        } catch (SQLException e) {
            System.err.println("HOADONNHAP_QUERY (insertAndGetId): SQLException: " + e.getMessage());
            throw e;
        } finally {
            if (generatedKeys != null) {
                try { generatedKeys.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    public static boolean insertListChiTietNhap(List<ChiTietHDNhap> dsChiTiet, Connection conn) throws SQLException {
        String sql = "INSERT INTO chitiethdnhap (masp, mahdn, soluong, dongianhap) VALUES (?, ?, ?, ?)";

        if (dsChiTiet == null || dsChiTiet.isEmpty()) {
            System.out.println("HOADONNHAP_QUERY (insertListChiTietNhap): Danh sách chi tiết rỗng, không có gì để chèn.");
            return true;
        }
        System.out.println("HOADONNHAP_QUERY (insertListChiTietNhap): Chuẩn bị chèn " + dsChiTiet.size() + " chi tiết HĐN.");

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (ChiTietHDNhap ct : dsChiTiet) {
                if (ct.getMaHDN() <= 0) { 
                    throw new SQLException("MaHDN trong ChiTietHDNhap không hợp lệ (<=0) cho MaSP: " + ct.getMaSP());
                }
                stmt.setInt(1, ct.getMaSP());
                stmt.setInt(2, ct.getMaHDN()); 
                stmt.setInt(3, ct.getSoLuong()); 
                stmt.setDouble(4, ct.getDonGiaNhap()); 
                stmt.addBatch();
            }

            int[] result = stmt.executeBatch();
            for (int i = 0; i < result.length; i++) {
                if (result[i] == Statement.EXECUTE_FAILED) {
                    System.err.println("HOADONNHAP_QUERY (insertListChiTietNhap): Chi tiết HĐN thứ " + (i+1) + " (MaSP: " + dsChiTiet.get(i).getMaSP() + ") chèn thất bại.");
                    throw new SQLException("Một hoặc nhiều chi tiết HĐN chèn thất bại. Chi tiết lỗi xem ở log server DB.");
                }
                if (result[i] < 0 && result[i] != Statement.SUCCESS_NO_INFO) {
                     System.err.println("HOADONNHAP_QUERY (insertListChiTietNhap): Chi tiết HĐN thứ " + (i+1) + " có mã kết quả không mong muốn: " + result[i]);
                     throw new SQLException("Chi tiết HĐN có mã kết quả không mong muốn: " + result[i]);
                }
            }
            System.out.println("HOADONNHAP_QUERY (insertListChiTietNhap): Tất cả " + result.length + " chi tiết HĐN đã được xử lý thành công (batch execution).");
            return true;
        } catch (SQLException e) {
            System.err.println("HOADONNHAP_QUERY (insertListChiTietNhap): SQLException: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    public static List<HoaDonNhap> getHoaDonNhapByMonthAndYear(int thang, int nam) {
        List<HoaDonNhap> list = new ArrayList<>();
        String sql = "SELECT hdn.mahdn, hdn.ngaynhap, hdn.manv, hdn.mancc " +
                     "FROM hoadonnhap hdn " +
                     "WHERE EXTRACT(MONTH FROM hdn.ngaynhap) = ? AND EXTRACT(YEAR FROM hdn.ngaynhap) = ? " +
                     "ORDER BY hdn.ngaynhap DESC, hdn.mahdn ASC";

        System.out.println("HOADONNHAP_QUERY (getByMonthYear): SQL=" + sql + ", Thang=" + thang + ", Nam=" + nam);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate ngayLap = null;
                Date sqlDateNgayLap = rs.getDate("ngaylap");
                if (sqlDateNgayLap != null) {
                    ngayLap = sqlDateNgayLap.toLocalDate();
                }
                HoaDonNhap hdn = new HoaDonNhap(
                        rs.getInt("mahdn"),
                        ngayLap, 
                        rs.getInt("manv"),
                        rs.getInt("mancc")
                );
                list.add(hdn);
            }
            System.out.println("HOADONNHAP_QUERY (getByMonthYear): Tìm thấy " + list.size() + " HĐN cho " + thang + "/" + nam);
        } catch (SQLException e) {
            System.err.println("HOADONNHAP_QUERY (getByMonthYear): Lỗi khi lấy HĐN theo tháng/năm: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }
    public static double tinhTongTienChoMotHoaDonNhap(int maHDN) {
        String sql = "SELECT COALESCE(SUM(ctn.soluong * ctn.dongianhap), 0.0) " +
                     "FROM chitiethdnhap ctn " +
                     "WHERE ctn.mahdn = ?";
        System.out.println("HOADONNHAP_QUERY (tinhTong): SQL=" + sql + " cho MaHDN=" + maHDN);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maHDN);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("HOADONNHAP_QUERY (tinhTong): Lỗi khi tính tổng tiền cho MaHDN " + maHDN + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return 0.0;
    }
    public static boolean checkMaHDNExists(String maHDNString) {
        int maHDN;
        try {
            maHDN = Integer.parseInt(maHDNString);
        } catch (NumberFormatException e) {
            System.err.println("HOADONNHAP_QUERY (checkMaHDNExists): MaHDN không phải là số hợp lệ: " + maHDNString);
            return false;
        }

        String sql = "SELECT 1 FROM hoadonnhap WHERE mahdn = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maHDN);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("HOADONNHAP_QUERY (checkMaHDNExists): Lỗi khi kiểm tra MaHDN " + maHDN + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}