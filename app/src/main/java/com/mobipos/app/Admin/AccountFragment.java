package com.mobipos.app.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Dashboard.DashboardFragment;
import com.mobipos.app.R;

/**
 * Created by root on 1/8/18.
 */

public class AccountFragment extends Fragment {

    CardView subscription_card,payments_card;
    public static AccountFragment newInstance(){
        AccountFragment fragment= new AccountFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_account_fragment, container, false);
    }
    public  void onViewCreated(View view,Bundle savedInstanceState){

        subscription_card= (CardView)view.findViewById(R.id.subscription_card);
        payments_card=view.findViewById(R.id.payments_card);
        showBackButton(true,"Account");


        subscription_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Subscription fragment;
                fragment =Subscription.newInstance();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                transaction.replace(R.id.frame_layout, fragment);
                transaction.commit();
            }
        });
        payments_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    public void showBackButton(Boolean state,String title) {
        if(state){
            if (getActivity() instanceof DashboardAdmin) {
                try {
                    ((DashboardAdmin) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getActivity().setTitle(title);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

}