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
            if (conn == null) {
                System.err.println("DEBUG HDX_QUERY: Connection null");
                return null;
            }
            conn.setAutoCommit(false);

            if (hdx.getNgayLap() == null) {
                hdx.setNgayLap(LocalDate.now());
            }

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
                // CHỈ COMMIT HDX Ở ĐÂY, CHI TIẾT VÀ ĐIỂM SẼ CÓ CONNECTION RIÊNG HOẶC QUẢN LÝ SAU
                conn.commit();
                System.out.println("DEBUG HDX_QUERY: ĐÃ COMMIT hoadonxuat MaHDX: " + hdx.getMaHDX());
            } else {
                conn.rollback();
                System.err.println("DEBUG HDX_QUERY: Rollback do không chèn được HDX.");
                return null;
            }
            // Không đóng connection ở đây nếu còn thao tác khác trong transaction
            // conn.setAutoCommit(true); // Tạm thời để true để các thao tác sau không bị ảnh hưởng nếu có lỗi

            // Chèn chi tiết hóa đơn (nên dùng connection riêng hoặc truyền connection vào)
            if (hdx.getChiTietList() != null && !hdx.getChiTietList().isEmpty()) {
                Connection connForDetails = null;
                try {
                    connForDetails = DBConnection.getConnection(); // Connection mới cho chi tiết
                     connForDetails.setAutoCommit(false);
                    if (!insertChiTietHoaDonXuat(hdx.getMaHDX(), hdx.getChiTietList(), connForDetails)) {
                        System.err.println("DEBUG HDX_QUERY: Lỗi chèn chi tiết HDX. Rollback chi tiết.");
                         connForDetails.rollback();
                        // Cân nhắc: có nên rollback cả HDX chính nếu chi tiết lỗi không?
                        // Hiện tại: HDX chính đã commit, chi tiết lỗi thì chỉ chi tiết lỗi.
                    } else {
                        connForDetails.commit();
                        System.out.println("DEBUG HDX_QUERY: Đã commit chi tiết HDX cho MaHDX: " + hdx.getMaHDX());
                    }
                } catch (SQLException exDetail) {
                     System.err.println("DEBUG HDX_QUERY: SQLException khi chèn chi tiết HDX: " + exDetail.getMessage());
                     if(connForDetails != null) try { connForDetails.rollback(); } catch (SQLException exRoll) { exRoll.printStackTrace(); }
                } finally {
                    if (connForDetails != null) try { connForDetails.setAutoCommit(true); connForDetails.close(); } catch (SQLException exClose) { exClose.printStackTrace(); }
                }
            }

            // Xử lý điểm thưởng: Chỉ tích điểm nếu maTichDiemDaNhap được cung cấp (không phải null/empty)
            // Nếu khách hàng dùng điểm để giảm giá, controller sẽ truyền maTichDiemDaNhap là null.
            if (hdx.getMaKH() != null && !hdx.getMaKH().isEmpty()
                    && maTichDiemDaNhap != null && !maTichDiemDaNhap.trim().isEmpty()) {

                // Số điểm thưởng tính trên tổng tiền cuối cùng của hóa đơn (sau thuế, sau giảm giá nếu có từ voucher khác)
                // THEO YÊU CẦU BAN ĐẦU: 100,000 VND = 1 điểm
                int soDiemThuong = (int) (hdx.getThanhTien() / 100000);

                if (soDiemThuong > 0) {
                    DiemThuongQuery dtq = new DiemThuongQuery();
                    KhachHangQuery khq = new KhachHangQuery(); // non-static methods
                    KhachHang kh = KhachHangQuery.getKhachHangById(hdx.getMaKH()); // static method

                    String tenKHForDiem = kh != null ? kh.getHoTen() : (tenKHDaNhap != null ? tenKHDaNhap : "N/A");
                    String sdtKHForDiem = kh != null ? kh.getSdtKH() : (sdtKHDaNhap != null ? sdtKHDaNhap : "N/A");

                    DiemThuong dt = new DiemThuong(
                        maTichDiemDaNhap,
                        hdx.getMaKH(),
                        tenKHForDiem,
                        hdx.getMaHDX(),
                        soDiemThuong,
                        hdx.getNgayLap(), // Ngày lập hóa đơn
                        sdtKHForDiem
                    );

                    if (dtq.insertDiemThuong(dt)) {
                        System.out.println("DEBUG HDX_QUERY: Đã ghi nhận lịch sử điểm thưởng cho MaHDX: " + hdx.getMaHDX());
                        if (!khq.congDiemTichLuy(hdx.getMaKH(), soDiemThuong)) {
                            System.err.println("DEBUG HDX_QUERY: Lỗi cập nhật tổng điểm cho MaKH: " + hdx.getMaKH());
                            // Cân nhắc xử lý lỗi ở đây, ví dụ rollback điểm thưởng đã insert?
                        } else {
                             System.out.println("DEBUG HDX_QUERY: Đã cộng " + soDiemThuong + " điểm cho MaKH: " + hdx.getMaKH());
                        }
                    } else {
                        System.err.println("DEBUG HDX_QUERY: Lỗi insert lịch sử điểm thưởng cho MaHDX: " + hdx.getMaHDX());
                    }
                } else {
                     System.out.println("DEBUG HDX_QUERY: Không đủ điều kiện tích điểm (thành tiền < 100,000) cho MaHDX: " + hdx.getMaHDX());
                }
            } else {
                 System.out.println("DEBUG HDX_QUERY: Không tích điểm cho MaHDX: " + hdx.getMaHDX() + " (MaKH rỗng hoặc MaTichDiem rỗng/null - có thể do khách dùng điểm)");
            }

            return hdx.getMaHDX();

        } catch (SQLException e) {
            System.err.println("DEBUG HDX_QUERY: SQLException trong insertHoaDonXuat cho MaHDX " + (hdx != null ? hdx.getMaHDX() : "UNKNOWN") + ": " + e.getMessage());
            e.printStackTrace();
            if (conn != null && !hdxInsertedSuccessfully) { // Chỉ rollback nếu HDX chưa được commit thành công
                try {
                    System.err.println("DEBUG HDX_QUERY: Rollback do lỗi trước khi commit HDX chính.");
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("DEBUG HDX_QUERY: Lỗi khi rollback: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (stmtHdx != null) stmtHdx.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Luôn trả lại trạng thái autoCommit
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("DEBUG HDX_QUERY: Lỗi khi đóng tài nguyên: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null; // Trả về null nếu có lỗi xảy ra và HDX không được tạo
    }

	public boolean insertChiTietHoaDonXuat(String maHDXChinh, List<ChiTietHDXuat> listCT, Connection connParam) throws SQLException {
        // Giả định connParam đã được setAutoCommit(false) bởi nơi gọi nếu muốn transaction
        String sql = "INSERT INTO chitiethdxuat (masp, mahdx, slxuat, dongiaxuat) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connParam.prepareStatement(sql)) {
            for (ChiTietHDXuat ct : listCT) {
                stmt.setString(1, ct.getMaSP());
                stmt.setString(2, maHDXChinh); // Sử dụng MaHDX từ hóa đơn chính
                stmt.setInt(3, ct.getSoLuong());
                stmt.setDouble(4, ct.getDonGiaXuat());
                stmt.addBatch();
            }
            int[] result = stmt.executeBatch();
            // Kiểm tra kết quả batch
            for (int res : result) {
                if (res == PreparedStatement.EXECUTE_FAILED) {
                    System.err.println("DEBUG HDX_QUERY: Một lệnh trong batch insertChiTietHoaDonXuat thất bại.");
                    return false; // Báo hiệu thất bại nếu một lệnh trong batch không thành công
                }
            }
            System.out.println("DEBUG HDX_QUERY: insertChiTietHoaDonXuat thành công cho MaHDX: " + maHDXChinh);
            return true;
        } catch (SQLException e) {
             System.err.println("DEBUG HDX_QUERY: SQLException trong insertChiTietHoaDonXuat cho MaHDX " + maHDXChinh + ": " + e.getMessage());
             throw e; // Ném lại lỗi để nơi gọi xử lý (ví dụ: rollback)
        }
    }
    // ... (các phương thức getChiTietHDXuat, getHoaDonByKhachHang, getHoaDonByMonthAndYear, getAllHoaDonXuat, getHoaDonById đã có) ...
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

    public List<HoaDonXuat> getHoaDonByMonthAndYear(int thang, int nam) {
        List<HoaDonXuat> list = new ArrayList<>();
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat " +
                     "WHERE EXTRACT(MONTH FROM ngaylap) = ? AND EXTRACT(YEAR FROM ngaylap) = ? " +
                     "ORDER BY SUBSTRING(mahdx FROM 4)::INT ASC"; // Sắp xếp theo phần số của MaHDX
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
        String sql = "SELECT mahdx, ngaylap, thanhtien, mucthue, manv, makh FROM hoadonxuat ORDER BY ngaylap DESC, SUBSTRING(mahdx FROM 4)::INT DESC";
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
            System.err.println("DEBUG HDX_QUERY: getHoaDonById - SQLException for MaHDX " + maHDX + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}