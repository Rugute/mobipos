package com.mobipos.app.Admin.DashboardFragments.Inventory.Items;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.PackageConfig;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 9/6/2018.
 */

public class UpdatePrice {

    Activity activity;
    String buying;
    String selling;
    String product_id;
    Context context;

    String up_buying,up_selling;

    public UpdatePrice(Activity activity, String buying, String selling,String product_id) {
        this.activity = activity;
        this.buying = buying;
        this.selling = selling;
        this.product_id=product_id;
    }

    public void showDialog(){
        AlertDialog alertDialog;
        alertDialog=new AlertDialog.Builder(activity).create();
        View view= LayoutInflater.from(activity).inflate(R.layout.update_price,null);

        EditText ed_buy= view.findViewById(R.id.upd_buying_price);
        EditText ed_sell= view.findViewById(R.id.upd_selling_price);
        Button btn_update= view.findViewById(R.id.btn_update_price);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buying = ed_buy.getText().toString();
                selling=ed_sell.getText().toString();
                new update_price().execute();
                alertDialog.dismiss();
            }
        });

       ed_buy.setText(buying);
       ed_sell.setText(selling);


        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    public class update_price extends AsyncTask<String,String,String>{

        ProgressDialog dialog=new ProgressDialog(activity);
        int success;
        String serverMessage;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            dialog.setMessage("Updating prices.please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {

            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            paramters.add(new BasicNameValuePair("product_id",product_id));
            paramters.add(new BasicNameValuePair("buying",buying));
            paramters.add(new BasicNameValuePair("selling",selling));


            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+
                            PackageConfig.update_product_price,
                    "GET",paramters);


            try {
                success=jsonObject.getInt("success");
                serverMessage=jsonObject.getString("message");
            }catch (Exception e){
                success=500;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            dialog.dismiss();

            if(success==1){
                
            }
        }
    }
}
