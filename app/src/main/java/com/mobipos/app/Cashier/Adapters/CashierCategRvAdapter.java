package com.mobipos.app.Cashier.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories.CashierCategoryData;
import com.mobipos.app.R;

import java.util.List;

/**
 * Created by root on 12/13/17.
 */

public class CashierCategRvAdapter extends RecyclerView.Adapter<CashierCategRvAdapter.ItemViewHolder> {

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView Category;
        TextView Item;
        ImageView CatPic;

        ItemViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            Category = (TextView)itemView.findViewById(R.id.text1);
            Item = (TextView)itemView.findViewById(R.id.text2);
            CatPic = (ImageView)itemView.findViewById(R.id.img1);
        }
    }

    List<CashierCategoryData> categoryData;

    public CashierCategRvAdapter(List<CashierCategoryData> categoryData){
        this.categoryData = categoryData;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_cardview, viewGroup, false);
        ItemViewHolder pvh = new ItemViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.Category.setText(categoryData.get(i).name);
        itemViewHolder.Item.setText(categoryData.get(i).id);
       itemViewHolder.CatPic.setImageResource(categoryData.get(i).image);
    }

    @Override
    public int getItemCount() {
        Log.d("recyclerView size",String.valueOf(categoryData.size()));
        return categoryData.size();
    }
}




