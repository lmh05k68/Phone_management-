package model;

import java.time.LocalDate; 
public class NhanVien {
    private int maNV;
    private String tenNV;
    private LocalDate ngaySinh; 
    private double luong;
    private String soDienThoai;
    public NhanVien(int maNV, String tenNV, LocalDate ngaySinh, double luong, String soDienThoai) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.ngaySinh = ngaySinh;
        this.luong = luong;
        this.soDienThoai = soDienThoai;
    }
    public NhanVien(String tenNV, LocalDate ngaySinh, double luong, String soDienThoai) {
        this.tenNV = tenNV;
        this.ngaySinh = ngaySinh;
        this.luong = luong;
        this.soDienThoai = soDienThoai;
    }

    public NhanVien() {
    }

    // Getters
    public int getMaNV() { return maNV; }
    public String getTenNV() { return tenNV; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public double getLuong() { return luong; }
    public String getSoDienThoai() { return soDienThoai; }

    // Setters
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public void setLuong(double luong) { this.luong = luong; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    @Override
    public String toString() {
        return "NhanVien{" +
               "maNV=" + maNV +
               ", tenNV='" + tenNV + '\'' +
               ", ngaySinh=" + ngaySinh +
               ", luong=" + luong +
               ", soDienThoai='" + soDienThoai + '\'' +
               '}';
    }
}