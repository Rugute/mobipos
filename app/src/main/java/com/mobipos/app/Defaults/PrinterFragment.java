package com.mobipos.app.Defaults;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.mobipos.app.Admin.AdminMeasurements;
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
 * Created by folio on 2/28/2018.
 */

public class PrinterFragment extends Fragment {
    ListView listView;
    Users users;
    FloatingActionButton faddprinter;
    List<PrinterData> printerDataList;


    public static PrinterFragment newInstance(){
        PrinterFragment fragment=new PrinterFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.printers, container, false);
    }
    public void onViewCreated(View view,Bundle savedInstanceState){
        users = new Users(getContext(), defaults.database_name, null, 1);
        faddprinter=view.findViewById(R.id.fab_add_printer);
        listView=view.findViewById(R.id.printer_listview);

       new PrinterSelection().execute();

        faddprinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                fragment= AddPrinterFragment.newInstance();
                FragmentTransaction transaction=getFragmentManager().beginTransaction().addToBackStack("Back");
                transaction.replace(R.id.frame_layout,fragment);
                transaction.commit();

            }
        });

    }

    public class PrinterSelection extends AsyncTask<String, String, String> {
        int success = 0;
        String serverMessage;
        JSONArray printerArray;
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
            printerDataList=new ArrayList<>();
            JSONParser jsonParser = new JSONParser();
            List paramters = new ArrayList();

            paramters.add(new BasicNameValuePair("user_id", users.get_user_id("admin")));

            JSONObject jsonObject = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.view_printers,
                    "GET", paramters);
            Log.d("data recieved", jsonObject.toString());

            try {
                success = jsonObject.getInt("success");
               JSONArray branches=jsonObject.getJSONArray("data");

               for(int j=0;j<branches.length();j++){
                   JSONObject jobj_printer=branches.getJSONObject(j);
                   String branch_name=jobj_printer.getString("branch_name");

                   JSONArray printerArray=jobj_printer.getJSONArray("branch_printers");
                   for(int i=0;i<printerArray.length();i++) {
                       JSONObject jObj = printerArray.getJSONObject(i);
                       printerDataList.add(new PrinterData(jObj.getString("id"), jObj.getString("printer_name"),
                               jObj.getString("printer_mac"),branch_name));
                   }
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
                if (printerDataList.size() > 0) {
                    listView.setAdapter(new PrinterSetAdapter(getContext(),printerDataList));
                } else {
                    Toast.makeText(getActivity(), "No Printers Available", Toast.LENGTH_SHORT).show();
                }

            }
            dialog.cancel();
        }
    }
//    public void AddPrinter() {
//        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.printer_add_popup, null);
//        Button addme=view.findViewById(R.id.add_printer);
//        final EditText newprinteritem=view.findViewById(R.id.new_printer_name);
//        final EditText newprintermacitem=view.findViewById(R.id.new_printer_mac);
//
//        addme.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                newprinter=newprinteritem.getText().toString();
//                newprintermac=newprintermacitem.getText().toString();
//                if(!TextUtils.isEmpty(newprinteritem.getText())&&!TextUtils.isEmpty(newprintermacitem.getText())){
//                    new PrinterAddition().execute();
//                    dialog.cancel();
//                }else {
//                    Toast.makeText(getContext(),"Please input all details",Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//
//        dialog.setView(view);
//        dialog.show();
//    }



}
