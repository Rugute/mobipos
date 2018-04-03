package com.mobipos.app.Admin;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.AdminSubsRvAdapter;
import com.mobipos.app.Admin.Adapters.AdminSubscriptionData;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 1/6/18.
 */

public class Subscription extends Fragment{

    public static Subscription newInstance(){
        Subscription fragment= new Subscription();
        return fragment;
    }
    Users usersdb;
    List<AdminSubscriptionData> SubscriptionData;
    RecyclerView srv;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subscription_status, container, false);
    }

    public void onViewCreated(View view,Bundle savedInstanceState){
        usersdb=new Users(getContext(), defaults.database_name,null,1);
        srv = view.findViewById(R.id.subsrv);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        srv.setLayoutManager(llm);
        srv.setHasFixedSize(true);

       new  loadData().execute();


    }
    private void initializeAdapter(List<AdminSubscriptionData> adminSubscriptionData){
        AdminSubsRvAdapter adapter = new AdminSubsRvAdapter(adminSubscriptionData);
        adapter.notifyDataSetChanged();
        srv.setAdapter(adapter);
    }
    public class loadData extends AsyncTask<String,String,String> {

        int success;
        ProgressDialog dialog=new ProgressDialog(getContext());
        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setMessage("Loading data.please wait...");
            dialog.setCancelable(false);
            dialog.show();

            //   dialog(true);
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            SubscriptionData=new ArrayList<>();
            List parameters=new ArrayList();

            parameters.add(new BasicNameValuePair("user_id",usersdb.get_user_id()));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+ AppConfig.hostname+
                            AppConfig.get_transactions,
                    "GET",parameters);

            try{
                success=jsonObject.getInt("success");
                JSONArray jsonArray=jsonObject.getJSONArray("data");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jObj=jsonArray.getJSONObject(i);
                    SubscriptionData.add(new AdminSubscriptionData(jObj.getString("date"),
                            jObj.getString("months"),
                            jObj.getString("trans"),
                            jObj.getInt("")));

                }


            }catch (Exception e){

            }
            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);

            if(success==1){
                initializeAdapter(SubscriptionData);
            }else{
                Toast.makeText(getActivity(),"error while loading data",Toast.LENGTH_SHORT).show();
            }
            dialog.cancel();
        }
    }
}
