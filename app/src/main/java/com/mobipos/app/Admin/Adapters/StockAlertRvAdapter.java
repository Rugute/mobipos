package com.mobipos.app.Admin.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobipos.app.Admin.AdminStockAlertData;

import com.mobipos.app.R;

import java.util.List;

/**
 * Created by root on 2/7/18.
 */

public class StockAlertRvAdapter extends RecyclerView.Adapter<StockAlertRvAdapter.ItemViewHolder> {

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView scv;
        TextView p_name,p_categ,stock_alert;

        public ItemViewHolder(View itemView) {
            super(itemView);

            scv=(CardView)itemView.findViewById(R.id.alertcv);
            p_categ=(TextView)itemView.findViewById(R.id.category_name);
            p_name=(TextView)itemView.findViewById(R.id.product_name);
            stock_alert=(TextView)itemView.findViewById(R.id.trans_alert);
        }
    }
    List<AdminStockAlertData> stockAlertData;

    public StockAlertRvAdapter(List<AdminStockAlertData> stockAlertData){
        this.stockAlertData = stockAlertData;
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int j) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.admin_alert_card, viewGroup, false);
        StockAlertRvAdapter.ItemViewHolder pvh = new StockAlertRvAdapter.ItemViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int j) {

        holder.p_name.setText(stockAlertData.get(j).productname);
        holder.p_categ.setText(stockAlertData.get(j).productcategory);
        holder.stock_alert.setText(stockAlertData.get(j).productalert);

    }

    @Override
    public int getItemCount() {
        Log.d("recyclerView size",String.valueOf(stockAlertData.size()));
        return stockAlertData.size();
    }


}
