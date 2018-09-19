package com.mobipos.app.Admin.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.DashboardFragments.Inventory.Categories.AdminCategoryData;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminProductData;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.UpdatePrice;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSale;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.login.PackageConfig;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/13/17.
 */

public class AdminProdRvAdapter extends RecyclerView.Adapter<AdminProdRvAdapter.ItemViewHolder> {

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

    List<AdminProductData> productData;
    Context context;
    Activity activity;
    public AdminProdRvAdapter(List<AdminProductData> productData,Context context,Activity activity){
        this.productData = productData;
        this.context=context;
        this.activity=activity;
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
    public void onBindViewHolder(final ItemViewHolder itemViewHolder, final int i) {
        itemViewHolder.Category.setText(productData.get(i).name);
        itemViewHolder.Item.setText("Remaining: "+productData.get(i).buying+" "+productData.get(i).measure);
        itemViewHolder.CatPic.setImageResource(R.drawable.price);
      //  itemViewHolder.Item.setVisibility(View.INVISIBLE);

        itemViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                product_id=productData.get(i).id;
                delete_pos=i;

                PopupMenu popupMenu=new PopupMenu(context,itemViewHolder.cv);
                popupMenu.inflate(R.menu.product_options_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.delete_product:
                                new delete_fxn().execute();
                                break;

                            case R.id.update_product:
                                product_info=productData.get(i).name+"\ncurrent inventory:"+productData.get(i).buying+" "+
                                        productData.get(i).measure;
                                update_pop_up();
                                break;

                            case R.id.update_price:
                                new UpdatePrice(activity,productData.get(i).buying_price,
                                        productData.get(i).selling,productData.get(i).id).showDialog();
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
        Log.d("recyclerView size",String.valueOf(productData.size()));
        return productData.size();
    }

    static String product_id;
    static int delete_pos;
    static String product_info;
    public class delete_fxn extends AsyncTask<String,String,String> {

        String TAG_MESSAGE="message";
        String TAG_SUCCESS="success";
        String USER_ID=null;
        int successState=0;
        ProgressDialog dialog=new ProgressDialog(context);
        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setMessage("Deleting Product...");
            dialog.setCancelable(false);
            dialog.show();

        }
        @Override
        protected String doInBackground(String... strings) {

            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("id",product_id));
            jsonObjectData.add(new BasicNameValuePair("tb_name","tb_products"));
            jsonObjectData.add(new BasicNameValuePair("col_name","product_id"));


            JSONObject jsonObjectResponse = jsonParser.makeHttpRequest(AppConfig.protocol +
                            AppConfig.admin_delete_user,
                    "GET", jsonObjectData);

            Log.d("result",jsonObjectResponse.toString());


            try {

                int success=jsonObjectResponse.getInt(TAG_SUCCESS);
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


            if(successState==1){
                productData.remove(delete_pos);
                notifyDataSetChanged();
                notifyItemRemoved(delete_pos);
                Toast.makeText(context,"item deleted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"error while deleting",Toast.LENGTH_SHORT).show();
            }

            dialog.cancel();
        }
    }

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
    public class update_inventory extends AsyncTask<String,String,String>{

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
//                Fragment fragment=null;
//                fragment= MakeSale.newInstance();
//                FragmentTransaction transaction =
//                transaction.replace(R.id.frame_layout, fragment);
//                transaction.commit();
            }else{
                Toast.makeText(context,"error in updating product",Toast.LENGTH_SHORT).show();
            }
        }
    }
}




