package com.mobipos.app.Admin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobipos.app.R;

/**
 * Created by root on 1/8/18.
 */

public class Payments extends Fragment {
    public static Payments newInstance() {
        Payments fragment = new Payments();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_payment_fragment, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
    }
}