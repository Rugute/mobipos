package com.mobipos.app.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mobipos.app.Cashier.Adapters.CashierCategRvAdapter;
import com.mobipos.app.Cashier.Adapters.CashierItemRvAdapter;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 1/11/2018.
 */


public class DatabaseInitializers {

    public static boolean productsLoaded=false;
    public static boolean categoriesLoaded=false;
    Categories categories;
    Users users;
    Context context;
    Products productsdb;
    Product_Prices pricesdb;
    Inventory inventorydb;
    public  DatabaseInitializers(Context context){
        this.context=context;
        categories=new Categories(context,defaults.database_name,null,1);
        users=new Users(context,defaults.database_name,null,1);
        productsdb=new Products(context,defaults.database_name,null,1);
        pricesdb=new Product_Prices(context,defaults.database_name,null,1);
        inventorydb=new Inventory(context,defaults.database_name,null,1);




    }

    public boolean loaded(){
        new loadCategories().execute();
        new loadItems().execute();

        if(productsLoaded && categoriesLoaded){
            return true;
        }else{
            return false;
        }
    }

    public class loadCategories extends AsyncTask<String,String,String> {

        int success=0;
        String serverMessage;
        JSONArray categoryArray;


        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            paramters.add(new BasicNameValuePair("user_id",users.get_user_id()));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+ PackageConfig.get_categories,
                    "GET",paramters);

            Log.d("json object",jsonObject.toString());

            try {
                success=jsonObject.getInt("success");
                serverMessage=jsonObject.getString("message");

                categoryArray=jsonObject.getJSONArray("data");

                PackageConfig.categoryArrayId=new String[categoryArray.length()];
                PackageConfig.categoryArrayName=new String[categoryArray.length()];

                for(int i=0;i<categoryArray.length();i++){
                    JSONObject jObj=categoryArray.getJSONObject(i);

                    PackageConfig.categoryArrayName[i]=jObj.getString("cat_name");
                    PackageConfig.categoryArrayId[i]=jObj.getString("cat_id");

                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);
            if (success==1){
                if(categories.getCategoryCount()==0){
                    for(int i=0;i<PackageConfig.categoryArrayId.length;i++){
                        if(!categories.insertCategory(PackageConfig.categoryArrayId[i],PackageConfig.categoryArrayName[i])) {
                            Log.d("error inserting data","data not inserted");

                        }
                    }
                }else{
                    //check if category exists
                    for(int i=0;i<PackageConfig.categoryArrayId.length;i++){
                        if(!categories.CategoryExists(PackageConfig.categoryArrayId[i])){
                            if(!categories.insertCategory(PackageConfig.categoryArrayId[i],PackageConfig.categoryArrayName[i])){
                                Log.d("error inserting data","data not inserted");
                            }
                        }
                    }
                }

                categoriesLoaded=true;
            }else{
                Toast.makeText(context,serverMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class loadItems extends AsyncTask<String,String,String>{

        int success=0;
        String serverMessage;
        JSONArray dataitems;
        protected void onPreExecute(){
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List parameters=new ArrayList();
            parameters.add(new BasicNameValuePair("user_id",users.get_user_id()));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+ PackageConfig.get_items,
                    "GET",parameters);
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

                productsLoaded=true;
            }else if(success==0){
                Toast.makeText(context,serverMessage,Toast.LENGTH_SHORT).show();
            }

        }
    }

}
