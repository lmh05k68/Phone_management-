package model;

import java.time.LocalDate; // Sử dụng LocalDate thay vì java.util.Date

public class DoiTra {
    private int idDT; // Đổi sang int
    private int maKH; // Đổi sang int
    private int maSP; // Đổi sang int
    private int maDonHang; // Đổi sang int
    private LocalDate ngayDoiTra; // Đổi sang LocalDate
    private String lyDo;
    private String trangThai;

    // Constructor khi đọc từ DB
    public DoiTra(int idDT, int maKH, int maSP, int maDonHang, LocalDate ngayDoiTra, String lyDo, String trangThai) {
        this.idDT = idDT;
        this.maKH = maKH;
        this.maSP = maSP;
        this.maDonHang = maDonHang;
        this.ngayDoiTra = ngayDoiTra;
        this.lyDo = lyDo;
        this.trangThai = trangThai;
    }

    // Constructor khi tạo mới (idDT tự sinh)
    public DoiTra(int maKH, int maSP, int maDonHang, LocalDate ngayDoiTra, String lyDo, String trangThai) {
        this.maKH = maKH;
        this.maSP = maSP;
        this.maDonHang = maDonHang;
        this.ngayDoiTra = ngayDoiTra;
        this.lyDo = lyDo;
        this.trangThai = trangThai;
    }
    
    // Constructor khi tạo mới với trạng thái mặc định
    public DoiTra(int maKH, int maSP, int maDonHang, LocalDate ngayDoiTra, String lyDo) {
        this.maKH = maKH;
        this.maSP = maSP;
        this.maDonHang = maDonHang;
        this.ngayDoiTra = ngayDoiTra;
        this.lyDo = lyDo;
        this.trangThai = "Chờ xử lý"; // Trạng thái mặc định
    }
    
    public DoiTra() {
        // Constructor rỗng
    }

    // Getters
    public int getIdDT() { return idDT; }
    public int getMaKH() { return maKH; }
    public int getMaSP() { return maSP; }
    public int getMaDonHang() { return maDonHang; }
    public LocalDate getNgayDoiTra() { return ngayDoiTra; }
    public String getLyDo() { return lyDo; }
    public String getTrangThai() { return trangThai; }

    // Setters
    public void setIdDT(int idDT) { this.idDT = idDT; }
    public void setMaKH(int maKH) { this.maKH = maKH; }
    public void setMaSP(int maSP) { this.maSP = maSP; }
    public void setMaDonHang(int maDonHang) { this.maDonHang = maDonHang; }
    public void setNgayDoiTra(LocalDate ngayDoiTra) { this.ngayDoiTra = ngayDoiTra; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return "DoiTra{" +
               "idDT=" + idDT +
               ", maKH=" + maKH +
               ", maSP=" + maSP +
               ", maDonHang=" + maDonHang +
               ", ngayDoiTra=" + ngayDoiTra +
               ", lyDo='" + lyDo + '\'' +
               ", trangThai='" + trangThai + '\'' +
               '}';
    }
}