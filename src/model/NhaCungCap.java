package model;

public class NhaCungCap {
    private String maNCC;
    private String tenNCC;
    private String diaChi;
    private String sdtNCC;

    public NhaCungCap(String maNCC, String tenNCC, String diaChi, String sdtNCC) {
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

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    public String getTenNCC() {
        return tenNCC;
    }

    public void setTenNCC(String tenNCC) {
        this.tenNCC = tenNCC;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSdtNCC() {
        return sdtNCC;
    }

    public void setSdtNCC(String sdtNCC) {
        this.sdtNCC = sdtNCC;
    }

    @Override
    public String toString() {
        return tenNCC;
    }
}