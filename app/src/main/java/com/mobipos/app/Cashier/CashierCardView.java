package com.mobipos.app.Cashier;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobipos.app.R;

/**
 * Created by root on 12/13/17.
 */

public class CashierCardView extends AppCompatActivity {
    TextView Category;
    TextView Item;
    ImageView CatPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_cardview);
         Category= (TextView)findViewById(R.id.text1);
        Item = (TextView)findViewById(R.id.text2);
        CatPic = (ImageView)findViewById(R.id.img1);

        Category.setText("Fruits");
        Item.setText("Mango");
        CatPic.setImageResource(R.mipmap.ic_launcher);
    }
}
