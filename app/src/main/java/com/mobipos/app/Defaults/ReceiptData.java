package com.mobipos.app.Defaults;

/**
 * Created by root on 1/9/18.
 */

public class ReceiptData {
    public String item_name;
    public String item_quantity;
    public String item_price;

    public ReceiptData(String item_name,String item_quantity, String item_price) {
        this.item_name= item_name;
        this.item_quantity= item_quantity;
        this.item_price= item_price;

    }
}
