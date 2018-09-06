package com.mobipos.app.Defaults;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobipos.app.R;

/**
 * Created by folio on 9/6/2018.
 */

public class Alerts {

    Activity activity;
    boolean error;
    String message;

    public Alerts(Activity activity, boolean error, String message) {
        this.activity = activity;
        this.error = error;
        this.message = message;
    }

    public void showDialog(){
        View view= LayoutInflater.from(activity).inflate(R.layout.alert_dialog,null);

        ImageView imageView=view.findViewById(R.id.error_img);
        TextView textView = view.findViewById(R.id.alert_message);

        if(!error) imageView.setImageResource(R.drawable.success_icon);
        textView.setText(message);

        AlertDialog alertDialog;
        alertDialog=new AlertDialog.Builder(activity).create();
        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        alertDialog.show();
    }
}
