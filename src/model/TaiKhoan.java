package model;

public class TaiKhoan {
    private String tenDangNhap; // Khóa chính, giữ nguyên String
    private String matKhau;
    private String vaiTro;
    private Integer maDoiTuong;  // Đổi sang Integer để có thể là null nếu vai trò không yêu cầu (ví dụ: admin)

    public TaiKhoan(String tenDangNhap, String matKhau, String vaiTro, Integer maDoiTuong) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.maDoiTuong = maDoiTuong;
    }
    
    public TaiKhoan() {
    }

    // Getters
    public String getTenDangNhap() { return tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public String getVaiTro() { return vaiTro; }
    public Integer getMaDoiTuong() { return maDoiTuong; }

    // Setters
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
    public void setMaDoiTuong(Integer maDoiTuong) { this.maDoiTuong = maDoiTuong; }

    @Override
    public String toString() {
        return "TaiKhoan{" +
               "tenDangNhap='" + tenDangNhap + '\'' +
               ", matKhau='********'" +
               ", vaiTro='" + vaiTro + '\'' +
               ", maDoiTuong=" + (maDoiTuong == null ? "null" : maDoiTuong.toString()) +
               '}';
    }
}