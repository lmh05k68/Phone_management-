package controller.admin;

import model.NhanVien;
import query.NhanVienQuery;
import view.admin.ManageEmployeeView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManageEmployee {
    
    private final ManageEmployeeView view;
    private List<NhanVien> employeeList; 
    public ManageEmployee(ManageEmployeeView view) {
        this.view = view;
        this.employeeList = new ArrayList<>();
    }
    public void loadEmployees() {
        this.employeeList = NhanVienQuery.getAll();
        view.updateTable(this.employeeList);
    }
    public void searchEmployees(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            view.updateTable(this.employeeList); // Nếu từ khóa rỗng, hiển thị lại toàn bộ danh sách
            return;
        }
        
        String lowerCaseKeyword = keyword.trim().toLowerCase();
        
        List<NhanVien> filteredList = this.employeeList.stream()
                .filter(nv -> 
                    (nv.getTenNV() != null && nv.getTenNV().toLowerCase().contains(lowerCaseKeyword)) ||
                    (nv.getSoDienThoai() != null && nv.getSoDienThoai().contains(lowerCaseKeyword))
                )
                .collect(Collectors.toList());
        
        view.updateTable(filteredList);
    }
    public void addEmployee(String tenNV, LocalDate ngaySinh, BigDecimal luong, String sdt) {
        if (tenNV == null || tenNV.trim().isEmpty() || sdt == null || sdt.trim().isEmpty()) {
            view.showMessage("Tên và Số điện thoại không được để trống.", false);
            return;
        }
        NhanVien newNv = new NhanVien(tenNV, ngaySinh, luong, sdt);
        boolean success = NhanVienQuery.insert(newNv);

        if (success) {
            view.showMessage("Thêm nhân viên thành công!", true);
            loadEmployees(); // Tải lại danh sách sau khi thêm thành công
        } else {
            view.showMessage("Thêm thất bại. Số điện thoại có thể đã tồn tại.", false);
        }
    }
    public void updateEmployee(int maNV, String tenNV, LocalDate ngaySinh, BigDecimal luong, String sdt) {
        if (tenNV == null || tenNV.trim().isEmpty() || sdt == null || sdt.trim().isEmpty()) {
            view.showMessage("Tên và Số điện thoại không được để trống.", false);
            return;
        }
        
        NhanVien updatedNv = new NhanVien(maNV, tenNV, ngaySinh, luong, sdt);
        boolean success = NhanVienQuery.update(updatedNv);

        if (success) {
            view.showMessage("Cập nhật thành công!", true);
            loadEmployees(); // Tải lại danh sách sau khi cập nhật thành công
        } else {
            view.showMessage("Cập nhật thất bại. Số điện thoại có thể đã được sử dụng.", false);
        }
    }
    public void deleteEmployee(int maNV) {
        boolean success = NhanVienQuery.delete(maNV);
        
        if (success) {
            view.showMessage("Xóa nhân viên thành công!", true);
            loadEmployees(); // Tải lại danh sách sau khi xóa thành công
        } else {
            view.showMessage("Xóa thất bại. Nhân viên có thể đang liên kết với các hóa đơn hoặc tài khoản.", false);
        }
    }
}