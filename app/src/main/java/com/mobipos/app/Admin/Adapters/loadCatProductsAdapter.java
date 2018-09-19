package com.mobipos.app.Admin.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobipos.app.Admin.DashboardFragments.Inventory.Categories.catProducts;
import com.mobipos.app.R;

import java.util.List;

/**
 * Created by folio on 9/10/2018.
 */

public class loadCatProductsAdapter extends RecyclerView.Adapter<loadCatProductsAdapter.ItemViewHolder> {

    List<catProducts> data;
    public loadCatProductsAdapter(List<catProducts> data){
        this.data=data;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row_view_products,
                parent, false);
        loadCatProductsAdapter.ItemViewHolder pvh = new loadCatProductsAdapter.ItemViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.Item.setText(data.get(position).getProductName()+"\n Remaining: "+
        data.get(position).getItemsRemaining());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView Item;
        public ItemViewHolder(View itemView) {
            super(itemView);
            Item = itemView.findViewById(R.id.text_product);
        }

    }

}
