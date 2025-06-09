package model;

import java.time.LocalDate;

public class HoaDonNhap {
    private int maHDN; // Đổi sang int
    private LocalDate ngayNhap;
    private int maNV; // Đổi sang int (tham chiếu đến NhanVien.maNV)
    private int maNCC; // Đổi sang int (tham chiếu đến NhaCungCap.maNCC)

    // Constructor khi đọc từ DB (có MaHDN)
    public HoaDonNhap(int maHDN, LocalDate ngayNhap, int maNV, int maNCC) {
        this.maHDN = maHDN;
        this.ngayNhap = ngayNhap;
        this.maNV = maNV;
        this.maNCC = maNCC;
    }

    // Constructor khi tạo mới (MaHDN tự sinh)
    public HoaDonNhap(LocalDate ngayNhap, int maNV, int maNCC) {
        this.ngayNhap = ngayNhap;
        this.maNV = maNV;
        this.maNCC = maNCC;
    }
    
    public HoaDonNhap() {
    }

    // Getters
    public int getMaHDN() { return maHDN; }
    public LocalDate getNgayNhap() { return ngayNhap; }
    public int getMaNV() { return maNV; }
    public int getMaNCC() { return maNCC; }

    // Setters
    public void setMaHDN(int maHDN) { this.maHDN = maHDN; }
    public void setNgayNhap(LocalDate ngayNhap) { this.ngayNhap = ngayNhap; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public void setMaNCC(int maNCC) { this.maNCC = maNCC; }

    @Override
    public String toString() {
        return "HoaDonNhap{" +
               "maHDN=" + maHDN +
               ", ngayNhap=" + ngayNhap +
               ", maNV=" + maNV +
               ", maNCC=" + maNCC +
               '}';
    }
}