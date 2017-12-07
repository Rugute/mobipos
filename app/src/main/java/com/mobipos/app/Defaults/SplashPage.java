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

/**
 * Created by folio on 12/6/2017.
 */

public class SplashPage extends Activity {
    public String[] homepage_headings={"ADMIN","CASHIER"};
    public static int SPLASH_TIME_OUT=2500;
    CardView cardCashier,cardAdmin;
    LinearLayout linear_loggers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);

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

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Users users=new Users(SplashPage.this,defaults.database_name,null,1);

                if(users.CheckUserOrPin(users.tb_name)>0){

                }else{
                    bar.setVisibility(View.GONE);
                    linear_loggers.setVisibility(View.VISIBLE);
                    linear_loggers.startAnimation(animation_linear);
                }
            }
        }, (long) SPLASH_TIME_OUT);


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
    }


}
