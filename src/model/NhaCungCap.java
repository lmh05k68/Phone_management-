package model;

public class NhaCungCap {
    private int maNCC; // Đổi sang int
    private String tenNCC;
    private String diaChi;
    private String sdtNCC;
    public NhaCungCap(int maNCC, String tenNCC, String diaChi, String sdtNCC) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.diaChi = diaChi;
        this.sdtNCC = sdtNCC;
    }
    public NhaCungCap(String tenNCC, String diaChi, String sdtNCC) {
        this.tenNCC = tenNCC;
        this.diaChi = diaChi;
        this.sdtNCC = sdtNCC;
    }
    
    public NhaCungCap() {
    }

    // Getters
    public int getMaNCC() { return maNCC; }
    public String getTenNCC() { return tenNCC; }
    public String getDiaChi() { return diaChi; }
    public String getSdtNCC() { return sdtNCC; }

    // Setters
    public void setMaNCC(int maNCC) { this.maNCC = maNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public void setSdtNCC(String sdtNCC) { this.sdtNCC = sdtNCC; }

    @Override
    public String toString() {
        return tenNCC; 
    }
}