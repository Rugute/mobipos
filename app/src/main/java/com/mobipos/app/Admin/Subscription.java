package com.mobipos.app.Admin;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobipos.app.R;

/**
 * Created by root on 1/6/18.
 */

public class Subscription extends Fragment{
    RecyclerView subsrv;
    public static Subscription newInstance(){
        Subscription fragment= new Subscription();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subscription_status, container, false);
    }

    public void onViewCreated(Bundle savedInstanceState,View view){


    }
}
