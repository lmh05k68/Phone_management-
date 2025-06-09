package model;

import java.time.LocalDate;
import java.util.List;

public class HoaDonXuat {
    private int maHDX;
    private LocalDate ngayLap;
    private double thanhTien;
    private double mucThue;
    private int maNV;
    private Integer maKH; 
    private List<ChiTietHDXuat> chiTietList;
    public HoaDonXuat(int maHDX, LocalDate ngayLap, double thanhTien, double mucThue, int maNV, Integer maKH) {
        this.maHDX = maHDX;
        this.ngayLap = ngayLap;
        this.thanhTien = thanhTien;
        this.mucThue = mucThue;
        this.maNV = maNV;
        this.maKH = maKH;
    }
    public HoaDonXuat(LocalDate ngayLap, double thanhTien, double mucThue, int maNV, Integer maKH) {
        this.ngayLap = ngayLap;
        this.thanhTien = thanhTien;
        this.mucThue = mucThue;
        this.maNV = maNV;
        this.maKH = maKH;
    }

    public HoaDonXuat() {
        // Constructor rỗng
    }

    // Getters
    public int getMaHDX() { return maHDX; }
    public LocalDate getNgayLap() { return ngayLap; }
    public double getThanhTien() { return thanhTien; }
    public double getMucThue() { return mucThue; }
    public int getMaNV() { return maNV; }
    public Integer getMaKH() { return maKH; } // <<<< THAY ĐỔI KIỂU TRẢ VỀ
    public List<ChiTietHDXuat> getChiTietList() { return chiTietList; }

    // Setters
    public void setMaHDX(int maHDX) { this.maHDX = maHDX; }
    public void setNgayLap(LocalDate ngayLap) { this.ngayLap = ngayLap; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }
    public void setMucThue(double mucThue) { this.mucThue = mucThue; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public void setMaKH(Integer maKH) { this.maKH = maKH; } // <<<< THAY ĐỔI KIỂU THAM SỐ
    public void setChiTietList(List<ChiTietHDXuat> chiTietList) { this.chiTietList = chiTietList; }

     @Override
    public String toString() {
        return "HoaDonXuat{" +
               "maHDX=" + maHDX +
               ", ngayLap=" + ngayLap +
               ", thanhTien=" + thanhTien +
               ", mucThue=" + mucThue +
               ", maNV=" + maNV +
               ", maKH=" + (maKH == null ? "null" : maKH) + // Xử lý hiển thị null
               '}';
    }
}