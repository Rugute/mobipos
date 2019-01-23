package com.mobipos.app.services;


import android.app.ProgressDialog;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSale;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Controller;
import com.mobipos.app.database.Discounts;
import com.mobipos.app.database.Inventory;
import com.mobipos.app.database.Printers;
import com.mobipos.app.database.Product_Prices;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.Taxes;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;
import com.mobipos.app.login.AdminLogin;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 1/16/2019.
 */


public class DatabaseUpdate  {

    Context context;
    Users users;
    Categories categoriesdb;
    Products productsdb;
    Product_Prices pricesdb;
    Inventory inventorydb;

    Discounts discountsdb;
    Taxes taxesdb;
    Printers printersdb;

    public DatabaseUpdate(Context context) {
        this.context=context;

        users = new Users(context, defaults.database_name,null,1);
        categoriesdb = new Categories(context, defaults.database_name,null,1);
        productsdb = new Products(context, defaults.database_name,null,1);
        pricesdb = new Product_Prices(context, defaults.database_name,null,1);
        inventorydb = new Inventory(context, defaults.database_name,null,1);
        discountsdb = new Discounts(context, defaults.database_name,null,1);
        taxesdb = new Taxes(context, defaults.database_name,null,1);
        printersdb = new Printers(context, defaults.database_name,null,1);
    }



    public void update(){
            new Controller(context,defaults.database_name,null,1).UpdateDropTables();
            new firstDataLoad();

           // context.startActivity(new Intent(context,DashboardCashier.class));

    }




    public class firstDataLoad extends AsyncTask<String,String,String> {

        ProgressDialog dialog=new ProgressDialog(context);
        protected void onPreExecute(){

            super.onPreExecute();
            dialog.setMessage("updating. Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            new load_parameters().execute();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            paramters.add(new BasicNameValuePair("user_id",users.get_user_id("cashier")));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+
                            com.mobipos.app.Cashier.PackageConfig.get_categories,
                    "GET",paramters);

            try {
                int success=jsonObject.getInt("success");
                String serverMessage=jsonObject.getString("message");

                JSONArray categoryArray=jsonObject.getJSONArray("data");

                if(success==1){
                    for(int i=0;i<categoryArray.length();i++){
                        JSONObject jObj=categoryArray.getJSONObject(i);
                        if(!categoriesdb.insertCategory(jObj.getString("cat_id"),jObj.getString("cat_name"))) {
                            Log.d("error inserting data","data not inserted");
                        }

                    }

                    List params=new ArrayList();
                    params.add(new BasicNameValuePair("user_id",users.get_user_id("cashier")));
                    jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+
                                    com.mobipos.app.Cashier.PackageConfig.get_items,
                            "GET",params);

                    try{
                        int  success_state=jsonObject.getInt("success");
                        serverMessage=jsonObject.getString("message");
                        JSONArray dataitems=jsonObject.getJSONArray("data");

                        com.mobipos.app.Cashier.PackageConfig.itemArrayId=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.itemArrayName=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.categoryArrayId=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.itemArrayMeasurement=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.price_id=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.price=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.stockData=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.lowStockData=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.tax_margin=new String[dataitems.length()];

                        for(int l=0;l<dataitems.length();l++){
                            JSONObject jObj=dataitems.getJSONObject(l);

                            com.mobipos.app.Cashier.PackageConfig.itemArrayId[l]=jObj.getString("product_id");
                            com.mobipos.app.Cashier.PackageConfig.itemArrayName[l]=jObj.getString("product_name");
                            com.mobipos.app.Cashier.PackageConfig.categoryArrayId[l]=jObj.getString("category_id");
                            com.mobipos.app.Cashier.PackageConfig.itemArrayMeasurement[l]=jObj.getString("measurement_name");
                            com.mobipos.app.Cashier.PackageConfig.price_id[l]=jObj.getString("price_id");
                            com.mobipos.app.Cashier.PackageConfig.price[l]=jObj.getString("price");
                            com.mobipos.app.Cashier.PackageConfig.stockData[l]=jObj.getString("opening_stock");
                            com.mobipos.app.Cashier.PackageConfig.lowStockData[l]=jObj.getString("low_stock_count");
                            com.mobipos.app.Cashier.PackageConfig.tax_margin[l]=jObj.getString("tax_mode");

                            Log.d("check this prod id",jObj.getString("product_id"));
                            Log.d(jObj.getString("product_id"),String.valueOf(productsdb.ProductExists(jObj.getString("product_id"))));

                            if(!productsdb.ProductExists(jObj.getString("product_id"))){
                                if(!productsdb.insertProduct(jObj.getString("product_id"),
                                        jObj.getString("product_name"),
                                        jObj.getString("category_id"),
                                        jObj.getString("measurement_name"),
                                        jObj.getString("tax_mode"))){
                                    Log.d("err inserting products","not inserted");
                                }else{
                                    if(!pricesdb.insertPrices(jObj.getString("price_id"),
                                            jObj.getString("product_id"),
                                            jObj.getString("price"))){
                                        Log.d("err inserting prices","not inserted");
                                    }else{
                                        if(!inventorydb.insertStock(jObj.getString("product_id"),
                                                jObj.getString("opening_stock"),
                                                jObj.getString("low_stock_count"))){
                                            Log.d("err inserting stocks","not inserted");
                                        }
                                    }
                                }
                            }

                            List params_a=new ArrayList();
                            params.add(new BasicNameValuePair("product_id",jObj.getString("product_id")));
//                            JSONObject UpdateSyncStatus=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+
//                                            com.mobipos.app.Cashier.PackageConfig.sync_product_movement,
//                                    "GET",params_a);
//
//                            try {
//                                int state=UpdateSyncStatus.getInt("success");
//                                if(state==1){
//                                    Log.d("product sync meso",UpdateSyncStatus.getString("message"));
//                                }
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }


                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }




            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);
            dialog.cancel();
           // startActivity(new Intent(getContext(),DashboardCashier.class));
           // getActivity().finish();
        }


    }

    public class load_parameters extends AsyncTask<String,String,String>{


        @Override
        protected String doInBackground(String... strings) {

            JSONParser jsonParser=new JSONParser();
            List params=new ArrayList();

            params.add(new BasicNameValuePair("user_id",users.get_user_id("cashier")));

            JSONObject jobjTaxes=jsonParser.makeHttpRequest(com.mobipos.app.login.PackageConfig.protocol+ com.mobipos.app.login.PackageConfig.hostname+
                    com.mobipos.app.login.PackageConfig.tax_load,"GET",params);


            int tax_success=0;
            JSONArray tax_data,printerArray;

            try{
                tax_success=jobjTaxes.getInt("success");

                if(tax_success==1){
                    tax_data=jobjTaxes.getJSONArray("data");

                    for (int i=0;i<tax_data.length();i++){
                        JSONObject j=tax_data.getJSONObject(i);
                        if(!taxesdb.insertTax(j.getString("tb_tax_id"),
                                j.getString("tax_margin"),j.getString("margin_mode"))){
                            System.out.println("Error while inserting tax");
                        //    Toast.makeText(getContext(),"Error inserting tax",Toast.LENGTH_SHORT).show();
                        }
                    }

                    List printerParameters=new ArrayList();
                    printerParameters.add(new BasicNameValuePair("user_id",users.get_user_id("user_id")));

                    JSONObject printerObject=jsonParser.makeHttpRequest(com.mobipos.app.login.PackageConfig.protocol+ com.mobipos.app.login.PackageConfig.hostname+
                            com.mobipos.app.login.PackageConfig.printer_load,"GET",params);

                    try {

                        int printer_success=printerObject.getInt("success");
                        printerArray=printerObject.getJSONArray("data");

                        if(printer_success==1){

                            Log.d("printer success",String.valueOf(printer_success));
                            Log.d("printer data",printerArray.toString());
                            for(int i=0;i<printerArray.length();i++){
                                JSONObject jprinterObj=printerArray.getJSONObject(i);

                                Log.d("printer id",jprinterObj.getString("id"));
                                Log.d("printer name",jprinterObj.getString("printer_name"));
                                Log.d("printer mac",jprinterObj.getString("printer_mac"));

                                if(!printersdb.InsertPrinter(jprinterObj.getString("id"),
                                        jprinterObj.getString("printer_name"),
                                        jprinterObj.getString("printer_mac"))){
                                    System.out.println("Error inserting printer");
                                 //   Toast.makeText(getContext(),"Error inserting printer",Toast.LENGTH_SHORT).show();
                                }
                            }

                            List discounts_params=new ArrayList();
                            discounts_params.add(new BasicNameValuePair("user_id",users.get_user_id("cashier")));
                            JSONObject discounts_object=jsonParser.makeHttpRequest(com.mobipos.app.login.PackageConfig.protocol+ com.mobipos.app.login.PackageConfig.hostname+
                                    com.mobipos.app.login.PackageConfig.discounts_load,"GET",discounts_params);

                            try{
                                int discounts_success=discounts_object.getInt("success");
                                if(discounts_success==1){
                                    JSONArray discounts_array=discounts_object.getJSONArray("data");
                                    for(int k=0;k<discounts_array.length();k++){

                                        JSONObject discounts_obj=discounts_array.getJSONObject(k);
                                        if(!discountsdb.InsertDiscount(discounts_obj.getString("id"),
                                                discounts_obj.getString("discount_name"),
                                                discounts_obj.getString("discount_value"))){
                                            System.out.println("error in inserting discounts");
                                         //   Toast.makeText(getContext(),"Error inserting discounts",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }catch (Exception e){

                            }
                        }

                    }catch (Exception e){

                    }

                }


            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
