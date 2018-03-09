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

public class DiscountViewAdapter extends BaseAdapter {

    Context context;
    List<DiscountInterface> data;
    LayoutInflater inflater=null;
    public DiscountViewAdapter(Context context, List<DiscountInterface> data){
        this.data=data;
        this.context=context;
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

        view=inflater.inflate(R.layout.discount_custom_item,null);
        TextView discount_name=view.findViewById(R.id.discount_name);
        TextView discount_value=view.findViewById(R.id.discount_value);
        ImageView imageView=view.findViewById(R.id.del);

        imageView.setVisibility(View.INVISIBLE);
        discount_name.setText(data.get(i).name);
        discount_value.setText(data.get(i).value+"%");


        return view;
    }
}
