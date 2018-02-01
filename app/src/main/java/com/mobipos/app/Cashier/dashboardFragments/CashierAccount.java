package com.mobipos.app.Cashier.dashboardFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.mobipos.app.R;

/**
 * Created by root on 1/31/18.
 */

public class CashierAccount extends Fragment {
    CardView updateinfo,reset;
    TextView name,phone,email,cash_id;

    public static CashierAccount newInstance(){
        CashierAccount fragment= new CashierAccount();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.cashier_account,container,false);
    }
    public void onViewCreated(View view, Bundle savedInstanceState){

        updateinfo=(CardView)view.findViewById(R.id.update_info_cv);
        reset=(CardView)view.findViewById(R.id.resetcv);
        name=(TextView) view.findViewById(R.id.nameOfPerson_cashier);
        phone=(TextView) view.findViewById(R.id.cashier_phone);
        email=(TextView) view.findViewById(R.id.cashier_email);
        cash_id=(TextView) view.findViewById(R.id.user_id);


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pinset();
            }
        });

    }
    public void Pinset (){
        final AlertDialog dialog= new AlertDialog.Builder(getContext()).create();
        View view=LayoutInflater.from(getContext()).inflate(R.layout.reset_pin_popup,null);


        dialog.setView(view);
        dialog.show();
    }

}
