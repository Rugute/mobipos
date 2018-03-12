package com.mobipos.app.Cashier.dashboardFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobipos.app.Cashier.dashboardFragments.Settings.CashierDiscounts;
import com.mobipos.app.Cashier.dashboardFragments.Settings.CashierPrinter;
import com.mobipos.app.Defaults.PrinterFragment;
import com.mobipos.app.R;


/**
 * Created by root on 1/31/18.
 */

public class CashierSettings extends Fragment {
    CardView printer_settings,discount_set;

    public static CashierSettings newInstance(){
        CashierSettings  fragment= new  CashierSettings ();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.cashier_settings,container,false);
    }
    public void onViewCreated(View view,Bundle savedInstanceState){

        printer_settings=view.findViewById(R.id.printer_set_card);
        discount_set=view.findViewById(R.id.sales_discount_card);


        printer_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = null;
                selectedFragment = CashierPrinter.newInstance();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                transaction.replace(R.id.frame_layout_new, selectedFragment);
                transaction.commit();
            }
        });

        discount_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = null;
                selectedFragment = CashierDiscounts.newInstance();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                transaction.replace(R.id.frame_layout_new, selectedFragment);
                transaction.commit();
            }
        });

    }


}
