package com.mobipos.app.Cashier.dashboardFragments.ViewSales;

/**
 * Created by folio on 1/13/2018.
 */

public class PullSaleData {

    public String sale_id;
    public String orderId;
    public String amount_total;
    public String transaction_type;
    public String transaction_code;

    public PullSaleData(String sale_id,String orderId,String amount_total,String transaction_type,String transaction_code){

        this.orderId=orderId;
        this.sale_id=sale_id;
        this.amount_total=amount_total;
        this.transaction_type=transaction_type;
        this.transaction_code=transaction_code;
    }
}
