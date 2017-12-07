package com.mobipos.app.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mobipos.app.R;

/**
 * Created by folio on 12/6/2017.
 */

public class AdminLogin extends Activity {

    TextView textLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


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

        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public void dialog(boolean state){
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Loading.please wait...");
    }
}
