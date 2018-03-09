package com.mobipos.app.Defaults;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.mobipos.app.R;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;
import com.mobipos.app.login.AdminLogin;
import com.mobipos.app.login.PackageConfig;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 12/22/2017.
 */

public class RegisterAdmin extends Activity {
    EditText personName,personPhone,personEmail,personPin,confirmPin,bizName,residenceName;
    TextView loginPage;
    Button register;
    private ProgressDialog pDialog;

    public static String name="";
    public static String biz="";
    public static String residence="";
    public static String phone="";
    public static String email="";
    public static String pin="";
    public static String cpin="";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        personName=(EditText) findViewById(R.id.name_reg);
        personPhone=(EditText) findViewById(R.id.phone_reg);
        personEmail=(EditText) findViewById(R.id.email_reg);
        personPin=(EditText) findViewById(R.id.pin_reg);
        confirmPin=(EditText) findViewById(R.id.cpin_reg);
        bizName=(EditText) findViewById(R.id.biz_reg);
        residenceName=(EditText) findViewById(R.id.residence_reg);
        loginPage=(TextView) findViewById(R.id.link_log);

        register=(Button)findViewById(R.id.register_btn);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Registering. Please wait...");

        loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterAdmin.this,AdminLogin.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                name= personName.getText().toString().trim();
                biz = bizName.getText().toString().trim();
                residence = residenceName.getText().toString().trim();
                phone = personPhone.getText().toString().trim();
                email = personEmail.getText().toString().trim();
                pin = personPin.getText().toString().trim();
                cpin=confirmPin.getText().toString().trim();

                if (!name.isEmpty() && !biz.isEmpty() && !residence.isEmpty() && !phone.isEmpty() && !email.isEmpty() && !pin.isEmpty() && !cpin.isEmpty()) {
                    if(pin.equals(cpin)){
                        new RegisterUser().execute();

                    }else {
                        Toast.makeText(getApplicationContext(),"password dont match",Toast.LENGTH_SHORT);
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }

    public class RegisterUser extends AsyncTask<String,String,String> {

        int successState=0;
        String serverMessage="request not sent";

        String TAG_MESSAGE="message";
        String TAG_SUCCESS="success";
        String USER_ID=null;


        protected void onPreExecute(){
            super.onPreExecute();
            showDialog();
        }
        @Override
        protected String doInBackground(String... strings) {

            JSONParser jsonParser=new JSONParser();

            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("name",name));
            jsonObjectData.add(new BasicNameValuePair("biz",biz));
            jsonObjectData.add(new BasicNameValuePair("residence",residence));
            jsonObjectData.add(new BasicNameValuePair("phoneValue",phone));
            jsonObjectData.add(new BasicNameValuePair("email",email));
            jsonObjectData.add(new BasicNameValuePair("password",pin));

            JSONObject jsonObjectResponse=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname_admin+AppConfig.url_register,
                    "GET",jsonObjectData);

            Log.d("result",jsonObjectResponse.toString());

            try {
                JSONArray result=jsonObjectResponse.getJSONArray("resultvalue");
                JSONObject jobj=result.getJSONObject(0);

                int success=jobj.getInt(TAG_SUCCESS);
                serverMessage=jobj.getString(TAG_MESSAGE);
                USER_ID=jobj.getString("UserID");

                if(success==1){
                    successState=1;

                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);
            if(successState==1){

                PackageConfig.login_data=new String[7];
                PackageConfig.login_data[0]=USER_ID;
                PackageConfig.login_data[1]=name;
                PackageConfig.login_data[2]=email;
                PackageConfig.login_data[3]=pin;
                PackageConfig.login_data[4]="1";
                PackageConfig.login_data[5]="admin";
                PackageConfig.login_data[6]="n/a";

                Users user_db=new Users(getApplicationContext(), defaults.database_name,null,1);
                if(!user_db.insertUserData(PackageConfig.login_data)){
                    Toast.makeText(getApplicationContext(),"data not inserted",Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(RegisterAdmin.this,AdminLogin.class));
                    Toast.makeText(getApplicationContext(),serverMessage,Toast.LENGTH_LONG).show();
                }


            }

            else{

                Toast.makeText(getApplicationContext(),serverMessage,Toast.LENGTH_LONG).show();
            }

            hideDialog();
        }
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
