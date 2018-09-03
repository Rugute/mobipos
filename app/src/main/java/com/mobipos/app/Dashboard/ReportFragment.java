package com.mobipos.app.Dashboard;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.Manifest.*;
import static android.os.Environment.DIRECTORY_DOCUMENTS;

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
    int flag_click=0;

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
                flag_click=1;
                new SelectBranch().execute();

            }
        });

        genreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(date1.getText())||TextUtils.isEmpty(date2.getText())||flag_click==0){
                    Toast.makeText(getContext(), "Missing some information", Toast.LENGTH_SHORT).show();
                }else{
                    downloader();
                }

            }
        });



    }

    public static final int reqeustcode=123;

    public static String fileName,url;

    public void downloader() {
        File rootDirectory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "MAUZO REPORTS");
        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }

        fileName = "Report From-" + date1.getText().toString() + " To- " + date2.getText().toString() + ".csv";


        String variables = "from=" + date1.getText().toString() + "&to=" +
                date2.getText().toString() + "&client_id=" + users.get_user_id("admin");
        url = AppConfig.protocol + AppConfig.admin_get_reports + variables;
        //  String url="http://mauzoafrica.mutengeneresort.com/app/reciepts/Atfortech%20Dynamics-03-04-18-21-45-49.pdf";
        //   String fileName=URLUtil.guessFileName(url,null,MimeTypeMap.getFileExtensionFromUrl(url));

        String nameOfFile = URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url));
        File file = new File(rootDirectory, fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                download(url,fileName);

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqeustcode);

            }

        } else {
            download(url, fileName);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == requestCode
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                download(url,fileName);
        }
    }


    public void download(String url,String fileName){
        Log.d("url link",url);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Mauzo Africa Reports.csv");
        request.setDescription("File is being Downloaded...");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);

        DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        manager.enqueue(request);
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

        ProgressDialog dialog=new ProgressDialog(getContext());

        protected  void onPreExecute(){
            super.onPreExecute();

            dialog.setMessage("Loading branches. Please wait..");
            dialog.setCancelable(false);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();

            paramters.add(new BasicNameValuePair("user_id",users.get_user_id("admin")));

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

            dialog.dismiss();
        }
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");

            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Loading... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {

                Log.d("download url",f_url[0]);
                String root = Environment.getExternalStorageDirectory().toString();

                System.out.println("Downloading");
                URL url = new URL(f_url[0]);

                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file

                OutputStream output = new FileOutputStream(root+"/mauzo-data.xls");
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;

                    // writing data to file
                    output.write(data, 0, count);

                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }



        /**
         * After completing background task
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            System.out.println("Downloaded");

            pDialog.dismiss();
        }

    }

}
