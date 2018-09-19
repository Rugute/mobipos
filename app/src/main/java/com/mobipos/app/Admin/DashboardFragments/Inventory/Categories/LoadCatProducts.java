package com.mobipos.app.Admin.DashboardFragments.Inventory.Categories;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.loadCatProductsAdapter;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminBranchSpinnerData;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminCategorySpinnerData;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminProductData;
import com.mobipos.app.Admin.PackageConfig;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mobipos.app.Admin.PackageConfig.branches;
import static com.mobipos.app.Admin.PackageConfig.branchesId;

/**
 * Created by folio on 9/10/2018.
 */

public class LoadCatProducts {
    ProgressBar bar;
    RecyclerView rv;
    Context context;
    String categoryId;
    Users usersdb;
    String categoryName;
    public LoadCatProducts(Context context,String categoryId,String categoryName){
        this.context=context;
        this.categoryId=categoryId;
        this.categoryName=categoryName;
        usersdb = new Users(context, defaults.database_name,null,1);
    }

    public void showProducts(){
        final android.support.v7.app.AlertDialog dialog=
                new android.support.v7.app.AlertDialog.Builder(context).create();
        View view= LayoutInflater.from(context).inflate(R.layout.view_products,null);
        dialog.setView(view);
        rv=view.findViewById(R.id.rv_view_products);
        bar=view.findViewById(R.id.bar_load_products);
        Button btn=view.findViewById(R.id.cat_name_prod);
        btn.setText(categoryName);
        rv.setLayoutManager(new LinearLayoutManager(context));
        dialog.show();
        load();
    }

    public void showBar(boolean state){
       if(state) {
           bar.setVisibility(View.VISIBLE);
           rv.setVisibility(View.INVISIBLE);
       } else{
           bar.setVisibility(View.GONE);
           rv.setVisibility(View.VISIBLE);
        }
    }
    private void initializeAdapter(List<catProducts> data){
        loadCatProductsAdapter adapter = new loadCatProductsAdapter(data);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }

    public class LoadItems extends AsyncTask<String,String,String>{

        private  List<AdminBranchSpinnerData> branchData;
        private  List<AdminCategorySpinnerData> categoryData;
        private  List<AdminProductData> productData;
        int success=0;
        String serverMessage;
        JSONArray data;
        protected void onPreExecute(){
            super.onPreExecute();
            showBar(true);
        }

        @Override
        protected String doInBackground(String... strings) {

            categoryData=new ArrayList<>();
            branchData=new ArrayList<>();
            AppConfig.data=new ArrayList<>();

            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            paramters.add(new BasicNameValuePair("user_id",usersdb.get_user_id("admin")));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+
                            PackageConfig.get_admin_items,
                    "GET",paramters);

            Log.d("json object",jsonObject.toString());

            try {
                success=jsonObject.getInt("success");
                serverMessage=jsonObject.getString("message");

                data=jsonObject.getJSONArray("data");
                branches=new String[data.length()+1];
                branchesId=new String[data.length()+1];
                branches[0]="All Branches";
                branchesId[0]="0";
                for(int i=0;i<data.length();i++){
                    JSONObject jobj=data.getJSONObject(i);
                    Log.d("branch object",data.getJSONObject(i).toString());
                    branches[i+1]=jobj.getString("shop_name");
                    branchesId[i+1]=jobj.getString("shop_id");
                    JSONArray category_data=jobj.getJSONArray("categories");
                    for (int j=0;j<category_data.length();j++){
                        JSONObject catObj=category_data.getJSONObject(j);

                        categoryData.add(new AdminCategorySpinnerData(Integer.parseInt(branchesId[i+1]),
                                catObj.getString("category_id"),catObj.getString("category_name")));
                        JSONArray products=catObj.getJSONArray("products");

                        for(int k=0;k<products.length();k++){
                            JSONObject prodObj=products.getJSONObject(k);
                            AppConfig.data.add(new AdminProductData(Integer.parseInt(branchesId[i+1]),
                                    Integer.parseInt(catObj.getString("category_id")),
                                    prodObj.getString("product_id"),
                                    prodObj.getString("product_name"),
                                    R.drawable.price,
                                    prodObj.getString("items_in_store"),
                                    prodObj.getString("selling_price"),
                                    prodObj.getString("measurement"),
                                    prodObj.getString("buying_price")
                            ));
                        }
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String s){

            super.onPostExecute(s);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    initializeAdapter(loadProducts());
                }
            showBar(false);

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<catProducts> loadProducts(){


        List<catProducts> data=new ArrayList<>();
        if(AppConfig.data==null)new LoadItems().execute();
        else{
            Log.d("category id",categoryId);

            AppConfig.data.forEach(dt->{

                if(dt.categoryId==Integer.parseInt(categoryId)){
                    catProducts prod=new catProducts();
                    prod.setProductName(dt.name);
                    prod.setProductId(dt.id);
                    prod.setItemsRemaining(dt.buying);

                    Log.d("product name",prod.getProductName());
                    data.add(prod);
                }
            });
        }


       return data;
    }

    public void load(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            initializeAdapter(loadProducts());
        }
    }
}
