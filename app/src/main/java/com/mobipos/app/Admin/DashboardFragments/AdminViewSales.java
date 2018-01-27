package com.mobipos.app.Admin.DashboardFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;


import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.JSONParser;

import com.mobipos.app.R;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;



import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 1/17/18.
 */

public class AdminViewSales extends Fragment {

    TextView dailysales,dailyinventory,dailyprofit;
    Spinner spin;
    CardView card_profit,card_sales,card_inventory;
    Users users;

    public static AdminViewSales newInstance() {
        AdminViewSales fragment = new AdminViewSales();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.admin_sales_fragment,container,false);

    }
    public void onViewCreated(View view,Bundle savedInstanceState){
        users=new Users(getContext(),defaults.database_name,null,1);
        dailysales=(TextView)view.findViewById(R.id.d_sales);
        dailyinventory=(TextView)view.findViewById(R.id.d_inventory);
        dailyprofit=(TextView)view.findViewById(R.id.d_profit);
        spin=(Spinner)view.findViewById(R.id.sales_spinner_admin);

       final CheckInternetSettings internet=new CheckInternetSettings(getActivity());
        if(internet.isNetworkConnected()){
            new ViewSale().execute();
        }else{
            AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getActivity()).
                    setTitle("Cannot Sync Data").
                    setMessage("Enable your internet to Synce Sales").
                    setPositiveButton((CharSequence) "Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            internet.context.startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
                        }
                    });
            alertBuilder.show();
        }



    }
    public class ViewSale extends AsyncTask<String,String,String>{
        int success=0;
        String serverMessage;
        JSONArray sale;
        String total_sales_value=null;
        String total_inventory_value=null;


        protected  void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            paramters.add(new BasicNameValuePair("user_id",users.get_user_id()));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+ AppConfig.get_today_sales,
                    "GET",paramters);
            Log.d("data recieved",jsonObject.toString());

            try {
                success=jsonObject.getInt("success");
                total_sales_value=jsonObject.getString("total_today_sale");
                total_inventory_value=jsonObject.getString("total_today_inventory");
            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //Toast.makeText(getContext(),"yoh",Toast.LENGTH_SHORT).show();

            dailysales.setText(total_sales_value);
            dailyinventory.setText(total_inventory_value);
            dailyprofit.setText(String.valueOf(Integer.parseInt(total_sales_value)-Integer.parseInt(total_inventory_value)));
        }
    }



    }
