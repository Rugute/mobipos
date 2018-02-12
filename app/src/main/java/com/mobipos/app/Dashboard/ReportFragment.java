package com.mobipos.app.Dashboard;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mobipos.app.Admin.Adapters.QuickSaleAdapter;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 12/8/17.
 */

public class ReportFragment extends Fragment{
    Button datefrom,dateto,genreport;
    TextView branchselect;
    EditText date1,date2;
    Users users;
    Categories categoriesdb;
    Calendar calendar=Calendar.getInstance();

    DatePickerDialog fromPicker,toPicker;
    public static ReportFragment newInstance(){
        ReportFragment fragment= new ReportFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_report_fragment, container, false);
    }
    public void onViewCreated(View view ,Bundle savedInstanceState){
       datefrom=(Button)view.findViewById(R.id.button_from);
       dateto=(Button)view.findViewById(R.id.button_to);
       genreport=(Button)view.findViewById(R.id.gen);
       date1=(EditText)view.findViewById(R.id.fro1);
       date2=(EditText)view.findViewById(R.id.editText);
       branchselect=(TextView)view.findViewById(R.id.choose);
        users=new Users(getContext(), defaults.database_name,null,1);
        //categoriesdb=new Categories(getContext(), defaults.database_name,null,1);


      final DatePickerDialog.OnDateSetListener fDate=new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker datePicker, int year, int month, int day) {
              calendar.set(Calendar.YEAR,year);
              calendar.set(Calendar.MONTH,month);
              calendar.set(Calendar.DAY_OF_MONTH,day);
              updateDate(date1);
          }
      } ;


        final DatePickerDialog.OnDateSetListener tDate=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
                updateDate(date2);
            }
        } ;

      datefrom.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              new DatePickerDialog(getContext(),fDate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                      calendar.get(Calendar.DAY_OF_MONTH)).show();
          }
      });

      dateto.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              new DatePickerDialog(getContext(),tDate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                      calendar.get(Calendar.DAY_OF_MONTH)).show();
          }
      });
        branchselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SelectBranch().execute();

            }
        });



    }

    public void updateDate(EditText editText){
        String format="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(format, Locale.US);
        editText.setText(dateFormat.format(calendar.getTime()));
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
                    branchselect.setText(AppConfig.branchNames[pos]);
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



        protected  void onPreExecute(){
            super.onPreExecute();
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
        }
    }

}
