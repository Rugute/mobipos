package com.mobipos.app.Defaults;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobipos.app.R;

/**
 * Created by folio on 2/28/2018.
 */

public class PrinterSetAdapter extends BaseAdapter {

    String[] printer,printermac;
    Context context;

    public static LayoutInflater inflater= null ;



    public PrinterSetAdapter(Context context, String[] printer,String[] printermac) {
        this.printer=printer;
        this.printermac=printermac;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {

        return printer.length;
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
        TextView pname=(TextView)view.findViewById(R.id.new_printer_name);
        TextView pmac=(TextView)view.findViewById(R.id.new_printer_mac);

        pname.setText(printer[i]);
        pmac.setText(printermac[i]);

        return view;
    }
}
