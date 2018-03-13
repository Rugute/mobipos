package com.mobipos.app.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.DashboardFragments.Inventory.Categories.AdminCategories;
import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Discounts;
import com.mobipos.app.database.Inventory;
import com.mobipos.app.database.Printers;
import com.mobipos.app.database.Product_Prices;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.Taxes;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by folio on 12/6/2017.
 */

public class CashierLogin extends Activity {

    public static String check_id;
    EditText ed_check_id;
    TextView check_login;
    Users users_db;
    Taxes taxesdb;
    Discounts discountsdb;
    Categories categoriesdb;
    Printers printersdb;

    Inventory inventorydb;
    Product_Prices pricesdb;

    Products productsdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emlog);

        users_db=new Users(getApplicationContext(),defaults.database_name,null,1);
        categoriesdb=new Categories(getApplicationContext(),defaults.database_name,null,1);
        productsdb=new Products(getApplicationContext(),defaults.database_name,null,1);
        taxesdb=new Taxes(getApplicationContext(),defaults.database_name,null,1);
        printersdb=new Printers(getApplicationContext(),defaults.database_name,null,1);
        inventorydb=new Inventory(getApplicationContext(),defaults.database_name,null,1);
        discountsdb=new Discounts(getApplicationContext(),defaults.database_name,null,1);
        pricesdb=new Product_Prices(getApplicationContext(),defaults.database_name,null,1);
        ed_check_id=findViewById(R.id.emp_id);
        check_login=findViewById(R.id.check_login);

        check_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_id=ed_check_id.getText().toString();
                try{

                    final CheckInternetSettings internetOn=new CheckInternetSettings(CashierLogin.this);
                    if(internetOn.isNetworkConnected()){
                        new employeeProcessor().execute();
                    }else{
                        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(CashierLogin.this).
                                setTitle("Internet Connectivity is out").
                                setMessage("Enable your internet to sync data from server").
                                setPositiveButton((CharSequence) "Settings", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        internetOn.context.startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
                                    }
                                });
                        alertBuilder.show();
                    }

                }catch (NullPointerException e){
                    e.printStackTrace();
                }

             }
        });

    }

    public class employeeProcessor extends AsyncTask<String,String,String> {

        int success,tax_success;
        String serverMessage;
        JSONArray data,tax_data,printerArray;

        String business_name;

      ProgressDialog pdialog=new ProgressDialog(CashierLogin.this);

        protected void onPreExecute() {
            super.onPreExecute();
        //    dialog(true);
            pdialog.setMessage("Authenticating.please wait...");
            pdialog.setCancelable(false);
            pdialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramaters = new ArrayList();
            paramaters.add(new BasicNameValuePair("employee_id",check_id));


            JSONObject jsonObject=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+
                            PackageConfig.employee_log,
                    "GET",paramaters);

            Log.d("json object",jsonObject.toString());
            try {
                success=jsonObject.getInt("success");
                serverMessage=jsonObject.getString("message");
                PackageConfig.branch_id=jsonObject.getString("branchId");
                PackageConfig.branch_name=jsonObject.getString("branchname");

                if(success==1){
                    data=jsonObject.getJSONArray("result");

                    JSONObject jobj=data.getJSONObject(0);

                    PackageConfig.login_data=new String[7];
                    PackageConfig.login_data[0]=jobj.getString("user_id");
                    PackageConfig.login_data[1]=jobj.getString("username");
                    PackageConfig.login_data[2]=jobj.getString("email");
                    PackageConfig.login_data[3]=jobj.getString("employee_id");
                    PackageConfig.login_data[4]="1";
                    PackageConfig.login_data[5]="cashier";
                    PackageConfig.login_data[6]="12345";
                  //  PackageConfig.login_data[6]=jobj.getString("phoneNumber");
                     business_name=jobj.getString("business_name");

                    List params=new ArrayList();
                    params.add(new BasicNameValuePair("user_id",jobj.getString("user_id")));

                    JSONObject jobjTaxes=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+
                            PackageConfig.tax_load,"GET",params);


                    Log.d("json object",jsonObject.toString());

                    try{
                        tax_success=jobjTaxes.getInt("success");

                        if(tax_success==1){
                            tax_data=jobjTaxes.getJSONArray("data");

                            for (int i=0;i<tax_data.length();i++){
                                JSONObject j=tax_data.getJSONObject(i);
                                if(!taxesdb.insertTax(j.getString("tb_tax_id"),
                                        j.getString("tax_margin"),j.getString("margin_mode"))){
                                    Toast.makeText(getApplicationContext(),"Error inserting tax",Toast.LENGTH_SHORT).show();
                                }
                            }

                            List printerParameters=new ArrayList();
                            printerParameters.add(new BasicNameValuePair("user_id",jobj.getString("user_id")));

                            JSONObject printerObject=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+
                                    PackageConfig.printer_load,"GET",params);

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
                                            Toast.makeText(getApplicationContext(),"Error inserting printer",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    List discounts_params=new ArrayList();
                                    discounts_params.add(new BasicNameValuePair("user_id",jobj.getString("user_id")));
                                    JSONObject discounts_object=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+
                                            PackageConfig.discounts_load,"GET",discounts_params);

                                    try{
                                        int discounts_success=discounts_object.getInt("success");
                                        if(discounts_success==1){
                                            JSONArray discounts_array=discounts_object.getJSONArray("data");
                                            for(int k=0;k<discounts_array.length();k++){

                                                JSONObject discounts_obj=discounts_array.getJSONObject(k);
                                                if(!discountsdb.InsertDiscount(discounts_obj.getString("id"),
                                                        discounts_obj.getString("discount_name"),
                                                        discounts_obj.getString("discount_value"))){
                                                    Toast.makeText(getApplicationContext(),"Error inserting discounts",Toast.LENGTH_SHORT).show();
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


                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pdialog.cancel();
            if(success==1){

                taxesdb.getTaxes();
                discountsdb.getDiscounts();


                try{
                    Users user_db=new Users(getApplicationContext(), defaults.database_name,null,1);
                    if(!user_db.insertUserData(PackageConfig.login_data)){
                        Toast.makeText(getApplicationContext(),"data not inserted",Toast.LENGTH_SHORT).show();
                    }else{
                        if(!user_db.insert_branch(PackageConfig.branch_id,PackageConfig.branch_name,business_name)){
                            Toast.makeText(getApplicationContext(),"branch data not inserted",Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

             //   Toast.makeText(getApplicationContext(),serverMessage,Toast.LENGTH_SHORT).show();
                pinPOpUp();
            }else{
                Toast.makeText(getApplicationContext(),serverMessage,Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void pinPOpUp(){
        View view= LayoutInflater.from(this).inflate(R.layout.pin_setter,null);
        TextView textView=view.findViewById(R.id.name);
        final TextView wrong_pin=view.findViewById(R.id.wrong_pin);
        final EditText new_pin=view.findViewById(R.id.new_pin);
        final EditText confirm_pin=view.findViewById(R.id.confirm_pin);
        final Button btn_pin=view.findViewById(R.id.btn_pin);
        final RelativeLayout lin=view.findViewById(R.id.lin);
        textView.setText(PackageConfig.login_data[1]);
        final AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        alertDialog.setView(view);
        alertDialog.setCancelable(false);

        btn_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(new_pin.getText())||TextUtils.isEmpty(confirm_pin.getText())){
                    Toast.makeText(getApplicationContext(), "Missing pin information", Toast.LENGTH_SHORT).show();
                }else{
                    if(new_pin.getText().toString().equals(confirm_pin.getText().toString())){
                        // Toast.makeText(getApplicationContext(),"password macth",Toast.LENGTH_SHORT).show();
                        try {
                            if(users_db.insertPin(new_pin.getText().toString(),PackageConfig.login_data[3])){

                                Toast.makeText(getApplicationContext(),"pin set successfully",Toast.LENGTH_SHORT).show();
                                alertDialog.cancel();
                                new firstDataLoad().execute();
                            }else{
                                Toast.makeText(getApplicationContext(),"error in setting pin",Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        //     lin.setBackgroundColor();
                        wrong_pin.setText("PINS DONT MATCH!!");
                    }
                }


            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();

    }


    public class firstDataLoad extends AsyncTask<String,String,String>{

        ProgressDialog dialog=new ProgressDialog(CashierLogin.this);

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
            paramters.add(new BasicNameValuePair("user_id",users_db.get_user_id()));

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
                    params.add(new BasicNameValuePair("user_id",users_db.get_user_id()));
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
                            JSONObject UpdateSyncStatus=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+
                                            com.mobipos.app.Cashier.PackageConfig.sync_product_movement,
                                    "GET",params_a);

                            try {
                                int state=UpdateSyncStatus.getInt("success");
                                if(state==1){
                                    Log.d("product sync meso",UpdateSyncStatus.getString("message"));
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }




            }catch (Exception e){

            }

            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);
            startActivity(new Intent(CashierLogin.this,DashboardCashier.class));
            dialog.cancel();
        }
    }




}
