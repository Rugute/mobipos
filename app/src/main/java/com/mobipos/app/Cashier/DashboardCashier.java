package com.mobipos.app.Cashier;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.mobipos.app.Cashier.dashboardFragments.Inventory.CashierInventory;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSale;
import com.mobipos.app.Dashboard.DashboardFragment;
import com.mobipos.app.Admin.DashboardFragments.SalesFragment;

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
        setTitle("Mauzo Africa: Cashier");




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
                        selectedFragment = MakeSale.newInstance();
                        break;
                    case R.id.cashier_item_settings:
                        selectedFragment = SalesFragment.newInstance();
                        break;
                    case R.id.cashier_item_account:
                        selectedFragment = SalesFragment.newInstance();
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
