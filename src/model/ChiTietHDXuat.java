package model;

public class ChiTietHDXuat {
    private int maSP; // Đổi sang int (khóa ngoại)
    private int maHDX; 
    private int soLuong;
    private double donGiaXuat; 
    public ChiTietHDXuat(int maHDX, int maSP, int soLuong, double donGiaXuat) {
        this.maHDX = maHDX;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGiaXuat = donGiaXuat;
    }
    public ChiTietHDXuat(int maSP, int soLuong, double donGiaXuat) {
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGiaXuat = donGiaXuat;
    }
    
    public ChiTietHDXuat() {
    }

    // Getters
    public int getMaSP() { return maSP; }
    public int getMaHDX() { return maHDX; }
    public int getSoLuong() { return soLuong; }
    public double getDonGiaXuat() { return donGiaXuat; }

    // Setters
    public void setMaSP(int maSP) { this.maSP = maSP; }
    public void setMaHDX(int maHDX) { this.maHDX = maHDX; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public void setDonGiaXuat(double donGiaXuat) { this.donGiaXuat = donGiaXuat; }

    @Override
    public String toString() {
        return "ChiTietHDXuat{" +
               "maSP=" + maSP +
               ", maHDX=" + maHDX +
               ", soLuong=" + soLuong +
               ", donGiaXuat=" + donGiaXuat +
               '}';
    }
}