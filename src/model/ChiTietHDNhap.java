package model;

public class ChiTietHDNhap {
    private String maSP;
    private String maHDN;
    private int soLuong;
    private double donGiaNhap;

    public ChiTietHDNhap() {
        // Constructor rỗng hỗ trợ serialization hoặc binding
    }

    public ChiTietHDNhap(String maSP, String maHDN, int soLuong, double donGiaNhap) {
        this.maSP = maSP;
        this.maHDN = maHDN;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getMaHDN() {
        return maHDN;
    }

    public void setMaHDN(String maHDN) {
        this.maHDN = maHDN;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(double donGiaNhap) {
        this.donGiaNhap = donGiaNhap;
    }
}