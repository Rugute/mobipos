package com.mobipos.app.Admin.DashboardFragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.JSONParser;

import com.mobipos.app.R;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;



import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 1/17/18.
 */

public class AdminViewSales extends Fragment {

    TextView dailysales,dailyinventory,dailyprofit;
    Spinner spin;
    CardView card_profit,card_sales,card_inventory;
    Users users;
    Button other_sales_information;
    Calendar calendar=Calendar.getInstance();

    static String selected_date;

    static String[] shop_ids,shop_names;

    List<ViewSalesInterface> shopDetails;
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
        other_sales_information=view.findViewById(R.id.new_sales_date);

        final DatePickerDialog.OnDateSetListener tDate=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
                updateDate();
            }
        } ;

        other_sales_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),tDate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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
        ProgressDialog dialog=new ProgressDialog(getActivity());


        protected  void onPreExecute(){
            super.onPreExecute();

            dialog.setMessage("Loading data.please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            shopDetails=new ArrayList<>();
            paramters.add(new BasicNameValuePair("user_id",users.get_user_id("admin")));
            String today = new Date().toString();

            paramters.add(new BasicNameValuePair("date",today));
            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+ AppConfig.get_today_sales,
                    "GET",paramters);
            Log.d("data recieved",jsonObject.toString());

            try {
                success=jsonObject.getInt("success");
                sale=jsonObject.getJSONArray("shops");

                for(int i=0;i<sale.length();i++){
                    JSONObject object=sale.getJSONObject(i);
                    shopDetails.add(new ViewSalesInterface(object.getString("shop_id"),
                            object.getString("shop_name"),
                            object.getString("total_today_sale"),
                            object.getString("total_today_inventory")));
                }

            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(success==1){
                dialog.cancel();
                spinnerUpdate();
            }else{
                Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void spinnerUpdate(){
       shop_ids=new String[shopDetails.size()];
        shop_names=new String[shopDetails.size()];

        for(int i=0;i<shopDetails.size();i++){
            shop_ids[i]=shopDetails.get(i).shop_id;
            shop_names[i]=shopDetails.get(i).shop_name;
        }

        ArrayAdapter adapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,shop_names);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos= (int) adapterView.getItemIdAtPosition(i);
                branchFilter(shop_ids[pos]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void branchFilter(String id){
        List<ViewSalesInterface> data=new ArrayList<>();

        for(int i=0;i<shopDetails.size();i++){
            String idOfShop=shopDetails.get(i).shop_id;
            if(idOfShop.equals(id)){
                data.add(new ViewSalesInterface(shopDetails.get(i).shop_id,
                        shopDetails.get(i).shop_name,shopDetails.get(i).shop_sales,shopDetails.get(i).shop_inv));
            }

        }

        dailysales.setText(data.get(0).shop_sales);
        dailyinventory.setText(data.get(0).shop_inv);
        dailyprofit.setText(String.valueOf(Integer.parseInt(data.get(0).shop_sales)-Integer.parseInt(data.get(0).shop_inv)));
    }

    public void updateDate(){
        String format="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(format, Locale.US);
        selected_date=dateFormat.format(calendar.getTime());

        new viewPreviousSales().execute();
    }

    public class viewPreviousSales extends AsyncTask<String,String,String>{
        int success=0;
        String serverMessage;
        JSONArray sale;
        String total_sales_value=null;
        String total_inventory_value=null;
        ProgressDialog dialog=new ProgressDialog(getActivity());


        protected  void onPreExecute(){
            super.onPreExecute();

            dialog.setMessage("Loading data.please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            shopDetails=new ArrayList<>();
            paramters.add(new BasicNameValuePair("user_id",users.get_user_id("admin")));
            paramters.add(new BasicNameValuePair("date",selected_date));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+ AppConfig.get_other_date_sales,
                    "GET",paramters);
            Log.d("data recieved",jsonObject.toString());

            try {
                success=jsonObject.getInt("success");
                sale=jsonObject.getJSONArray("shops");

                for(int i=0;i<sale.length();i++){
                    JSONObject object=sale.getJSONObject(i);
                    shopDetails.add(new ViewSalesInterface(object.getString("shop_id"),
                            object.getString("shop_name"),
                            object.getString("total_today_sale"),
                            object.getString("total_today_inventory")));
                }

            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(success==1){
                dialog.cancel();
                spinnerUpdate();
            }else{
                Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
            }

        }
    }

}
