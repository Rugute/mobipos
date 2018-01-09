package com.mobipos.app.Defaults;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobipos.app.R;

import java.util.List;


/**
 * Created by root on 1/9/18.
 */

public class ReceiptRvAdapter extends RecyclerView.Adapter<ReceiptRvAdapter.ItemViewHolder>{

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        CardView receipt_cv;
        TextView item,quantity,item_price;

        public ItemViewHolder (View itemView){
            super(itemView);
            receipt_cv=(CardView)itemView.findViewById(R.id.receipt_cv);
            item=(TextView)itemView.findViewById(R.id.item);
            quantity=(TextView)itemView.findViewById(R.id.quantity);
            item_price=(TextView)itemView.findViewById(R.id.item_price);

        }
    }
    List<ReceiptData>receiptData;
    public ReceiptRvAdapter(List<ReceiptData>receiptData){
        this.receiptData=receiptData;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int j) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt, parent, false);
        ReceiptRvAdapter.ItemViewHolder pvh = new ReceiptRvAdapter.ItemViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.item.setText(receiptData.get(position).item_name);
        holder.quantity.setText(receiptData.get(position).item_quantity);
        holder.item_price.setText(receiptData.get(position).item_price);

    }

    @Override
    public int getItemCount() {
        Log.d("recyclerView size",String.valueOf(receiptData.size()));
        return receiptData.size();
    }



}
