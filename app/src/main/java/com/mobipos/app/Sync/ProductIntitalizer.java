package com.mobipos.app.Sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Inventory;
import com.mobipos.app.database.Product_Prices;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 1/18/2018.
 */

public class ProductIntitalizer {
    Users users;
    Categories categories;
    Context context;
    Products productsdb;
    Product_Prices pricesdb;
    Inventory inventorydb;
    int user_type;
    public ProductIntitalizer(Context context,int user_type){
        this.context=context;
        users=new Users(context, defaults.database_name,null,1);
        categories=new Categories(context, defaults.database_name,null,1);
        productsdb=new Products(context, defaults.database_name,null,1);
        pricesdb=new Product_Prices(context, defaults.database_name,null,1);
        inventorydb=new Inventory(context, defaults.database_name,null,1);
        this.user_type=user_type;
        new loadItems().execute();

    }

    public class loadItems extends AsyncTask<String,String,String> {

        int success=0;
        String serverMessage;
        JSONArray dataitems;
        protected void onPreExecute(){
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            JSONObject jsonObject;
            List parameters=new ArrayList();


            if(user_type==0) {
                //admin loading data
                parameters.add(new BasicNameValuePair("branch_id",AppConfig.selected_branch_id));
                jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+ AppConfig.get_items,
                        "GET",parameters);
            }else{
                //cashier load data
                parameters.add(new BasicNameValuePair("user_id",users.get_user_id()));
                jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+ PackageConfig.get_items,
                        "GET",parameters);
            }

            Log.d("json object",jsonObject.toString());

            try{
                success=jsonObject.getInt("success");
                serverMessage=jsonObject.getString("message");
                dataitems=jsonObject.getJSONArray("data");

                PackageConfig.itemArrayId=new String[dataitems.length()];
                PackageConfig.itemArrayName=new String[dataitems.length()];
                PackageConfig.categoryArrayId=new String[dataitems.length()];
                PackageConfig.itemArrayMeasurement=new String[dataitems.length()];
                PackageConfig.price_id=new String[dataitems.length()];
                PackageConfig.price=new String[dataitems.length()];
                PackageConfig.stockData=new String[dataitems.length()];

                for(int i=0;i<dataitems.length();i++){
                    JSONObject jObj=dataitems.getJSONObject(i);

                    PackageConfig.itemArrayId[i]=jObj.getString("product_id");
                    PackageConfig.itemArrayName[i]=jObj.getString("product_name");
                    PackageConfig.categoryArrayId[i]=jObj.getString("category_id");
                    PackageConfig.itemArrayMeasurement[i]=jObj.getString("measurement_name");
                    PackageConfig.price_id[i]=jObj.getString("price_id");
                    PackageConfig.price[i]=jObj.getString("price");
                    PackageConfig.stockData[i]=jObj.getString("opening_stock");

                }

            }catch (Exception e){
                e.printStackTrace();
            }


            Log.d("json object",jsonObject.toString());
            return  null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);


            if(success==1){

                if(productsdb.getProductCount("all")==0){
                    for(int i=0;i<dataitems.length();i++){
                        if(!productsdb.insertProduct(PackageConfig.itemArrayId[i],PackageConfig.itemArrayName[i],
                                PackageConfig.categoryArrayId[i],PackageConfig.itemArrayMeasurement[i])){
                            Log.d("err inserting products","not inserted");
                        }else{
                            if(!pricesdb.insertPrices(PackageConfig.price_id[i],PackageConfig.itemArrayId[i],
                                    PackageConfig.price[i])){
                                Log.d("err inserting price","not inserted");
                            }else{
                                if(!inventorydb.insertStock(PackageConfig.itemArrayId[i],PackageConfig.stockData[i])){
                                    Log.d("err inserting stocks","not inserted");
                                }
                            }
                        }
                    }


                }else{
                    for(int i=0;i<dataitems.length();i++){

                        if(!productsdb.ProductExists(PackageConfig.itemArrayId[i])){
                            if(!productsdb.insertProduct(PackageConfig.itemArrayId[i],PackageConfig.itemArrayName[i],
                                    PackageConfig.categoryArrayId[i],PackageConfig.itemArrayMeasurement[i])){
                                Log.d("err inserting products","not inserted");
                            }else{
                                if(!pricesdb.insertPrices(PackageConfig.price_id[i],PackageConfig.itemArrayId[i],
                                        PackageConfig.price[i])){
                                    Log.d("err inserting prices","not inserted");
                                }else{
                                    if(!inventorydb.insertStock(PackageConfig.itemArrayId[i],PackageConfig.stockData[i])){
                                        Log.d("err inserting stocks","not inserted");
                                    }
                                }
                            }
                        }

                    }
                }

              //  productsLoaded=true;
            }else if(success==0){
                Toast.makeText(context,serverMessage,Toast.LENGTH_SHORT).show();
            }

        }
    }
}
