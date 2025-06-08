package model;

import java.time.LocalDate;
import java.util.List;

public class HoaDonXuat {
    private String maHDX;
    private LocalDate ngayLap; 
    private double thanhTien;
    private double mucThue;
    private String maNV;
    private String maKH;
    private List<ChiTietHDXuat> chiTietList;
    public HoaDonXuat(String maHDX, LocalDate ngayLap, double thanhTien, double mucThue, String maNV, String maKH) {
        this.maHDX = maHDX;
        this.ngayLap = ngayLap;
        this.thanhTien = thanhTien;
        this.mucThue = mucThue;
        this.maNV = maNV;
        this.maKH = maKH;
    }

    // Getters
    public String getMaHDX() { return maHDX; }
    public LocalDate getNgayLap() { return ngayLap; }
    public double getThanhTien() { return thanhTien; }
    public double getMucThue() { return mucThue; }
    public String getMaNV() { return maNV; }
    public String getMaKH() { return maKH; }
    public List<ChiTietHDXuat> getChiTietList() { return chiTietList; }

    // Setters
    public void setMaHDX(String maHDX) { this.maHDX = maHDX; }
    public void setNgayLap(LocalDate ngayLap) { this.ngayLap = ngayLap; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }
    public void setMucThue(double mucThue) { this.mucThue = mucThue; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public void setChiTietList(List<ChiTietHDXuat> chiTietList) { this.chiTietList = chiTietList; }
}