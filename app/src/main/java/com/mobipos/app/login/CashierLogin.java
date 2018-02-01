package com.mobipos.app.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Taxes;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by folio on 12/6/2017.
 */

public class CashierLogin extends Activity {

    public static String check_id;
    EditText ed_check_id;
    TextView check_login;
    Users users_db;
    Taxes taxesdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emlog);

        users_db=new Users(getApplicationContext(),defaults.database_name,null,1);
        taxesdb=new Taxes(getApplicationContext(),defaults.database_name,null,1);
        ed_check_id=findViewById(R.id.emp_id);
        check_login=findViewById(R.id.check_login);

        check_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_id=ed_check_id.getText().toString();
                try{
                    new employeeProcessor().execute();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

             }
        });

    }

    public class employeeProcessor extends AsyncTask<String,String,String> {

        int success,tax_success;
        String serverMessage;
        JSONArray data,tax_data;

        protected void onPreExecute() {
            super.onPreExecute();
            //dialog(true);

        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramaters = new ArrayList();
            paramaters.add(new BasicNameValuePair("employee_id",check_id));


            JSONObject jsonObject=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+
                            PackageConfig.employee_log,
                    "GET",paramaters);

            Log.d("json object",jsonObject.toString());
            try {
                success=jsonObject.getInt("success");
                serverMessage=jsonObject.getString("message");
                PackageConfig.branch_id=jsonObject.getString("branchId");
                PackageConfig.branch_name=jsonObject.getString("branchname");

                if(success==1){
                    data=jsonObject.getJSONArray("result");

                    JSONObject jobj=data.getJSONObject(0);

                    PackageConfig.login_data=new String[6];
                    PackageConfig.login_data[0]=jobj.getString("user_id");
                    PackageConfig.login_data[1]=jobj.getString("username");
                    PackageConfig.login_data[2]=jobj.getString("email");
                    PackageConfig.login_data[3]=jobj.getString("employee_id");
                    PackageConfig.login_data[4]="1";
                    PackageConfig.login_data[5]="cashier";

                    List params=new ArrayList();
                    params.add(new BasicNameValuePair("user_id",jobj.getString("user_id")));

                    JSONObject jobjTaxes=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+
                            PackageConfig.tax_load,"GET",params);


                    Log.d("json object",jsonObject.toString());

                    try{
                        tax_success=jobjTaxes.getInt("success");

                        if(tax_success==1){
                            tax_data=jobjTaxes.getJSONArray("data");

                            for (int i=0;i<tax_data.length();i++){
                                JSONObject j=tax_data.getJSONObject(i);
                                if(!taxesdb.insertTax(j.getString("tb_tax_id"),
                                        j.getString("tax_margin"),j.getString("margin_mode"))){
                                    Toast.makeText(getApplicationContext(),"Error inserting tax",Toast.LENGTH_SHORT).show();
                                }
                            }



                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //dialog(false);
            if(success==1){

                taxesdb.getTaxes();

                try{
                    Users user_db=new Users(getApplicationContext(), defaults.database_name,null,1);
                    if(!user_db.insertUserData(PackageConfig.login_data)){
                        Toast.makeText(getApplicationContext(),"data not inserted",Toast.LENGTH_SHORT).show();
                    }else{
                        if(!user_db.insert_branch(PackageConfig.branch_id,PackageConfig.branch_name)){
                            Toast.makeText(getApplicationContext(),"branch data not inserted",Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

             //   Toast.makeText(getApplicationContext(),serverMessage,Toast.LENGTH_SHORT).show();
                pinPOpUp();
            }else{
                Toast.makeText(getApplicationContext(),serverMessage,Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void pinPOpUp(){
        View view= LayoutInflater.from(this).inflate(R.layout.pin_setter,null);
        TextView textView=view.findViewById(R.id.name);
        final TextView wrong_pin=view.findViewById(R.id.wrong_pin);
        final EditText new_pin=view.findViewById(R.id.new_pin);
        final EditText confirm_pin=view.findViewById(R.id.confirm_pin);
        final Button btn_pin=view.findViewById(R.id.btn_pin);
        final RelativeLayout lin=view.findViewById(R.id.lin);
        textView.setText(PackageConfig.login_data[1]);
        AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        alertDialog.setView(view);
        alertDialog.setCancelable(false);

        btn_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(new_pin.getText().toString().equals(confirm_pin.getText().toString())){
                   // Toast.makeText(getApplicationContext(),"password macth",Toast.LENGTH_SHORT).show();
                    try {
                        if(users_db.insertPin(new_pin.getText().toString(),PackageConfig.login_data[3])){
                            startActivity(new Intent(CashierLogin.this,DashboardCashier.class));
                            Toast.makeText(getApplicationContext(),"pin set successfully",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"error in setting pin",Toast.LENGTH_SHORT).show();
                        }
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else {
               //     lin.setBackgroundColor();
                    wrong_pin.setText("PINS DONT MATCH!!");
                }
            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();

    }
}
