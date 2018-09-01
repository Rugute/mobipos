package com.mobipos.app.Admin;

/**
 * Created by root on 2/7/18.
 */

public class AdminStockAlertData {


    public String productname;
    public String productId;
    public String productcategory;
    public String productalert;
    public String alert_type;
    public String remainder;

    public AdminStockAlertData(String productId,String productname,String productcategory, String productalert,
                               String alert_type,String remainder) {
        this.productId = productId;
        this.productname = productname;
        this.productcategory= productcategory;
        this.productalert= productalert;
        this.alert_type= alert_type;
        this.remainder= remainder;


    }
}
