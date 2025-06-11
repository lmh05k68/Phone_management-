package model; 
import java.math.BigDecimal;
public class ChiTietDonHang {
    private String maSPCuThe;
    private String tenSP;
    private String mau;
    private BigDecimal giaXuat;

    public ChiTietDonHang(String maSPCuThe, String tenSP, String mau, BigDecimal giaXuat) {
        this.maSPCuThe = maSPCuThe;
        this.tenSP = tenSP;
        this.mau = mau;
        this.giaXuat = giaXuat;
    }

    // Getters
    public String getMaSPCuThe() { return maSPCuThe; }
    public String getTenSP() { return tenSP; }
    public String getMau() { return mau; }
    public BigDecimal getGiaXuat() { return giaXuat; }

    @Override
    public String toString() {
        return "ChiTietDonHangDTO{" +
                "maSPCuThe='" + maSPCuThe + '\'' +
                ", tenSP='" + tenSP + '\'' +
                ", mau='" + mau + '\'' +
                ", giaXuat=" + giaXuat +
                '}';
    }
}