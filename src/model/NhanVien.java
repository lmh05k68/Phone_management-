package model;
public class NhanVien {
    private String maNV;
    private String tenNV;
    private String ngaySinh;
    private double luong;
    private String soDienThoai; // << SỬA TÊN THUỘC TÍNH

    public NhanVien(String maNV, String tenNV, String ngaySinh, double luong, String soDienThoai) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.ngaySinh = ngaySinh;
        this.luong = luong;
        this.soDienThoai = soDienThoai; // << SỬA Ở ĐÂY
    }

    public String getMaNV() { return maNV; }
    public String getTenNV() { return tenNV; }
    public String getNgaySinh() { return ngaySinh; } 
    public double getLuong() { return luong; }
    public String getSoDienThoai() { return soDienThoai; } // << SỬA GETTER

    public void setMaNV(String maNV) { this.maNV = maNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }
    public void setLuong(double luong) { this.luong = luong; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; } // << SỬA SETTER
}