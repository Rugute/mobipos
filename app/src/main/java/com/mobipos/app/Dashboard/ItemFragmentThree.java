package com.mobipos.app.Dashboard;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobipos.app.R;

/**
 * Created by root on 12/8/17.
 */

public class ItemFragmentThree extends Fragment {
    public static ItemFragmentThree newInstance(){
        ItemFragmentThree fragment= new ItemFragmentThree();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_three, container, false);
    }
}
