package com.mobipos.app.Defaults;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog.Builder;

import com.mobipos.app.services.DatabaseUpdate;

public class CheckInternetSettings {
    public Context context;

    public CheckInternetSettings(Context activity) {
        this.context = activity;
    }



    public void showSettingsAlert() {
        Builder alertDialog = new Builder(this.context);
        alertDialog.setTitle((CharSequence) "Internet setting");
        alertDialog.setMessage((CharSequence) "Internet is not enabled. do you want to go to settings menu");
        alertDialog.setPositiveButton((CharSequence) "Settings", new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                CheckInternetSettings.this.context.startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
            }
        });
        alertDialog.setNegativeButton((CharSequence) "cancel", new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    public boolean isNetworkConnected() {
        if (((ConnectivityManager) this.context.getSystemService("connectivity")).getActiveNetworkInfo() != null) {
            return true;
        }
        return false;
    }
}
