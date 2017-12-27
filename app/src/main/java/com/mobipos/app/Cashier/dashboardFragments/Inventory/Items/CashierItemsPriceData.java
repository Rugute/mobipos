package com.mobipos.app.Cashier.dashboardFragments.Inventory.Items;

/**
 * Created by folio on 12/20/2017.
 */

public class CashierItemsPriceData {
    public String product_id;
    public String price_id;
    public String price;
    public int image;

    public CashierItemsPriceData(String product_id, String price_id,String price, int image) {
        this.product_id = product_id;
        this.price_id = price_id;
        this.price = price;
        this.image = image;
    }
}
