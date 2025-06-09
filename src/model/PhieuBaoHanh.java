package model;

import java.time.LocalDate; // Sử dụng LocalDate

public class PhieuBaoHanh {
    private int idBH; // Đổi sang int
    private int maSP; // Đổi sang int
    private LocalDate ngayNhanSanPham; // Đổi sang LocalDate
    private LocalDate ngayTraSanPham;  // Đổi sang LocalDate, có thể null
    private int maKH; // Đổi sang int
    private String trangThai;

    // Constructor khi đọc từ DB
    public PhieuBaoHanh(int idBH, int maSP, LocalDate ngayNhanSanPham, LocalDate ngayTraSanPham, int maKH, String trangThai) {
        this.idBH = idBH;
        this.maSP = maSP;
        this.ngayNhanSanPham = ngayNhanSanPham;
        this.ngayTraSanPham = ngayTraSanPham;
        this.maKH = maKH;
        this.trangThai = trangThai;
    }

    // Constructor khi tạo mới (idBH tự sinh)
    public PhieuBaoHanh(int maSP, LocalDate ngayNhanSanPham, LocalDate ngayTraSanPham, int maKH, String trangThai) {
        this.maSP = maSP;
        this.ngayNhanSanPham = ngayNhanSanPham;
        this.ngayTraSanPham = ngayTraSanPham;
        this.maKH = maKH;
        this.trangThai = trangThai;
    }
    
    public PhieuBaoHanh() {
        // Constructor rỗng
    }

    // Getters
    public int getIdBH() { return idBH; }
    public int getMaSP() { return maSP; }
    public LocalDate getNgayNhanSanPham() { return ngayNhanSanPham; }
    public LocalDate getNgayTraSanPham() { return ngayTraSanPham; }
    public int getMaKH() { return maKH; }
    public String getTrangThai() { return trangThai; }

    // Setters
    public void setIdBH(int idBH) { this.idBH = idBH; }
    public void setMaSP(int maSP) { this.maSP = maSP; }
    public void setNgayNhanSanPham(LocalDate ngayNhanSanPham) { this.ngayNhanSanPham = ngayNhanSanPham; }
    public void setNgayTraSanPham(LocalDate ngayTraSanPham) { this.ngayTraSanPham = ngayTraSanPham; }
    public void setMaKH(int maKH) { this.maKH = maKH; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return "PhieuBaoHanh{" +
               "idBH=" + idBH +
               ", maSP=" + maSP +
               ", ngayNhanSanPham=" + ngayNhanSanPham +
               ", ngayTraSanPham=" + ngayTraSanPham +
               ", maKH=" + maKH +
               ", trangThai='" + trangThai + '\'' +
               '}';
    }
}