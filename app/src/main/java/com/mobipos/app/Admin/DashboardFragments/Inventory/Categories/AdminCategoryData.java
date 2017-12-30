package com.mobipos.app.Admin.DashboardFragments.Inventory.Categories;

/**
 * Created by folio on 12/15/2017.
 */

public class AdminCategoryData {
    public String name;
    public String id;
    public int branchId;
    public int image;

    public AdminCategoryData(int branchId,String id, String name, int image) {
        this.name = name;
        this.id = id;
        this.branchId = branchId;
        this.image = image;
    }
}
