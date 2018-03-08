package com.mobipos.app.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.R;
import com.mobipos.app.Sync.DatabaseInitializers;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;



public class PinLogin extends Activity {

    EditText ed_pin;
    public static String strPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setpin);

        ed_pin=findViewById(R.id.ed_pin);
        final Users users_id =new Users(getApplicationContext(), defaults.database_name,null,1);


        ed_pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                strPin=ed_pin.getText().toString();
                if(strPin.length()==4){

                    try {
                        if(users_id.password_match(strPin)){
                            final CheckInternetSettings internet=new CheckInternetSettings(PinLogin.this);
                            if(internet.isNetworkConnected()){
                                try{
                                   // new DatabaseInitializers(getApplicationContext(),1);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }finally {
                                    startActivity(new Intent(PinLogin.this, DashboardCashier.class));
                                }
                            }else{
                                startActivity(new Intent(PinLogin.this, DashboardCashier.class));
                            }


                        }else {
                            Toast.makeText(getApplicationContext(), "Wrong Pin", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}