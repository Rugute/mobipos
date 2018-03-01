package com.mobipos.app.Admin;


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
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminAddItemData;
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
 * Created by root on 2/12/18.
 */

public class AdminAddEmployees extends Fragment {
    EditText user1,email1,phone1,id1,res1;
    Users users;
    Button btnadd,shop1;

    private ProgressDialog pDialog;

    public static String username="";
    public static String shop="";
    public static String residence="";
    public static String phone="";
    public static String useremail="";
    public static String idnumber="";


    public static AdminAddEmployees newInstance(){
        AdminAddEmployees fragment=new AdminAddEmployees();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_add_users, container, false);
    }
    public  void onViewCreated(View view,Bundle savedInstanceState){
        users = new Users(getContext(), defaults.database_name, null, 1);
        user1=view.findViewById(R.id.emp_username);
        email1=view.findViewById(R.id.emp_email);
        phone1=view.findViewById(R.id.emp_phone);
        id1=view.findViewById(R.id.id_number);
        res1=view.findViewById(R.id.emp_residence);
        shop1=view.findViewById(R.id.choose_shop);
        btnadd=view.findViewById(R.id.addbtn);


        shop1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SelectBranch().execute();

            }
        });



        btnadd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                username= user1.getText().toString().trim();
                shop = shop1.getText().toString().trim();
                residence = res1.getText().toString().trim();
                phone = phone1.getText().toString().trim();
                useremail = email1.getText().toString().trim();
                idnumber = id1.getText().toString().trim();


                if (!username.isEmpty() && !shop.isEmpty() && !residence.isEmpty() && !phone.isEmpty() && !useremail.isEmpty() && !idnumber.isEmpty()) {

                        new AddFunction().execute();



                } else {
                    Toast.makeText(getContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
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
                    shop1.setText(AppConfig.branchNames[pos]);
                    dialog.cancel();
                }
            });
        }

        dialog.setView(view);
        dialog.show();
    }

    public class AddFunction extends AsyncTask<String, String, String> {
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
            jsonObjectData.add(new BasicNameValuePair("name",username));
            jsonObjectData.add(new BasicNameValuePair("id",idnumber));
            jsonObjectData.add(new BasicNameValuePair("email",useremail));
            jsonObjectData.add(new BasicNameValuePair("residence",residence));
            jsonObjectData.add(new BasicNameValuePair("phone_number",phone));
            jsonObjectData.add(new BasicNameValuePair("shop",shop));
            jsonObjectData.add(new BasicNameValuePair("user_id",users.get_user_id()));

            JSONObject jsonObjectResponse = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.admin_add_employees,
                    "GET", jsonObjectData);

            Log.d("result",jsonObjectResponse.toString());

            try {
                JSONArray result=jsonObjectResponse.getJSONArray("resultvalue");
                JSONObject jobj=result.getJSONObject(0);

                int success=jobj.getInt(TAG_SUCCESS);
                serverMessage=jobj.getString(TAG_MESSAGE);
                USER_ID=jobj.getString("UserID");

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

            Toast.makeText(getActivity(),serverMessage,Toast.LENGTH_SHORT).show();

            Fragment fragment;
            fragment = EmployeesFragment.newInstance();
            FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
            transaction.replace(R.id.frame_layout, fragment);
            transaction.commit();

            dialog.cancel();

        }
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


            if(success==1){
                if(AppConfig.branchIds.length>0){
                    SelectOutletPop(1);
                }else{
                    SelectOutletPop(0);
                }

            }

            dialog.cancel();
        }
    }


}
