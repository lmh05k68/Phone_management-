package model;

import java.time.LocalDate;

public class PhieuBaoHanh {
    private int idBH;
    private int maSP;
    private LocalDate ngayNhanSanPham;
    private LocalDate ngayTraSanPham;  // Có thể null
    private int maKH;
    private String trangThai;
    private Integer maHDX; // Thêm trường Mã hóa đơn xuất (Integer để cho phép null)

    // Constructor khi đọc từ DB (bao gồm idBH và maHDX)
    public PhieuBaoHanh(int idBH, int maSP, LocalDate ngayNhanSanPham, LocalDate ngayTraSanPham, int maKH, String trangThai, Integer maHDX) {
        this.idBH = idBH;
        this.maSP = maSP;
        this.ngayNhanSanPham = ngayNhanSanPham;
        this.ngayTraSanPham = ngayTraSanPham;
        this.maKH = maKH;
        this.trangThai = trangThai;
        this.maHDX = maHDX; // Gán giá trị cho maHDX
    }

    // Constructor khi tạo mới (idBH tự sinh, bao gồm maHDX)
    public PhieuBaoHanh(int maSP, LocalDate ngayNhanSanPham, LocalDate ngayTraSanPham, int maKH, String trangThai, Integer maHDX) {
        this.maSP = maSP;
        this.ngayNhanSanPham = ngayNhanSanPham;
        this.ngayTraSanPham = ngayTraSanPham;
        this.maKH = maKH;
        this.trangThai = trangThai;
        this.maHDX = maHDX; // Gán giá trị cho maHDX
    }

    // Constructor cũ (tạo mới không có maHDX) - Bạn có thể giữ lại nếu vẫn cần hoặc xóa đi nếu không dùng
    // Để đơn giản, tôi sẽ giả sử bạn muốn constructor tạo mới luôn có khả năng nhận maHDX
    // Nếu bạn muốn giữ lại, thì controller sẽ phải quyết định gọi constructor nào
    // public PhieuBaoHanh(int maSP, LocalDate ngayNhanSanPham, LocalDate ngayTraSanPham, int maKH, String trangThai) {
    //     this(maSP, ngayNhanSanPham, ngayTraSanPham, maKH, trangThai, null); // Gọi constructor mới với maHDX là null
    // }

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
    public Integer getMaHDX() { return maHDX; } // Getter cho maHDX

    // Setters
    public void setIdBH(int idBH) { this.idBH = idBH; }
    public void setMaSP(int maSP) { this.maSP = maSP; }
    public void setNgayNhanSanPham(LocalDate ngayNhanSanPham) { this.ngayNhanSanPham = ngayNhanSanPham; }
    public void setNgayTraSanPham(LocalDate ngayTraSanPham) { this.ngayTraSanPham = ngayTraSanPham; }
    public void setMaKH(int maKH) { this.maKH = maKH; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void setMaHDX(Integer maHDX) { this.maHDX = maHDX; } // Setter cho maHDX

    @Override
    public String toString() {
        return "PhieuBaoHanh{" +
               "idBH=" + idBH +
               ", maSP=" + maSP +
               ", ngayNhanSanPham=" + ngayNhanSanPham +
               ", ngayTraSanPham=" + ngayTraSanPham +
               ", maKH=" + maKH +
               ", trangThai='" + trangThai + '\'' +
               ", maHDX=" + maHDX + // Thêm maHDX vào toString
               '}';
    }
}