package controller.admin;

import model.SanPham;
import query.SanPhamQuery;
import view.admin.ManageProductView;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
public class ManageProduct {
    private final ManageProductView view;
    private List<SanPham> currentProductList; // Lưu trữ danh sách sản phẩm hiện tại để lọc và tìm kiếm nhanh

    public ManageProduct(ManageProductView view) {
        this.view = view;
    }

    /**
     * Tải danh sách tất cả sản phẩm từ CSDL và cập nhật lên view.
     */
    public void loadProducts() {
        this.currentProductList = SanPhamQuery.getAllWithTonKho();
        if (this.currentProductList == null) {
            this.currentProductList = Collections.emptyList(); // Tránh lỗi NullPointerException
        }
        view.updateTable(this.currentProductList);
    }

    /**
     * MỚI: Phương thức để lấy một đối tượng SanPham từ danh sách hiện tại dựa vào mã sản phẩm.
     * @param maSP Mã sản phẩm cần tìm.
     * @return Đối tượng SanPham nếu tìm thấy, ngược lại trả về null.
     */
    public SanPham getProductById(int maSP) {
        if (this.currentProductList == null) {
            return null;
        }
        // Sử dụng stream để tìm kiếm sản phẩm trong danh sách đã tải
        return this.currentProductList.stream()
                .filter(sp -> sp.getMaSP() == maSP)
                .findFirst()
                .orElse(null);
    }

    /**
     * Lọc danh sách sản phẩm theo tên (không phân biệt hoa thường) trên danh sách đã tải.
     * @param keyword Từ khóa tìm kiếm.
     */
    public void searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            view.updateTable(this.currentProductList); // Hiển thị lại toàn bộ nếu keyword rỗng
            return;
        }
        String lowerCaseKeyword = keyword.trim().toLowerCase();
        List<SanPham> filteredList = this.currentProductList.stream()
                .filter(sp -> sp.getTenSP().toLowerCase().contains(lowerCaseKeyword))
                .collect(Collectors.toList());
        view.updateTable(filteredList);
    }

    /**
     * Lọc danh sách sản phẩm theo hãng sản xuất.
     * @param brand Tên hãng cần lọc.
     */
    public void filterByBrand(String brand) {
        if (brand == null || "Tất cả".equals(brand)) {
            view.updateTable(this.currentProductList);
            return;
        }
        List<SanPham> filteredList = this.currentProductList.stream()
                .filter(sp -> brand.equals(sp.getHangSX()))
                .collect(Collectors.toList());
        view.updateTable(filteredList);
    }

    /**
     * Xử lý logic thêm sản phẩm mới.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addProduct(String tenSP, String mau, BigDecimal giaNiemYet, String nuocSX, String hangSX) {
        if (tenSP == null || tenSP.trim().isEmpty()) {
            view.showMessage("Tên sản phẩm không được để trống.", false);
            return false;
        }
        if (giaNiemYet == null || giaNiemYet.compareTo(BigDecimal.ZERO) <= 0) {
            view.showMessage("Giá niêm yết phải là một số lớn hơn 0.", false);
            return false;
        }
        
        SanPham sp = new SanPham(tenSP, mau, giaNiemYet, nuocSX, hangSX);
        boolean success = SanPhamQuery.insertSanPham(sp); 
        if (success) {
            loadProducts(); // Tải lại danh sách sau khi thêm thành công
            view.showMessage("Thêm sản phẩm thành công!", true);
            return true;
        } else {
            view.showMessage("Thêm sản phẩm thất bại. Tên sản phẩm có thể đã tồn tại.", false);
            return false;
        }
    }
    public boolean updateProduct(int maSP, String tenSP, String mau, BigDecimal giaNiemYet, String nuocSX, String hangSX) {
        if (tenSP == null || tenSP.trim().isEmpty()) {
            view.showMessage("Tên sản phẩm không được để trống.", false);
            return false;
        }
        if (giaNiemYet == null || giaNiemYet.compareTo(BigDecimal.ZERO) <= 0) {
            view.showMessage("Giá niêm yết phải là một số lớn hơn 0.", false);
            return false;
        }
        
        SanPham sp = new SanPham(maSP, tenSP, mau, giaNiemYet, nuocSX, hangSX);
        boolean success = SanPhamQuery.updateSanPham(sp);

        if (success) {
            loadProducts(); // Tải lại danh sách để hiển thị thông tin mới
            view.showMessage("Cập nhật sản phẩm thành công!", true);
        } else {
            view.showMessage("Cập nhật sản phẩm thất bại.", false);
        }
        return success;
    }

    /**
     * Xử lý logic xóa sản phẩm.
     * @param maSP Mã sản phẩm cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteProduct(int maSP) {
        boolean success = SanPhamQuery.deleteSanPham(maSP);
        if (success) {
            loadProducts(); // Tải lại danh sách sau khi xóa
            view.showMessage("Xóa sản phẩm thành công!", true);
        } else {
            view.showMessage("Xóa sản phẩm thất bại. Sản phẩm có thể đã được sử dụng trong các hóa đơn.", false);
        }
        return success;
    }

    /**
     * Lấy danh sách tất cả các hãng sản xuất để đổ vào ComboBox.
     * @return Một List<String> chứa tên các hãng.
     */
    public List<String> getAllBrands() {
        return SanPhamQuery.getAllHangSX();
    }
}