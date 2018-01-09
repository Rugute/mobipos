package com.mobipos.app.Cashier.dashboardFragments.MakeSales;

/**
 * Created by folio on 1/8/2018.
 */

public class viewCartData  {

    public  String product_id;
    public  String product_name;
    public  String price;
    public  String count;

    public viewCartData(String product_id,String product_name,String price,String count){
        this.product_id=product_id;
        this.count=count;
        this.price=price;
        this.product_name=product_name;
    }
}
