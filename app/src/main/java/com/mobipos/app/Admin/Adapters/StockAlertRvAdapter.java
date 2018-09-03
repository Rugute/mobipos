package com.mobipos.app.Admin.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.AdminStockAlertData;

import com.mobipos.app.Admin.BranchFragment;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminProductData;
import com.mobipos.app.Admin.DashboardFragments.Inventory.StockAlert.StockAlertFragment;
import com.mobipos.app.Admin.PackageConfig;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 2/7/18.
 */

public class StockAlertRvAdapter extends RecyclerView.Adapter<StockAlertRvAdapter.ItemViewHolder> {

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView scv;
        TextView p_name,p_categ,stock_alert,remainder;
        ImageView imageView;

        static boolean updatedItem;
        public ItemViewHolder(View itemView) {
            super(itemView);

            scv=(CardView)itemView.findViewById(R.id.alertcv);
            p_categ=(TextView)itemView.findViewById(R.id.category_name);
            p_name=(TextView)itemView.findViewById(R.id.product_name);
            remainder=(TextView)itemView.findViewById(R.id.remainder);
            imageView=itemView.findViewById(R.id.andro);
            stock_alert=(TextView)itemView.findViewById(R.id.trans_alert);
        }
    }
    List<AdminStockAlertData> stockAlertData;
    Context context;
    List<AdminProductData> productData;


    public StockAlertRvAdapter(List<AdminStockAlertData> stockAlertData,Context context){
        this.stockAlertData = stockAlertData;
        this.context=context;

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
    public void onBindViewHolder( final ItemViewHolder holder,final int j) {

        holder.p_name.setText(stockAlertData.get(j).productname);
        holder.p_categ.setText(stockAlertData.get(j).productcategory);
        holder.stock_alert.setText(stockAlertData.get(j).productalert);
        holder.remainder.setText(stockAlertData.get(j).remainder);

        if(stockAlertData.get(j).alert_type.equals("1")){
            holder.imageView.setImageResource(R.drawable.ic_warning_orange_24dp);
            holder.stock_alert.setTextColor(Color.parseColor("#f2b525"));
        }
        holder.scv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                product_id=stockAlertData.get(j).productId;
                delete_pos=j;

                PopupMenu popupMenu=new PopupMenu(context,holder.scv);
                popupMenu.inflate(R.menu.stock_update_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.delete_product:
                              //  new AdminProdRvAdapter.delete_fxn().execute();
                                break;

                            case R.id.update_product:
                                product_info=stockAlertData.get(j).productname+"\ncurrent inventory:"+stockAlertData.get(j).remainder
                                        ;
                                update_pop_up();

                                if(PackageConfig.isItemUpdated)notifyItemRemoved(j);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("recyclerView size",String.valueOf(stockAlertData.size()));
        return stockAlertData.size();
    }
    static String product_id;
    static int delete_pos;
    static String product_info;

    public void update_pop_up(){
        View view= LayoutInflater.from(context).inflate(R.layout.update_product_inventory,null);
        TextView product_information=view.findViewById(R.id.update_info);
        final EditText update_count=view.findViewById(R.id.update_p_count);
        Button btn_update=view.findViewById(R.id.btn_inventory_update);



        product_information.setText(product_info);
        final AlertDialog alertDialog=new AlertDialog.Builder(context).create();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(update_count.getText())){
                    new_count=update_count.getText().toString();
                    new update_inventory().execute();


                    alertDialog.cancel();
                }else{
                    Toast.makeText(context,"no product updated",Toast.LENGTH_SHORT).show();

                }

            }
        });
        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        alertDialog.show();
    }
    static String new_count;
    public class update_inventory extends AsyncTask<String,String,String> {

        int successState=0;
        ProgressDialog dialog=new ProgressDialog(context);
        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setMessage("Update Product inventory...");
            dialog.setCancelable(false);
            dialog.show();

        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("id",product_id));
            jsonObjectData.add(new BasicNameValuePair("new_inv",new_count));

            JSONObject jsonObjectResponse = jsonParser.makeHttpRequest(AppConfig.protocol +
                            AppConfig.hostname+AppConfig.admin_update_inventory,
                    "GET", jsonObjectData);

            Log.d("result",jsonObjectResponse.toString());


            try {

                int success=jsonObjectResponse.getInt("success");
                //   serverMessage=jsonObjectResponse.getString(TAG_MESSAGE);

                if(success==1){
                    successState=1;

                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);
            dialog.dismiss();

            if(successState==1){
                Toast.makeText(context,"product updated successfully",Toast.LENGTH_SHORT).show();

                PackageConfig.isItemUpdated=true;

            }
        }
    }


}
