package com.mobipos.app.Admin.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.EmployeesFragment;
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
 * Created by root on 2/12/18.
 */

public class EmployeesAdapter extends BaseAdapter {
    List<EmployeeData> employeeData;
    Context context;
    LayoutInflater inflater=null;


    static String user_id;

    public EmployeesAdapter(Context context, List<EmployeeData> employeeData){
        this.employeeData=employeeData;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return employeeData.size();
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
        view=inflater.inflate(R.layout.admin_users_item,null);
        TextView name=view.findViewById(R.id.user_name);
        final TextView code=view.findViewById(R.id.user_unicode);
        TextView branch=view.findViewById(R.id.user_branch);
        ImageView delete_icon=view.findViewById(R.id.del);
        name.setText(employeeData.get(i).user_name);
        code.setText(employeeData.get(i).user_code);
        branch.setText(employeeData.get(i).user_branch);

        delete_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id=code.getText().toString();

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete user?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteUser().execute();

                        employeeData.remove(i);
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

    public class DeleteUser extends AsyncTask<String, String, String> {
        int successState = 0;
        String serverMessage;
        JSONArray branch;
        String outlet = null;

        String TAG_MESSAGE="message";
        String TAG_SUCCESS="success";
        String USER_ID=null;
        ProgressDialog dialog=new ProgressDialog(context);

        protected void onPreExecute() {


            dialog.setMessage("Deleting User...");
            dialog.setCancelable(false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("tb_name","tb_employees"));
            jsonObjectData.add(new BasicNameValuePair("id",user_id));
            jsonObjectData.add(new BasicNameValuePair("col_name","employee_id"));


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
