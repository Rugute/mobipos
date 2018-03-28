package com.mobipos.app.Cashier;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.mobipos.app.Cashier.dashboardFragments.Inventory.CashDummy;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories.CashierCategoryData;
import com.mobipos.app.R;
import com.mobipos.app.database.Sales;
import com.mobipos.app.database.defaults;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/12/17.
 */

public class CashierCategory extends AppCompatActivity {

    private List<CashDummy> cashDummies;
    List<CashierCategoryData>categoryData;

    private RecyclerView rv;
    Sales salesdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashier_category);
        salesdb =new Sales(getApplicationContext(), defaults.database_name,null,1);
        rv=(RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        RelativeLayout rela=(RelativeLayout)findViewById(R.id.no_cashier_category_layout);

        if(categoryData.size()==0){
            rela.setVisibility(View.VISIBLE);
        }

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