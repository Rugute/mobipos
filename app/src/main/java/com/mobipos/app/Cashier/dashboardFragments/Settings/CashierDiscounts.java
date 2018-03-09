package com.mobipos.app.Cashier.dashboardFragments.Settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.mobipos.app.Cashier.Adapters.DiscountViewAdapter;
import com.mobipos.app.Cashier.Adapters.PrinterViewAdapter;
import com.mobipos.app.R;
import com.mobipos.app.database.Discounts;
import com.mobipos.app.database.Printers;
import com.mobipos.app.database.defaults;

/**
 * Created by folio on 3/9/2018.
 */

public class CashierDiscounts extends Fragment {
    public static CashierDiscounts newInstance() {
        CashierDiscounts fragment = new CashierDiscounts();
        return fragment;
    }
    Discounts discountsdb;
    ListView listView;
    Button button_title;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_list_branches, container, false);

    }
    public void onViewCreated(View view, Bundle savedInstanceState){
        discountsdb=new Discounts(getActivity(), defaults.database_name,null,1);
        listView=view.findViewById(R.id.view_outlet);
        button_title=view.findViewById(R.id.btn_title);

        button_title.setText("Discounts");

        listView.setAdapter(new DiscountViewAdapter(getContext(),discountsdb.getDiscounts()));
    }
}