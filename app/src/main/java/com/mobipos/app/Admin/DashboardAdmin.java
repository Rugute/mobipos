package com.mobipos.app.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mobipos.app.Admin.DashboardFragments.AdminViewSales;
import com.mobipos.app.Admin.DashboardFragments.Inventory.AdminInventory;
import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Dashboard.DashboardFragment;
import com.mobipos.app.Admin.DashboardFragments.NotificationsFragment;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.SplashPage;
import com.mobipos.app.R;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

/**
 * Created by folio on 12/11/2017.
 */

public class DashboardAdmin extends AppCompatActivity {
    Users usersdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_admin);
       // setTitle("Mauzo Africa: Administrator");

        BottomNavigationView navigationMenuView=findViewById(R.id.bottom_navigation);

        usersdb=new Users(this, defaults.database_name,null,1);
        Fragment fragment;
        fragment= AdminInventory.newInstance();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout,fragment);
        transaction.commit();

        navigationMenuView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment=null;
                switch (item.getItemId()){
                    case R.id.navigation_home:
                       selectedFragment= AdminInventory.newInstance();
                        break;
                   // case R.id.navigation_dashboard:
                      //  selectedFragment= AdminInventory.newInstance();
                      //  break;
                    case R.id.navigation_account:
                        selectedFragment= AccountFragment.newInstance();
                        break;
                    case R.id.navigation_sales:
                        selectedFragment= AdminViewSales.newInstance();
                        break;
                }
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout,selectedFragment);
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
            //setTitle("Mauzo Africa: Administator");
        }
           else if(item.getItemId()==R.id.logout_btn){
            final CheckInternetSettings internet=new CheckInternetSettings(this);

            if(internet.isNetworkConnected()){
                  finish();
                  System.exit(0);

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

    public void onBackPressed(){


        int count = getSupportFragmentManager().getBackStackEntryCount();
        if(count !=0){
                FragmentManager manager=getSupportFragmentManager();
                manager.popBackStack();
        }else{
            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);

            alertDialog.setMessage((CharSequence) "Are you sure you want to Logout?");
            alertDialog.setPositiveButton((CharSequence) "Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                    finish();
                }
            });
            alertDialog.setNegativeButton((CharSequence) "No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alertDialog.show();
        }


    }
}
