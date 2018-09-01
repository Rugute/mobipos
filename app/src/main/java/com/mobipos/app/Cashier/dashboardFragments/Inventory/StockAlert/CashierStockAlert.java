package com.mobipos.app.Cashier.dashboardFragments.Inventory.StockAlert;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mobipos.app.Admin.Adapters.StockAlertRvAdapter;
import com.mobipos.app.Admin.AdminStockAlertData;
import com.mobipos.app.R;
import com.mobipos.app.database.Inventory;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import java.util.List;

/**
 * Created by folio on 3/6/2018.
 */

public class CashierStockAlert extends Fragment{
    public static CashierStockAlert newInstance(){
        CashierStockAlert fragment = new CashierStockAlert();
        return fragment;
    }

    Users usersdb;
    List<AdminStockAlertData> stockAlertData;
    RecyclerView rv;
    Inventory inventorydb;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_alert_fragment, container, false);
    }

    public void onViewCreated(View view,Bundle savedInstanceState){
        usersdb=new Users(getContext(), defaults.database_name,null,1);
        inventorydb=new Inventory(getContext(), defaults.database_name,null,1);
        rv = view.findViewById(R.id.stock_alert_rv);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        RelativeLayout rela=(RelativeLayout)view.findViewById(R.id.no_alert_layout);

        if(inventorydb.getLowStockProducts().size()==0){
            rela.setVisibility(View.VISIBLE);
        }

        initializeAdapter(inventorydb.getLowStockProducts());

    }


    private void initializeAdapter(List<AdminStockAlertData> data){
        StockAlertRvAdapter adapter = new StockAlertRvAdapter(data,getContext());
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }



}
