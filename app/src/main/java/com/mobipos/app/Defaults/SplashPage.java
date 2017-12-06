package com.mobipos.app.Defaults;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobipos.app.R;

/**
 * Created by folio on 12/6/2017.
 */

public class SplashPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);

        ImageView imageView= findViewById(R.id.image_view);
        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash_fade_in);
        Animation animation_text= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash_names);
        Animation animation_linear= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.text_translation);
        imageView.startAnimation(animation);
        TextView textView= findViewById(R.id.splash_text);
        LinearLayout linear=findViewById(R.id.linear);
        textView.setAnimation(animation_text);
        linear.setAnimation(animation_linear);

    }
}
