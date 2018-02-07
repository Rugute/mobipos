package com.mobipos.app.Admin;

/**
 * Created by root on 2/7/18.
 */

public class AdminStockAlertData {

    public String productname;
    public String productcategory;
    public String productalert;

    public AdminStockAlertData(String productname,String productcategory, String productalert) {
        this.productname = productname;
        this.productcategory= productcategory;
        this.productalert= productalert;

    }
}
