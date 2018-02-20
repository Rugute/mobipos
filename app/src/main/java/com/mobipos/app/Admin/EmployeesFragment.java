package com.mobipos.app.Admin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.EmployeeData;
import com.mobipos.app.Admin.Adapters.EmployeesAdapter;
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

public class EmployeesFragment extends Fragment {
    ListView listView;
    Users users;
    private List<EmployeeData> employeeData;
    FloatingActionButton fadduser;
    ImageView del_user;



    public static EmployeesFragment newInstance(){
        EmployeesFragment fragment= new EmployeesFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_view_users, container, false);
    }
    public  void onViewCreated(View view,Bundle savedInstanceState){
        users = new Users(getContext(), defaults.database_name, null, 1);
        listView=view.findViewById(R.id.list_users);
        fadduser=view.findViewById(R.id.fab_add_users);
        del_user=view.findViewById(R.id.del);



        fadduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                fragment = AdminAddEmployees.newInstance();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                transaction.replace(R.id.frame_layout, fragment);
                transaction.commit();
            }
        });

        new ViewEmployees().execute();





    }
    public class ViewEmployees extends AsyncTask<String, String, String> {
        int success = 0;
        String serverMessage;
        JSONArray user_name,user_code,user_branch;
        String outlet = null;


        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List paramters = new ArrayList();
            employeeData=new ArrayList<>();

            paramters.add(new BasicNameValuePair("user_id", users.get_user_id()));

            JSONObject jsonObject = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.admin_get_employees,
                    "GET", paramters);
            Log.d("data recieved", jsonObject.toString());

            try {
                success = jsonObject.getInt("success");
                user_name = jsonObject.getJSONArray("data");
                user_code=jsonObject.getJSONArray("data");
                user_branch=jsonObject.getJSONArray("data");


                for (int i = 0; i <user_name.length(); i++) {
                    JSONObject jobj = user_name.getJSONObject(i);
                    employeeData.add(new EmployeeData(jobj.getString("employee_name"),
                            jobj.getString("employee_id"),jobj.getString("branch")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (success == 1) {
                if (employeeData.size() > 0) {
                    EmployeesAdapter adapter=new EmployeesAdapter(getContext(),employeeData);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);

                } else {
                    Toast.makeText(getActivity(), "No Employees Available", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }



}
