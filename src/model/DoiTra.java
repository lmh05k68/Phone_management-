package model;

import java.time.LocalDate;

public class DoiTra {
    private int idDT;
    private String maSPCuThe;
    private int maKH;
    private int maDonHang;
    private LocalDate ngayDoiTra;
    private String lyDo;
    private String trangThai;
    private String maSPMoi; // *** SỬA ĐỔI 1: Đổi tên trường dữ liệu

    // Constructor để đọc dữ liệu từ CSDL (đầy đủ các trường)
    // *** SỬA ĐỔI 2: Thêm tham số maSPMoi ***
    public DoiTra(int idDT, String maSPCuThe, int maKH, int maDonHang, LocalDate ngayDoiTra, String lyDo, String trangThai, String maSPMoi) {
        this.idDT = idDT;
        this.maSPCuThe = maSPCuThe;
        this.maKH = maKH;
        this.maDonHang = maDonHang;
        this.ngayDoiTra = ngayDoiTra;
        this.lyDo = lyDo;
        this.trangThai = trangThai;
        this.maSPMoi = maSPMoi;
    }

    // Constructor để TẠO MỚI yêu cầu từ khách hàng (5 tham số) - Không đổi
    public DoiTra(String maSPCuThe, int maKH, int maDonHang, LocalDate ngayDoiTra, String lyDo) {
        this.maSPCuThe = maSPCuThe;
        this.maKH = maKH;
        this.maDonHang = maDonHang;
        this.ngayDoiTra = ngayDoiTra;
        this.lyDo = lyDo;
        this.trangThai = "Cho xu ly";
        this.maSPMoi = null; // Mới đầu chưa có sản phẩm đổi
    }

    // Constructor rỗng
    public DoiTra() {}

    // Getters
    public int getIdDT() { return idDT; }
    public String getMaSPCuThe() { return maSPCuThe; }
    public int getMaKH() { return maKH; }
    public int getMaDonHang() { return maDonHang; }
    public LocalDate getNgayDoiTra() { return ngayDoiTra; }
    public String getLyDo() { return lyDo; }
    public String getTrangThai() { return trangThai; }
    public String getMaSPMoi() { return maSPMoi; } // *** SỬA ĐỔI 3: Thêm Getter

    // Setters
    public void setIdDT(int idDT) { this.idDT = idDT; }
    public void setMaSPCuThe(String maSPCuThe) { this.maSPCuThe = maSPCuThe; }
    public void setMaKH(int maKH) { this.maKH = maKH; }
    public void setMaDonHang(int maDonHang) { this.maDonHang = maDonHang; }
    public void setNgayDoiTra(LocalDate ngayDoiTra) { this.ngayDoiTra = ngayDoiTra; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void setMaSPMoi(String maSPMoi) { this.maSPMoi = maSPMoi; } // *** SỬA ĐỔI 4: Thêm Setter

    @Override
    public String toString() {
        return "DoiTra{" +
               "idDT=" + idDT +
               ", maSPCuThe='" + maSPCuThe + '\'' +
               ", maKH=" + maKH +
               ", maDonHang=" + maDonHang +
               ", ngayDoiTra=" + ngayDoiTra +
               ", lyDo='" + lyDo + '\'' +
               ", trangThai='" + trangThai + '\'' +
               ", maSPMoi='" + maSPMoi + '\'' + // *** SỬA ĐỔI 5: Cập nhật toString
               '}';
    }
}