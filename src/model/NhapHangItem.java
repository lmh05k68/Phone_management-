package model;
import java.math.BigDecimal;
public class NhapHangItem {

    private SanPham sanPham;
    private int soLuong;
    private BigDecimal giaNhap;
    public NhapHangItem() {
    }
    public NhapHangItem(SanPham sanPham, int soLuong, BigDecimal giaNhap) {
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.giaNhap = giaNhap;
    }

    // --- GETTERS ---

    public SanPham getSanPham() {
        return sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public BigDecimal getGiaNhap() {
        return giaNhap;
    }

    // --- SETTERS ---

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public void setGiaNhap(BigDecimal giaNhap) {
        this.giaNhap = giaNhap;
    }

    @Override
    public String toString() {
        return "NhapHangItem{" +
                "sanPham=" + (sanPham != null ? sanPham.getTenSP() : "null") +
                ", soLuong=" + soLuong +
                ", giaNhap=" + giaNhap +
                '}';
    }
}