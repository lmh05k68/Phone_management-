package controller.employee;

import view.employee.ManageCustomerView;

public class ManageCustomer {
    private String maNV;

    public ManageCustomer(String maNV) {
        this.maNV = maNV;
    }

    public void showManageCustomerView() {
        new ManageCustomerView().setVisible(true);
    }
}