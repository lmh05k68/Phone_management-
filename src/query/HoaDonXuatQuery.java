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
        System.out.println("HDX_QUERY (insertFullGetIdTx): Bắt đầu. MaKH ban đầu=" + hdx.getMaKH());
        Integer maHDXGenerated = null;

        Integer maKHLuuDB = hdx.getMaKH();
        if (maKHLuuDB == null && // Chỉ cần kiểm tra null nếu ID tự sinh luôn dương
            (tenKHMoi != null && !tenKHMoi.trim().isEmpty() && sdtKHMoi != null && !sdtKHMoi.trim().isEmpty())) {
            KhachHang khMoi = new KhachHang(tenKHMoi.trim(), sdtKHMoi.trim(), 0);
            maKHLuuDB = KhachHangQuery.insertKhachHangAndGetId(khMoi, conn);
            if (maKHLuuDB == null) { // Nếu ID tự sinh không thể <=0, chỉ cần kiểm tra null
                throw new SQLException("Lỗi khi tạo khách hàng mới, không nhận được ID.");
            }
            hdx.setMaKH(maKHLuuDB);
            System.out.println("HDX_QUERY (insertFullGetIdTx): Đã tạo KH mới với MaKH: " + maKHLuuDB);
        }

        String sqlHdx = "INSERT INTO hoadonxuat (ngaylap, thanhtien, mucthue, manv, makh) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmtHdx = conn.prepareStatement(sqlHdx, Statement.RETURN_GENERATED_KEYS)) {
            stmtHdx.setDate(1, Date.valueOf(hdx.getNgayLap()));
            stmtHdx.setDouble(2, hdx.getThanhTien());
            stmtHdx.setDouble(3, hdx.getMucThue());
            stmtHdx.setInt(4, hdx.getMaNV());
            if (hdx.getMaKH() != null) {
                stmtHdx.setInt(5, hdx.getMaKH());
            } else {
                stmtHdx.setNull(5, Types.INTEGER);
            }

            int hdxRows = stmtHdx.executeUpdate();
            if (hdxRows == 0) {
                throw new SQLException("Không thể chèn hóa đơn xuất, không có hàng nào được thêm.");
            }

            try (ResultSet generatedKeys = stmtHdx.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    maHDXGenerated = generatedKeys.getInt(1);
                    hdx.setMaHDX(maHDXGenerated);
                    System.out.println("HDX_QUERY (insertFullGetIdTx): Hóa Đơn Xuất MaHDX=" + maHDXGenerated + " đã được chèn.");
                } else {
                    throw new SQLException("Chèn HĐX thành công nhưng không lấy được ID tự sinh.");
                }
            }
        }

        if (hdx.getChiTietList() == null || hdx.getChiTietList().isEmpty()) {
            throw new SQLException("Danh sách chi tiết hóa đơn rỗng, không thể chèn.");
        }
        String sqlCt = "INSERT INTO chitiethdxuat (masp, mahdx, soluong, dongiaxuat) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmtCt = conn.prepareStatement(sqlCt)) {
            for (ChiTietHDXuat ct : hdx.getChiTietList()) {
                ct.setMaHDX(maHDXGenerated); // Quan trọng: gán MaHDX cho chi tiết
                stmtCt.setInt(1, ct.getMaSP());
                stmtCt.setInt(2, ct.getMaHDX());
                stmtCt.setInt(3, ct.getSoLuong());
                stmtCt.setDouble(4, ct.getDonGiaXuat());
                stmtCt.addBatch();
            }
            stmtCt.executeBatch();
            System.out.println("HDX_QUERY (insertFullGetIdTx): Đã chèn " + hdx.getChiTietList().size() + " chi tiết HĐX cho MaHDX=" + maHDXGenerated);
        }

        if (!suDungDiemDeGiamGia && hdx.getMaKH() != null) { // MaKH đã là Integer, kiểm tra null là đủ
            int diemCongThem = (int) (hdx.getThanhTien() / 10000); // Ví dụ quy đổi điểm
            if (diemCongThem > 0) {
                // KhachHangQuery.getKhachHangById(int) sẽ nhận giá trị unboxed của hdx.getMaKH()
                KhachHang khHienTai = KhachHangQuery.getKhachHangById(hdx.getMaKH());
                String tenKHChoDiem = "N/A";
                String sdtKHChoDiem = "N/A";

                if (khHienTai != null) {
                    tenKHChoDiem = khHienTai.getHoTen();
                    sdtKHChoDiem = khHienTai.getSdtKH();
                } else if (tenKHMoi != null && !tenKHMoi.trim().isEmpty()) { // KH vừa được tạo trong giao dịch này
                    tenKHChoDiem = tenKHMoi.trim();
                    sdtKHChoDiem = sdtKHMoi != null ? sdtKHMoi.trim() : "N/A";
                }
                // Constructor của DiemThuong cho việc tạo mới (maTichDiem tự sinh)
                // DiemThuong(int maKH, String tenKH, int maDonHang, int soDiem, LocalDate ngayTichLuy, String sdtKH)
                DiemThuong dt = new DiemThuong(
                        hdx.getMaKH(), // Auto-unboxed
                        tenKHChoDiem,
                        maHDXGenerated, // MaDonHang (MaHDX)
                        diemCongThem,
                        hdx.getNgayLap(), // NgayTichLuy
                        sdtKHChoDiem
                );

                Integer maTichDiemGenerated = DiemThuongQuery.insertDiemThuongAndGetId(dt, conn);
                if (maTichDiemGenerated == null || maTichDiemGenerated <= 0) { // Kiểm tra ID trả về
                     throw new SQLException("Lỗi khi tạo bản ghi điểm thưởng, không nhận được ID hợp lệ.");
                }

                if (!KhachHangQuery.congDiemTichLuy(hdx.getMaKH(), diemCongThem, conn)) { // Auto-unboxed
                     throw new SQLException("Lỗi khi cập nhật tổng điểm cho khách hàng MaKH: " + hdx.getMaKH());
                }
                System.out.println("HDX_QUERY (insertFullGetIdTx): Đã xử lý điểm thưởng (MaPhieu=" + maTichDiemGenerated +
                                   ", + " + diemCongThem + " điểm) cho MaKH " + hdx.getMaKH());
            } else {
                 System.out.println("HDX_QUERY (insertFullGetIdTx): Không đủ điều kiện giá trị hóa đơn để tích điểm cho MaKH " + hdx.getMaKH());
            }
        } else {
            LogNoPointReason(suDungDiemDeGiamGia, hdx.getMaKH());
        }
        return maHDXGenerated;
    }

    public static List<ChiTietHDXuat> getChiTietHDXuat(int maHDX) {
        List<ChiTietHDXuat> list = new ArrayList<>();
        String sql = "SELECT masp, mahdx, soluong, dongiaxuat FROM chitiethdxuat WHERE mahdx = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maHDX);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Đảm bảo constructor của ChiTietHDXuat khớp: (int maHDX, int maSP, int soLuong, double donGiaXuat)
                    ChiTietHDXuat ct = new ChiTietHDXuat(
                        rs.getInt("mahdx"),
                        rs.getInt("masp"),
                        rs.getInt("soluong"),
                        rs.getDouble("dongiaxuat")
                    );
                    list.add(ct);
                }
            }
        } catch (SQLException e) {
            System.err.println("HDX_QUERY (getChiTietHDXuat): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static List<HoaDonXuat> getHoaDonByKhachHang(int maKH) {
        List<HoaDonXuat> list = new ArrayList<>();
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat WHERE makh = ? ORDER BY ngaylap DESC, mahdx DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new HoaDonXuat(
                        rs.getInt("mahdx"),
                        rs.getDate("ngaylap").toLocalDate(),
                        rs.getDouble("thanhtien"),
                        rs.getDouble("mucthue"),
                        rs.getInt("manv"),
                        rs.getInt("makh") // Ở đây MaKH không thể null
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("HDX_QUERY (getHoaDonByKhachHang): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static List<HoaDonXuat> getHoaDonByMonthAndYear(int thang, int nam) {
        List<HoaDonXuat> list = new ArrayList<>();
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat " +
                     "WHERE EXTRACT(MONTH FROM ngaylap) = ? AND EXTRACT(YEAR FROM ngaylap) = ? " +
                     "ORDER BY ngaylap DESC, mahdx DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new HoaDonXuat(
                        rs.getInt("mahdx"),
                        rs.getDate("ngaylap").toLocalDate(),
                        rs.getDouble("thanhtien"),
                        rs.getDouble("mucthue"),
                        rs.getInt("manv"),
                        rs.getObject("makh", Integer.class) // MaKH có thể null
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("HDX_QUERY (getHoaDonByMonthAndYear): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static List<HoaDonXuat> getAllHoaDonXuat() {
        List<HoaDonXuat> list = new ArrayList<>();
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat ORDER BY ngaylap DESC, mahdx DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new HoaDonXuat(
                    rs.getInt("mahdx"),
                    rs.getDate("ngaylap").toLocalDate(),
                    rs.getDouble("thanhtien"),
                    rs.getDouble("mucthue"),
                    rs.getInt("manv"),
                    rs.getObject("makh", Integer.class) // MaKH có thể null
                ));
            }
        } catch (SQLException e) {
            System.err.println("HDX_QUERY (getAllHoaDonXuat): Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static HoaDonXuat getHoaDonById(int maHDX) {
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat WHERE mahdx = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maHDX);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new HoaDonXuat(
                        rs.getInt("mahdx"),
                        rs.getDate("ngaylap").toLocalDate(),
                        rs.getDouble("thanhtien"),
                        rs.getDouble("mucthue"),
                        rs.getInt("manv"),
                        rs.getObject("makh", Integer.class) // MaKH có thể null
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("HDX_QUERY (getHoaDonById): Lỗi SQL cho MaHDX " + maHDX + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkMaHDXExists(int maHDX) {
        String sql = "SELECT 1 FROM hoadonxuat WHERE mahdx = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maHDX);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("HDX_QUERY (checkMaHDXExists): Lỗi SQL cho MaHDX " + maHDX + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void LogNoPointReason(boolean suDungDiem, Integer maKH) {
        if (suDungDiem) {
            System.out.println("HDX_QUERY (LogNoPoint): Khách hàng đã sử dụng điểm, không tích điểm thêm cho hóa đơn này.");
        } else if (maKH == null) {
             System.out.println("HDX_QUERY (LogNoPoint): Khách vãng lai (MaKH là null), không tích điểm.");
        }
    }
}