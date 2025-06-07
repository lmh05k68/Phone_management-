package query;
import java.time.LocalDate; 
import model.HoaDonXuat;
import model.ChiTietHDXuat;
import model.DiemThuong;
import model.KhachHang;
import dbConnection.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonXuatQuery {
	public String insertHoaDonXuat(HoaDonXuat hdx, String maTichDiemDaNhap, String tenKHDaNhap, String sdtKHDaNhap) {
	    String sqlHdx = "INSERT INTO hoadonxuat (mahdx, ngaylap, thanhtien, mucthue, manv, makh) VALUES (?, ?, ?, ?, ?, ?)";
	    Connection conn = null;
	    PreparedStatement stmtHdx = null;
	    boolean hdxInsertedSuccessfully = false; 
	    try {
	        conn = DBConnection.getConnection();
	        if (conn == null) { /* ... */ return null; }
	        conn.setAutoCommit(false); 
	        stmtHdx = conn.prepareStatement(sqlHdx);
	        stmtHdx.setString(1, hdx.getMaHDX());
	        stmtHdx.setDate(2, Date.valueOf(hdx.getNgayLap()));
	        stmtHdx.setDouble(3, hdx.getThanhTien());
	        stmtHdx.setDouble(4, hdx.getMucThue());
	        stmtHdx.setString(5, hdx.getMaNV());
	        stmtHdx.setString(6, hdx.getMaKH());
	        int rowsAffected = stmtHdx.executeUpdate();
	        System.out.println("DEBUG HDX_QUERY: Đã chèn " + rowsAffected + " dòng vào hoadonxuat.");

	        if (rowsAffected > 0) {
	            hdxInsertedSuccessfully = true;
	            conn.commit(); 
	            System.out.println("DEBUG HDX_QUERY: ĐÃ COMMIT hoadonxuat MaHDX: " + hdx.getMaHDX());
	        } else {
	            conn.rollback();
	            System.err.println("DEBUG HDX_QUERY: Rollback do không chèn được vào hoadonxuat.");
	            return null;
	        }
	        if (hdxInsertedSuccessfully) {
	            conn.setAutoCommit(true); 

	            if (hdx.getChiTietList() != null && !hdx.getChiTietList().isEmpty()) {
	                try (Connection connForDetails = DBConnection.getConnection()) { 
	                     if (!insertChiTietHoaDonXuat(hdx.getMaHDX(), hdx.getChiTietList(), connForDetails)) {
	                        System.err.println("DEBUG HDX_QUERY: Lỗi chèn ChiTietHDXuat (không rollback hóa đơn chính).");
	                    }
	                }
	            }
	            if (hdx.getMaKH() != null && !hdx.getMaKH().trim().isEmpty() &&
	                maTichDiemDaNhap != null && !maTichDiemDaNhap.trim().isEmpty()) {
	                int soDiemThuong = (int) (hdx.getThanhTien() / 100000);
	                if (soDiemThuong > 0) {
	                    DiemThuongQuery diemThuongQuery = new DiemThuongQuery();
	                    KhachHangQuery khachHangQuery = new KhachHangQuery();
	                    KhachHang kh = khachHangQuery.getKhachHangByMaKH(hdx.getMaKH());
	                    String tenKHForDiem = (kh != null) ? kh.getHoTen() : ((tenKHDaNhap != null && !tenKHDaNhap.isEmpty()) ? tenKHDaNhap : "N/A");
	                    String sdtKHForDiem = (kh != null) ? kh.getSdtKH() : ((sdtKHDaNhap != null && !sdtKHDaNhap.isEmpty()) ? sdtKHDaNhap : "N/A");

	                    DiemThuong dt = new DiemThuong(maTichDiemDaNhap, hdx.getMaKH(), tenKHForDiem, hdx.getMaHDX(), soDiemThuong, hdx.getNgayLap(), sdtKHForDiem);
	                    if (diemThuongQuery.insertDiemThuong(dt)) {
	                        System.out.println("DEBUG HDX_QUERY: Đã ghi nhận điểm vào DiemThuong.");
	                        if (!khachHangQuery.congDiemTichLuy(hdx.getMaKH(), soDiemThuong)) {
	                            System.err.println("DEBUG HDX_QUERY: Lỗi cập nhật sodiemtichluy (không rollback hóa đơn chính).");
	                        }
	                    } else {
	                        System.err.println("DEBUG HDX_QUERY: Lỗi ghi nhận điểm vào DiemThuong (không rollback hóa đơn chính).");
	                    }
	                }
	            }
	            return hdx.getMaHDX(); 
	        }

	    } catch (SQLException e) {
	        System.err.println("DEBUG HDX_QUERY: insertHoaDonXuat - SQLException: " + e.getMessage());
	        e.printStackTrace();
	        if (conn != null && !hdxInsertedSuccessfully) { 
	            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
	        }
	    } finally {
	        try {
	            if (stmtHdx != null) stmtHdx.close();
	            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
	        } catch (SQLException e) { e.printStackTrace(); }
	    }
	    return null;
	}
	public boolean insertChiTietHoaDonXuat(String maHDXChinh, List<ChiTietHDXuat> listCT, Connection connParam) throws SQLException {
	    String sql = "INSERT INTO chitiethdxuat (masp, mahdx, slxuat, dongiaxuat) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement stmt = connParam.prepareStatement(sql)) {
	        for (ChiTietHDXuat ct : listCT) {
	            stmt.setString(1, ct.getMaSP());
	            stmt.setString(2, maHDXChinh);
	            stmt.setInt(3, ct.getSoLuong());
	            stmt.setDouble(4, ct.getDonGiaXuat());
	            stmt.addBatch();
	        }
	        int[] result = stmt.executeBatch();
	        for (int res : result) {
	            if (res == PreparedStatement.EXECUTE_FAILED) return false;
	        }
	        return true;
	    }
	}
    public List<ChiTietHDXuat> getChiTietHDXuat(String maHDX) {
        List<ChiTietHDXuat> list = new ArrayList<>();
        String sql = "SELECT masp, mahdx, slxuat, dongiaxuat FROM chitiethdxuat WHERE mahdx = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHDX);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ChiTietHDXuat ct = new ChiTietHDXuat(
                    rs.getString("mahdx"),
                    rs.getString("masp"),
                    rs.getInt("slxuat"),
                    rs.getDouble("dongiaxuat")
                );
                list.add(ct);
            }
        } catch (SQLException e) {
            System.err.println("DEBUG HDX_QUERY: getChiTietHDXuat - SQLException: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<HoaDonXuat> getHoaDonByKhachHang(String maKH) {
        List<HoaDonXuat> list = new ArrayList<>();
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat WHERE makh = ? ORDER BY ngaylap DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maKH);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HoaDonXuat hdx = new HoaDonXuat(
                    rs.getString("mahdx"),
                    rs.getDate("ngaylap").toLocalDate(),
                    rs.getDouble("thanhtien"),
                    rs.getDouble("mucthue"),
                    rs.getString("manv"),
                    rs.getString("makh")
                );
                list.add(hdx);
            }
        } catch (SQLException e) {
            System.err.println("DEBUG HDX_QUERY: getHoaDonByKhachHang - SQLException: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

 // Trong query/HoaDonXuatQuery.java

    public List<HoaDonXuat> getHoaDonByMonthAndYear(int thang, int nam) {
        List<HoaDonXuat> list = new ArrayList<>();
        // SỬA CÂU SQL Ở ĐÂY
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat " +
                     "WHERE EXTRACT(MONTH FROM ngaylap) = ? AND EXTRACT(YEAR FROM ngaylap) = ? " +
                     "ORDER BY SUBSTRING(mahdx FROM 4)::INT ASC";
        // GIẢI THÍCH:
        // SUBSTRING(mahdx FROM 4): Lấy chuỗi con từ vị trí thứ 4 của mahdx (bỏ qua "HDX")
        // ::INT : Ép kiểu chuỗi con đó thành INTEGER

        System.out.println("DEBUG HDX_QUERY: SQL getHoaDonByMonthAndYear: " + sql);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate ngayLap = null;
                if (rs.getDate("ngaylap") != null) {
                    ngayLap = rs.getDate("ngaylap").toLocalDate();
                }
                HoaDonXuat hdx = new HoaDonXuat(
                    rs.getString("mahdx"),
                    ngayLap,
                    rs.getDouble("thanhtien"),
                    rs.getDouble("mucthue"),
                    rs.getString("manv"),
                    rs.getString("makh")
                );
                list.add(hdx);
            }
            System.out.println("DEBUG HDX_QUERY: Found " + list.size() + " invoices for " + thang + "/" + nam);
        } catch (SQLException e) {
            System.err.println("DEBUG HDX_QUERY: getHoaDonByMonthAndYear - SQLException: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    public List<HoaDonXuat> getAllHoaDonXuat() {
        List<HoaDonXuat> list = new ArrayList<>();
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat ORDER BY ngaylap DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                HoaDonXuat hdx = new HoaDonXuat(
                    rs.getString("mahdx"),
                    rs.getDate("ngaylap").toLocalDate(),
                    rs.getDouble("thanhtien"),
                    rs.getDouble("mucthue"),
                    rs.getString("manv"),
                    rs.getString("makh")
                );
                list.add(hdx);
            }
        } catch (SQLException e) {
            System.err.println("DEBUG HDX_QUERY: getAllHoaDonXuat - SQLException: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public HoaDonXuat getHoaDonById(String maHDX) {
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat WHERE mahdx = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHDX);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new HoaDonXuat(
                    rs.getString("mahdx"),
                    rs.getDate("ngaylap").toLocalDate(),
                    rs.getDouble("thanhtien"),
                    rs.getDouble("mucthue"),
                    rs.getString("manv"),
                    rs.getString("makh")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}