package com.mobipos.app.Defaults;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.R;
import com.mobipos.app.database.*;
import com.mobipos.app.database.Users;
import com.mobipos.app.login.AdminLogin;
import com.mobipos.app.login.CashierLogin;
import com.mobipos.app.login.PinLogin;

/**
 * Created by folio on 12/6/2017.
 */

public class SplashPage extends Activity {
    public String[] homepage_headings={"ADMIN","CASHIER"};
    public static int SPLASH_TIME_OUT=2500;
    CardView cardCashier,cardAdmin;
    LinearLayout linear_loggers;
    TextView register_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);

        register_user=findViewById(R.id.register_new_user);
        ImageView imageView= findViewById(R.id.image_view);
        cardAdmin=findViewById(R.id.admin);
        cardCashier=findViewById(R.id.cashier);
        linear_loggers=findViewById(R.id.linear_loggers);
        final ProgressBar bar=findViewById(R.id.progressBar);
        //  GridView gridView= findViewById(R.id.grid_view);
        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash_fade_in);
        Animation animation_text= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash_names);
        final Animation animation_linear= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.text_translation);
        imageView.startAnimation(animation);
        TextView textView= findViewById(R.id.splash_text);

        textView.setAnimation(animation_text);
        //linear.setAnimation(animation_linear);

      final  Users users=new Users(SplashPage.this,defaults.database_name,null,1);
        new Handler().postDelayed(new Runnable() {
            public void run() {


                if(users.CheckUserOrPin(users.tb_name)>0) {

                   Toast.makeText(getApplicationContext(),String.valueOf(users.CheckUserOrPin(users.tb_name))
                           +users.get_login_details()[2],Toast.LENGTH_SHORT).show();
                    bar.setVisibility(View.GONE);

                    if(users.get_login_details()[2].equals("cashier")){
                        startActivity(new Intent(SplashPage.this,PinLogin.class));
                    }else {
                        startActivity(new Intent(SplashPage.this,AdminLogin.class));
                    }

               }else{
                    Toast.makeText(getApplicationContext(),String.valueOf(users.CheckUserOrPin(users.tb_name)),Toast.LENGTH_SHORT).show();
                    bar.setVisibility(View.GONE);
                linear_loggers.setVisibility(View.VISIBLE);
                linear_loggers.startAnimation(animation_linear);
               }

         //      finish();
            }
        }, (long) SPLASH_TIME_OUT);

//        if(users.get_login_details()[2].equals("admin")){
//            startActivity(new Intent(SplashPage.this,AdminLogin.class));
//        }

        cardCashier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashPage.this, CashierLogin.class));
            }
        });

        cardAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashPage.this, AdminLogin.class));
            }
        });
        // gridView.setAdapter(new HomepageAdapter(homepage_headings));

        register_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashPage.this,RegisterAdmin.class));
            }
        });
    }


}
