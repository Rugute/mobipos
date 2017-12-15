package com.mobipos.app.Cashier.dashboardFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobipos.app.Dashboard.SalesFragment;
import com.mobipos.app.R;

/**
 * Created by root on 12/12/17.
 */

public class CashierInventory extends Fragment {

    public static CashierInventory newInstance(){
        CashierInventory fragment = new CashierInventory();
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cashier_inventory, container, false);
    }
}
