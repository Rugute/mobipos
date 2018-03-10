package com.mobipos.app.Admin.Adapters;

import android.app.Activity;
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

import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.MeasureMarginData;
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
 * Created by folio on 2/8/2018.
 */

public class MeasureMarginAdapter extends BaseAdapter {
    List<MeasureMarginData> data;
    Context context;
    LayoutInflater inflater=null;
    int state;
    public MeasureMarginAdapter(Context context, List<MeasureMarginData> data,int state){
        this.context=context;
        this.data=data;
        this.state=state;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        view=inflater.inflate(R.layout.admin_select_margin_mode,null);
        TextView name=view.findViewById(R.id.unit_mode);
        TextView description=view.findViewById(R.id.unit);
        ImageView delet=view.findViewById(R.id.del);
        final TextView meas=view.findViewById(R.id.measure_id);

          meas.setText(data.get(i).id);
        name.setText(data.get(i).name);
        description.setText(data.get(i).description);

        if(state==0){
            delet.setVisibility(View.GONE);
        }

        delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marginid=meas.getText().toString();

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this tax?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteMargin().execute();

                        data.remove(i);
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

    static String marginid;

    public class DeleteMargin extends AsyncTask<String, String, String> {
        int successState = 0;
        String serverMessage;
        JSONArray branch;
        String outlet = null;

        String TAG_MESSAGE="message";
        String TAG_SUCCESS="success";
        String USER_ID=null;
        ProgressDialog dialog=new ProgressDialog(context);

        protected void onPreExecute() {


            dialog.setMessage("Deleting Tax...");
            dialog.setCancelable(false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("id",marginid));
            jsonObjectData.add(new BasicNameValuePair("tb_name","tb_tax_margins"));
            jsonObjectData.add(new BasicNameValuePair("col_name","tb_tax_id"));


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
