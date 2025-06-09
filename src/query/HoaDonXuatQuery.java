package query;

import model.ChiTietHDXuat;
import model.HoaDonXuat;
import model.KhachHang;
import model.DiemThuong;
import dbConnection.DBConnection; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonXuatQuery {
    public static Integer insertHoaDonXuatFullAndGetId(HoaDonXuat hdx,
                                                 String tenKHMoi, String sdtKHMoi,
                                                 boolean suDungDiemDeGiamGia, Connection conn) throws SQLException {
        System.out.println("HOADONXUAT_QUERY (insertFullGetId): Bắt đầu. MaKH ban đầu trong HĐX=" + hdx.getMaKH());
        Integer maHDXGenerated = null;
        Integer maKHLuuVaoHDX = hdx.getMaKH();
        if (maKHLuuVaoHDX == null &&
            (tenKHMoi != null && !tenKHMoi.trim().isEmpty() && sdtKHMoi != null && !sdtKHMoi.trim().isEmpty())) {
            KhachHang khMoi = new KhachHang(tenKHMoi.trim(), sdtKHMoi.trim(), 0); 
            Integer maKHGenerated = KhachHangQuery.insertKhachHangAndGetId(khMoi, conn);
            if (maKHGenerated == null) {
                throw new SQLException("Lỗi khi tạo khách hàng mới, không nhận được ID.");
            }
            maKHLuuVaoHDX = maKHGenerated;
            hdx.setMaKH(maKHLuuVaoHDX);
            System.out.println("HOADONXUAT_QUERY (insertFullGetId): Đã tạo KH mới với MaKH: " + maKHLuuVaoHDX);
        }
        String sqlHdx = "INSERT INTO hoadonxuat (ngaylap, thanhtien, mucthue, manv, makh) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmtHdx = null;
        ResultSet generatedKeysHdx = null;
        try {
            stmtHdx = conn.prepareStatement(sqlHdx, Statement.RETURN_GENERATED_KEYS);
            stmtHdx.setDate(1, Date.valueOf(hdx.getNgayLap()));
            stmtHdx.setDouble(2, hdx.getThanhTien());
            stmtHdx.setDouble(3, hdx.getMucThue());
            stmtHdx.setInt(4, hdx.getMaNV());
            if (maKHLuuVaoHDX != null) {
                stmtHdx.setInt(5, maKHLuuVaoHDX);
            } else {
                stmtHdx.setNull(5, Types.INTEGER); // Cho khách vãng lai
            }

            int hdxRows = stmtHdx.executeUpdate();
            if (hdxRows == 0) {
                throw new SQLException("Không thể chèn hóa đơn xuất (hoadonxuat).");
            }

            generatedKeysHdx = stmtHdx.getGeneratedKeys();
            if (generatedKeysHdx.next()) {
                maHDXGenerated = generatedKeysHdx.getInt(1);
                hdx.setMaHDX(maHDXGenerated); // Gán lại MaHDX cho đối tượng
                System.out.println("HOADONXUAT_QUERY (insertFullGetId): HĐX MaHDX=" + maHDXGenerated + " đã chèn.");
            } else {
                throw new SQLException("Không lấy được ID tự sinh cho HĐX sau khi chèn.");
            }
        } finally {
            if (generatedKeysHdx != null) try { generatedKeysHdx.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmtHdx != null) try { stmtHdx.close(); } catch (SQLException e) { e.printStackTrace(); }
        }


        // Bước 3: Insert chitiethdxuat
        if (hdx.getChiTietList() == null || hdx.getChiTietList().isEmpty()) {
            throw new SQLException("Danh sách chi tiết hóa đơn rỗng, không thể tiếp tục.");
        }
        String sqlCt = "INSERT INTO chitiethdxuat (masp, mahdx, soluong, dongiaxuat) VALUES (?, ?, ?, ?)";
        PreparedStatement stmtCt = null;
        try {
            stmtCt = conn.prepareStatement(sqlCt);
            for (ChiTietHDXuat ct : hdx.getChiTietList()) {
                ct.setMaHDX(maHDXGenerated); // Gán MaHDX cho từng chi tiết
                stmtCt.setInt(1, ct.getMaSP());
                stmtCt.setInt(2, ct.getMaHDX());
                stmtCt.setInt(3, ct.getSoLuong());
                stmtCt.setDouble(4, ct.getDonGiaXuat());
                stmtCt.addBatch();
            }
            stmtCt.executeBatch(); // Thực thi batch insert
            System.out.println("HOADONXUAT_QUERY (insertFullGetId): Đã chèn " + hdx.getChiTietList().size() + " chi tiết HĐX cho MaHDX=" + maHDXGenerated);
        } finally {
            if (stmtCt != null) try { stmtCt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Bước 4: Xử lý điểm thưởng (Tích điểm nếu không dùng điểm để giảm giá và có MaKH)
        // GIẢ ĐỊNH TRIGGER TRONG DB SẼ TỰ ĐỘNG CẬP NHẬT TỔNG ĐIỂM TRONG BẢNG KHACHHANG
        if (!suDungDiemDeGiamGia && maKHLuuVaoHDX != null) {
            int diemCongThem = (int) (hdx.getThanhTien() / 10000); // Ví dụ: 10,000 VND = 1 điểm
            if (diemCongThem > 0) {
                // Lấy thông tin khách hàng để lưu vào bảng diemthuong (nếu cần)
                // Nếu khách hàng vừa được tạo, tenKHMoi và sdtKHMoi đã có.
                // Nếu khách hàng cũ, cần query lại.
                String tenKHChoDiem = "N/A";
                String sdtKHChoDiem = "N/A";

                if (tenKHMoi != null && !tenKHMoi.trim().isEmpty() && maKHLuuVaoHDX.equals(hdx.getMaKH()) ) { // KH mới được tạo
                    tenKHChoDiem = tenKHMoi.trim();
                    sdtKHChoDiem = (sdtKHMoi != null) ? sdtKHMoi.trim() : "N/A";
                } else { // KH cũ, query lại thông tin
                    KhachHang khHienTai = KhachHangQuery.getKhachHangById(maKHLuuVaoHDX); // Cần có phương thức này
                    if (khHienTai != null) {
                        tenKHChoDiem = khHienTai.getHoTen();
                        sdtKHChoDiem = khHienTai.getSdtKH();
                    }
                }

                DiemThuong dt = new DiemThuong(
                        maKHLuuVaoHDX,
                        tenKHChoDiem,
                        maHDXGenerated, // MaDonHang (chính là MaHDX)
                        diemCongThem,
                        hdx.getNgayLap(), // NgayTichLuy
                        sdtKHChoDiem
                );

                Integer maTichDiemGenerated = DiemThuongQuery.insertDiemThuongAndGetId(dt, conn);
                if (maTichDiemGenerated == null || maTichDiemGenerated <= 0) {
                     throw new SQLException("Lỗi khi tạo bản ghi chi tiết điểm thưởng (không nhận được ID MaTichDiem).");
                }
                System.out.println("HOADONXUAT_QUERY (insertFullGetId): Đã tạo phiếu điểm MaTichDiem=" + maTichDiemGenerated +
                                   " cho KH " + maKHLuuVaoHDX + " với " + diemCongThem + " điểm.");
                // LƯU Ý: Không gọi KhachHangQuery.congDiemTichLuy(...) ở đây nữa
                // vì trigger trg_update_diemtichluy hoặc trg_recalculate_after_insert_diemthuong
                // trên bảng diemthuong sẽ tự động cập nhật sodiemtichluy trong bảng khachhang.
            } else {
                 System.out.println("HOADONXUAT_QUERY (insertFullGetId): Không đủ điều kiện giá trị hóa đơn để tích điểm ("+ diemCongThem + " điểm) cho MaKH " + maKHLuuVaoHDX);
            }
        } else {
            LogNoPointReason(suDungDiemDeGiamGia, maKHLuuVaoHDX);
        }
        return maHDXGenerated;
    }


    public static List<ChiTietHDXuat> getChiTietHDXuat(int maHDX) {
        List<ChiTietHDXuat> list = new ArrayList<>();
        String sql = "SELECT masp, mahdx, soluong, dongiaxuat FROM chitiethdxuat WHERE mahdx = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maHDX);
            rs = stmt.executeQuery();
            while (rs.next()) {
                // Giả sử constructor ChiTietHDXuat(int maHDX, int maSP, int soLuong, double donGiaXuat)
                list.add(new ChiTietHDXuat(
                    rs.getInt("mahdx"),
                    rs.getInt("masp"),
                    rs.getInt("soluong"),
                    rs.getDouble("dongiaxuat")
                ));
            }
        } catch (SQLException e) {
            System.err.println("HOADONXUAT_QUERY (getChiTietHDXuat): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }

    public static List<HoaDonXuat> getHoaDonByKhachHang(int maKH) {
        List<HoaDonXuat> list = new ArrayList<>();
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat WHERE makh = ? ORDER BY ngaylap DESC, mahdx DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maKH);
            rs = stmt.executeQuery();
            while (rs.next()) {
                // Giả sử constructor HoaDonXuat(int maHDX, LocalDate ngayLap, double thanhTien, double mucThue, int maNV, Integer maKH)
                // Model HoaDonXuat nên có MaKH là Integer để chấp nhận null
                list.add(new HoaDonXuat(
                    rs.getInt("mahdx"),
                    rs.getDate("ngaylap").toLocalDate(),
                    rs.getDouble("thanhtien"),
                    rs.getDouble("mucthue"),
                    rs.getInt("manv"),
                    rs.getObject("makh", Integer.class) // Lấy MaKH, có thể null
                ));
            }
        } catch (SQLException e) {
            System.err.println("HOADONXUAT_QUERY (getHoaDonByKhachHang): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }

    public static List<HoaDonXuat> getHoaDonByMonthAndYear(int thang, int nam) {
        List<HoaDonXuat> list = new ArrayList<>();
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat " +
                     "WHERE EXTRACT(MONTH FROM ngaylap) = ? AND EXTRACT(YEAR FROM ngaylap) = ? " +
                     "ORDER BY ngaylap DESC, mahdx DESC";
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
                list.add(new HoaDonXuat(
                    rs.getInt("mahdx"),
                    rs.getDate("ngaylap").toLocalDate(),
                    rs.getDouble("thanhtien"),
                    rs.getDouble("mucthue"),
                    rs.getInt("manv"),
                    rs.getObject("makh", Integer.class)
                ));
            }
        } catch (SQLException e) {
            System.err.println("HOADONXUAT_QUERY (getHoaDonByMonthAndYear): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }

    public static List<HoaDonXuat> getAllHoaDonXuat() {
        List<HoaDonXuat> list = new ArrayList<>();
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat ORDER BY ngaylap DESC, mahdx DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new HoaDonXuat(
                    rs.getInt("mahdx"),
                    rs.getDate("ngaylap").toLocalDate(),
                    rs.getDouble("thanhtien"),
                    rs.getDouble("mucthue"),
                    rs.getInt("manv"),
                    rs.getObject("makh", Integer.class)
                ));
            }
        } catch (SQLException e) {
            System.err.println("HOADONXUAT_QUERY (getAllHoaDonXuat): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }

    public static HoaDonXuat getHoaDonById(int maHDX) {
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat WHERE mahdx = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maHDX);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return new HoaDonXuat(
                    rs.getInt("mahdx"),
                    rs.getDate("ngaylap").toLocalDate(),
                    rs.getDouble("thanhtien"),
                    rs.getDouble("mucthue"),
                    rs.getInt("manv"),
                    rs.getObject("makh", Integer.class) // Lấy MaKH, có thể null
                );
            }
        } catch (SQLException e) {
            System.err.println("HOADONXUAT_QUERY (getHoaDonById): Lỗi SQL cho MaHDX " + maHDX + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return null;
    }

    public static boolean checkMaHDXExists(int maHDX) {
        String sql = "SELECT 1 FROM hoadonxuat WHERE mahdx = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maHDX);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("HOADONXUAT_QUERY (checkMaHDXExists): Lỗi SQL cho MaHDX " + maHDX + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private static void LogNoPointReason(boolean suDungDiem, Integer maKH) {
        if (suDungDiem) {
            System.out.println("HOADONXUAT_QUERY (LogNoPoint): Khách hàng đã sử dụng điểm, không tích điểm thêm cho hóa đơn này.");
        } else if (maKH == null) {
             System.out.println("HOADONXUAT_QUERY (LogNoPoint): Khách vãng lai (MaKH là null), không tích điểm.");
        }
        // Có thể có trường hợp khác, ví dụ: thanhTien không đủ để tích điểm.
    }
}