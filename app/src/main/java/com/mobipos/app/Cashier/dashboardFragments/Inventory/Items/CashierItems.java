package com.mobipos.app.Cashier.dashboardFragments.Inventory.Items;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobipos.app.Cashier.Adapters.CashierItemRvAdapter;
import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Product_Prices;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;
import com.mobipos.app.database.Inventory;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 12/18/2017.
 */

public class CashierItems extends Fragment {

    private RecyclerView rv;
   private Spinner spinner;
   ProgressBar bar;
   Users users;
   Products productsdb;
   Product_Prices pricesdb;
   Categories categorydb;
   Inventory inventorydb;


    public static CashierItems newInstance(){
        CashierItems fragment = new CashierItems();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.cashier_items, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(View view,Bundle savedInstanceState){

        users=new Users(getActivity(), defaults.database_name,null,1);
        productsdb=new Products(getActivity(), defaults.database_name,null,1);
        pricesdb=new Product_Prices(getActivity(), defaults.database_name,null,1);
        categorydb=new Categories(getActivity(), defaults.database_name,null,1);
        inventorydb=new Inventory(getActivity(), defaults.database_name,null,1);


        rv=view.findViewById(R.id.cashier_items_rv);
        spinner=view.findViewById(R.id.category_spinner);
        bar=view.findViewById(R.id.progessbar_items);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        spinnerUpdate();

        showBackButton(true,"Products");

        final CheckInternetSettings internetOn=new CheckInternetSettings(getActivity());
        if(internetOn.isNetworkConnected()){
            new loadItems().execute();
        }else{
            if(productsdb.getProductCount("all")>0){
                initializeAdapter("all");
            }else{
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getActivity()).
                        setTitle("Items not found").
                        setMessage("Enable your internet to sync data from server").
                        setPositiveButton((CharSequence) "Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                internetOn.context.startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
                            }
                        });
                alertBuilder.show();
            }
        }

    }

    private void initializeAdapter(String group){
        CashierItemRvAdapter adapter = new CashierItemRvAdapter(productsdb.getProducts(group));
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);

    }

    public class loadItems extends AsyncTask<String,String,String>{

        int success=0;
        String serverMessage;
        JSONArray dataitems;
        protected void onPreExecute(){
            super.onPreExecute();
            showBar(true);
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
            showBar(false);

            if(success==1){

                if(productsdb.getProductCount("all")==0){
                    for(int i=0;i<dataitems.length();i++){
                       if(!productsdb.insertProduct(PackageConfig.itemArrayId[i],PackageConfig.itemArrayName[i],
                               PackageConfig.categoryArrayId[i],PackageConfig.itemArrayMeasurement[i])){
                           Toast.makeText(getActivity(),"product not inserted",Toast.LENGTH_SHORT).show();
                       }else{
                           if(!pricesdb.insertPrices(PackageConfig.price_id[i],PackageConfig.itemArrayId[i],
                                   PackageConfig.price[i])){
                               Toast.makeText(getActivity(),"price not inserted",Toast.LENGTH_SHORT).show();
                           }else{
                               if(!inventorydb.insertStock(PackageConfig.itemArrayId[i],PackageConfig.stockData[i])){
                                   Toast.makeText(getActivity(),"inventory not inserted",Toast.LENGTH_SHORT).show();
                               }
                           }
                       }
                    }


                }else{
                    for(int i=0;i<dataitems.length();i++){

                        if(!productsdb.ProductExists(PackageConfig.itemArrayId[i])){
                            if(!productsdb.insertProduct(PackageConfig.itemArrayId[i],PackageConfig.itemArrayName[i],
                                    PackageConfig.categoryArrayId[i],PackageConfig.itemArrayMeasurement[i])){
                                Toast.makeText(getActivity(),"product not inserted",Toast.LENGTH_SHORT).show();
                            }else{
                                if(!pricesdb.insertPrices(PackageConfig.price_id[i],PackageConfig.itemArrayId[i],
                                        PackageConfig.price[i])){
                                    Toast.makeText(getActivity(),"price not inserted",Toast.LENGTH_SHORT).show();
                                }else{
                                    if(!inventorydb.insertStock(PackageConfig.itemArrayId[i],PackageConfig.stockData[i])){
                                        Toast.makeText(getActivity(),"inventory not inserted",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }

                    }
                }
                CashierItemRvAdapter adapter=new CashierItemRvAdapter(productsdb.getProducts("all"));
                rv.setAdapter(adapter);

                Toast.makeText(getActivity(),serverMessage,Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(),String.valueOf(productsdb.getProductCount("all")),Toast.LENGTH_SHORT).show();
            }else if(success==0){
                Toast.makeText(getActivity(),serverMessage,Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void showBar(boolean state){
        if (state){
            bar.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }else{
            bar.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
    }


    public void showBackButton(Boolean state,String title) {
        if(state){
            if (getActivity() instanceof DashboardCashier) {
                try {
                    ((DashboardCashier) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getActivity().setTitle(title);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public void spinnerUpdate(){
      final ArrayAdapter<String> adapter;
      adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,categorydb.getSpinnerData());

      adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
      spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String category_name=(String)adapterView.getItemAtPosition(i);

                initializeAdapter(categorydb.categoryId(category_name));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


}
