package com.mobipos.app.Admin.Adapters;

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

import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 2/8/18.
 */

public class MeasurementAdapter extends BaseAdapter {

    List<MeasureData>measureData;

    String[] measure,value;
    Context context;
   public static String measure_id;

    public static LayoutInflater inflater= null ;



    public MeasurementAdapter(Context context,List<MeasureData> measureData) {
        this.measureData=measureData;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {

        return measureData.size();
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

        view=inflater.inflate(R.layout.measure_custom_item,null);
        TextView unit1=(TextView)view.findViewById(R.id.unit_measure);
        TextView unit_v=(TextView)view.findViewById(R.id.unit_value);
        TextView measure1=view.findViewById(R.id.measure_id1);
        ImageView delet=(ImageView)view.findViewById(R.id.del);

        unit1.setText(measureData.get(i).measure);
        unit_v.setText(measureData.get(i).value);
        measure1.setText(measureData.get(i).measurement_id);

        delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                measure_id=measureData.get(i).measurement_id;

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this Measurement?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteMeasure().execute();

                        measureData.remove(i);
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




    public class DeleteMeasure extends AsyncTask<String, String, String> {
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
            dialog.setMessage("Deleting Measurement...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("id",measure_id));
            jsonObjectData.add(new BasicNameValuePair("tb_name","tb_measurements"));
            jsonObjectData.add(new BasicNameValuePair("col_name","measurement_id"));


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
