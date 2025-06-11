package controller.employee;
import model.KhachHang;
import query.KhachHangQuery;
import java.util.List;
public class ManageCustomer {
	public List<KhachHang> getAllCustomersWithAccounts() {
        return KhachHangQuery.getCustomersWithAccounts();
    }
    public List<KhachHang> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomersWithAccounts();
        }
        return KhachHangQuery.searchCustomersWithAccounts(keyword.trim());
    }
}