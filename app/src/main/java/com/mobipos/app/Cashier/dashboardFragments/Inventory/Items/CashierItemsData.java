package com.mobipos.app.Cashier.dashboardFragments.Inventory.Items;

/**
 * Created by folio on 12/20/2017.
 */

public class CashierItemsData {
    public String product_name;
    public String product_id;
    public String price;
    public String measure;
    public String stock;
    public int image;

    public CashierItemsData(String product_id, String product_name,String price,String stock,String measure, int image) {
        this.product_name = product_name;
        this.product_id = product_id;
        this.price = price;
        this.stock = stock;
        this.measure = measure;
        this.image = image;
    }
}
