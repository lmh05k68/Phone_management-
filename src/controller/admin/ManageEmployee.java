package controller.admin;

import model.NhanVien;
import query.NhanVienQuery;
import java.util.List;

public class ManageEmployee {

    public List<NhanVien> getAllNhanVien() {
        return NhanVienQuery.getAll();
    }

    public List<NhanVien> searchNhanVien(String keyword, String type) {
        return NhanVienQuery.search(keyword, type);
    }
    public boolean insertNhanVien(NhanVien nv) {
        if (nv.getTenNV() == null || nv.getTenNV().trim().isEmpty()) {
            System.err.println("CONTROLLER_ManageEmployee (insert): Tên nhân viên không được để trống.");
            return false;
        }
        if (nv.getSoDienThoai() == null || nv.getSoDienThoai().trim().isEmpty()){
            System.err.println("CONTROLLER_ManageEmployee (insert): Số điện thoại không được để trống.");
            return false;
        }
        return NhanVienQuery.insert(nv);
    }
    public boolean updateNhanVien(NhanVien nv) {
        if (!NhanVienQuery.exists(nv.getMaNV())) {
            System.err.println("CONTROLLER_ManageEmployee (update): Không tìm thấy nhân viên với Mã NV: " + nv.getMaNV());
            return false;
        }
        return NhanVienQuery.update(nv);
    }
    public boolean deleteNhanVien(String maNVStr) {
        if (maNVStr == null || maNVStr.trim().isEmpty()) {
            System.err.println("CONTROLLER_ManageEmployee (delete): Mã nhân viên không được để trống.");
            return false;
        }
        try {
            int maNV = Integer.parseInt(maNVStr.trim());
            return NhanVienQuery.delete(maNV);
        } catch (NumberFormatException e) {
            System.err.println("CONTROLLER_ManageEmployee (delete): Mã nhân viên không hợp lệ (không phải số): " + maNVStr);
            return false;
        }
    }
}