package model;

public class KhachHang {
    private String maKH;
    private String hoTen;
    private String sdtKH;
    private int soDiemTichLuy;

    public KhachHang() {
    }
    
    public KhachHang(String maKH, String hoTen, String sdtKH, int soDiemTichLuy) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.sdtKH = sdtKH;
        this.soDiemTichLuy = soDiemTichLuy;
    }

    public KhachHang(String hoTen, String sdtKH, int soDiemTichLuy) {
        this.hoTen = hoTen;
        this.sdtKH = sdtKH;
        this.soDiemTichLuy = soDiemTichLuy;
    }

    public KhachHang(String maKH, String hoTen, String sdtKH) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.sdtKH = sdtKH;
    }

    public String getMaKH() {
        return maKH;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getSdtKH() {
        return sdtKH;
    }

    public int getSoDiemTichLuy() {
        return soDiemTichLuy;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setSdtKH(String sdtKH) {
        this.sdtKH = sdtKH;
    }

    public void setSoDiemTichLuy(int soDiemTichLuy) {
        this.soDiemTichLuy = soDiemTichLuy;
    }
}