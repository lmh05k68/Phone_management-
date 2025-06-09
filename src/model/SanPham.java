package model;

public class SanPham {
    private int maSP; 
    private String tenSP;
    private String mau;
    private double donGia; 
    private String nuocSX;
    private String hangSX;
    private int soLuong;
    public SanPham(int maSP, String tenSP, String mau, double donGia,
                   String nuocSX, String hangSX, int soLuong) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.mau = mau;
        this.donGia = donGia;
        this.nuocSX = nuocSX;
        this.hangSX = hangSX;
        this.soLuong = soLuong;
    }
    public SanPham(String tenSP, String mau, double donGia,
                   String nuocSX, String hangSX, int soLuong) {
        this.tenSP = tenSP;
        this.mau = mau;
        this.donGia = donGia;
        this.nuocSX = nuocSX;
        this.hangSX = hangSX;
        this.soLuong = soLuong;
    }
    
    public SanPham() {
    }

    // Getters
    public int getMaSP() { return maSP; }
    public String getTenSP() { return tenSP; }
    public String getMau() { return mau; }
    public double getDonGia() { return donGia; }
    public String getNuocSX() { return nuocSX; }
    public String getHangSX() { return hangSX; }
    public int getSoLuong() { return soLuong; }

    // Setters
    public void setMaSP(int maSP) { this.maSP = maSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }
    public void setMau(String mau) { this.mau = mau; }
    public void setDonGia(double donGia) { this.donGia = donGia; }
    public void setNuocSX(String nuocSX) { this.nuocSX = nuocSX; }
    public void setHangSX(String hangSX) { this.hangSX = hangSX; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    @Override
    public String toString() {
        return tenSP; 
    }
}