package com.mobipos.app.Cashier;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mobipos.app.Admin.DashboardAdmin;
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
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import static android.app.PendingIntent.getActivity;

/**
 * Created by root on 12/8/17.
 */

public class DashboardCashier extends AppCompatActivity{

    Users usersdb;
    Categories categoriesdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_cashier);
        setTitle("Mauzo Africa: Cashier");



        usersdb=new Users(this, defaults.database_name,null,1);
        categoriesdb=new Categories(this, defaults.database_name,null,1);
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

        final CheckInternetSettings internet=new CheckInternetSettings(this);
       if(item.getItemId()==android.R.id.home){
           FragmentManager manager=getSupportFragmentManager();
           manager.popBackStack();
           setTitle("Mauzo Africa");
       }
       else if(item.getItemId()==R.id.logout_btn){


           if(internet.isNetworkConnected()){

               new Synchronizer(getApplicationContext());
               
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
       }else if(item.getItemId()==R.id.switch_account){

           if(usersdb.get_login_details()[2].equals("cashier")){
               AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this).
                       setTitle("Switch Failure").
                       setMessage("You dont have administrator rights to perform this operation").
                       setCancelable(true);
               alertBuilder.show();
           }else{
               if(internet.isNetworkConnected()){
                   new switch_account().execute();
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
                    if(usersdb.check_admin_sale_id() > 0 && categoriesdb.EmptyTables()==0){
                        finish();
                    }
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

    public class switch_account extends AsyncTask<String,String,String>{

        ProgressDialog dialog=new ProgressDialog(DashboardCashier.this);
        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setMessage("Switching Account.please wait...");
            dialog.setCancelable(false);
            dialog.show();
            new Synchronizer(getApplicationContext());
        }
        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);

            if(categoriesdb.EmptyTables()==0){
                startActivity(new Intent(DashboardCashier.this, DashboardAdmin.class));
                finish();
            }

            dialog.cancel();

        }
    }
}
