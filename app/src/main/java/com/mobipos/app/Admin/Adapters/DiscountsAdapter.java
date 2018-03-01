package com.mobipos.app.Admin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobipos.app.R;

/**
 * Created by root on 2/8/18.
 */

public class DiscountsAdapter extends BaseAdapter {

    String[] discounts,dvalue;
    Context context;

    public static LayoutInflater inflater= null ;



    public DiscountsAdapter(Context context, String[] discounts,String[] dvalue) {
        this.discounts=discounts;
        this.dvalue=dvalue;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {

        return discounts.length;
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

        view=inflater.inflate(R.layout.discount_custom_item,null);
        TextView unit1=(TextView)view.findViewById(R.id.discount_name);
        TextView unit_v=(TextView)view.findViewById(R.id.discount_value);

        unit1.setText(discounts[i]);
        unit_v.setText(dvalue[i]);

        return view;
    }
}
