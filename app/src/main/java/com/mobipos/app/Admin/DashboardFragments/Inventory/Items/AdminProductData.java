package com.mobipos.app.Admin.DashboardFragments.Inventory.Items;

/**
 * Created by folio on 12/29/2017.
 */

public class AdminProductData {
    public String name;
    public String id;
    public String buying;
    public String selling;
    public String measure;
    public int branchId;
    public int categoryId;
    public int image;

    public AdminProductData(int branchId,int categoryId,String id, String name, int image,String buying,String selling,
                            String measure) {
        this.name = name;
        this.id = id;
        this.branchId = branchId;
        this.categoryId = categoryId;
        this.image = image;
        this.buying = buying;
        this.selling = selling;
        this.measure = measure;
    }
}
