package model;

import java.util.Objects;

public class TaiKhoan {
    
	public enum VaiTro {
        ADMIN("Admin"),
        NHAN_VIEN("NhanVien"),
        KHACH_HANG("KhachHang");

        private final String value;
        VaiTro(String value) { this.value = value; }
        public String getValue() { return value; }

        /**
         * SỬA LỖI: Phương thức an toàn để chuyển đổi chuỗi từ CSDL sang Enum.
         * Nó sẽ so sánh giá trị chuỗi (ví dụ: "KhachHang") thay vì tên hằng số (KHACH_HANG).
         * @param text Chuỗi giá trị từ cột VaiTro trong CSDL.
         * @return Hằng số Enum tương ứng, hoặc null nếu không tìm thấy.
         */
        public static VaiTro fromString(String text) {
            if (text != null) {
                for (VaiTro b : VaiTro.values()) {
                    // So sánh không phân biệt hoa thường với giá trị của enum
                    if (text.equalsIgnoreCase(b.value)) {
                        return b;
                    }
                }
            }
            // Ném ra lỗi nếu không tìm thấy vai trò hợp lệ
            throw new IllegalArgumentException("Không tìm thấy vai trò nào khớp với: " + text);
        }
    }

    private int idTaiKhoan;
    private String tenDangNhap;
    private String matKhau;
    private VaiTro vaiTro;
    private Integer maNV;
    private Integer maKH;

    // Constructors và các phương thức khác giữ nguyên như phiên bản trước...
    public TaiKhoan() {}
    
    public TaiKhoan(String tenDangNhap, String matKhau, VaiTro vaiTro) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
    }

    public TaiKhoan(int idTaiKhoan, String tenDangNhap, String matKhau, VaiTro vaiTro, Integer maNV, Integer maKH) {
        this(tenDangNhap, matKhau, vaiTro);
        this.idTaiKhoan = idTaiKhoan;
        this.maNV = maNV;
        this.maKH = maKH;
    }

    // Getters and Setters...
    public int getIdTaiKhoan() { return idTaiKhoan; }
    public void setIdTaiKhoan(int idTaiKhoan) { this.idTaiKhoan = idTaiKhoan; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public VaiTro getVaiTro() { return vaiTro; }
    public void setVaiTro(VaiTro vaiTro) { this.vaiTro = vaiTro; }
    public Integer getMaNV() { return maNV; }
    public void setMaNV(Integer maNV) { this.maNV = maNV; }
    public Integer getMaKH() { return maKH; }
    public void setMaKH(Integer maKH) { this.maKH = maKH; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaiKhoan taiKhoan = (TaiKhoan) o;
        if (idTaiKhoan != 0) {
            return idTaiKhoan == taiKhoan.idTaiKhoan;
        }
        return Objects.equals(tenDangNhap, taiKhoan.tenDangNhap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTaiKhoan == 0 ? tenDangNhap : idTaiKhoan);
    }

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "idTaiKhoan=" + idTaiKhoan +
                ", tenDangNhap='" + tenDangNhap + '\'' +
                ", matKhau='[PROTECTED]'" + 
                ", vaiTro=" + vaiTro +
                ", maNV=" + maNV +
                ", maKH=" + maKH +
                '}';
    }
}