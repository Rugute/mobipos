package com.mobipos.app.Cashier.Adapters;

/**
 * Created by folio on 1/13/2018.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItemsData;
import com.mobipos.app.Cashier.dashboardFragments.ViewSales.PullSaleData;
import com.mobipos.app.R;

import java.util.List;

public class ViewSalesAdapter extends RecyclerView.Adapter<ViewSalesAdapter.ItemListViewHolder>{

    public  static class ItemListViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView salesId;
        TextView order_no;
        TextView trans_code;
        TextView total_sale;
        TextView sale_id;



        ItemListViewHolder(View itemView){
            super(itemView);
            cv = itemView.findViewById(R.id.view_sale_cv);
            salesId = itemView.findViewById(R.id.text_id);
            order_no =itemView.findViewById(R.id.view_order_no);
            trans_code = itemView.findViewById(R.id.view_trans_code);
            total_sale = itemView.findViewById(R.id.view_total_sale);
            sale_id = itemView.findViewById(R.id.view_sale_id);



        }
    }
    List<PullSaleData> itemsData;

    public ViewSalesAdapter(List<PullSaleData> itemsData){
        this.itemsData = itemsData;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ItemListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cashier_custom_view_sales, viewGroup, false);
        ViewSalesAdapter.ItemListViewHolder pvh = new ViewSalesAdapter.ItemListViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemListViewHolder itemListViewHolder, int i) {
        itemListViewHolder.sale_id.setText(itemsData.get(i).sale_id);
        itemListViewHolder.order_no.setText("Order id: "+itemsData.get(i).orderId);
        itemListViewHolder.total_sale.setText("KSH: "+itemsData.get(i).amount_total);
        itemListViewHolder.trans_code.setText("CODE:  "+itemsData.get(i).transaction_code);


    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}


