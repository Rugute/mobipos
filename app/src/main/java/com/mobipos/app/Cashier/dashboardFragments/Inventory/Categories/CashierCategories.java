package com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mobipos.app.Cashier.Adapters.CashierCategRvAdapter;
import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 12/15/2017.
 */

public class CashierCategories extends Fragment {

    private Object item;
    private List<CashierCategoryData> categoryData;
    private RecyclerView rv;
    Categories categories;
    Users users;
    Context context;

    private ProgressBar bar;
    public static CashierCategories newInstance(){
        CashierCategories fragment = new CashierCategories();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.cashier_category, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(View view,Bundle savedInstanceState){

        categories=new Categories(getActivity(), defaults.database_name,null,1);
        users=new Users(getActivity(), defaults.database_name,null,1);
        bar=view.findViewById(R.id.cashier_category_bar);
        rv=view.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        RelativeLayout rela=(RelativeLayout)view.findViewById(R.id.no_cashier_category_layout);

        if(categories.getCategories().size()==0){
            rela.setVisibility(View.VISIBLE);
        }

      //  initializeData();
        showBackButton(true,"Categories");
      //  initializeAdapter();

        final CheckInternetSettings internetOn=new CheckInternetSettings(getActivity());
        if(internetOn.isNetworkConnected()){
            new loadCategories().execute();
        }else{
            if(categories.getCategoryCount()>0){
                initializeAdapter();
            }else{
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getActivity()).
                        setTitle("Categories not found").
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

    private void initializeData(){
        categoryData= new ArrayList<>();
        categoryData.add(new CashierCategoryData("Fruits", "Mango", R.mipmap.ic_launcher));
        categoryData.add(new CashierCategoryData("Beverage", "Coffee", R.mipmap.ic_launcher));
        categoryData.add(new CashierCategoryData("Meat", "Beef", R.mipmap.ic_launcher));
    }

    private void initializeAdapter(){
        CashierCategRvAdapter adapter = new CashierCategRvAdapter(categories.getCategories());
        rv.setAdapter(adapter);
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


    public class loadCategories extends AsyncTask<String,String,String>{

        int success=0;
        String serverMessage;
        JSONArray categoryArray;


        protected void onPreExecute(){
            super.onPreExecute();
            showBar(true);
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
            showBar(false);

            if (success==1){
               if(categories.getCategoryCount()==0){
                   for(int i=0;i<PackageConfig.categoryArrayId.length;i++){
                      if(!categories.insertCategory(PackageConfig.categoryArrayId[i],PackageConfig.categoryArrayName[i])) {
                          Toast.makeText(getActivity(),"data not inserted",Toast.LENGTH_SHORT).show();

                      }
                   }
               }else{
                   //check if category exists
                   for(int i=0;i<PackageConfig.categoryArrayId.length;i++){
                       if(!categories.CategoryExists(PackageConfig.categoryArrayId[i])){
                          if(!categories.insertCategory(PackageConfig.categoryArrayId[i],PackageConfig.categoryArrayName[i])){
                              Toast.makeText(getActivity(),"data not inserted",Toast.LENGTH_SHORT).show();
                          }
                       }
                   }

               }

                CashierCategRvAdapter adapter = new CashierCategRvAdapter(categories.getCategories());
                rv.setAdapter(adapter);
                Toast.makeText(getActivity(),serverMessage,Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(),String.valueOf(categories.getCategoryCount()),Toast.LENGTH_SHORT).show();
            }else{
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


}
