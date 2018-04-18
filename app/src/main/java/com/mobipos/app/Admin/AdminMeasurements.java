package com.mobipos.app.Admin;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.MeasureData;
import com.mobipos.app.Admin.Adapters.MeasurementAdapter;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 2/8/18.
 */

public class AdminMeasurements extends Fragment {
    ListView listView;
    Users users;
    List<MeasureData> measureDataList;
    FloatingActionButton fmeasure;

    public static String newmeasure="";
    public static String newmeasurevalue="";

    public static AdminMeasurements newInstance(){
        AdminMeasurements fragment=new AdminMeasurements();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_measurement_fragment, container, false);
    }
    public void onViewCreated(View view,Bundle savedInstanceState){
        users = new Users(getContext(), defaults.database_name, null, 1);
        fmeasure=view.findViewById(R.id.fab_add_measure);
        listView=view.findViewById(R.id.measure_listview);

        new MeasureSelection().execute();

        fmeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMeasurement();

            }
        });

    }

    public class MeasureSelection extends AsyncTask<String, String, String> {
        int success = 0;
        String serverMessage;
        JSONArray measureArray;
        String outlet = null;
        ProgressDialog dialog=new ProgressDialog(getActivity());

        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Loading data.please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            measureDataList=new ArrayList<>();
            JSONParser jsonParser = new JSONParser();
            List paramters = new ArrayList();

            paramters.add(new BasicNameValuePair("user_id", users.get_user_id("admin")));

            JSONObject jsonObject = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.admin_get_measurements,
                    "GET", paramters);
            Log.d("data recieved", jsonObject.toString());

            try {
                success = jsonObject.getInt("success");
                measureArray=jsonObject.getJSONArray("data");
                for(int i=0;i<measureArray.length();i++) {
                    JSONObject jObj = measureArray.getJSONObject(i);
                    measureDataList.add(new MeasureData(jObj.getString("measurement_id"), jObj.getString("measurement_name"),
                            jObj.getString("single_unit")+"per unit"));
                }
                //measureDataList.add(new MeasureData("measurement_id", "","" ));
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (success == 1) {
                if (measureDataList.size() > 0) {
                    listView.setAdapter(new MeasurementAdapter(getContext(),measureDataList));
                } else {
                    Toast.makeText(getActivity(), "No Measurement Available", Toast.LENGTH_SHORT).show();
                }

            }
            dialog.cancel();
        }
    }

    public void AddMeasurement() {
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.measurement_add_popup, null);
        Button addme=view.findViewById(R.id.btn_add_measurement);
         final EditText newmeasureitem=view.findViewById(R.id.new_measure);
        final EditText newmeasurev=view.findViewById(R.id.new_measure_unit);

        addme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newmeasure=newmeasureitem.getText().toString();
                newmeasurevalue=newmeasurev.getText().toString();
                if(!TextUtils.isEmpty(newmeasureitem.getText())&&!TextUtils.isEmpty(newmeasureitem.getText())){
                    new MeasurementAddition().execute();
                    dialog.cancel();
                }else {
                    Toast.makeText(getContext(),"Please input all details",Toast.LENGTH_SHORT).show();

                }
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    public class  MeasurementAddition extends AsyncTask<String, String, String> {
        int successState = 0;
        String serverMessage;
        JSONArray branch;
        String outlet = null;

        String TAG_MESSAGE="message";
        String TAG_SUCCESS="success";
        String USER_ID=null;


        ProgressDialog dialog=new ProgressDialog(getContext());
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Loading data.please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("name",newmeasure));
            jsonObjectData.add(new BasicNameValuePair("single_unit",newmeasurevalue));
            jsonObjectData.add(new BasicNameValuePair("user_id",users.get_user_id("admin")));

            JSONObject jsonObjectResponse = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.admin_add_measurements,
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

            if(successState==1){
                Toast.makeText(getActivity(),serverMessage,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(),"server error",Toast.LENGTH_SHORT).show();
            }

            dialog.cancel();
            new MeasureSelection().execute();
        }
    }
}
