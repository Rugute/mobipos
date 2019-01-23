package com.mobipos.app.Cashier.dashboardFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Cashier.dashboardFragments.Account.CashierData;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Sales;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;
import com.mobipos.app.login.PinLogin;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 1/31/18.
 */

public class CashierAccount extends Fragment {
    CardView updateinfo,reset;
    TextView name,phone,email,cash_id;
    Sales salesdb;
    Users users;

    String phoneNumber,emailAdress;

    public static CashierAccount newInstance(){
        CashierAccount fragment= new CashierAccount();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.cashier_account,container,false);
    }
    public void onViewCreated(View view, Bundle savedInstanceState){
        users=new Users(getActivity(), defaults.database_name,null,1);
        updateinfo=(CardView)view.findViewById(R.id.update_info_cv);
        reset=(CardView)view.findViewById(R.id.resetcv);
        name=(TextView) view.findViewById(R.id.nameOfPerson_cashier);
        phone=(TextView) view.findViewById(R.id.cashier_phone);
        email=(TextView) view.findViewById(R.id.cashier_email);
        cash_id=(TextView) view.findViewById(R.id.user_id);

        name.setText(users.getCashierData().get(0).name);
        email.setText(users.getCashierData().get(0).email);
        phone.setText(users.getCashierData().get(0).phone);
        cash_id.setText(users.getCashierData().get(0).code);






        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pinset();
            }
        });

        updateinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoUpdate();
            }
        });

    }

    public static String string_pin;

    public void Pinset (){
        final AlertDialog dialog= new AlertDialog.Builder(getContext()).create();
        View view=LayoutInflater.from(getContext()).inflate(R.layout.reset_pin_popup,null);

     final   EditText current_pin=view.findViewById(R.id.cur_pin_reset);
     final   EditText new_pin=view.findViewById(R.id.new_pin_reset);
     final   EditText confirm_pin=view.findViewById(R.id.confirm_pin_reset);

     final   Button reset_pin=view.findViewById(R.id.reset_action);

        reset_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(new_pin.getText())||!TextUtils.isEmpty(confirm_pin.getText())){
                    if(TextUtils.equals(new_pin.getText(),confirm_pin.getText())){
                        if(!users.insertPin(new_pin.getText().toString(),users.get_user_id("cashier"))){
                            Toast.makeText(getContext(),"Error while resetting pin!!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(),"Pin reset successful",Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }

                    }else{
                        Toast.makeText(getContext(),"Pins dont match!!",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(),"missing some information",Toast.LENGTH_SHORT).show();
                }

            }
        });

        new_pin.setEnabled(false);
        confirm_pin.setEnabled(false);
        reset_pin.setEnabled(false);



        current_pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                string_pin=current_pin.getText().toString();
                if(string_pin.length()==4){

                     try {
                        if(users.password_match(string_pin)){

                            new_pin.setEnabled(true);
                            confirm_pin.setEnabled(true);

                            reset_pin.setEnabled(true);


                        }else {
                            Toast.makeText(getContext(), "Wrong Pin", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });




        dialog.setView(view);
        dialog.show();
    }

    public void InfoUpdate (){
        final AlertDialog dialog= new AlertDialog.Builder(getContext()).create();
        View view=LayoutInflater.from(getContext()).inflate(R.layout.cashier_update_info,null);

        List<CashierData> data = users.getCashierData();
        EditText ed_phone = view.findViewById(R.id.new_phone_number);
        EditText ed_email = view.findViewById(R.id.new_email);
        Button btn_update = view.findViewById(R.id.update_action);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = ed_phone.getText().toString();
                emailAdress=ed_email.getText().toString();
                new updateContactInformation().execute();
            }
        });
        ed_email.setText(data.get(0).email);
        ed_phone.setText(data.get(0).phone);

        dialog.setView(view);
        dialog.show();
    }


    public class updateContactInformation extends AsyncTask<String,String,String>{

        int success;
        String message;
        ProgressDialog dialog=new ProgressDialog(getContext());
        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.setMessage("Updating your information. Please wait...");
            dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("phone",phoneNumber));
            params.add(new BasicNameValuePair("email",emailAdress));
            params.add(new BasicNameValuePair("user_id",users.get_user_id("cashier")));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+
                            AppConfig.hostname+
                            PackageConfig.update_info,
                    "GET",params);


            try {
                success=jsonObject.getInt("success");
                message = jsonObject.getString("message");
            }catch (NullPointerException e){
                success=400;
            } catch (JSONException e) {
                success=400;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            dialog.dismiss();
            if(success==1){
                users.update_casher_info(phoneNumber,emailAdress,Integer.parseInt(users.get_user_id("cashier")));

            }
            Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
        }
    }

}
