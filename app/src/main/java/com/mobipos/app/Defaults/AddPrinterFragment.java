package com.mobipos.app.Defaults;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.QuickSaleAdapter;
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
 * Created by root on 2/28/18.
 */

public class AddPrinterFragment extends Fragment {
    Users users;
    Button pickbranch,add_printer_in;
    EditText newPrinterName,newPrinterMac;

    static String printer_name,st_branch_id,str_printer_mac;

    public static String newprinter="";
    public static String newprintermac="";

    public static AddPrinterFragment newInstance(){
        AddPrinterFragment fragment=new AddPrinterFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.printer_add_fragmnent, container, false);
    }
    public void onViewCreated(View view,Bundle savedInstanceState){
        users = new Users(getContext(), defaults.database_name, null, 1);
        newPrinterName=(EditText) view.findViewById(R.id.new_printer_name);
        newPrinterMac=(EditText) view.findViewById(R.id.new_printer_mac);
        pickbranch=(Button) view.findViewById(R.id.branch_picker);
        add_printer_in=view.findViewById(R.id.add_printer);

        add_printer_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_printer_mac=newPrinterMac.getText().toString();
                printer_name=newPrinterName.getText().toString();
                new PrinterAddition().execute();
            }
        });

        pickbranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SelectBranch().execute();
            }
        });

    }

    public void SelectOutletPop (int i){
        final AlertDialog dialog= new AlertDialog.Builder(getContext()).create();
        View view=LayoutInflater.from(getContext()).inflate(R.layout.admin_list_branches,null);
        ListView listView=view.findViewById(R.id.view_outlet);
        TextView no_branch=view.findViewById(R.id.no_branch);

        if(i==0){
            no_branch.setVisibility(View.VISIBLE);
        }else{
            listView.setAdapter(new QuickSaleAdapter(getContext(),AppConfig.branchNames));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int pos= (int) adapterView.getItemIdAtPosition(i);
                    AppConfig.selected_branch_id=AppConfig.branchIds[pos];
                    pickbranch.setText(AppConfig.branchNames[pos]);
                    dialog.cancel();
                }
            });
        }

        dialog.setView(view);
        dialog.show();
    }

    public class SelectBranch extends AsyncTask<String,String,String> {
        int success=0;
        String serverMessage;
        JSONArray branch ;
        String outlet=null;

        ProgressDialog dialog=new ProgressDialog(getContext());

        protected  void onPreExecute(){

            super.onPreExecute();

            dialog.setMessage("Loading data.please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();


            paramters.add(new BasicNameValuePair("user_id",users.get_user_id()));
            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+
                            AppConfig.admin_select_branches,
                    "GET",paramters);
            Log.d("data recieved",jsonObject.toString());

            try {
                success=jsonObject.getInt("success");
                branch=jsonObject.getJSONArray("data");

                AppConfig.branchNames=new String[branch.length()];
                AppConfig.branchIds=new String[branch.length()];
                for(int i=0;i<branch.length();i++){
                    JSONObject jobj=branch.getJSONObject(i);
                    AppConfig.branchNames[i]=jobj.getString("shop_name");
                    AppConfig.branchIds[i]=jobj.getString("shop_id");
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            dialog.cancel();
            if(success==1){
                if(AppConfig.branchIds.length>0){
                    SelectOutletPop(1);
                }else{
                    SelectOutletPop(0);
                }

            }
        }
    }







    public class  PrinterAddition extends AsyncTask<String, String, String> {
        int successState = 0;
        String serverMessage;
        JSONArray branch;
        String outlet = null;

        String TAG_MESSAGE="message";
        String TAG_SUCCESS="success";
        String USER_ID=null;

        ProgressDialog dialog=new ProgressDialog(getContext());

        protected void onPreExecute() {

            dialog.setMessage("Loading data.please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> parameters=new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("branch_id",AppConfig.selected_branch_id));
            parameters.add(new BasicNameValuePair("name",printer_name));
            parameters.add(new BasicNameValuePair("mac_address",str_printer_mac));

            JSONObject jsonObjectResponse = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.admin_add_printer,
                    "GET", parameters);

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

            Fragment fragment;
            fragment= PrinterFragment.newInstance();
            FragmentTransaction transaction=getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout,fragment);
            transaction.commit();
        }
    }
}
