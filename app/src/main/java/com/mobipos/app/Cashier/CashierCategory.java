package com.mobipos.app.Cashier;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mobipos.app.Cashier.dashboardFragments.Inventory.CashDummy;
import com.mobipos.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/12/17.
 */

public class CashierCategory extends AppCompatActivity {

    private List<CashDummy> cashDummies;

    private RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashier_category);

        rv=(RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        initializeData();
      //  initializeAdapter();
    }

    private void initializeData(){
        cashDummies= new ArrayList<>();
        cashDummies.add(new CashDummy("Fruits", "Mango", R.mipmap.ic_launcher));
        cashDummies.add(new CashDummy("Beverage", "Coffee", R.mipmap.ic_launcher));
        cashDummies.add(new CashDummy("Meat", "Beef", R.mipmap.ic_launcher));
    }

//    private void initializeAdapter(){
//        CashierCategRvAdapter adapter = new CashierCategRvAdapter(ca);
//        rv.setAdapter(adapter);
//    }
}