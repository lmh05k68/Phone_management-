package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
public class TraGop {
    private int maPhieuTG;
    private int maHDX;
    private int soThang;
    private BigDecimal laiSuat;
    private BigDecimal tienGoc;
    private LocalDate ngayBatDau;
    private boolean daThanhToan;
    public TraGop() {
    }
    public TraGop(int maHDX, int soThang, BigDecimal laiSuat, BigDecimal tienGoc, LocalDate ngayBatDau, boolean daThanhToan) {
        this.maHDX = maHDX;
        this.soThang = soThang;
        this.laiSuat = laiSuat;
        this.tienGoc = tienGoc;
        this.ngayBatDau = ngayBatDau;
        this.daThanhToan = daThanhToan;
    }
    public TraGop(int maPhieuTG, int maHDX, int soThang, BigDecimal laiSuat, BigDecimal tienGoc, LocalDate ngayBatDau, boolean daThanhToan) {
        this.maPhieuTG = maPhieuTG;
        this.maHDX = maHDX;
        this.soThang = soThang;
        this.laiSuat = laiSuat;
        this.tienGoc = tienGoc;
        this.ngayBatDau = ngayBatDau;
        this.daThanhToan = daThanhToan;
    }
    public int getMaPhieuTG() {
        return maPhieuTG;
    }

    public void setMaPhieuTG(int maPhieuTG) {
        this.maPhieuTG = maPhieuTG;
    }

    public int getMaHDX() {
        return maHDX;
    }

    public void setMaHDX(int maHDX) {
        this.maHDX = maHDX;
    }

    public int getSoThang() {
        return soThang;
    }

    public void setSoThang(int soThang) {
        this.soThang = soThang;
    }

    public BigDecimal getLaiSuat() {
        return laiSuat;
    }

    public void setLaiSuat(BigDecimal laiSuat) {
        this.laiSuat = laiSuat;
    }

    public BigDecimal getTienGoc() {
        return tienGoc;
    }

    public void setTienGoc(BigDecimal tienGoc) {
        this.tienGoc = tienGoc;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public boolean isDaThanhToan() {
        return daThanhToan;
    }

    public void setDaThanhToan(boolean daThanhToan) {
        this.daThanhToan = daThanhToan;
    }


    // --- Ghi đè các phương thức của lớp Object ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TraGop that = (TraGop) o;
        if (maPhieuTG == 0) return false;
        return maPhieuTG == that.maPhieuTG;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuTG);
    }

    @Override
    public String toString() {
        return "PhieuTraGop{" +
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