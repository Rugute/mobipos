package com.mobipos.app.Cashier;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobipos.app.R;

/**
 * Created by root on 12/12/17.
 */

public class CashierInventoryAdapter extends BaseAdapter  {

    int[] images;
    String[] title;
    Context context;
    public static LayoutInflater inflater=null;


    public CashierInventoryAdapter(Activity activity,int[] images,String[] title){
        this.images=images;
        this.title=title;
        context=activity;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return images.length;
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

        view = inflater.inflate(R.layout.custom_gridview, null);
        TextView title = (TextView) view.findViewById(R.id.menu_name);
        ImageView imageView = (ImageView) view.findViewById(R.id.menu_action_image);

        try{
            imageView.setImageResource(images[i]);
        }catch (OutOfMemoryError e){
           imageView.setImageResource(R.mipmap.ic_launcher);
        }

        title.setText(this.title[i]);
        return view;
    }
}
