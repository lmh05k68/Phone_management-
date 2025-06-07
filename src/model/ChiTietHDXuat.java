package model;

public class ChiTietHDXuat {
    private String maHDX;
    private String maSP;
    private int soLuong;
    private double donGiaXuat;

    public ChiTietHDXuat(String maHDX, String maSP, int soLuong, double donGiaXuat) {
        this.maHDX = maHDX;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGiaXuat = donGiaXuat;
    }
    public ChiTietHDXuat(String maSP, int soLuong, double donGiaXuat) {
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGiaXuat = donGiaXuat;
    }
    public String getMaHDX() { return maHDX; }
    public void setMaHDX(String maHDX) { this.maHDX = maHDX; }
    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public double getDonGiaXuat() { return donGiaXuat; }
    public void setDonGiaXuat(double donGiaXuat) { this.donGiaXuat = donGiaXuat; }
}