package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; 
public class HoaDonXuat {
    private int maHDX;
    private LocalDate ngayLap;
    private BigDecimal thanhTien;
    private BigDecimal mucThue;
    private int maNV;
    private int maKH;
    public HoaDonXuat() {}

    public HoaDonXuat(int maHDX, LocalDate ngayLap, BigDecimal thanhTien, BigDecimal mucThue, int maNV, int maKH) {
        this.maHDX = maHDX;
        this.ngayLap = ngayLap;
        this.thanhTien = thanhTien;
        this.mucThue = mucThue;
        this.maNV = maNV;
        this.maKH = maKH;
    }
    public int getMaHDX() { return maHDX; }
    public void setMaHDX(int maHDX) { this.maHDX = maHDX; }
    public LocalDate getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDate ngayLap) { this.ngayLap = ngayLap; }
    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
    public BigDecimal getMucThue() { return mucThue; }
    public void setMucThue(BigDecimal mucThue) { this.mucThue = mucThue; }
    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public int getMaKH() { return maKH; }
    public void setMaKH(int maKH) { this.maKH = maKH; }

    /**
     * *** SỬA ĐỔI QUAN TRỌNG ***
     * Ghi đè phương thức toString() để JComboBox có thể hiển thị thông tin này.
     * Đây là cách thay thế cho việc tạo lớp DonHangInfo.
     */
    @Override
    public String toString() {
        if (this.maHDX == 0) { // Xử lý trường hợp đối tượng chưa được khởi tạo đầy đủ
            return "Vui lòng chọn đơn hàng";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("Đơn hàng #%d - Ngày: %s", this.maHDX, this.ngayLap.format(formatter));
    }
}