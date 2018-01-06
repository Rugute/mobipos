package com.mobipos.app.Cashier.dashboardFragments.MakeSales;

import java.util.List;

/**
 * Created by folio on 1/5/2018.
 */

public class MakeSaleCategoryData {
    public String name;
    public String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MakeSaleProductData> getProductData() {
        return productData;
    }

    public void setProductData(List<MakeSaleProductData> productData) {
        this.productData = productData;
    }

    public List<MakeSaleProductData> productData;

}
