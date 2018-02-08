package com.mobipos.app.Admin.DashboardFragments.Inventory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.mobipos.app.Admin.AccountFragment;
import com.mobipos.app.Admin.BranchFragment;
import com.mobipos.app.Admin.DashboardAdmin;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Categories.AdminCategories;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminItems;
import com.mobipos.app.Cashier.CashierInventoryAdapter;
import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories.CashierCategories;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItems;
import com.mobipos.app.Dashboard.ReportFragment;
import com.mobipos.app.Dashboard.StockAlertFragment;
import com.mobipos.app.R;

/**
 * Created by root on 12/12/17.
 */

public class AdminInventory extends Fragment {

    public static AdminInventory newInstance(){
        AdminInventory fragment = new AdminInventory();
        return fragment;
    }


    GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.cashier_inventory, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(View view,Bundle savedInstanceState){

        gridView=view.findViewById(R.id.grid_view);
        gridView.setAdapter(new CashierInventoryAdapter(getActivity(), PackageConfig.images,PackageConfig.inventory_title));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos= (int) adapterView.getItemIdAtPosition(i);
                if (pos==0){
                    Fragment fragment;
                    fragment = AdminCategories.newInstance();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                    transaction.replace(R.id.frame_layout, fragment);
                    transaction.commit();
                }else if(pos==1){
                    Fragment fragment;
                    fragment = AdminItems.newInstance();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                    transaction.replace(R.id.frame_layout, fragment);
                    transaction.commit();
                }else if(pos==2){
                    Fragment fragment;
                    fragment = StockAlertFragment.newInstance();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                    transaction.replace(R.id.frame_layout, fragment);
                    transaction.commit();
                }else if(pos==3){
                    Fragment fragment;
                    fragment = ReportFragment.newInstance();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                    transaction.replace(R.id.frame_layout, fragment);
                    transaction.commit();
                    }else if(pos==4){
                    Fragment fragment;
                    fragment = BranchFragment.newInstance();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                    transaction.replace(R.id.frame_layout, fragment);
                    transaction.commit();
                }else if(pos==5){
                    Fragment fragment;
                    fragment = StockAlertFragment.newInstance();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                    transaction.replace(R.id.frame_layout, fragment);
                    transaction.commit();
                }

            }
        });

        ((DashboardAdmin) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

}
