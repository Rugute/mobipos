package com.mobipos.app.Admin;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.DiscountData;
import com.mobipos.app.Admin.Adapters.DiscountsAdapter;
import com.mobipos.app.Admin.Adapters.MeasureData;
import com.mobipos.app.Admin.Adapters.MeasurementAdapter;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 3/1/18.
 */

public class AdminDiscounts extends Fragment {

    ListView listView;
    Users users;
    FloatingActionButton fabdiscount;
    List<DiscountData> discountDataList;

    public static String newdiscount="";
    public static String newdiscountvalue="";

    public static AdminDiscounts newInstance(){
        AdminDiscounts fragment=new AdminDiscounts();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_discount_setting, container, false);
    }
    public void onViewCreated(View view,Bundle savedInstanceState){
        users = new Users(getContext(), defaults.database_name, null, 1);
        fabdiscount=view.findViewById(R.id.fab_add_discount);
        listView=view.findViewById(R.id.discount_listview);

        new DiscountSelection().execute();

        fabdiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDiscount();

            }
        });

    }

    public class DiscountSelection extends AsyncTask<String, String, String> {
        int success = 0;
        String serverMessage;
        JSONArray discountArray;
        String outlet = null;
        ProgressDialog dialog=new ProgressDialog(getActivity());

        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Loading data.please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            discountDataList=new ArrayList<>();
            JSONParser jsonParser = new JSONParser();
            List paramters = new ArrayList();

            paramters.add(new BasicNameValuePair("user_id", users.get_user_id("admin")));

            JSONObject jsonObject = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.admin_get_discounts,
                    "GET", paramters);
            Log.d("data recieved", jsonObject.toString());

            try {
                success = jsonObject.getInt("success");
                discountArray=jsonObject.getJSONArray("data");
                for(int i=0;i<discountArray.length();i++) {
                    JSONObject jObj = discountArray.getJSONObject(i);
                    discountDataList.add(new DiscountData(jObj.getString("id"), jObj.getString("discount_name"),
                            jObj.getString("discount_value")+"%"));
                }
                //discountDataList.add(new DiscountData("", "",""+"%" ));
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (success == 1) {
                if (discountDataList.size() > 0) {
                    listView.setAdapter(new DiscountsAdapter(getContext(),discountDataList));
                } else {
                    Toast.makeText(getActivity(), "No Discount Available", Toast.LENGTH_SHORT).show();
                }

            }
            dialog.cancel();
        }
    }

    public void AddDiscount() {
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.discount_add_popup, null);
        Button addme=view.findViewById(R.id.btn_add_discount);
        final EditText newdiscountitem=view.findViewById(R.id.new_discount);
        final EditText newdiscountvalueitem=view.findViewById(R.id.new_discount_unit);

        addme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newdiscount=newdiscountitem.getText().toString();
                newdiscountvalue=newdiscountvalueitem.getText().toString();
                if(!TextUtils.isEmpty(newdiscountitem.getText())&&!TextUtils.isEmpty(newdiscountvalueitem.getText())){
                    new DiscountAddition().execute();
                    dialog.cancel();
                }else {
                    Toast.makeText(getContext(),"Please input all details",Toast.LENGTH_SHORT).show();

                }
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    public class  DiscountAddition extends AsyncTask<String, String, String> {
        int successState = 0;
        String serverMessage;
        JSONArray branch;
        String outlet = null;

        String TAG_MESSAGE="message";
        String TAG_SUCCESS="success";
        String USER_ID=null;


        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("name",newdiscount));
            jsonObjectData.add(new BasicNameValuePair("value",newdiscountvalue));
            jsonObjectData.add(new BasicNameValuePair("user_id",users.get_user_id("admin")));

            JSONObject jsonObjectResponse = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.admin_add_discounts,
                    "GET", jsonObjectData);

            Log.d("result",jsonObjectResponse.toString());

            try {



                int success=jsonObjectResponse.getInt(TAG_SUCCESS);
                serverMessage=jsonObjectResponse.getString(TAG_MESSAGE);


                if(success==1){
                    successState=1;

                }
            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(successState==1){
                Toast.makeText(getActivity(),serverMessage,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(),"server error",Toast.LENGTH_SHORT).show();
            }


            new DiscountSelection().execute();
        }
    }
}
