package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
public class HoaDonXuat {
    private int maHDX;
    private LocalDate ngayLap;
    private BigDecimal thanhTien;
    private BigDecimal mucThue;
    private int maNV;
    private int maKH;
    public HoaDonXuat() {
    }
    public HoaDonXuat(LocalDate ngayLap, BigDecimal mucThue, int maNV, int maKH) {
        this.ngayLap = ngayLap;
        this.mucThue = mucThue;
        this.maNV = maNV;
        this.maKH = maKH;
    }
    public HoaDonXuat(int maHDX, LocalDate ngayLap, BigDecimal thanhTien, BigDecimal mucThue, int maNV, int maKH) {
        this.maHDX = maHDX;
        this.ngayLap = ngayLap;
        this.thanhTien = thanhTien;
        this.mucThue = mucThue;
        this.maNV = maNV;
        this.maKH = maKH;
    }
    public int getMaHDX() {
        return maHDX;
    }

    public void setMaHDX(int maHDX) {
        this.maHDX = maHDX;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        this.ngayLap = ngayLap;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    public BigDecimal getMucThue() {
        return mucThue;
    }

    public void setMucThue(BigDecimal mucThue) {
        this.mucThue = mucThue;
    }

    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public int getMaKH() {
        return maKH;
    }

    public void setMaKH(int maKH) {
        this.maKH = maKH;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDonXuat that = (HoaDonXuat) o;
        if (maHDX == 0) return false;
        return maHDX == that.maHDX;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHDX);
    }

    @Override
    public String toString() {
        return "HoaDonXuat{" +
                "maHDX=" + maHDX +
                ", ngayLap=" + ngayLap +
                ", thanhTien=" + thanhTien +
                ", mucThue=" + mucThue +
                ", maNV=" + maNV +
                ", maKH=" + maKH +
                '}';
    }
}