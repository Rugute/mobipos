package com.mobipos.app.Cashier;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mobipos.app.Admin.DashboardFragments.AdminViewSales;
import com.mobipos.app.Cashier.dashboardFragments.CashierAccount;
import com.mobipos.app.Cashier.dashboardFragments.CashierSettings;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.CashierInventory;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSale;
import com.mobipos.app.Cashier.dashboardFragments.ViewSales.ViewSale;

import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.SplashPage;
import com.mobipos.app.R;
import com.mobipos.app.Sync.Synchronizer;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import static android.app.PendingIntent.getActivity;

/**
 * Created by root on 12/8/17.
 */

public class DashboardCashier extends AppCompatActivity{

    Users usersdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_cashier);
        setTitle("Mauzo Africa: Cashier");



        usersdb=new Users(this, defaults.database_name,null,1);
        BottomNavigationView navigationMenuView = findViewById(R.id.cashier_bottom_nav);

        Fragment fragment;
        fragment = MakeSale.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout_new, fragment);
        transaction.commit();

        navigationMenuView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.cashier_item_sale:
                        selectedFragment = ViewSale.newInstance();
                        break;
                    case R.id.cashier_item_inventory_details:
                        selectedFragment = CashierInventory.newInstance();
                        break;

                    case R.id.cashier_item_dashboard:
                        selectedFragment = MakeSale.newInstance();
                        break;
                    case R.id.cashier_item_settings:
                        selectedFragment = CashierSettings.newInstance();
                        break;
                    case R.id.cashier_item_account:
                        selectedFragment = CashierAccount.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout_new, selectedFragment);
                 transaction.commit();

                return true;
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.logout,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
       if(item.getItemId()==android.R.id.home){
           FragmentManager manager=getSupportFragmentManager();
           manager.popBackStack();
           setTitle("Mauzo Africa");
       }
       else if(item.getItemId()==R.id.logout_btn){
          final CheckInternetSettings internet=new CheckInternetSettings(this);

           if(internet.isNetworkConnected()){

               new Synchronizer(getApplicationContext());
                if(usersdb.clearUserData()<=0){
                    startActivity(new Intent(DashboardCashier.this, SplashPage.class));
                }
           }else{
               AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this).
                       setTitle("Cannot Logout").
                       setMessage("Enable your internet to logout").
                       setPositiveButton((CharSequence) "Settings", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialogInterface, int i) {
                               internet.context.startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
                           }
                       });
               alertBuilder.show();
           }
       }
       return  true;
    }
}
