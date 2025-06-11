package model;

import java.math.BigDecimal;
import java.util.Objects;

public class SanPham {
    private int maSP;
    private String tenSP;
    private String mau;
    private BigDecimal giaNiemYet; 
    private String nuocSX;
    private String hangSX;
    private int soLuongTon;

    // Constructors
    public SanPham() {}
    public SanPham(String tenSP, String mau, BigDecimal giaNiemYet, String nuocSX, String hangSX) {
        this.tenSP = tenSP;
        this.mau = mau;
        this.giaNiemYet = giaNiemYet;
        this.nuocSX = nuocSX;
        this.hangSX = hangSX;
    }
    public SanPham(int maSP, String tenSP, String mau, BigDecimal giaNiemYet, String nuocSX, String hangSX) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.mau = mau;
        this.giaNiemYet = giaNiemYet;
        this.nuocSX = nuocSX;
        this.hangSX = hangSX;
    }

    // Getters and Setters
    public int getMaSP() { return maSP; }
    public void setMaSP(int maSP) { this.maSP = maSP; }
    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }
    public String getMau() { return mau; }
    public void setMau(String mau) { this.mau = mau; }
    public BigDecimal getGiaNiemYet() { return giaNiemYet; }
    public void setGiaNiemYet(BigDecimal giaNiemYet) { this.giaNiemYet = giaNiemYet; }
    public String getNuocSX() { return nuocSX; }
    public void setNuocSX(String nuocSX) { this.nuocSX = nuocSX; }
    public String getHangSX() { return hangSX; }
    public void setHangSX(String hangSX) { this.hangSX = hangSX; }
    
    // Getter/Setter cho thuộc tính được tính toán
    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPham sanPham = (SanPham) o;
        if (maSP == 0 || sanPham.maSP == 0) return false;
        return maSP == sanPham.maSP;
    }

    @Override
    public int hashCode() { return Objects.hash(maSP); }
}