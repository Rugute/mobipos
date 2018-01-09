package com.mobipos.app.Defaults;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.mobipos.app.R;

/**
 * Created by root on 1/9/18.
 */

public class ReceiptActivity extends AppCompatActivity {
    CardView cardView;
    private CollapsingToolbarLayout collapsing_toolbar;
    RecyclerView recyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsing_toolbar=(CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        recyclerView=(RecyclerView) findViewById(R.id.recrv);


    }
}