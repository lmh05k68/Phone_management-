package model;

import java.math.BigDecimal;

public class KPI {
    private int idKpi;
    private int manv;
    private String hoTen;
    private int thang;
    private int nam;
    private BigDecimal tongDoanhSo;
    private BigDecimal thuongMuc;
    private BigDecimal thuongRank;
    public int getIdKpi() { return idKpi; }
    public void setIdKpi(int idKpi) { this.idKpi = idKpi; }
    public int getManv() { return manv; }
    public void setManv(int manv) { this.manv = manv; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public int getThang() { return thang; }
    public void setThang(int thang) { this.thang = thang; }
    public int getNam() { return nam; }
    public void setNam(int nam) { this.nam = nam; }
    public BigDecimal getTongDoanhSo() { return tongDoanhSo; }
    public void setTongDoanhSo(BigDecimal tongDoanhSo) { this.tongDoanhSo = tongDoanhSo; }
    public BigDecimal getThuongMuc() { return thuongMuc; }
    public void setThuongMuc(BigDecimal thuongMuc) { this.thuongMuc = thuongMuc; }
    public BigDecimal getThuongRank() { return thuongRank; }
    public void setThuongRank(BigDecimal thuongRank) { this.thuongRank = thuongRank; }
    public BigDecimal getTongThuong() {
        BigDecimal muc = (this.thuongMuc != null) ? this.thuongMuc : BigDecimal.ZERO;
        BigDecimal rank = (this.thuongRank != null) ? this.thuongRank : BigDecimal.ZERO;
        return muc.add(rank);
    }
}