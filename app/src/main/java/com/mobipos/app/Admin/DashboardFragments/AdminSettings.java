package com.mobipos.app.Admin.DashboardFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.mobipos.app.Admin.Adapters.AdminSettingsAdapter;
import com.mobipos.app.Admin.Adapters.SettingsData;

import com.mobipos.app.Admin.AdminMeasurements;
import com.mobipos.app.Admin.BranchFragment;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Categories.AdminCategories;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminItems;
import com.mobipos.app.Admin.DashboardFragments.Inventory.StockAlert.StockAlertFragment;
import com.mobipos.app.Admin.DashboardFragments.Settings.TaxesFragment;
import com.mobipos.app.Admin.EmployeesFragment;
import com.mobipos.app.Dashboard.ReportFragment;
import com.mobipos.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 2/8/18.
 */

public class AdminSettings extends Fragment {
    ListView listView;
    private List<SettingsData> settingsData;

    public static AdminSettings newInstance() {
        AdminSettings fragment = new AdminSettings();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_settings_fragment, container, false);
    }
    public void onViewCreated(View view,Bundle savedInstanceState){
        settingsData=new ArrayList<>();
        settingsData.add(new SettingsData("Employees"));
        settingsData.add(new SettingsData("Measurements"));
        settingsData.add(new SettingsData("Taxes"));
        settingsData.add(new SettingsData("Printers"));
        settingsData.add(new SettingsData("Discounts"));
        AdminSettingsAdapter adapter= new AdminSettingsAdapter(getActivity(),settingsData);
         listView=view.findViewById(R.id.setting_list);
         listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos= (int) adapterView.getItemIdAtPosition(i);
                if (pos==0){
                  Fragment fragment;
                  fragment = EmployeesFragment.newInstance();
                   FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                   transaction.replace(R.id.frame_layout, fragment);
                    transaction.commit();
                }else if(pos==1){
                    Fragment fragment;
                    fragment = AdminMeasurements.newInstance();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                    transaction.replace(R.id.frame_layout, fragment);
                    transaction.commit();
                }else if(pos==2){
                    Fragment fragment;
                    fragment = TaxesFragment.newInstance();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                    transaction.replace(R.id.frame_layout, fragment);
                    transaction.commit();
                }else if(pos==3){
//                    Fragment fragment;
//                    fragment = ReportFragment.newInstance();
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
//                    transaction.replace(R.id.frame_layout, fragment);
//                    transaction.commit();
                }else if(pos==4){
//                    Fragment fragment;
//                    fragment = BranchFragment.newInstance();
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
//                    transaction.replace(R.id.frame_layout, fragment);
//                    transaction.commit();
                }

            }
        });




    }


}
