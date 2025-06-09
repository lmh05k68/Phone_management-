package controller.admin;

import model.SanPham;
import query.SanPhamQuery; // Gọi phương thức static
import view.admin.ManageProductView; 
import java.util.Comparator;
import java.util.List;

public class ManageProduct {
    private final ManageProductView view;
    public ManageProduct(ManageProductView view) {
        this.view = view;
    }
    public void loadSanPhamList() {
        List<SanPham> list = SanPhamQuery.getAll(); // Hoặc SanPhamQuery.getAllSanPhamOrderedByMaSP(true) nếu có
        if (list != null) {
            list.sort(Comparator.comparingInt(SanPham::getMaSP)); // Sửa thành comparingInt
            view.updateTable(list); // Giả sử View có phương thức này
        }
    }
    public void searchSanPhamByTen(String keyword) {
        List<SanPham> list = SanPhamQuery.searchSanPhamByTen(keyword);
        if (list != null) {
            view.updateTable(list);
        }
    }
    public void filterSanPhamByHangSX(String hangSX) {
        List<SanPham> list = SanPhamQuery.filterSanPhamByHangSX(hangSX);
        if (list != null) {
            view.updateTable(list);
        }
    }
    public boolean insertSanPham(SanPham sp) {
        if (sp.getTenSP() == null || sp.getTenSP().trim().isEmpty()) {
            System.err.println("CONTROLLER_ManageProduct (insert): Tên sản phẩm không được để trống.");
            return false;
        }
        Integer generatedId = SanPhamQuery.insertSanPhamAndGetId(sp);
        boolean ok = (generatedId != null && generatedId > 0);
        if (ok) {
            System.out.println("CONTROLLER_ManageProduct (insert): Sản phẩm được thêm với MaSP: " + generatedId);
            loadSanPhamList(); // Tải lại danh sách sau khi thêm
        } else {
            System.err.println("CONTROLLER_ManageProduct (insert): Thêm sản phẩm thất bại.");
        }
        return ok;
    }
    public boolean updateSanPham(SanPham sp) {
        if (SanPhamQuery.getSanPhamById(sp.getMaSP()) == null) { // Giả sử getSanPhamById là static
            System.err.println("CONTROLLER_ManageProduct (update): Không tìm thấy sản phẩm với Mã SP: " + sp.getMaSP());
            return false;
        }
        boolean ok = SanPhamQuery.updateSanPham(sp);
        if (ok) {
            loadSanPhamList(); // Tải lại danh sách sau khi cập nhật
        }
        return ok;
    }
    public boolean deleteSanPham(String maSPStr) {
        if (maSPStr == null || maSPStr.trim().isEmpty()) {
            System.err.println("CONTROLLER_ManageProduct (delete): Mã sản phẩm không được để trống.");
            return false;
        }
        try {
            int maSP = Integer.parseInt(maSPStr.trim());
            boolean ok = SanPhamQuery.deleteSanPham(maSP); // Truyền int maSP
            if (ok) {
                loadSanPhamList(); // Tải lại danh sách sau khi xóa
            }
            return ok;
        } catch (NumberFormatException e) {
            System.err.println("CONTROLLER_ManageProduct (delete): Mã sản phẩm không hợp lệ (không phải số): " + maSPStr);
            return false;
        }
    }

    /** Lấy danh sách hãng sản xuất để fill vào ComboBox */
    public List<String> getAllHangSX() {
        return SanPhamQuery.getAllHangSX();
    }
}