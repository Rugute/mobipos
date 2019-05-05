package com.mobipos.app.Admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.QuickSaleAdapter;
import com.mobipos.app.Admin.DashboardFragments.AdminViewSales;
import com.mobipos.app.Admin.DashboardFragments.Inventory.AdminInventory;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Categories.AdminCategories;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminAddItem;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminAddItemData;
import com.mobipos.app.Cashier.*;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Dashboard.DashboardFragment;
import com.mobipos.app.Admin.DashboardFragments.NotificationsFragment;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.Defaults.SplashPage;
import com.mobipos.app.R;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Inventory;
import com.mobipos.app.database.Product_Prices;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;
import com.mobipos.app.login.CashierLogin;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 12/11/2017.
 */

public class DashboardAdmin extends AppCompatActivity {
    Users usersdb;
    Products productsdb;
    Categories categoriesdb;
    Product_Prices pricesdb;
    Inventory inventorydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_admin);
       // setTitle("Mauzo Africa: Administrator");

        BottomNavigationView navigationMenuView=findViewById(R.id.bottom_navigation);

        usersdb=new Users(this, defaults.database_name,null,1);
        productsdb=new Products(this, defaults.database_name,null,1);
        categoriesdb=new Categories(this, defaults.database_name,null,1);
        pricesdb=new Product_Prices(this, defaults.database_name,null,1);
        inventorydb=new Inventory(this, defaults.database_name,null,1);
        Fragment fragment;
        fragment= AdminInventory.newInstance();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout,fragment);
        transaction.commit();

        navigationMenuView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment=null;
                switch (item.getItemId()){
                    case R.id.navigation_home:
                       selectedFragment= AdminInventory.newInstance();
                        break;
                   // case R.id.navigation_dashboard:
                      //  selectedFragment= AdminInventory.newInstance();
                      //  break;
                    case R.id.navigation_account:
                        selectedFragment= AccountFragment.newInstance();
                        break;
                    case R.id.navigation_sales:
                        selectedFragment= AdminViewSales.newInstance();
                        break;
                }
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction.replace(R.id.frame_layout,selectedFragment);
                transaction.commit();

                return true;
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.logout,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        final CheckInternetSettings internet=new CheckInternetSettings(this);
        if(item.getItemId()==android.R.id.home){
            FragmentManager manager=getSupportFragmentManager();
            manager.popBackStack();
            //setTitle("Mauzo Africa: Administator");
        }else if(item.getItemId()==R.id.logout_btn){

            if(internet.isNetworkConnected()){
                  finish();
                  System.exit(0);

            }else{
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this).
                        setTitle("Cannot Logout").
                        setMessage("Enable your internet to logout").
                        setPositiveButton((CharSequence) "Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                internet.context.startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
                            }
                        });
                alertBuilder.show();
            }
//        }else if(item.getItemId()==R.id.switch_account){
            usersdb.deleteTables();
            if(usersdb.check_admin_sale_id() > 0){
                if(categoriesdb.getCategoryCount()>0){
                    PackageConfig.flag_restart=0;
                }else{
                    PackageConfig.flag_restart=1;
                }

                startActivity(new Intent(DashboardAdmin.this,DashboardCashier.class));
                finish();
            }else{
                if(internet.isNetworkConnected()){
                    new checkAdminDefaultBranch().execute();
                }else{
                    AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this).
                            setTitle("Cannot Logout").
                            setMessage("Enable your internet to switch accounts").
                            setPositiveButton((CharSequence) "Settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    internet.context.startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
                                }
                            });
                    alertBuilder.show();
                }
            }

        }

        return  true;
    }

    public void onBackPressed(){


        int count = getSupportFragmentManager().getBackStackEntryCount();
        if(count !=0){
                FragmentManager manager=getSupportFragmentManager();
                manager.popBackStack();
        }else{
            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);

            alertDialog.setMessage((CharSequence) "Are you sure you want to Logout?");
            alertDialog.setPositiveButton((CharSequence) "Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                    finish();
                }
            });
            alertDialog.setNegativeButton((CharSequence) "No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alertDialog.show();
        }


    }

    public  class checkAdminDefaultBranch extends AsyncTask<String,String,String>{

        int success=0;
        String id;
        ProgressDialog dialog;
        protected void onPreExecute(){
            super.onPreExecute();
            dialog=new ProgressDialog(DashboardAdmin.this);
            dialog.setMessage("Checking for default Sale Branch.please wait...");
             dialog.setCancelable(false);
             dialog.show();


        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List paramters = new ArrayList();

            paramters.add(new BasicNameValuePair("user_id", usersdb.get_admin_id()));
            JSONObject jsonObject = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.admin_check_default_sales_branch,
                    "GET", paramters);

            try{
                success=jsonObject.getInt("success");
                id=jsonObject.getString("sales_id");
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);
            dialog.cancel();
            if(success==1){
                    if(!usersdb.update_admin_sales_id(id,usersdb.get_admin_id())){
                        Toast.makeText(getApplicationContext(),"Error in creating admin sales id",Toast.LENGTH_SHORT).show();
                    }

                startActivity(new Intent(DashboardAdmin.this,DashboardCashier.class));
                finish();
            }else if(success==2){
                new BranchSelection().execute();
            }else if(success==100){
                Fragment fragment;
                fragment = BranchFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().addToBackStack("Back");
                transaction.replace(R.id.frame_layout, fragment);
                transaction.commit();
            }


        }

    }

    public class BranchSelection extends AsyncTask<String, String, String> {
        int success = 0;
        String serverMessage;
        JSONArray branch;
        String outlet = null;


        ProgressDialog dialog;
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(DashboardAdmin.this);
            dialog.setMessage("Checking branch data.please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List paramters = new ArrayList();

            paramters.add(new BasicNameValuePair("user_id", usersdb.get_admin_id()));

            JSONObject jsonObject = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.admin_select_branches,
                    "GET", paramters);
            Log.d("data recieved", jsonObject.toString());

            try {
                success = jsonObject.getInt("success");
                branch = jsonObject.getJSONArray("data");

                if(success==1){
                    AppConfig.branchNames = new String[branch.length()];
                    AppConfig.branchIds = new String[branch.length()];
                    for (int i = 0; i < branch.length(); i++) {
                        JSONObject jobj = branch.getJSONObject(i);
                        AppConfig.branchNames[i] = jobj.getString("shop_name");
                        AppConfig.branchIds[i] = jobj.getString("shop_id");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
                dialog.dismiss();
            if (success == 1) {
                branch();
                dialog.cancel();
            }else{
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            }
            dialog.cancel();
        }
    }


    public void branch(){
        final AlertDialog dialog = new AlertDialog.Builder(DashboardAdmin.this).create();
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.load_branches,null);
        ListView listView=view.findViewById(R.id.admin_sales_listview);
        listView.setAdapter(new QuickSaleAdapter(getApplicationContext(), AppConfig.branchNames));
        dialog.setView(view);
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos= (int) adapterView.getItemIdAtPosition(i);
                AdminAddItemData.select_branch=AppConfig.branchIds[pos];
                dialog.dismiss();

                final AlertDialog.Builder alertDialog=new AlertDialog.Builder(DashboardAdmin.this).
                setTitle("Default Branch").
                setMessage("you have selected "+AppConfig.branchNames[pos]+" As your default selling branch").
                        setPositiveButton( "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                        new setDefaultBranch(DashboardAdmin.this).execute();

                    }
                });

                alertDialog.show();

            }
        });


    }


    public class setDefaultBranch extends AsyncTask<String,String,String>{

        int success=0;
        String admin_sale_id;
        private ProgressDialog progressDialog;
        Context context;
        public setDefaultBranch(Context context){
            this.context=context;
        }


        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog=new ProgressDialog(context);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
               // progressDialog=new ProgressDialog(context);
            }

            progressDialog.setMessage("Setting default branch.please wait...");
            progressDialog.setCancelable(false);

                Toast.makeText(context,"setting the default branch...",Toast.LENGTH_SHORT).show();


        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List params=new ArrayList();
            params.add(new BasicNameValuePair("user_id",usersdb.get_admin_id()));
            params.add(new BasicNameValuePair("branch_id",AdminAddItemData.select_branch));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+AppConfig.admin_create_default_sales_branch,
                    "GET",params);

            try {
                success=jsonObject.getInt("success");
                admin_sale_id=jsonObject.getString("admin_sales_id");
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(success==1){
                if(!usersdb.update_admin_sales_id(admin_sale_id,usersdb.get_admin_id())){
                    Toast.makeText(getApplicationContext(),"Error in creating admin sales id",Toast.LENGTH_SHORT).show();
                }else{
                    new firstDataLoad().execute();
                }

            }


        }
    }

    public class firstDataLoad extends AsyncTask<String,String,String>{

        ProgressDialog dialog=new ProgressDialog(DashboardAdmin.this);

        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setMessage("Synchronizing data from server. Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            paramters.add(new BasicNameValuePair("user_id",usersdb.get_user_id("cashier")));

            JSONObject  jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+ com.mobipos.app.Cashier.PackageConfig.get_categories,
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
                    params.add(new BasicNameValuePair("user_id",usersdb.get_user_id("cashier")));
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


            startActivity(new Intent(DashboardAdmin.this,DashboardCashier.class));
            finish();

        }
    }



}
