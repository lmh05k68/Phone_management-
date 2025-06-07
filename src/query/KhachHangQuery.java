package query;

import model.KhachHang;
import dbConnection.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangQuery {
    public static boolean insert(KhachHang kh) {
        String sql = "INSERT INTO khachhang (makh, hoten, sdtkh, sodiemtichluy) VALUES (?, ?, ?, ?)";
        System.out.println("DEBUG KH_QUERY (static insert): Chuẩn bị insert KhachHang: MaKH=" + kh.getMaKH());
        try (Connection conn = DBConnection.getConnection(); // Connection riêng
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, kh.getMaKH());
            stmt.setString(2, kh.getHoTen());
            stmt.setString(3, kh.getSdtKH());
            stmt.setInt(4, kh.getSoDiemTichLuy());

            boolean result = stmt.executeUpdate() > 0;
            System.out.println("DEBUG KH_QUERY (static insert): Insert KhachHang thành công " + result);
            return result;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm khách hàng (static insert): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean insertKhachHangInTransaction(KhachHang kh, Connection conn) throws SQLException {
        String sql = "INSERT INTO khachhang (makh, hoten, sdtkh, sodiemtichluy) VALUES (?, ?, ?, ?)";
        System.out.println("DEBUG KH_QUERY (insertKhachHangInTransaction): MaKH=" + kh.getMaKH());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kh.getMaKH());
            stmt.setString(2, kh.getHoTen());
            stmt.setString(3, kh.getSdtKH());
            stmt.setInt(4, kh.getSoDiemTichLuy());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    public static boolean exists(String maKH) {
        String sql = "SELECT 1 FROM khachhang WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiểm tra sự tồn tại của khách hàng MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public static List<KhachHang> getCustomersWithAccounts() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT kh.* FROM khachhang kh " +
                     "JOIN taikhoan tk ON kh.makh = tk.madoituong " + 
                     "WHERE tk.vaitro = 'khachhang'"; 
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                    rs.getString("makh"),
                    rs.getString("hoten"),
                    rs.getString("sdtkh"),
                    rs.getInt("sodiemtichluy")
                );
                list.add(kh);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách khách hàng có tài khoản: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static List<KhachHang> searchCustomersWithAccounts(String keyword) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT kh.* FROM khachhang kh " +
                     "JOIN taikhoan tk ON kh.makh = tk.madoituong " +
                     "WHERE tk.vaitro = 'khachhang' " +
                     "AND (kh.hoten ILIKE ? OR kh.sdtkh ILIKE ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    KhachHang kh = new KhachHang(
                        rs.getString("makh"),
                        rs.getString("hoten"),
                        rs.getString("sdtkh"),
                        rs.getInt("sodiemtichluy")
                    );
                    list.add(kh);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm khách hàng có tài khoản: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static KhachHang getKhachHangById(String maKH) { // Sửa lại tên phương thức cho nhất quán
        String sql = "SELECT makh, hoten, sdtkh, sodiemtichluy FROM khachhang WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                        rs.getString("makh"),
                        rs.getString("hoten"),
                        rs.getString("sdtkh"),
                        rs.getInt("sodiemtichluy")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy khách hàng bằng ID MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public KhachHang getKhachHangByMaKH(String maKH) {
        return getKhachHangById(maKH);
    }


    public static boolean capNhatThongTin(KhachHang kh) {
        String sql = "UPDATE khachhang SET hoten = ?, sdtkh = ?, sodiemtichluy = ? WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kh.getHoTen());
            stmt.setString(2, kh.getSdtKH());
            stmt.setInt(3, kh.getSoDiemTichLuy());
            stmt.setString(4, kh.getMaKH());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật thông tin khách hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean congDiemTichLuy(String maKH, int soDiemCongThem) {
        String sql = "UPDATE khachhang SET sodiemtichluy = COALESCE(sodiemtichluy, 0) + ? WHERE makh = ?";
        System.out.println("DEBUG KHACHHANG_QUERY: Cộng thêm " + soDiemCongThem + " điểm cho MaKH " + maKH);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, soDiemCongThem);
            stmt.setString(2, maKH);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("DEBUG KHACHHANG_QUERY: Cập nhật điểm thành công cho MaKH " + maKH);
                return true;
            } else {
                System.err.println("DEBUG KHACHHANG_QUERY: Không tìm thấy MaKH " + maKH + " để cập nhật điểm.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cộng điểm tích lũy cho khách hàng MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int getSoDiemTichLuy(String maKH) {
        String sql = "SELECT sodiemtichluy FROM khachhang WHERE makh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maKH);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("sodiemtichluy");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy số điểm tích lũy cho MaKH " + maKH + ": " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
}