package model;

public class SanPham {
    private int maSP;
    private String tenSP;
    private String mau;
    private double donGia; // Đây có thể là giá bán, không phải giá nhập
    private String nuocSX;
    private String hangSX;
    private int soLuong; // Đây có thể là số lượng tồn kho

    // Constructor đầy đủ
    public SanPham(int maSP, String tenSP, String mau, double donGia,
                   String nuocSX, String hangSX, int soLuong) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.mau = mau;
        this.donGia = donGia; // Xem xét lại ý nghĩa của trường này (giá bán hay giá nhập)
        this.nuocSX = nuocSX;
        this.hangSX = hangSX;
        this.soLuong = soLuong; // Số lượng tồn kho
    }

    // Constructor không có maSP (ví dụ: khi thêm mới chưa có ID từ DB)
    public SanPham(String tenSP, String mau, double donGia,
                   String nuocSX, String hangSX, int soLuong) {
        this.tenSP = tenSP;
        this.mau = mau;
        this.donGia = donGia;
        this.nuocSX = nuocSX;
        this.hangSX = hangSX;
        this.soLuong = soLuong;
    }

    // Constructor rỗng
    public SanPham() {
    }

    // Getters
    public int getMaSP() { return maSP; }
    public String getTenSP() { return tenSP; }
    public String getMau() { return mau; }
    public double getDonGia() { return donGia; } // Giá bán hoặc giá chung
    public String getNuocSX() { return nuocSX; }
    public String getHangSX() { return hangSX; }
    public int getSoLuong() { return soLuong; } 
    public void setMaSP(int maSP) { this.maSP = maSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }
    public void setMau(String mau) { this.mau = mau; }
    public void setDonGia(double donGia) { this.donGia = donGia; }
    public void setNuocSX(String nuocSX) { this.nuocSX = nuocSX; }
    public void setHangSX(String hangSX) { this.hangSX = hangSX; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    @Override
    public String toString() {
        if (maSP == 0 && (tenSP == null || tenSP.contains("Khong tim thay"))) {
             return tenSP != null ? tenSP : "Chon san pham"; // Cho placeholder
        }
        return maSP + " - " + tenSP;
    }
}