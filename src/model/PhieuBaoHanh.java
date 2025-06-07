package model;

public class PhieuBaoHanh {
    private String idBH;
    private String maSP;
    private String ngayNhanSanPham;
    private String ngayTraSanPham;
    private String maKH;
    private String trangThai;

    public PhieuBaoHanh(String idBH, String maSP, String ngayNhanSanPham, String ngayTraSanPham, String maKH, String trangThai) {
        this.idBH = idBH;
        this.maSP = maSP;
        this.ngayNhanSanPham = ngayNhanSanPham;
        this.ngayTraSanPham = ngayTraSanPham;
        this.maKH = maKH;
        this.trangThai = trangThai;
    }

    // Getters
    public String getIdBH() { return idBH; }
    public String getMaSP() { return maSP; }
    public String getNgayNhanSanPham() { return ngayNhanSanPham; }
    public String getNgayTraSanPham() { return ngayTraSanPham; }
    public String getMaKH() { return maKH; }
    public String getTrangThai() { return trangThai; }

    // Setters
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}