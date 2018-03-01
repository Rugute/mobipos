package com.mobipos.app.Defaults;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mobipos.app.Admin.AdminMeasurements;
import com.mobipos.app.Cashier.Adapters.PrinterAdapter;
import com.mobipos.app.R;
import com.mobipos.app.database.Printers;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

/**
 * Created by folio on 3/1/2018.
 */

public class CashierSelectPrinter extends Fragment {

    Button btn_title;
    ListView listView;
    Printers printersdb;

    public static CashierSelectPrinter newInstance(){
        CashierSelectPrinter fragment=new CashierSelectPrinter();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_list_branches, container, false);
    }
    public void onViewCreated(View view,Bundle savedInstanceState){
        printersdb=new Printers(getContext(), defaults.database_name,null,1);


        btn_title=view.findViewById(R.id.btn_title);
        listView=view.findViewById(R.id.view_outlet);

        btn_title.setText("SELECT PRINTER");

        listView.setAdapter(new PrinterAdapter(getContext(),printersdb.getPrintes()));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos= (int) adapterView.getItemIdAtPosition(i);

                Toast.makeText(getContext(),printersdb.getPrintes().get(pos).macAdress,Toast.LENGTH_SHORT).show();
            }
        });

    }

}





















