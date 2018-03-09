package com.mobipos.app.Cashier.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobipos.app.R;
import com.mobipos.app.database.PrinterInterface;

import java.util.List;

/**
 * Created by folio on 3/8/2018.
 */

public class PrinterViewAdapter extends BaseAdapter {

    List<PrinterInterface> data;
    Context context;
    LayoutInflater inflater=null;
    public PrinterViewAdapter(Context context, List<PrinterInterface> data){
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
        view=inflater.inflate(R.layout.printer_item,null);

        TextView p_name=view.findViewById(R.id.printer_name);
        TextView p_mac=view.findViewById(R.id.printer_mac);
        TextView p_id=view.findViewById(R.id.printer_id);
        TextView p_branch=view.findViewById(R.id.printer_branch);
        ImageView imageView=view.findViewById(R.id.delete_printer);
        imageView.setVisibility(View.INVISIBLE);
        p_branch.setVisibility(View.INVISIBLE);

        p_name.setText(data.get(i).name);
        p_id.setText(data.get(i).id);
        p_mac.setText(data.get(i).macAdress);



        return view;
    }
}
