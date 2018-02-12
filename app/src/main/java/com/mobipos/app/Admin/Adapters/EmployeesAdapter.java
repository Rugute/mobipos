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
 * Created by root on 2/12/18.
 */

public class EmployeesAdapter extends BaseAdapter {
    List<EmployeeData> employeeData;
    Context context;
    LayoutInflater inflater=null;

    public EmployeesAdapter(Context context,List<EmployeeData> employeeData){
        this.employeeData=employeeData;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return employeeData.size();
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
        view=inflater.inflate(R.layout.admin_users_item,null);
        TextView name=view.findViewById(R.id.user_name);
        TextView code=view.findViewById(R.id.user_unicode);
        TextView branch=view.findViewById(R.id.user_branch);
        name.setText(employeeData.get(i).user_name);
        code.setText(employeeData.get(i).user_code);
        branch.setText(employeeData.get(i).user_branch);
        return view;
    }
}
