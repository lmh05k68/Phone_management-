package model;

public class KhachHang {
    private int maKH; // Đổi sang int
    private String hoTen;
    private String sdtKH;
    private int soDiemTichLuy;

    // Constructor khi đọc từ DB (có MaKH)
    public KhachHang(int maKH, String hoTen, String sdtKH, int soDiemTichLuy) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.sdtKH = sdtKH;
        this.soDiemTichLuy = soDiemTichLuy;
    }

    // Constructor khi tạo mới (MaKH tự sinh)
    public KhachHang(String hoTen, String sdtKH, int soDiemTichLuy) {
        this.hoTen = hoTen;
        this.sdtKH = sdtKH;
        this.soDiemTichLuy = soDiemTichLuy;
    }
    
    // Constructor khi tạo mới chỉ với thông tin cơ bản, điểm mặc định là 0
    public KhachHang(String hoTen, String sdtKH) {
        this.hoTen = hoTen;
        this.sdtKH = sdtKH;
        this.soDiemTichLuy = 0; // Mặc định điểm tích lũy ban đầu
    }

    public KhachHang() {
    }

    // Getters
    public int getMaKH() { return maKH; }
    public String getHoTen() { return hoTen; }
    public String getSdtKH() { return sdtKH; }
    public int getSoDiemTichLuy() { return soDiemTichLuy; }

    // Setters
    public void setMaKH(int maKH) { this.maKH = maKH; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public void setSdtKH(String sdtKH) { this.sdtKH = sdtKH; }
    public void setSoDiemTichLuy(int soDiemTichLuy) { this.soDiemTichLuy = soDiemTichLuy; }

    @Override
    public String toString() {
        return "KhachHang{" +
               "maKH=" + maKH +
               ", hoTen='" + hoTen + '\'' +
               ", sdtKH='" + sdtKH + '\'' +
               ", soDiemTichLuy=" + soDiemTichLuy +
               '}';
    }
}