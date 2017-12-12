package com.mobipos.app.Dashboard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.mobipos.app.R;

/**
 * Created by folio on 12/11/2017.
 */

public class DashboardAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_admin);

        BottomNavigationView navigationMenuView=findViewById(R.id.bottom_navigation);

        Fragment fragment;
        fragment=DashboardFragment.newInstance();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout,fragment);
        transaction.commit();

        navigationMenuView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment=null;
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        selectedFragment=DashboardFragment.newInstance();
                        break;
                    case R.id.navigation_dashboard:
                        selectedFragment=ProductFragment.newInstance();
                        break;
                    case R.id.navigation_notifications:
                        selectedFragment=SalesFragment.newInstance();
                        break;
                    case R.id.navigation_sales:
                        selectedFragment=SettingsFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout,selectedFragment);
                transaction.commit();

                return true;
            }
        });

    }
}
