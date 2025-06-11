package model;

import java.time.LocalDate;
import java.util.Objects;

public class PhieuBaoHanh {

    public enum TrangThaiBaoHanh {
        CHO_TIEP_NHAN("Cho tiep nhan"),
        DANG_XU_LY("Dang xu ly"),
        CHO_LINH_KIEN("Cho linh kien"),
        DA_SUA_CHUA_XONG("Da sua chua xong"),
        KHONG_THE_SUA_CHUA("Khong the sua chua"),
        DA_TRA_KHACH("Da tra khach");

        private final String value;

        TrangThaiBaoHanh(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
        public static TrangThaiBaoHanh fromString(String text) {
            for (TrangThaiBaoHanh b : TrangThaiBaoHanh.values()) {
                if (b.value.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            // Trả về một giá trị mặc định hoặc ném exception nếu không tìm thấy
            return DANG_XU_LY;
        }
    }

    private int idBH;
    private String maSPCuThe;
    private LocalDate ngayNhanSanPham;
    private LocalDate ngayTraSanPham;
    private int maKH;
    private TrangThaiBaoHanh trangThai;
    private int maHDX;

    public PhieuBaoHanh() {
    }

    // Constructor để tạo mới
    public PhieuBaoHanh(String maSPCuThe, LocalDate ngayNhanSanPham, LocalDate ngayTraSanPham, int maKH, TrangThaiBaoHanh trangThai, int maHDX) {
        this.maSPCuThe = maSPCuThe;
        this.ngayNhanSanPham = ngayNhanSanPham;
        this.ngayTraSanPham = ngayTraSanPham;
        this.maKH = maKH;
        this.trangThai = trangThai;
        this.maHDX = maHDX;
    }

    // Constructor để đọc từ DB
    public PhieuBaoHanh(int idBH, String maSPCuThe, LocalDate ngayNhanSanPham, LocalDate ngayTraSanPham, int maKH, TrangThaiBaoHanh trangThai, int maHDX) {
        this.idBH = idBH;
        this.maSPCuThe = maSPCuThe;
        this.ngayNhanSanPham = ngayNhanSanPham;
        this.ngayTraSanPham = ngayTraSanPham;
        this.maKH = maKH;
        this.trangThai = trangThai;
        this.maHDX = maHDX;
    }

    // Getters and Setters
    public int getIdBH() { return idBH; }
    public void setIdBH(int idBH) { this.idBH = idBH; }
    public String getMaSPCuThe() { return maSPCuThe; }
    public void setMaSPCuThe(String maSPCuThe) { this.maSPCuThe = maSPCuThe; }
    public LocalDate getNgayNhanSanPham() { return ngayNhanSanPham; }
    public void setNgayNhanSanPham(LocalDate ngayNhanSanPham) { this.ngayNhanSanPham = ngayNhanSanPham; }
    public LocalDate getNgayTraSanPham() { return ngayTraSanPham; }
    public void setNgayTraSanPham(LocalDate ngayTraSanPham) { this.ngayTraSanPham = ngayTraSanPham; }
    public int getMaKH() { return maKH; }
    public void setMaKH(int maKH) { this.maKH = maKH; }
    public TrangThaiBaoHanh getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiBaoHanh trangThai) { this.trangThai = trangThai; }
    public int getMaHDX() { return maHDX; }
    public void setMaHDX(int maHDX) { this.maHDX = maHDX; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuBaoHanh that = (PhieuBaoHanh) o;
        if (idBH == 0) return false;
        return idBH == that.idBH;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBH);
    }
}