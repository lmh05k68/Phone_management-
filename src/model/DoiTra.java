package model;

import java.util.Date;

public class DoiTra {
    private String idDT;
    private String maKH;
    private String maSP;
    private String maDonHang;
    private Date ngayDoiTra; // Hoặc ngayYeuCau tùy bạn đặt tên
    private String lyDo;
    private String trangThai; // << THÊM NẾU CÓ

    // Constructor đầy đủ
    public DoiTra(String idDT, String maKH, String maSP, String maDonHang, Date ngayDoiTra, String lyDo, String trangThai) {
        this.idDT = idDT;
        this.maKH = maKH;
        this.maSP = maSP;
        this.maDonHang = maDonHang;
        this.ngayDoiTra = ngayDoiTra;
        this.lyDo = lyDo;
        this.trangThai = trangThai; // << THÊM
    }

    // Constructor nếu không có trạng thái ban đầu khi tạo từ khách hàng
    public DoiTra(String idDT, String maKH, String maSP, String maDonHang, Date ngayDoiTra, String lyDo) {
        this.idDT = idDT;
        this.maKH = maKH;
        this.maSP = maSP;
        this.maDonHang = maDonHang;
        this.ngayDoiTra = ngayDoiTra;
        this.lyDo = lyDo;
        this.trangThai = "Chờ xử lý"; // Hoặc một giá trị mặc định
    }


    // Getters
    public String getIdDT() { return idDT; }
    public String getMaKH() { return maKH; }
    public String getMaSP() { return maSP; }
    public String getMaDonHang() { return maDonHang; }
    public Date getNgayDoiTra() { return ngayDoiTra; }
    public String getLyDo() { return lyDo; }
    public String getTrangThai() { return trangThai; } // << THÊM

    // Setters
    public void setIdDT(String idDT) { this.idDT = idDT; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }
    public void setNgayDoiTra(Date ngayDoiTra) { this.ngayDoiTra = ngayDoiTra; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; } // << THÊM
}