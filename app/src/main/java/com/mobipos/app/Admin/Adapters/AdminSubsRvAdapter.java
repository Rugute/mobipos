package com.mobipos.app.Admin.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobipos.app.Admin.Subscription;
import com.mobipos.app.R;

import java.util.List;

/**
 * Created by root on 1/8/18.
 */

public class AdminSubsRvAdapter extends RecyclerView.Adapter<AdminSubsRvAdapter.ItemViewHolder > {

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        CardView cvsubs;
        TextView sub_name,validity_status,trans_code;
        ImageView andro;

        public ItemViewHolder(View itemView) {
            super(itemView);
            cvsubs=(CardView)itemView.findViewById(R.id.cvsubs);
            sub_name=(TextView)itemView.findViewById(R.id.sub_name);
            validity_status=(TextView)itemView.findViewById(R.id.validity_status);
            trans_code=(TextView)itemView.findViewById(R.id.trans_code);
            andro=(ImageView)itemView.findViewById(R.id.andro);
        }
    }
    List<AdminSubscriptionData>SubscriptionData;

    public AdminSubsRvAdapter(List<AdminSubscriptionData>SubscriptionData){

        this.SubscriptionData =SubscriptionData;
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.admin_subs_card, viewGroup, false);
        AdminSubsRvAdapter.ItemViewHolder pvh = new AdminSubsRvAdapter.ItemViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int i) {
        holder.sub_name.setText("Date of Payment: "+SubscriptionData.get(i).name);
        holder.validity_status.setText("Subscription Time: "+SubscriptionData.get(i).valid);
        holder.trans_code.setText(SubscriptionData.get(i).trans);
        holder.andro.setImageResource(R.drawable.ic_subscriptions_black_24dp);

    }

    @Override
    public int getItemCount() {
        Log.d("recyclerView size",String.valueOf(SubscriptionData.size()));
        return SubscriptionData.size();
    }
}
