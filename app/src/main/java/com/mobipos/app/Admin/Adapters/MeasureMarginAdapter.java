package com.mobipos.app.Admin.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.MeasureMarginData;
import com.mobipos.app.R;

import java.util.List;

/**
 * Created by folio on 2/8/2018.
 */

public class MeasureMarginAdapter extends BaseAdapter {
    List<MeasureMarginData> data;
    Context context;
    LayoutInflater inflater=null;
    public MeasureMarginAdapter(Context context, List<MeasureMarginData> data){
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

        view=inflater.inflate(R.layout.admin_select_margin_mode,null);
        TextView name=view.findViewById(R.id.unit_mode);
        TextView description=view.findViewById(R.id.unit);
        name.setText(data.get(i).name);
        description.setText(data.get(i).description);

        return view;
    }

}
