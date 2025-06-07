package model;

public class TaiKhoan {
    private String tenDangNhap; // Khóa chính
    private String matKhau;     // Trong thực tế, đây nên là mật khẩu đã được băm (hashed)
    private String vaiTro;      // Ví dụ: "admin", "nhanvien", "khachhang"
    private String maDoiTuong;  // MaKH, MaNV, hoặc null/giá trị đặc biệt cho admin

    /**
     * Constructor đầy đủ cho đối tượng TaiKhoan.
     * @param tenDangNhap Tên đăng nhập của tài khoản (khóa chính).
     * @param matKhau Mật khẩu của tài khoản (nên là mật khẩu đã băm).
     * @param vaiTro Vai trò của tài khoản trong hệ thống.
     * @param maDoiTuong Mã của đối tượng liên quan (MaKH, MaNV), có thể null.
     */
    public TaiKhoan(String tenDangNhap, String matKhau, String vaiTro, String maDoiTuong) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.maDoiTuong = maDoiTuong;
    }

    // Getters
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public String getMaDoiTuong() {
        return maDoiTuong;
    }

    // Setters
    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public void setMatKhau(String matKhau) {
        // Nếu bạn có logic băm mật khẩu, nó có thể được thực hiện ở đây
        // hoặc trước khi gọi setter này.
        this.matKhau = matKhau;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public void setMaDoiTuong(String maDoiTuong) {
        this.maDoiTuong = maDoiTuong;
    }

    // (Tùy chọn) Ghi đè phương thức toString() để dễ dàng debug
    @Override
    public String toString() {
        return "TaiKhoan{" +
               "tenDangNhap='" + tenDangNhap + '\'' +
               ", matKhau='********'" + // Không nên in mật khẩu ra log
               ", vaiTro='" + vaiTro + '\'' +
               ", maDoiTuong='" + maDoiTuong + '\'' +
               '}';
    }
}