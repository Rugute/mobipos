package com.mobipos.app.Admin;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

    TextView name,paydate,trans_code,expiry,phone_number;
    String str_paydate,str_transcode,str_expiring;

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

        name=view.findViewById(R.id.nameOfPerson);
        paydate=view.findViewById(R.id.paydate);
        trans_code=view.findViewById(R.id.transaction_code);
        expiry=view.findViewById(R.id.expiry);
        phone_number=view.findViewById(R.id.phoneOfPerson);

        name.setText(usersdb.get_user_name());



       new  loadData().execute();


    }
    private void initializeAdapter(List<AdminSubscriptionData> data){
        AdminSubsRvAdapter adapter = new AdminSubsRvAdapter(data);
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

            parameters.add(new BasicNameValuePair("user_id",usersdb.get_user_id("admin")));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+ AppConfig.hostname+
                            AppConfig.get_transactions,
                    "GET",parameters);

            try{
                success=jsonObject.getInt("success");
                Log.d("success state",String.valueOf(success));
                JSONArray jsonArray=jsonObject.getJSONArray("data");

                if(success==1){
                    Log.d("subscription details",jsonArray.toString());
                    str_expiring=jsonObject.getString("expiring_date");
                    str_paydate=jsonObject.getString("date_of_payment");
                    str_transcode=jsonObject.getString("transcation_code");
                    int i=0;
                    while (i<jsonArray.length()){
                      JSONObject jsonObject1=jsonArray.getJSONObject(i);
                      Log.d("jobj",jsonObject1.toString());

                      AdminSubscriptionData data_new=new AdminSubscriptionData(
                              jsonObject1.getString("subscriptions_id"),
                              jsonObject1.getString("trans"),
                              jsonObject1.getString("months"),
                              jsonObject1.getString("date")
                      );
                      Log.d("new Data sub",data_new.subscriptionId);
                      Log.d("new Data trans",data_new.trans);
                      Log.d("new Data months",data_new.valid);
                      Log.d("new Data date",data_new.name);

                      SubscriptionData.add(data_new);
                        i++;

                    }

                }


            }catch (Exception e){

            }
            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);
            Log.d("subscription itself",SubscriptionData.toString());
            if(success==1){

                initializeAdapter(SubscriptionData);
                expiry.setText("Expiring date: "+str_expiring);
                paydate.setText("Date of Payment: "+str_paydate);
                trans_code.setText("Code: "+str_transcode);
            }else{
                Toast.makeText(getActivity(),"error while loading data",Toast.LENGTH_SHORT).show();
            }
            dialog.cancel();
        }
    }
}
