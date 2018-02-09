package com.mobipos.app.Admin.DashboardFragments.Settings;

import android.app.ProgressDialog;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.MeasureMarginAdapter;
import com.mobipos.app.Admin.Adapters.QuickSaleAdapter;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminAddItem;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminAddItemData;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.MeasureMarginData;
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
 * Created by folio on 2/9/2018.
 */

public class TaxesFragment extends Fragment {


    public static TaxesFragment newInstance(){
        TaxesFragment fragment = new TaxesFragment();
        return fragment;
    }
    Button btn_title;
    ListView listView;
    Users usersdb;
    List<MeasureMarginData> tax_margins;
    FloatingActionButton fab_add_tax;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_list_branches, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(View view,Bundle savedInstanceState){
        usersdb=new Users(getContext(), defaults.database_name,null,1);
        listView=view.findViewById(R.id.view_outlet);
        btn_title=view.findViewById(R.id.btn_title);
        fab_add_tax=view.findViewById(R.id.fab_add_items);

        fab_add_tax.setVisibility(View.VISIBLE);

        fab_add_tax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_tax_popup();
            }
        });



        btn_title.setText("Tax Margins");

        new loadData().execute();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos= (int) adapterView.getItemIdAtPosition(i);
                AdminAddItemData.tax_margin=tax_margins.get(pos).id;
            }
        });

    }

    public class loadData extends AsyncTask<String, String, String> {
        int success = 0;
        String serverMessage;
        JSONArray taxArray;
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
            tax_margins=new ArrayList<>();
            JSONParser jsonParser = new JSONParser();
            List paramters = new ArrayList();

            paramters.add(new BasicNameValuePair("user_id", usersdb.get_user_id()));

            JSONObject jsonObject = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.get_taxes,
                    "GET", paramters);
            Log.d("data recieved", jsonObject.toString());

            try {
                success = jsonObject.getInt("success");
                taxArray=jsonObject.getJSONArray("tax_margins");
                for(int i=0;i<taxArray.length();i++) {
                    JSONObject jObj = taxArray.getJSONObject(i);
                    tax_margins.add(new MeasureMarginData(jObj.getString("tax_margin_id"),
                            jObj.getString("tax")+"%",
                            jObj.getString("margin_mode")));
                }
                tax_margins.add(new MeasureMarginData("0","0%","No Tax"));
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (success == 1) {
                if (tax_margins.size() > 0) {
                    listView.setAdapter(new MeasureMarginAdapter(getContext(), tax_margins));
                } else {
                    Toast.makeText(getActivity(), "No Taxes Available", Toast.LENGTH_SHORT).show();
                }

            }
            dialog.cancel();
        }
    }

//    public void dialog(boolean state){
//        ProgressDialog dialog=new ProgressDialog(getActivity());
//        if(state){
//            dialog.setMessage("Loading data.please wait...");
//            dialog.setCancelable(false);
//            dialog.show();
//        }else {
//            dialog.cancel();
//        }
//
//    }

    static String mode_selected;
    static String tax_margin_created;

    public void add_tax_popup(){
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.admin_add_tax,null);
        final android.app.AlertDialog alertDialog=new android.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.setView(view);

        RadioGroup radioGroup= view.findViewById(R.id.margin_mode_group);
        final RadioButton radioInc=view.findViewById(R.id.radio_inclusive);
        final RadioButton radioExc=view.findViewById(R.id.radio_exclusive);
        final EditText ed_tax_margin=view.findViewById(R.id.new_tax_margin);
        Button add_tax=view.findViewById(R.id.add_tax_margin);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioExc.isChecked()){
                    mode_selected="2";
                }else if(radioInc.isChecked()){
                    mode_selected="1";
                }
            }
        });

        add_tax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tax_margin_created=ed_tax_margin.getText().toString();
                new upLoadData().execute();
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }

    public class upLoadData extends AsyncTask<String, String, String> {
        int success = 0;
        String serverMessage;
        JSONArray taxArray;
        String outlet = null;


        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... strings) {
            tax_margins=new ArrayList<>();
            JSONParser jsonParser = new JSONParser();
            List paramters = new ArrayList();

            paramters.add(new BasicNameValuePair("user_id", usersdb.get_user_id()));
            paramters.add(new BasicNameValuePair("mode", mode_selected));
            paramters.add(new BasicNameValuePair("margin",tax_margin_created));

            JSONObject jsonObject = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.add_taxes,
                    "GET", paramters);
            Log.d("data recieved", jsonObject.toString());

            try {
                success = jsonObject.getInt("success");
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (success==1){
                new loadData().execute();
                Toast.makeText(getActivity(),"Tax Created ",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
