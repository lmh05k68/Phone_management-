package controller.admin;

import model.SanPham;
import query.SanPhamQuery;
import view.admin.ManageProductView;

import java.util.Comparator;
import java.util.List;

public class ManageProduct {
    private final ManageProductView view;
    private final SanPhamQuery sanPhamQuery;

    public ManageProduct(ManageProductView view) {
        this.view = view;
        this.sanPhamQuery = new SanPhamQuery();
    }

    /** Load tất cả sản phẩm và sắp xếp theo mã tăng dần */
    public void loadSanPhamList() {
        List<SanPham> list = sanPhamQuery.getAll();
        if (list != null) {
            list.sort(Comparator.comparing(SanPham::getMaSP)); // sắp xếp theo mã SP
            view.updateTable(list);
        }
    }

    /** Tìm kiếm theo tên */
    public void searchSanPhamByTen(String keyword) {
        List<SanPham> list = sanPhamQuery.searchSanPhamByTen(keyword);
        if (list != null) {
            view.updateTable(list);
        }
    }

    /** Lọc theo hãng sản xuất */
    public void filterSanPhamByHangSX(String hangSX) {
        List<SanPham> list = sanPhamQuery.filterSanPhamByHangSX(hangSX);
        if (list != null) {
            view.updateTable(list);
        }
    }

    /** Thêm sản phẩm mới */
    public boolean insertSanPham(SanPham sp) {
        boolean ok = sanPhamQuery.insertSanPham(sp);
        if (ok) loadSanPhamList();
        return ok;
    }

    /** Cập nhật sản phẩm */
    public boolean updateSanPham(SanPham sp) {
        boolean ok = sanPhamQuery.updateSanPham(sp);
        if (ok) loadSanPhamList();
        return ok;
    }

    /** Xóa sản phẩm theo mã */
    public boolean deleteSanPham(String maSP) {
        boolean ok = sanPhamQuery.deleteSanPham(maSP);
        if (ok) loadSanPhamList();
        return ok;
    }

    /** Lấy danh sách hãng sản xuất để fill vào ComboBox */
    public List<String> getAllHangSX() {
        return sanPhamQuery.getAllHangSX();
    }
}