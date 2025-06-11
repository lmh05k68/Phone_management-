package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;
public class NhanVien {

    private int maNV;
    private String tenNV;
    private LocalDate ngaySinh;
    private BigDecimal luong;
    private String soDienThoai;

    // --- Constructors ---
    public NhanVien() {
         this.luong = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

     // Constructor for INSERT (no ID)
    public NhanVien(String tenNV, LocalDate ngaySinh, BigDecimal luong, String soDienThoai) {
        this.tenNV = tenNV;
        this.ngaySinh = ngaySinh;
       // Ensure scale is set correctly
        this.luong = (luong != null) ? luong.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.soDienThoai = soDienThoai;
    }

     // Constructor for SELECT/UPDATE (with ID)
    public NhanVien(int maNV, String tenNV, LocalDate ngaySinh, BigDecimal luong, String soDienThoai) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.ngaySinh = ngaySinh;
       // Ensure scale is set correctly
       this.luong = (luong != null) ? luong.setScale(2, RoundingMode.HALF_UP) : null; // Allow null if db has null
        this.soDienThoai = soDienThoai;
    }


    // --- Getters và Setters ---
    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public BigDecimal getLuong() {
         // Return ZERO instead of null for safer calculations, unless null has specific meaning
        // return luong == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : luong;
         return luong;
    }

    public void setLuong(BigDecimal luong) {
       // Ensure scale is set correctly upon setting
       this.luong = (luong != null) ? luong.setScale(2, RoundingMode.HALF_UP) : null;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }


    // --- Ghi đè các phương thức của lớp Object ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhanVien nhanVien = (NhanVien) o;
        // Only compare by ID if ID is set
        if (maNV == 0 || nhanVien.maNV == 0) return false;
        return maNV == nhanVien.maNV;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNV);
    }

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