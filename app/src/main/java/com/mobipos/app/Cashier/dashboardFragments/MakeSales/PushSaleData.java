package com.mobipos.app.Cashier.dashboardFragments.MakeSales;

/**
 * Created by folio on 1/13/2018.
 */

public class PushSaleData {

    public String orderId;
    public String amountTendered;
    public String amount_total;
    public String transaction_type;
    public String transaction_code;
    public String discount;
    public String discounted_amount;

    public PushSaleData(String orderId, String amountTendered,
                        String amount_total, String transaction_type, String transaction_code,String discount,String discounted_amount){

        this.orderId=orderId;
        this.amountTendered=amountTendered;
        this.amount_total=amount_total;
        this.transaction_type=transaction_type;
        this.transaction_code=transaction_code;
        this.discount=discount;
        this.discounted_amount=discounted_amount;
    }
}
