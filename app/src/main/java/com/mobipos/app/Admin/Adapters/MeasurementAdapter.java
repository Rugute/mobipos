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

public class MeasurementAdapter extends BaseAdapter {

    String[] measure,value;
    Context context;

    public static LayoutInflater inflater= null ;



    public MeasurementAdapter(Context context, String[] measure,String[] value) {
        this.measure=measure;
        this.value=value;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {

        return measure.length;
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

        view=inflater.inflate(R.layout.measure_custom_item,null);
        TextView unit1=(TextView)view.findViewById(R.id.unit_measure);
        TextView unit_v=(TextView)view.findViewById(R.id.unit_value);

        unit1.setText(measure[i]);
        unit_v.setText(value[i]);

        return view;
    }
}
