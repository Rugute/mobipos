package com.mobipos.app.Admin.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.DashboardAdmin;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Categories.AdminCategoryData;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Categories.LoadCatProducts;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories.CashierCategoryData;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/13/17.
 */

public class AdminCategRvAdapter extends RecyclerView.Adapter<AdminCategRvAdapter.ItemViewHolder> {

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView Category;
        TextView Item;
        ImageView CatPic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            Category = (TextView)itemView.findViewById(R.id.text1);
            Item = (TextView)itemView.findViewById(R.id.text2);
            CatPic = (ImageView)itemView.findViewById(R.id.img1);


        }

    }

    List<AdminCategoryData> categoryData;

    Context context;
    public AdminCategRvAdapter(List<AdminCategoryData> categoryData,Context context){
        this.categoryData = categoryData;
        this.context=context;
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
    public void onBindViewHolder(final ItemViewHolder itemViewHolder,final int i) {
        itemViewHolder.Category.setText(categoryData.get(i).name);
        itemViewHolder.Item.setText(categoryData.get(i).id);
        itemViewHolder.Item.setVisibility(View.INVISIBLE);
        itemViewHolder.CatPic.setImageResource(R.drawable.stack);

        itemViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                category_id=categoryData.get(i).id;
                delete_pos=i;

                PopupMenu popupMenu=new PopupMenu(context,itemViewHolder.cv);
                popupMenu.inflate(R.menu.category_option_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.delete_category:
                                new delete_fxn().execute();
                                break;

                            case R.id.view_products:
                                new LoadCatProducts(context,category_id,categoryData.get(i).name).showProducts();
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
        Log.d("recyclerView size",String.valueOf(categoryData.size()));
        return categoryData.size();
    }

    static String category_id;
    static int delete_pos;
    public class delete_fxn extends AsyncTask<String,String,String>{

        String TAG_MESSAGE="message";
        String TAG_SUCCESS="success";
        String USER_ID=null;
        int successState=0;
        ProgressDialog dialog=new ProgressDialog(context);
        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setMessage("Deleting Category...");
            dialog.setCancelable(false);
            dialog.show();

        }
        @Override
        protected String doInBackground(String... strings) {

            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("id",category_id));
            jsonObjectData.add(new BasicNameValuePair("tb_name","tb_categories"));
            jsonObjectData.add(new BasicNameValuePair("col_name","category_id"));


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
                categoryData.remove(delete_pos);
                notifyDataSetChanged();
                notifyItemRemoved(delete_pos);
                Toast.makeText(context,"item deleted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"error while deleting",Toast.LENGTH_SHORT).show();
            }

            dialog.cancel();
        }
    }
}




