package model;

import java.math.BigDecimal;
import java.util.Objects;
public class SPCuThe {
    public enum TrangThaiSP {
        TRONG_KHO("Trong Kho"),
        DA_BAN("Da Ban"),
        DANG_BAO_HANH("Dang Bao Hanh"),
        LOI("Loi"),
        DA_TRA_HANG("Da Tra Hang");

        private final String value;

        TrangThaiSP(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
        public static TrangThaiSP fromValue(String text) {
            for (TrangThaiSP b : TrangThaiSP.values()) {
                if (b.value.equalsIgnoreCase(text.trim())) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Khong tim thay trang thai hop le cho gia tri: '" + text + "'");
        }
    }

    private String maSPCuThe;     // Khóa chính (VARCHAR), ví dụ: IMEI/Serial
    private int maSP;             // Khóa ngoại đến SanPham (INT NOT NULL)
    private BigDecimal giaNhap;   // Giá nhập (DECIMAL NOT NULL)
    private BigDecimal giaXuat;   // Giá bán (DECIMAL, có thể NULL)
    private int maHDN;            // Khóa ngoại đến HoaDonNhap (INT NOT NULL)
    private Integer maHDX;        // Khóa ngoại đến HoaDonXuat (INT, có thể NULL)
    private TrangThaiSP trangThai; // Thuộc tính kiểu enum

    // Constructors
    public SPCuThe() {
    }

    public SPCuThe(String maSPCuThe, int maSP, BigDecimal giaNhap, BigDecimal giaXuat, int maHDN, Integer maHDX, TrangThaiSP trangThai) {
        this.maSPCuThe = maSPCuThe;
        this.maSP = maSP;
        this.giaNhap = giaNhap;
        this.giaXuat = giaXuat;
        this.maHDN = maHDN;
        this.maHDX = maHDX;
        this.trangThai = trangThai;
    }

    // Getters and Setters
    public String getMaSPCuThe() {
        return maSPCuThe;
    }

    public void setMaSPCuThe(String maSPCuThe) {
        this.maSPCuThe = maSPCuThe;
    }

    public int getMaSP() {
        return maSP;
    }

    public void setMaSP(int maSP) {
        this.maSP = maSP;
    }

    public BigDecimal getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(BigDecimal giaNhap) {
        this.giaNhap = giaNhap;
    }

    public BigDecimal getGiaXuat() {
        return giaXuat;
    }

    public void setGiaXuat(BigDecimal giaXuat) {
        this.giaXuat = giaXuat;
    }

    public int getMaHDN() {
        return maHDN;
    }

    public void setMaHDN(int maHDN) {
        this.maHDN = maHDN;
    }

    public Integer getMaHDX() {
        return maHDX;
    }

    public void setMaHDX(Integer maHDX) {
        this.maHDX = maHDX;
    }

    public TrangThaiSP getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiSP trangThai) {
        this.trangThai = trangThai;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SPCuThe spCuThe = (SPCuThe) o;
        return Objects.equals(maSPCuThe, spCuThe.maSPCuThe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSPCuThe);
    }

    @Override
    public String toString() {
        return "SPCuThe{" +
                "maSPCuThe='" + maSPCuThe + '\'' +
                ", maSP=" + maSP +
                ", giaNhap=" + giaNhap +
                ", giaXuat=" + giaXuat +
                ", maHDN=" + maHDN +
                ", maHDX=" + maHDX +
                ", trangThai=" + (trangThai != null ? trangThai.getValue() : "null") +
                '}';
    }
}