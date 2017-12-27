package com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories;

/**
 * Created by folio on 12/15/2017.
 */

public class CashierCategoryData {
    public String name;
    public String id;
    public int image;

    public CashierCategoryData(String id, String name, int image) {
        this.name = name;
        this.id = id;
        this.image = image;
    }
}
