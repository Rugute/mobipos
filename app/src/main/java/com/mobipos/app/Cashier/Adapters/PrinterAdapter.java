package com.mobipos.app.Cashier.Adapters;

import android.content.Context;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobipos.app.R;
import com.mobipos.app.database.PrinterInterface;

import java.util.List;

/**
 * Created by folio on 3/1/2018.
 */

public class PrinterAdapter extends BaseAdapter {

    Context context;
    List<PrinterInterface> data;

    LayoutInflater inflater;
    public PrinterAdapter(Context context, List<PrinterInterface> data){
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

        view=inflater.inflate(R.layout.branch_list_item,null);
        TextView printer_name=view.findViewById(R.id.branch);

        printer_name.setText(data.get(i).name);
        return view;
    }
}
