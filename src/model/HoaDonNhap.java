package model;

import java.time.LocalDate;
import java.util.Objects;
public class HoaDonNhap {
    private int maHDN;
    private LocalDate ngayNhap;
    private int maNV;
    private int maNCC;
    public HoaDonNhap() {
    }
    public HoaDonNhap(LocalDate ngayNhap, int maNV, int maNCC) {
        this.ngayNhap = ngayNhap;
        this.maNV = maNV;
        this.maNCC = maNCC;
    }
    public HoaDonNhap(int maHDN, LocalDate ngayNhap, int maNV, int maNCC) {
        this.maHDN = maHDN;
        this.ngayNhap = ngayNhap;
        this.maNV = maNV;
        this.maNCC = maNCC;
    }
    public int getMaHDN() {
        return maHDN;
    }

    public void setMaHDN(int maHDN) {
        this.maHDN = maHDN;
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public int getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(int maNCC) {
        this.maNCC = maNCC;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDonNhap that = (HoaDonNhap) o;
        if (maHDN == 0) return false;
        return maHDN == that.maHDN;
    }
    @Override
    public int hashCode() {
        return Objects.hash(maHDN);
    }
    @Override
    public String toString() {
        return "HoaDonNhap{" +
                "maHDN=" + maHDN +
                ", ngayNhap=" + ngayNhap +
                ", maNV=" + maNV +
                ", maNCC=" + maNCC +
                '}';
    }
}