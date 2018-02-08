package com.mobipos.app.Admin.DashboardFragments.Inventory.StockAlert;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.StockAlertRvAdapter;
import com.mobipos.app.Admin.AdminStockAlertData;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;
import com.mobipos.app.login.PackageConfig;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/8/17.
 */

public class StockAlertFragment extends Fragment {
    public static StockAlertFragment newInstance(){
        StockAlertFragment fragment= new StockAlertFragment();
        return fragment;
    }

    Users usersdb;
    List<AdminStockAlertData> stockAlertData;
    RecyclerView rv;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_alert_fragment, container, false);
    }

    public void onViewCreated(View view,Bundle savedInstanceState){
        usersdb=new Users(getContext(), defaults.database_name,null,1);
        rv = view.findViewById(R.id.stock_alert_rv);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        new loadData().execute();
    }

    private void initializeAdapter(List<AdminStockAlertData> data){
        StockAlertRvAdapter adapter = new StockAlertRvAdapter(data);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }

    public class loadData extends AsyncTask<String,String,String>{

        int success;
        protected void onPreExecute(){
            super.onPreExecute();

         //   dialog(true);
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            stockAlertData=new ArrayList<>();
            List parameters=new ArrayList();

            parameters.add(new BasicNameValuePair("user_id",usersdb.get_user_id()));

            JSONObject jsonObject=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+
                            com.mobipos.app.Admin.PackageConfig.get_low_stock,
                    "GET",parameters);

            try{
                success=jsonObject.getInt("success");
                JSONArray jsonArray=jsonObject.getJSONArray("data");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jObj=jsonArray.getJSONObject(i);
                    stockAlertData.add(new AdminStockAlertData(jObj.getString("product_id"),
                            jObj.getString("product_name"),
                            jObj.getString("category_name"),
                            jObj.getString("message"),
                            jObj.getString("alert_type"),
                            jObj.getString("remainder")));

                }


            }catch (Exception e){

            }
            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);

            if(success==1){
                initializeAdapter(stockAlertData);
            }else{
                Toast.makeText(getActivity(),"error while loading data",Toast.LENGTH_SHORT).show();
            }
        //    dialog(false);
        }
    }
    public void dialog(boolean state){
        ProgressDialog dialog=new ProgressDialog(getActivity());
        if(state){
            dialog.setMessage("Loading.please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }else {
            dialog.cancel();
        }

    }

}
