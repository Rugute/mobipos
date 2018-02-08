package com.mobipos.app.Admin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

public class BranchFragment extends Fragment {
    Users users;
    ListView listView;
    FloatingActionButton fbranch;
    public static String newoutlet="";

    public static BranchFragment newInstance() {
        BranchFragment fragment = new BranchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_view_outlets, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        users = new Users(getContext(), defaults.database_name, null, 1);
        listView = view.findViewById(R.id.branch_listview);
        fbranch=(FloatingActionButton)view.findViewById(R.id.fab_add_branch);


        fbranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBranch();
            }
        });


        new BranchSelection().execute();

    }


    public class BranchSelection extends AsyncTask<String, String, String> {
        int success = 0;
        String serverMessage;
        JSONArray branch;
        String outlet = null;


        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List paramters = new ArrayList();

            paramters.add(new BasicNameValuePair("user_id", users.get_user_id()));

            JSONObject jsonObject = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.admin_select_branches,
                    "GET", paramters);
            Log.d("data recieved", jsonObject.toString());

            try {
                success = jsonObject.getInt("success");
                branch = jsonObject.getJSONArray("data");

                AppConfig.branchNames = new String[branch.length()];
                AppConfig.branchIds = new String[branch.length()];
                for (int i = 0; i < branch.length(); i++) {
                    JSONObject jobj = branch.getJSONObject(i);
                    AppConfig.branchNames[i] = jobj.getString("shop_name");
                    AppConfig.branchIds[i] = jobj.getString("shop_id");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (success == 1) {
                if (AppConfig.branchIds.length > 0) {
                    QuickSaleAdapter adapter=new QuickSaleAdapter(getContext(), AppConfig.branchNames);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);

                } else {
                    Toast.makeText(getActivity(), "No Branches Available", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public void AddBranch() {
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.admin_add_branch, null);
        Button add=view.findViewById(R.id.btn_add_branch);
        final EditText newbranch=view.findViewById(R.id.new_branch);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newoutlet=newbranch.getText().toString().trim();
                if(!newoutlet.isEmpty()){
                   new BranchAddition().execute();
                   dialog.cancel();
                }else {
                    Toast.makeText(getContext(),"Enter New Branch",Toast.LENGTH_SHORT).show();

                }
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    public class BranchAddition extends AsyncTask<String, String, String> {
        int successState = 0;
        String serverMessage;
        JSONArray branch;
        String outlet = null;

        String TAG_MESSAGE="message";
        String TAG_SUCCESS="success";
        String USER_ID=null;


        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("name",newoutlet));
            jsonObjectData.add(new BasicNameValuePair("user_id",users.get_user_id()));

            JSONObject jsonObjectResponse = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.add_outlet,
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
                new BranchSelection().execute();
            }
        }
    }

