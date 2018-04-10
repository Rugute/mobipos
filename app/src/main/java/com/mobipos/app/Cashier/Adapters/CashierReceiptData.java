package com.mobipos.app.Cashier.Adapters;

/**
 * Created by root on 4/9/18.
 */

public class CashierReceiptData {

   public String product_name1,product_quantity,product_amount;

   public CashierReceiptData(String product_amount,String product_name1,String product_quantity){

       this.product_name1= product_name1;
       this.product_amount=product_amount;
       this.product_quantity=product_quantity;

   }


}
