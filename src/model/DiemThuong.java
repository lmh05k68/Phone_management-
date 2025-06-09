package model;

import java.time.LocalDate;

public class DiemThuong {
    private int maTichDiem; // Đổi sang int
    private int maKH; // Đổi sang int
    // Cân nhắc bỏ TenKH và SdtKH nếu có thể lấy từ KhachHang qua MaKH
    private String tenKH; // Giữ lại nếu bạn muốn lưu snapshot tại thời điểm tích điểm
    private int maDonHang; // Đổi sang int
    private int soDiem;
    private LocalDate ngayTichLuy;
    private String sdtKH; // Giữ lại nếu bạn muốn lưu snapshot

    // Constructor khi đọc từ DB
    public DiemThuong(int maTichDiem, int maKH, String tenKH, int maDonHang, int soDiem, LocalDate ngayTichLuy, String sdtKH) {
        this.maTichDiem = maTichDiem;
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.maDonHang = maDonHang;
        this.soDiem = soDiem;
        this.ngayTichLuy = ngayTichLuy;
        this.sdtKH = sdtKH;
    }

    // Constructor khi tạo mới (maTichDiem tự sinh)
    public DiemThuong(int maKH, String tenKH, int maDonHang, int soDiem, LocalDate ngayTichLuy, String sdtKH) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.maDonHang = maDonHang;
        this.soDiem = soDiem;
        this.ngayTichLuy = ngayTichLuy;
        this.sdtKH = sdtKH;
    }
    
    public DiemThuong() {
        // Constructor rỗng
    }

    // Getters
    public int getMaTichDiem() { return maTichDiem; }
    public int getMaKH() { return maKH; }
    public String getTenKH() { return tenKH; }
    public int getMaDonHang() { return maDonHang; }
    public int getSoDiem() { return soDiem; }
    public LocalDate getNgayTichLuy() { return ngayTichLuy; }
    public String getSdtKH() { return sdtKH; }

    // Setters
    public void setMaTichDiem(int maTichDiem) { this.maTichDiem = maTichDiem; }
    public void setMaKH(int maKH) { this.maKH = maKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }
    public void setMaDonHang(int maDonHang) { this.maDonHang = maDonHang; }
    public void setSoDiem(int soDiem) { this.soDiem = soDiem; }
    public void setNgayTichLuy(LocalDate ngayTichLuy) { this.ngayTichLuy = ngayTichLuy; }
    public void setSdtKH(String sdtKH) { this.sdtKH = sdtKH; }

    @Override
    public String toString() {
        return "DiemThuong{" +
               "maTichDiem=" + maTichDiem +
               ", maKH=" + maKH +
               ", tenKH='" + tenKH + '\'' +
               ", maDonHang=" + maDonHang +
               ", soDiem=" + soDiem +
               ", ngayTichLuy=" + ngayTichLuy +
               ", sdtKH='" + sdtKH + '\'' +
               '}';
    }
}