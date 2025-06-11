package model;

import java.time.LocalDate;

public class DoiTra {
    private int idDT;
    private String maSPCuThe; // SỬA: từ int maSP thành String maSPCuThe
    private int maKH;
    private int maDonHang;
    private LocalDate ngayDoiTra;
    private String lyDo;
    private String trangThai;

    // Constructor đầy đủ
    public DoiTra(int idDT, String maSPCuThe, int maKH, int maDonHang, LocalDate ngayDoiTra, String lyDo, String trangThai) {
        this.idDT = idDT;
        this.maSPCuThe = maSPCuThe; // SỬA
        this.maKH = maKH;
        this.maDonHang = maDonHang;
        this.ngayDoiTra = ngayDoiTra;
        this.lyDo = lyDo;
        this.trangThai = trangThai;
    }
    
    // Constructor khi tạo mới với trạng thái mặc định
    public DoiTra(String maSPCuThe, int maKH, int maDonHang, LocalDate ngayDoiTra, String lyDo) {
        this.maSPCuThe = maSPCuThe; // SỬA
        this.maKH = maKH;
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
    public String getMaSPCuThe() { return maSPCuThe; } // SỬA
    public int getMaKH() { return maKH; }
    public int getMaDonHang() { return maDonHang; }
    public LocalDate getNgayDoiTra() { return ngayDoiTra; }
    public String getLyDo() { return lyDo; }
    public String getTrangThai() { return trangThai; }

    // Setters
    public void setIdDT(int idDT) { this.idDT = idDT; }
    public void setMaSPCuThe(String maSPCuThe) { this.maSPCuThe = maSPCuThe; } // SỬA
    public void setMaKH(int maKH) { this.maKH = maKH; }
    public void setMaDonHang(int maDonHang) { this.maDonHang = maDonHang; }
    public void setNgayDoiTra(LocalDate ngayDoiTra) { this.ngayDoiTra = ngayDoiTra; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return "DoiTra{" +
               "idDT=" + idDT +
               ", maKH=" + maKH +
               ", maSPCuThe='" + maSPCuThe + '\'' + // SỬA
               ", maDonHang=" + maDonHang +
               ", ngayDoiTra=" + ngayDoiTra +
               ", lyDo='" + lyDo + '\'' +
               ", trangThai='" + trangThai + '\'' +
               '}';
    }
}