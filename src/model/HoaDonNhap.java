package model;

import java.time.LocalDate;

public class HoaDonNhap {
    private String maHDN;
    private LocalDate ngayNhap;
    private String maNV;
    private String maNCC;
    public HoaDonNhap(String maHDN, LocalDate ngayNhap, String maNV, String maNCC) {
        this.maHDN = maHDN;
        this.ngayNhap = ngayNhap;
        this.maNV = maNV;
        this.maNCC = maNCC;
    }
    public HoaDonNhap(LocalDate ngayNhap, String maNV, String maNCC) {
        this.ngayNhap = ngayNhap;
        this.maNV = maNV;
        this.maNCC = maNCC;
    }
    public String getMaHDN() {
        return maHDN;
    }

    public void setMaHDN(String maHDN) {
        this.maHDN = maHDN;
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }
}