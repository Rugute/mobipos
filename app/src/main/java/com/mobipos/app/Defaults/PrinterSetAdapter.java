package com.mobipos.app.Defaults;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.MeasurementAdapter;
import com.mobipos.app.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 2/28/2018.
 */

public class PrinterSetAdapter extends BaseAdapter {

    List<PrinterData>printerData;

   // String[] printer,printermac;
    Context context;
    public static String printer_id;
    public static LayoutInflater inflater= null ;



    public PrinterSetAdapter(Context context,List<PrinterData>printerData) {
        this.printerData=printerData;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {

        return printerData.size();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        view=inflater.inflate(R.layout.printer_item,null);
        TextView pname=(TextView)view.findViewById(R.id.printer_name);
        TextView pmac=(TextView)view.findViewById(R.id.printer_mac);
        TextView pbranch=(TextView)view.findViewById(R.id.printer_branch);
        ImageView delet=view.findViewById(R.id.delete_printer);

        pname.setText(printerData.get(i).printer_name);
        pmac.setText(printerData.get(i).printer_mac);
        pbranch.setText(printerData.get(i).branch);


        delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printer_id=printerData.get(i).printer_id;

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this Printer?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeletePrinter().execute();

                        printerData.remove(i);
                        notifyDataSetInvalidated();
                        notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        return view;
    }

    public class DeletePrinter extends AsyncTask<String, String, String> {
        String measure_id_;

        int successState = 0;
        String serverMessage;
        JSONArray branch;
        String outlet = null;

        String TAG_MESSAGE="message";
        String TAG_SUCCESS="success";
        String USER_ID=null;
        ProgressDialog dialog=new ProgressDialog(context);

        protected void onPreExecute() {

            super.onPreExecute();
            dialog.setMessage("Deleting Printer...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("id",printer_id));
            jsonObjectData.add(new BasicNameValuePair("tb_name","tb_printers"));
            jsonObjectData.add(new BasicNameValuePair("col_name","id"));


            JSONObject jsonObjectResponse = jsonParser.makeHttpRequest(AppConfig.protocol +
                            AppConfig.admin_delete_user,
                    "GET", jsonObjectData);

            Log.d("result",jsonObjectResponse.toString());

            try {

                int success=jsonObjectResponse.getInt(TAG_SUCCESS);
                serverMessage=jsonObjectResponse.getString(TAG_MESSAGE);

                if(success==1){
                    successState=1;

                }
            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Toast.makeText(context,serverMessage,Toast.LENGTH_SHORT).show();

            dialog.cancel();
        }
    }
}
