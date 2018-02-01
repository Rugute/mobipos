package com.mobipos.app.Admin.DashboardFragments;

/**
 * Created by root on 1/30/18.
 */

public class ViewSalesInterface {

    public String shop_id;
    public String shop_name;
    public String shop_sales;
    public String shop_inv;

    public ViewSalesInterface(String shop_id,String shop_name,String shop_sales,String shop_inv){
        this.shop_id=shop_id;
        this.shop_name=shop_name;
        this.shop_sales=shop_sales;
        this.shop_inv=shop_inv;
    }

}
