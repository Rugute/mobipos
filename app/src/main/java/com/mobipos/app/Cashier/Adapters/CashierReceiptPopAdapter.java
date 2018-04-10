package com.mobipos.app.Cashier.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobipos.app.Cashier.dashboardFragments.MakeSales.viewCartData;
import com.mobipos.app.R;

import java.util.List;

/**
 * Created by root on 4/9/18.
 */

public class CashierReceiptPopAdapter extends BaseAdapter {
    Context context;
    List<viewCartData> cashierReceiptData;
    public static LayoutInflater inflater=null;

    public CashierReceiptPopAdapter(Context context,List<viewCartData> cashierReceiptData){
        this.context= context;
        this.cashierReceiptData=cashierReceiptData;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return cashierReceiptData.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
       view= inflater.inflate(R.layout.custom_list_item_receipt,null);
        TextView product_names = view.findViewById(R.id.prodname_receipt);
        TextView product_quan =view.findViewById(R.id.prodqty_receipt);
        TextView product_amounts = view.findViewById(R.id.prodamount_receipt);
        CardView cardView=view.findViewById(R.id.card_reciept);

        if(i%2==0){}
        cardView.setCardBackgroundColor(Color.WHITE);


        product_names.setText("Product Name: "+cashierReceiptData.get(i).product_name);
        product_quan.setText("QTY: "+cashierReceiptData.get(i).count);
        int total=Integer.parseInt(cashierReceiptData.get(i).count)*Integer.parseInt(cashierReceiptData.get(i).price);
        product_amounts.setText("Total: "+String.valueOf(total));

        return view;
    }
}
