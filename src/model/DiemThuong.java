package model;

import java.time.LocalDate;

/**
 * Lớp này đại diện cho một giao dịch điểm thưởng trong bảng DiemThuong.
 * Nó ánh xạ 1-1 với cấu trúc của bảng trong CSDL.
 */
public class DiemThuong {
    private int maTichDiem;
    private int maKH;
    private int maHDX; // Đã đổi tên từ maDonHang để khớp với CSDL
    private int soDiem;
    private LocalDate ngayTichLuy;

    // Constructor để đọc dữ liệu từ CSDL (đã có maTichDiem)
    public DiemThuong(int maTichDiem, int maKH, int maHDX, int soDiem, LocalDate ngayTichLuy) {
        this.maTichDiem = maTichDiem;
        this.maKH = maKH;
        this.maHDX = maHDX;
        this.soDiem = soDiem;
        this.ngayTichLuy = ngayTichLuy;
    }

    // Constructor để tạo một giao dịch mới (chưa có maTichDiem)
    public DiemThuong(int maKH, int maHDX, int soDiem) {
        this.maKH = maKH;
        this.maHDX = maHDX;
        this.soDiem = soDiem;
        this.ngayTichLuy = LocalDate.now(); // Tự động lấy ngày hiện tại
    }

    public DiemThuong() {
    }

    // --- Getters ---
    public int getMaTichDiem() { return maTichDiem; }
    public int getMaKH() { return maKH; }
    public int getMaHDX() { return maHDX; }
    public int getSoDiem() { return soDiem; }
    public LocalDate getNgayTichLuy() { return ngayTichLuy; }

    // --- Setters ---
    public void setMaTichDiem(int maTichDiem) { this.maTichDiem = maTichDiem; }
    public void setMaKH(int maKH) { this.maKH = maKH; }
    public void setMaHDX(int maHDX) { this.maHDX = maHDX; }
    public void setSoDiem(int soDiem) { this.soDiem = soDiem; }
    public void setNgayTichLuy(LocalDate ngayTichLuy) { this.ngayTichLuy = ngayTichLuy; }

    @Override
    public String toString() {
        return "DiemThuong{" +
               "maTichDiem=" + maTichDiem +
               ", maKH=" + maKH +
               ", maHDX=" + maHDX +
               ", soDiem=" + soDiem +
               ", ngayTichLuy=" + ngayTichLuy +
               '}';
    }
}