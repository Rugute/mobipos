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

public class DiscountsAdapter extends BaseAdapter {

    String[] discounts,dvalue,id;
    List<DiscountData> discountData;
    Context context;
    public  static String disc_id;

    public static LayoutInflater inflater= null ;



    public DiscountsAdapter(Context context, List<DiscountData>discountData) {
        this.discountData=discountData;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {

        return discountData.size();
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

        view=inflater.inflate(R.layout.discount_custom_item,null);
        TextView unit1=(TextView)view.findViewById(R.id.discount_name);
        TextView unit_v=(TextView)view.findViewById(R.id.discount_value);
        ImageView delete_disc=(ImageView)view.findViewById(R.id.del);

        unit1.setText(discountData.get(i).discount);
        unit_v.setText(discountData.get(i).dvalue);


        delete_disc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disc_id=discountData.get(i).discount_id;

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete discount?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteDiscounts().execute();

                        discountData.remove(i);
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

    public class DeleteDiscounts extends AsyncTask<String, String, String> {
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
            dialog.setMessage("Deleting Discount...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("id",disc_id));
            jsonObjectData.add(new BasicNameValuePair("tb_name","tb_discounts"));
            jsonObjectData.add(new BasicNameValuePair("col_name","discount_id"));


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
