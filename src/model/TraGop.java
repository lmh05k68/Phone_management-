package model;

import java.time.LocalDate;

public class TraGop {
    private String maPhieuTG;
    private String maHDX;
    private int soThang;
    private double laiSuat;
    private double tienGoc;
    private LocalDate ngayBatDau;
    private boolean daThanhToan;

    public TraGop(String maPhieuTG, String maHDX, int soThang, double laiSuat, double tienGoc, LocalDate ngayBatDau, boolean daThanhToan) {
        this.maPhieuTG = maPhieuTG;
        this.maHDX = maHDX;
        this.soThang = soThang;
        this.laiSuat = laiSuat;
        this.tienGoc = tienGoc;
        this.ngayBatDau = ngayBatDau;
        this.daThanhToan = daThanhToan;
    }

    public String getMaPhieuTG() { return maPhieuTG; }
    public String getMaHDX() { return maHDX; }
    public int getSoThang() { return soThang; }
    public double getLaiSuat() { return laiSuat; }
    public double getTienGoc() { return tienGoc; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public boolean isDaThanhToan() { return daThanhToan; }

    public void setDaThanhToan(boolean daThanhToan) {
        this.daThanhToan = daThanhToan;
    }
}