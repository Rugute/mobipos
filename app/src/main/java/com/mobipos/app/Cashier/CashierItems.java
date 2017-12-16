package com.mobipos.app.Cashier;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobipos.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/12/17.
 */

public class CashierItems extends AppCompatActivity {

    TextView cattext;
    Spinner spinner;


    private List<CashDummy> cashDummies;

    private RecyclerView rv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashier_items);

        spinner=(Spinner)findViewById(R.id.spinner);
        cattext=(TextView)findViewById(R.id.cattext);

        rv1=(RecyclerView)findViewById(R.id.rv1);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv1.setLayoutManager(llm);
        rv1.setHasFixedSize(true);

        initializeData();
        initializeAdapter();
    }

    private void initializeData(){
        cashDummies= new ArrayList<>();
        cashDummies.add(new CashDummy("FineGhel", "Oops", R.mipmap.ic_launcher));
        cashDummies.add(new CashDummy("Beaver", "Cough", R.mipmap.ic_launcher));
        cashDummies.add(new CashDummy("Meaning", "Trump", R.mipmap.ic_launcher));
    }

    private void initializeAdapter(){
        CashierCategRvAdapter adapter = new CashierCategRvAdapter(cashDummies);
        rv1.setAdapter(adapter);
    }

}
