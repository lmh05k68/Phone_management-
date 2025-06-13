package model;
import java.math.BigDecimal;
public class BangLuong {
	private int maNV;
    private String tenNV;
    private BigDecimal luongCoBan;
    private BigDecimal tongDoanhSo;
    private BigDecimal thuongMuc;
    private BigDecimal thuongRank;
    private BigDecimal tongThuong;
    private BigDecimal luongThucLanh; 
    public BangLuong(int maNV, String tenNV, BigDecimal luongCoBan, BigDecimal tongDoanhSo, BigDecimal thuongMuc, BigDecimal thuongRank, BigDecimal tongThuong, BigDecimal luongThucLanh) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.luongCoBan = luongCoBan;
        this.tongDoanhSo = tongDoanhSo;
        this.thuongMuc = thuongMuc;
        this.thuongRank = thuongRank;
        this.tongThuong = tongThuong;
        this.luongThucLanh = luongThucLanh;
    }
    public BangLuong() {
    }
    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public BigDecimal getLuongCoBan() {
        return luongCoBan;
    }

    public void setLuongCoBan(BigDecimal luongCoBan) {
        this.luongCoBan = luongCoBan;
    }
    
    public BigDecimal getTongDoanhSo() {
        return tongDoanhSo;
    }

    public void setTongDoanhSo(BigDecimal tongDoanhSo) {
        this.tongDoanhSo = tongDoanhSo;
    }

    public BigDecimal getThuongMuc() {
        return thuongMuc;
    }

    public void setThuongMuc(BigDecimal thuongMuc) {
        this.thuongMuc = thuongMuc;
    }

    public BigDecimal getThuongRank() {
        return thuongRank;
    }

    public void setThuongRank(BigDecimal thuongRank) {
        this.thuongRank = thuongRank;
    }

    public BigDecimal getTongThuong() {
        return tongThuong;
    }

    public void setTongThuong(BigDecimal tongThuong) {
        this.tongThuong = tongThuong;
    }

    public BigDecimal getLuongThucLanh() {
        return luongThucLanh;
    }

    public void setLuongThucLanh(BigDecimal luongThucLanh) {
        this.luongThucLanh = luongThucLanh;
    }

    @Override
    public String toString() {
        return "BangLuong{" +
                "maNV=" + maNV +
                ", tenNV='" + tenNV + '\'' +
                ", luongCoBan=" + luongCoBan +
                ", tongThuong=" + tongThuong +
                ", luongThucLanh=" + luongThucLanh +
                '}';
    }
}
