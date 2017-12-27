package com.mobipos.app.Cashier;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mobipos.app.Cashier.dashboardFragments.Inventory.CashierInventory;
import com.mobipos.app.Dashboard.DashboardFragment;
import com.mobipos.app.Dashboard.SalesFragment;
import com.mobipos.app.Dashboard.SettingsFragment;
import com.mobipos.app.R;

import static android.app.PendingIntent.getActivity;

/**
 * Created by root on 12/8/17.
 */

public class DashboardCashier extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_cashier);
        setTitle("Mauzo Africa");




        BottomNavigationView navigationMenuView = findViewById(R.id.cashier_bottom_nav);

        Fragment fragment;
        fragment = DashboardFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout_new, fragment);
        transaction.commit();

        navigationMenuView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.cashier_item_sale:
                        selectedFragment = DashboardFragment.newInstance();
                        break;
                    case R.id.cashier_item_inventory_details:
                        selectedFragment = CashierInventory.newInstance();
                        break;
                    case R.id.cashier_item_dashboard:
                        selectedFragment = SalesFragment.newInstance();
                        break;
                    case R.id.cashier_item_settings:
                        selectedFragment = SalesFragment.newInstance();
                        break;
                    case R.id.cashier_item_account:
                        selectedFragment = SettingsFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout_new, selectedFragment);
                 transaction.commit();

                return true;
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
       if(item.getItemId()==android.R.id.home){
           FragmentManager manager=getSupportFragmentManager();
           manager.popBackStack();
           setTitle("Mauzo Africa");
       }
       return  true;
    }
}
