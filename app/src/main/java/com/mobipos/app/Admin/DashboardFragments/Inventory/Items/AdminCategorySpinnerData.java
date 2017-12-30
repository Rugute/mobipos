package com.mobipos.app.Admin.DashboardFragments.Inventory.Items;

/**
 * Created by folio on 12/29/2017.
 */

public class AdminCategorySpinnerData {

    public String id;
    public String name;
    public int branchId;

    public AdminCategorySpinnerData(int branchId,String id, String name) {
        this.id = id;
        this.name = name;
        this.branchId = branchId;

    }
}
