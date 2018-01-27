package com.mobipos.app.Admin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobipos.app.R;

/**
 * Created by root on 1/23/18.
 */

public class QuickSaleAdapter extends BaseAdapter {

    String[] branch;
    Context context;

    public static LayoutInflater inflater= null ;



    public QuickSaleAdapter(Context context,String[] branch) {
        this.branch=branch;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {

        return branch.length;
    }

    @Override
    public Object getItem(int i) {

        return i;
    }

    @Override
    public long getItemId(int i) {

        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view=inflater.inflate(R.layout.branch_list_item,null);
        TextView branchtxt=(TextView)view.findViewById(R.id.branch);

        branchtxt.setText(branch[i]);

        return view;
    }
}
