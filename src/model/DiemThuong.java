package model;

import java.time.LocalDate;

public class DiemThuong {
    private String maTichDiem;
    private String maKH;
    private String tenKH;
    private String maDonHang;
    private int soDiem;
    private LocalDate ngayTichLuy;
    private String sdtKH;

    // Constructor đầy đủ
    public DiemThuong(String maTichDiem, String maKH, String tenKH, String maDonHang, int soDiem, LocalDate ngayTichLuy, String sdtKH) {
        this.maTichDiem = maTichDiem;
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.maDonHang = maDonHang;
        this.soDiem = soDiem;
        this.ngayTichLuy = ngayTichLuy;
        this.sdtKH = sdtKH;
    }

    // Constructor không có mã tích điểm (có thể dùng khi insert mới, mã tự sinh)
    public DiemThuong(String maKH, String tenKH, String maDonHang, int soDiem, LocalDate ngayTichLuy, String sdtKH) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.maDonHang = maDonHang;
        this.soDiem = soDiem;
        this.ngayTichLuy = ngayTichLuy;
        this.sdtKH = sdtKH;
    }

    // Getters
    public String getMaTichDiem() {
        return maTichDiem;
    }

    public String getMaKH() {
        return maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public int getSoDiem() {
        return soDiem;
    }

    public LocalDate getNgayTichLuy() {
        return ngayTichLuy;
    }

    public String getSdtKH() {
        return sdtKH;
    }

    // Setters
    public void setMaTichDiem(String maTichDiem) {
        this.maTichDiem = maTichDiem;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public void setSoDiem(int soDiem) {
        this.soDiem = soDiem;
    }

    public void setNgayTichLuy(LocalDate ngayTichLuy) {
        this.ngayTichLuy = ngayTichLuy;
    }

    public void setSdtKH(String sdtKH) {
        this.sdtKH = sdtKH;
    }
}