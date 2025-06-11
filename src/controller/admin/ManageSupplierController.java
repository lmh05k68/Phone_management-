package controller.admin;

import model.NhaCungCap;
import query.NhaCungCapQuery;
import view.admin.ManageSupplierView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManageSupplierController {
    private final ManageSupplierView view;
    private List<NhaCungCap> supplierList;
    public ManageSupplierController(ManageSupplierView view) {
        this.view = view;
        this.supplierList = new ArrayList<>();
    }
    public void loadSuppliers() {
        this.supplierList = NhaCungCapQuery.getAll();
        view.updateTable(this.supplierList);
    }
    public void searchAndFilter(String keyword, String province) {
        List<NhaCungCap> filteredList = new ArrayList<>(this.supplierList);
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerCaseKeyword = keyword.trim().toLowerCase();
            filteredList = filteredList.stream()
                .filter(ncc -> ncc.getTenNCC().toLowerCase().contains(lowerCaseKeyword) ||
                               ncc.getDiaChi().toLowerCase().contains(lowerCaseKeyword) ||
                               ncc.getSdtNCC().contains(lowerCaseKeyword))
                .collect(Collectors.toList());
        }
        if (province != null && !"Tất cả".equals(province)) {
            filteredList = filteredList.stream()
                .filter(ncc -> ncc.getDiaChi() != null && ncc.getDiaChi().contains(province))
                .collect(Collectors.toList());
        }
        
        view.updateTable(filteredList);
    }
    public void addSupplier(String tenNCC, String diaChi, String sdtNCC) {
        if (tenNCC.isEmpty() || diaChi.isEmpty() || sdtNCC.isEmpty()) {
            view.showMessage("Vui lòng điền đầy đủ thông tin.", false);
            return;
        }

        NhaCungCap newNcc = new NhaCungCap(tenNCC, diaChi, sdtNCC);
        Integer newId = NhaCungCapQuery.insertAndGetId(newNcc);

        if (newId != null) {
            view.showMessage("Thêm nhà cung cấp thành công!", true);
            loadSuppliers(); // Tải lại dữ liệu từ DB
        } else {
            view.showMessage("Thêm nhà cung cấp thất bại. Vui lòng thử lại.", false);
        }
    }

    // Cập nhật nhà cung cấp
    public void updateSupplier(int maNCC, String tenNCC, String diaChi, String sdtNCC) {
        if (tenNCC.isEmpty() || diaChi.isEmpty() || sdtNCC.isEmpty()) {
            view.showMessage("Vui lòng điền đầy đủ thông tin.", false);
            return;
        }

        NhaCungCap updatedNcc = new NhaCungCap(maNCC, tenNCC, diaChi, sdtNCC);
        boolean success = NhaCungCapQuery.update(updatedNcc);

        if (success) {
            view.showMessage("Cập nhật thành công!", true);
            loadSuppliers();
        } else {
            view.showMessage("Cập nhật thất bại. Vui lòng thử lại.", false);
        }
    }
    public void deleteSupplier(int maNCC) {
        boolean success = NhaCungCapQuery.delete(maNCC);
        if (success) {
            view.showMessage("Xóa nhà cung cấp thành công!", true);
            loadSuppliers();
        } else {
            view.showMessage("Xóa thất bại. Nhà cung cấp có thể đang được sử dụng.", false);
        }
    }
}