package com.mobipos.app.Cashier.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.mobipos.app.Cashier.dashboardFragments.MakeSales.viewCartData;
import com.mobipos.app.R;
import com.mobipos.app.database.Order_Items;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.Sales;
import com.mobipos.app.database.Taxes;
import com.mobipos.app.database.defaults;

import java.util.List;

/**
 * Created by root on 4/9/18.
 */

public class AlertDialogViewSales  {

    Order_Items orderItemsdb;
    Taxes taxesdb;
    Sales salesdb;
    Products productsdb;
    String order_no;
    Context context;
    ListView listView;
    public AlertDialogViewSales(Context context,String order_no,String total_amount){
        this.order_no=order_no;
        this.context=context;
        orderItemsdb=new Order_Items(context, defaults.database_name,null,1);
        taxesdb=new Taxes(context, defaults.database_name,null,1);
        productsdb=new Products(context, defaults.database_name,null,1);
        salesdb=new Sales(context, defaults.database_name,null,1);
        View view= LayoutInflater.from(context).inflate(R.layout.cashier_receipt_pop,null);
        TextView txt_order_no=view.findViewById(R.id.textView_orderNO);
        TextView txt_total=view.findViewById(R.id.total_receipt);
        listView=view.findViewById(R.id.list_products_receipt);

        txt_order_no.setText(order_no);


        String order_information="Gross: "+String.valueOf(orderItemsdb.getCartTotal(order_no))+"\nExc Tax: "+exclusive_tax(order_no)
                +"\nDiscount: "+get_discount(order_no)+
                "\nNet: "+String.valueOf(Integer.parseInt(salesdb.getSalesData("total",order_no).get(0).amount_total)+exclusive_tax(order_no));
        txt_total.setText(order_information);
        initializeListAdapter(order_no);

        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setView(view);
        dialog.setCancelable(true);
        dialog.show();

    }

    public void initializeListAdapter(String order_id){
        Log.d("list of data",orderItemsdb.getCartData(order_id).toString());
        final CashierReceiptPopAdapter adapter = new CashierReceiptPopAdapter(context,
                orderItemsdb.getCartData(order_id));

        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
        listView.setAdapter(adapter);



    }

    public int exclusive_tax(String order_no){
        List<viewCartData> data=orderItemsdb.getCartData(order_no);
        int amount=0;
        for(int i=0;i<data.size();i++){
            if(taxesdb.taxMode(productsdb.taxMode(data.get(i).product_id))==2){
                int tax_value=taxesdb.tax(productsdb.taxMode(data.get(i).product_id))*Integer.parseInt(data.get(i).price)/100;
                amount +=tax_value;
            }
        }

        return amount;
    }

    public int get_discount(String order_no){
        int ex_tax=exclusive_tax(order_no);
        int get_cart_data=orderItemsdb.getCartTotal(order_no);
        int discount_percentage=Integer.parseInt(salesdb.getSalesData("total",order_no).get(0).discount);

        int total_amount=ex_tax+get_cart_data;
        int discount_total=Integer.parseInt(salesdb.getSalesData("total",order_no).get(0).discount_amount);;


        return discount_total;
    }


}
