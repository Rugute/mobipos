package com.mobipos.app.Admin.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;


import com.mobipos.app.R;

import java.util.List;

/**
 * Created by root on 2/8/18.
 */

public class AdminSettingsAdapter extends BaseAdapter {

    Context context;
    List<SettingsData> settingsData;
    LayoutInflater inflater=null;


    public AdminSettingsAdapter(Context context, List<SettingsData> settingsData) {
        this.context = context;
        this.settingsData = settingsData;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return settingsData.size();
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
        view=inflater.inflate(R.layout.admin_custom_settings,null);
        TextView setting_item=view.findViewById(R.id.setting_textview);
        setting_item.setText(settingsData.get(i).item);
        return view;
    }
}