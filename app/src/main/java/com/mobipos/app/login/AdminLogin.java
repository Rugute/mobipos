package com.mobipos.app.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.AdminProdRvAdapter;
import com.mobipos.app.Admin.DashboardAdmin;
import com.mobipos.app.Cashier.DashboardCashier;
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
 * Created by folio on 12/6/2017.
 */

public class AdminLogin extends Activity {

    TextView textLink;
    Button btn_login;
    EditText email,password;
    static String stremail,strpassword;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btn_login=findViewById(R.id.login);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);

        VectorDrawableCompat drawableCompat= VectorDrawableCompat.create(getResources(), R.drawable.ic_account_circle, email.getContext().getTheme());
        email.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableCompat, null, null, null);

        VectorDrawableCompat drawableCompat1= VectorDrawableCompat.create(getResources(), R.drawable.ic_vpn_key_black_24dp, password.getContext().getTheme());
        password.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableCompat1, null, null, null);

        Users user_db=new Users(getApplicationContext(),defaults.database_name,null,1);
        if (user_db.CheckUserOrPin(Users.tb_name)>0){
            email.setText(user_db.get_login_details()[0]);
            password.setText(user_db.get_login_details()[1]);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stremail=email.getText().toString();
                strpassword=password.getText().toString();

                if (stremail == null || strpassword == null){
                    Toast.makeText(getApplicationContext(),"fill all details",Toast.LENGTH_SHORT).show();
                }else{

                    final CheckInternetSettings internetOn=new CheckInternetSettings(AdminLogin.this);
                    if(internetOn.isNetworkConnected()){
                        new loginProcessor().execute();
                    }else{
                        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(AdminLogin.this).
                                setTitle("Internet Connectivity is out").
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
        });

        textLink=findViewById(R.id.link);
        textLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customPopUp();
            }
        });
    }

    public void customPopUp(){
       View view=LayoutInflater.from(this).inflate(R.layout.forgot_password,null);
        AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        final EditText editText=view.findViewById(R.id.email_add);
        alertDialog.setView(view);
                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        forgot_email=editText.getText().toString();
                        new forgotPassword().execute();
                    }
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();

    }

    public class loginProcessor extends AsyncTask<String,String,String>{

        int success;
        String serverMessage;
        JSONArray data;
        ProgressDialog pdialog=new ProgressDialog(AdminLogin.this);
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog.setMessage("Authenticating.please wait...");
            pdialog.setCancelable(false);
            pdialog.show();

        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramaters = new ArrayList();
            paramaters.add(new BasicNameValuePair("email",stremail));
            paramaters.add(new BasicNameValuePair("password",strpassword));

            JSONObject jsonObject=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+PackageConfig.admin_log,
                    "GET",paramaters);

            Log.d("json object",jsonObject.toString());
            try {
                success=jsonObject.getInt("success");
                serverMessage=jsonObject.getString("message");

                if(success==1){
                    data=jsonObject.getJSONArray("result");

                    JSONObject jobj=data.getJSONObject(0);

                    PackageConfig.login_data=new String[7];
                    PackageConfig.login_data[0]=jobj.getString("user_id");
                    PackageConfig.login_data[1]=jobj.getString("username");
                    PackageConfig.login_data[2]=jobj.getString("email");
                    PackageConfig.login_data[3]=jobj.getString("password");
                    PackageConfig.login_data[4]="1";
                    PackageConfig.login_data[5]="admin";
                    PackageConfig.login_data[6]="n/a";



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

                try{
                    Users user_db=new Users(getApplicationContext(), defaults.database_name,null,1);
                   if(!user_db.insertUserData(PackageConfig.login_data)){
                       Toast.makeText(getApplicationContext(),"data not inserted",Toast.LENGTH_SHORT).show();
                   }else{
                       startActivity(new Intent(AdminLogin.this,DashboardAdmin.class));
                   }


                }catch (Exception e){
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(),serverMessage,Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(getApplicationContext(),serverMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void admin_sale_popup(){
        View view= LayoutInflater.from(AdminLogin.this).inflate(R.layout.admin_login_switcher,null);
        Button admin_btn=view.findViewById(R.id.btn_admin_login);
        Button cashier_btn=view.findViewById(R.id.btn_cashier_login);

        admin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminLogin.this,DashboardAdmin.class));
            }
        });

        cashier_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminLogin.this,DashboardCashier.class));
            }
        });

        final AlertDialog alertDialog=new AlertDialog.Builder(AdminLogin.this).create();

        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    public void dialog(boolean state){
        ProgressDialog dialog=new ProgressDialog(this);
        if(state){
            dialog.setMessage("Loading.please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }else {
            dialog.cancel();
        }

    }
    static String forgot_email;
    public class forgotPassword extends  AsyncTask<String,String,String>{

        int success;
        String server_message;
        protected void onPreExecute(){
            super.onPreExecute();

            Toast.makeText(getApplicationContext(),"sending activation link. please wait",Toast.LENGTH_SHORT);
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List parameters=new ArrayList();
            parameters.add(new BasicNameValuePair("email",forgot_email));

            JSONObject jsonObject=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.admin_hostname+
                            PackageConfig.forgot_password,"GET",parameters);

            try{
                success=jsonObject.getInt("success");
                server_message=jsonObject.getString("message");
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);

            AlertDialog.Builder alert=new AlertDialog.Builder(AdminLogin.this).setTitle("Account Recovery")
                    .setMessage(server_message)
                    .setCancelable(true);
            alert.show();

        }
    }
}
