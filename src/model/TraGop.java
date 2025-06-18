package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class TraGop {
    // Các trường dữ liệu gốc từ bảng
    private int maPhieuTG;
    private int maHDX;
    private int soThang;
    private BigDecimal laiSuat; // Lãi suất theo năm (%)
    private BigDecimal tienGoc;
    private LocalDate ngayBatDau;
    private boolean daThanhToan;

    // Các trường dùng để lưu trữ giá trị được tính toán
    private BigDecimal tienTraHangThang;
    private LocalDate ngayDaoHan;

    /**
     * Constructor đầy đủ, dùng để khởi tạo đối tượng từ ResultSet của CSDL.
     * Bao gồm cả các trường được tính toán từ câu lệnh SELECT.
     */
    public TraGop(int maPhieuTG, int maHDX, int soThang, BigDecimal laiSuat, BigDecimal tienGoc, LocalDate ngayBatDau, boolean daThanhToan, BigDecimal tienTraHangThang, LocalDate ngayDaoHan) {
        this.maPhieuTG = maPhieuTG;
        this.maHDX = maHDX;
        this.soThang = soThang;
        this.laiSuat = laiSuat;
        this.tienGoc = tienGoc;
        this.ngayBatDau = ngayBatDau;
        this.daThanhToan = daThanhToan;
        this.tienTraHangThang = tienTraHangThang;
        this.ngayDaoHan = ngayDaoHan;
    }

    /**
     * Constructor dùng để tạo một phiếu trả góp mới trước khi lưu vào CSDL.
     * Chỉ cần các thông tin đầu vào.
     */
    public TraGop(int maHDX, int soThang, BigDecimal laiSuat, BigDecimal tienGoc, LocalDate ngayBatDau, boolean daThanhToan) {
        this.maHDX = maHDX;
        this.soThang = soThang;
        this.laiSuat = laiSuat;
        this.tienGoc = tienGoc;
        this.ngayBatDau = ngayBatDau;
        this.daThanhToan = daThanhToan;
    }

    // --- Getters ---

    public int getMaPhieuTG() { return maPhieuTG; }
    public int getMaHDX() { return maHDX; }
    public int getSoThang() { return soThang; }
    public BigDecimal getLaiSuat() { return laiSuat; }
    public BigDecimal getTienGoc() { return tienGoc; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public boolean isDaThanhToan() { return daThanhToan; }
    public BigDecimal getTienTraHangThang() { return tienTraHangThang; }
    public LocalDate getNgayDaoHan() { return ngayDaoHan; }
    public BigDecimal getTongTienPhaiTra() {
        if (this.tienTraHangThang == null || this.soThang <= 0) {
            return this.tienGoc;
        }
        return this.tienTraHangThang.multiply(new BigDecimal(this.soThang)).setScale(0, RoundingMode.HALF_UP);
    }
}