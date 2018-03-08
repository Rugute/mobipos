package com.mobipos.app.Cashier.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobipos.app.R;
import com.mobipos.app.database.DiscountInterface;

import java.util.List;

/**
 * Created by folio on 3/8/2018.
 */

public class DiscountAdapter extends BaseAdapter {

    Context context;
    List<DiscountInterface> data;
    LayoutInflater inflater=null;
    public DiscountAdapter(Context context, List<DiscountInterface> data){
        this.context=context;
        this.data=data;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return data.size();
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
        TextView discount_name=view.findViewById(R.id.unit_measure);
        TextView discount_value=view.findViewById(R.id.unit_value);

        ImageView delete_icon=view.findViewById(R.id.del);
        delete_icon.setVisibility(View.INVISIBLE);

        discount_name.setText(data.get(i).name);
        discount_value.setText(data.get(i).value+"%");

        return view;
    }
}
