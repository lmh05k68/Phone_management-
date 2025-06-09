package model;

import java.time.LocalDate;

public class TraGop { // Đổi tên lớp nếu bạn muốn (ví dụ: PhieuTraGop)
    private int maPhieuTG; // Đổi sang int
    private int maHDX; // Đổi sang int
    private int soThang;
    private double laiSuat; // Cân nhắc BigDecimal
    private double tienGoc; // Cân nhắc BigDecimal
    private LocalDate ngayBatDau;
    private boolean daThanhToan;

    // Constructor khi đọc từ DB
    public TraGop(int maPhieuTG, int maHDX, int soThang, double laiSuat, double tienGoc, LocalDate ngayBatDau, boolean daThanhToan) {
        this.maPhieuTG = maPhieuTG;
        this.maHDX = maHDX;
        this.soThang = soThang;
        this.laiSuat = laiSuat;
        this.tienGoc = tienGoc;
        this.ngayBatDau = ngayBatDau;
        this.daThanhToan = daThanhToan;
    }

    // Constructor khi tạo mới (maPhieuTG tự sinh)
    public TraGop(int maHDX, int soThang, double laiSuat, double tienGoc, LocalDate ngayBatDau, boolean daThanhToan) {
        this.maHDX = maHDX;
        this.soThang = soThang;
        this.laiSuat = laiSuat;
        this.tienGoc = tienGoc;
        this.ngayBatDau = ngayBatDau;
        this.daThanhToan = daThanhToan;
    }
    
    public TraGop() {
        // Constructor rỗng
    }

    // Getters
    public int getMaPhieuTG() { return maPhieuTG; }
    public int getMaHDX() { return maHDX; }
    public int getSoThang() { return soThang; }
    public double getLaiSuat() { return laiSuat; }
    public double getTienGoc() { return tienGoc; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public boolean isDaThanhToan() { return daThanhToan; } 
    public void setMaPhieuTG(int maPhieuTG) { this.maPhieuTG = maPhieuTG; }
    public void setMaHDX(int maHDX) { this.maHDX = maHDX; }
    public void setSoThang(int soThang) { this.soThang = soThang; }
    public void setLaiSuat(double laiSuat) { this.laiSuat = laiSuat; }
    public void setTienGoc(double tienGoc) { this.tienGoc = tienGoc; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public void setDaThanhToan(boolean daThanhToan) { this.daThanhToan = daThanhToan; }

    @Override
    public String toString() {
        return "TraGop{" +
               "maPhieuTG=" + maPhieuTG +
               ", maHDX=" + maHDX +
               ", soThang=" + soThang +
               ", laiSuat=" + laiSuat +
               ", tienGoc=" + tienGoc +
               ", ngayBatDau=" + ngayBatDau +
               ", daThanhToan=" + daThanhToan +
               '}';
    }
}