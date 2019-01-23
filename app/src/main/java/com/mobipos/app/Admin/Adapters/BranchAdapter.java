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
 * Created by root on 1/23/18.
 */

public class BranchAdapter extends BaseAdapter {

    String[] branch;
    Context context;
    String branchId;

    List<Holder> branchList=new ArrayList<>();
    public static LayoutInflater inflater= null ;



    public BranchAdapter(Context context,String[] branch) {
        this.branch=branch;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {

        return branch.length;
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

        for (int k=0;k<branch.length;k++){
            Holder h = new Holder(branch[k]);
            branchList.add(h);
        }

        view=inflater.inflate(R.layout.branch_list_item,null);
        TextView branchtxt=(TextView)view.findViewById(R.id.branch);

        ImageView imageView = view.findViewById(R.id.del);
        imageView.setVisibility(View.VISIBLE);
        branchtxt.setText(branchList.get(i).branchName);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                branchId = AppConfig.branchIds[i];

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to deactivate your "+branchList.get(i).branchName+" branch?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteBranch().execute();

                        branchList.remove(i);
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

    public class DeleteBranch extends AsyncTask<String, String, String> {
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
            dialog.setMessage("Deactivating branch...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("id",branchId));
            jsonObjectData.add(new BasicNameValuePair("tb_name","tb_shops"));
            jsonObjectData.add(new BasicNameValuePair("col_name","tb_shop_id"));


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

    class Holder{
        private String branchName;

        public Holder(String branchName) {
            this.branchName = branchName;
        }


        public String getBranchName() {
            return branchName;
        }
    }
}
