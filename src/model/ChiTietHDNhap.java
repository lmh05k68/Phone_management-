package model;

public class ChiTietHDNhap {
    private int maSP; // Đổi sang int (khóa ngoại)
    private int maHDN; // Đổi sang int (khóa ngoại)
    private int soLuong;
    private double donGiaNhap; // Cân nhắc BigDecimal

    // Constructor
    public ChiTietHDNhap(int maSP, int maHDN, int soLuong, double donGiaNhap) {
        this.maSP = maSP;
        this.maHDN = maHDN;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
    }

    public ChiTietHDNhap() {
    }

    // Getters
    public int getMaSP() { return maSP; }
    public int getMaHDN() { return maHDN; }
    public int getSoLuong() { return soLuong; }
    public double getDonGiaNhap() { return donGiaNhap; }

    // Setters
    public void setMaSP(int maSP) { this.maSP = maSP; }
    public void setMaHDN(int maHDN) { this.maHDN = maHDN; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public void setDonGiaNhap(double donGiaNhap) { this.donGiaNhap = donGiaNhap; }

    @Override
    public String toString() {
        return "ChiTietHDNhap{" +
               "maSP=" + maSP +
               ", maHDN=" + maHDN +
               ", soLuong=" + soLuong +
               ", donGiaNhap=" + donGiaNhap +
               '}';
    }
}