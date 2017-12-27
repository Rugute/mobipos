package com.mobipos.app.Cashier.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobipos.app.Cashier.dashboardFragments.Inventory.CashDummy;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItemsData;
import com.mobipos.app.R;

import java.util.List;

/**
 * Created by root on 12/15/17.
 */

public class CashierItemRvAdapter extends RecyclerView.Adapter<CashierItemRvAdapter.ItemListViewHolder>{

    public  static class ItemListViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView product_id;
        TextView product_name;
        TextView product_price;
        TextView product_stock;
        ImageView CatPic;


        ItemListViewHolder(View itemView){
            super(itemView);
            cv = itemView.findViewById(R.id.item_cv);
            product_id = itemView.findViewById(R.id.text_id);
            product_name =itemView.findViewById(R.id.text_item_name);
            product_price = itemView.findViewById(R.id.text_item_price);
            product_stock = itemView.findViewById(R.id.text_stock);
            CatPic = itemView.findViewById(R.id.img1);


        }
    }
    List<CashierItemsData> itemsData;

    public CashierItemRvAdapter(List<CashierItemsData> itemsData){
        this.itemsData = itemsData;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ItemListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_carditem, viewGroup, false);
        CashierItemRvAdapter.ItemListViewHolder pvh = new CashierItemRvAdapter.ItemListViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemListViewHolder itemListViewHolder, int i) {
        itemListViewHolder.product_id.setText(itemsData.get(i).product_id);
        itemListViewHolder.product_name.setText(itemsData.get(i).product_name);
        itemListViewHolder.product_price.setText("KSH "+itemsData.get(i).price);
        itemListViewHolder.product_stock.setText(itemsData.get(i).stock+' '+itemsData.get(i).measure);
        itemListViewHolder.CatPic.setImageResource(itemsData.get(i).image);
    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}


