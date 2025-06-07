package controller.admin;

import model.NhanVien;
import query.NhanVienQuery; // Quan trọng: Import lớp Query
import java.util.List;

/**
 * Lớp Controller này điều phối các hoạt động quản lý nhân viên.
 * Nó không chứa code truy cập CSDL trực tiếp, thay vào đó nó
 * ủy quyền công việc đó cho lớp NhanVienQuery.
 */
public class ManageEmployee {

    public List<NhanVien> getAllNhanVien() {
        // Gọi đến lớp Query để lấy dữ liệu
        return NhanVienQuery.getAll();
    }

    public List<NhanVien> searchNhanVien(String keyword, String type) {
        // Gọi đến lớp Query để tìm kiếm
        return NhanVienQuery.search(keyword, type);
    }

    public boolean insertNhanVien(NhanVien nv) {
        // Thêm các logic kiểm tra nghiệp vụ ở đây nếu cần, ví dụ:
        if (nv.getMaNV() == null || nv.getMaNV().trim().isEmpty()) {
            System.err.println("Mã nhân viên không được để trống.");
            return false;
        }
        if (NhanVienQuery.exists(nv.getMaNV())) {
            System.err.println("Mã nhân viên đã tồn tại.");
            return false;
        }
        // Gọi đến lớp Query để thêm mới
        return NhanVienQuery.insert(nv);
    }

    public boolean updateNhanVien(NhanVien nv) {
        // Gọi đến lớp Query để cập nhật
        return NhanVienQuery.update(nv);
    }

    public boolean deleteNhanVien(String maNV) {
        // Trước khi xóa có thể kiểm tra các ràng buộc khác (ví dụ: nhân viên này có đang quản lý hóa đơn nào không?)
        
        // Gọi đến lớp Query để xóa
        return NhanVienQuery.delete(maNV);
    }
}