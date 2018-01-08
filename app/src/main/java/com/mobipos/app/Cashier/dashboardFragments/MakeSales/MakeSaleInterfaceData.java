package com.mobipos.app.Cashier.dashboardFragments.MakeSales;

import java.util.List;

/**
 * Created by folio on 1/6/2018.
 */

public class MakeSaleInterfaceData  {

    public String category_id;
    public String category_name;
    public List<MakeSaleProductInfo> product;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public List<MakeSaleProductInfo> getProduct() {
        return product;
    }

    public void setProduct(List<MakeSaleProductInfo> product) {
        this.product = product;
    }


}
