package com.mobipos.app.Defaults;

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

    public static String newprinter="";
    public static String newprintermac="";

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

    //    new PrinterSelection().execute();

        faddprinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPrinter();

            }
        });

    }

    public class PrinterSelection extends AsyncTask<String, String, String> {
        int success = 0;
        String serverMessage;
        JSONArray printer,printermac;
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
                            AppConfig.view_printers,
                    "GET", paramters);
            Log.d("data recieved", jsonObject.toString());

            try {
                success = jsonObject.getInt("success");
                printer = jsonObject.getJSONArray("data");
                printermac=jsonObject.getJSONArray("data");

                AppConfig.printerName = new String[printer.length()];
                AppConfig.printerMac = new String[printermac.length()];
                for (int i = 0; i <printer.length(); i++) {
                    JSONObject jobj = printer.getJSONObject(i);
                    AppConfig.printerName[i] = jobj.getString("measurement_name");
                    AppConfig.printerMac[i] = jobj.getString("single_unit")+" Per Unit";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (success == 1) {
                if (AppConfig.printerName.length > 0) {
                    PrinterSetAdapter adapter=new PrinterSetAdapter(getContext(),AppConfig.printerName,AppConfig.printerMac);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);

                } else {
                    Toast.makeText(getActivity(), "No Printer Available", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public void AddPrinter() {
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.printer_add_popup, null);
        Button addme=view.findViewById(R.id.add_printer);
        final EditText newprinteritem=view.findViewById(R.id.new_printer_name);
        final EditText newprintermacitem=view.findViewById(R.id.new_printer_mac);

        addme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newprinter=newprinteritem.getText().toString();
                newprintermac=newprintermacitem.getText().toString();
                if(!TextUtils.isEmpty(newprinteritem.getText())&&!TextUtils.isEmpty(newprintermacitem.getText())){
                    new PrinterAddition().execute();
                    dialog.cancel();
                }else {
                    Toast.makeText(getContext(),"Please input all details",Toast.LENGTH_SHORT).show();

                }
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    public class  PrinterAddition extends AsyncTask<String, String, String> {
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
            jsonObjectData.add(new BasicNameValuePair("name",newprinter));
            jsonObjectData.add(new BasicNameValuePair("single_unit",newprintermac));
            jsonObjectData.add(new BasicNameValuePair("user_id",users.get_user_id()));

            JSONObject jsonObjectResponse = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.add_printer,
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


            new PrinterSelection().execute();
        }
    }

}
