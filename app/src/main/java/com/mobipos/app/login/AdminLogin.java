package com.mobipos.app.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 12/6/2017.
 */

public class AdminLogin extends Activity {

    TextView textLink,login;
    EditText email,password;
    static String stremail,strpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login=findViewById(R.id.login);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);

        Users user_db=new Users(getApplicationContext(),defaults.database_name,null,1);
        if (user_db.CheckUserOrPin(Users.tb_name)>0){
            email.setText(user_db.get_login_details()[0]);
            password.setText(user_db.get_login_details()[1]);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stremail=email.getText().toString();
                strpassword=password.getText().toString();

                if (stremail == null || strpassword == null){
                    Toast.makeText(getApplicationContext(),"fill all boxes",Toast.LENGTH_SHORT).show();
                }else{
                    new loginProcessor().execute();
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
        alertDialog.setView(view);
                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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

        protected void onPreExecute() {
            super.onPreExecute();
            dialog(true);

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

                    PackageConfig.login_data=new String[6];
                    PackageConfig.login_data[0]=jobj.getString("user_id");
                    PackageConfig.login_data[1]=jobj.getString("username");
                    PackageConfig.login_data[2]=jobj.getString("email");
                    PackageConfig.login_data[3]=jobj.getString("password");
                    PackageConfig.login_data[4]="1";
                    PackageConfig.login_data[5]="admin";


                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog(false);
            if(success==1){

                try{
                    Users user_db=new Users(getApplicationContext(), defaults.database_name,null,1);
                   if(!user_db.insertUserData(PackageConfig.login_data)){
                       Toast.makeText(getApplicationContext(),"data not inserted",Toast.LENGTH_SHORT).show();
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

    public void dialog(boolean state){
        ProgressDialog dialog=new ProgressDialog(this);
        if(state){
            dialog.setMessage("Loading.please wait...");
            dialog.setCancelable(false);

        }else {
            dialog.cancel();
        }

    }
}
