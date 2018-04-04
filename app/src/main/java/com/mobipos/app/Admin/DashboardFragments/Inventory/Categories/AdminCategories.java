package com.mobipos.app.Admin.DashboardFragments.Inventory.Categories;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.AdminCategRvAdapter;
import com.mobipos.app.Admin.Adapters.QuickSaleAdapter;
import com.mobipos.app.Admin.DashboardAdmin;
import com.mobipos.app.Cashier.Adapters.CashierCategRvAdapter;
import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Admin.PackageConfig;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories.CashierCategoryData;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.mobipos.app.Admin.PackageConfig.branches;
import static com.mobipos.app.Admin.PackageConfig.branchesId;
import static com.mobipos.app.Admin.PackageConfig.catname;
import static com.mobipos.app.Admin.PackageConfig.selectedBranchId;

/**
 * Created by folio on 12/15/2017.
 */

public class AdminCategories extends Fragment {

    private Object item;
    private  List<AdminCategoryData> categoryData;
    private RecyclerView rv;
    Categories categories;
    Users users;
    Context context;
    RelativeLayout relativeLayout;
    Spinner branchSpinner;
    FloatingActionButton fab_add_cat;

    private ProgressBar bar;
    public static AdminCategories newInstance(){
        AdminCategories fragment = new AdminCategories();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_category, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(View view,Bundle savedInstanceState){

        users=new Users(getActivity(),defaults.database_name,null,1);

        bar=view.findViewById(R.id.progessbar_admin_category);
        categoryData=new ArrayList<>();
        fab_add_cat=view.findViewById(R.id.fab_add_category);
        rv=view.findViewById(R.id.admin_category_rv);
        branchSpinner=view.findViewById(R.id.category_spinner_admin);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        relativeLayout=(RelativeLayout)view.findViewById(R.id.no_category_layout);



        fab_add_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 new BranchSelection().execute();
            }
        });

      //  initializeData();
        showBackButton(true,"Categories");
      //  initializeAdapter();

        final CheckInternetSettings internetOn=new CheckInternetSettings(getActivity());
        if(internetOn.isNetworkConnected()){
            new loadCategories().execute();
        }else{
            AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getActivity()).
                    setTitle("Categories not found").
                    setMessage("Enable your internet to sync data from server").
                    setPositiveButton((CharSequence) "Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            internetOn.context.startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
                        }
                    });
            alertBuilder.show();
        }
    }

    private void initializeData(){
   //     categoryData= new ArrayList<>();
//        categoryData.add(new CashierCategoryData("Fruits", "Mango", R.mipmap.ic_launcher));
//        categoryData.add(new CashierCategoryData("Beverage", "Coffee", R.mipmap.ic_launcher));
//        categoryData.add(new CashierCategoryData("Meat", "Beef", R.mipmap.ic_launcher));
    }

    private void initializeAdapter(List<AdminCategoryData> data){
        AdminCategRvAdapter adapter = new AdminCategRvAdapter(data);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }

    public void showBackButton(Boolean state,String title) {
        if(state){
            if (getActivity() instanceof DashboardCashier) {
                try {
                    ((DashboardAdmin) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getActivity().setTitle(title);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }


    public class loadCategories extends AsyncTask<String,String,String>{

        int success=0;
        String serverMessage;
        JSONArray data;

        ProgressDialog dialog=new ProgressDialog(getActivity());
        protected void onPreExecute(){
            super.onPreExecute();
            showBar(true);
            dialog.setMessage("Loading data.please wait...");
            dialog.setCancelable(false);
            dialog.show();

        }
        @Override
        protected String doInBackground(String... strings) {
            categoryData=new ArrayList<>();
            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            paramters.add(new BasicNameValuePair("user_id",users.get_user_id()));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+ PackageConfig.get_admin_categories,
                    "GET",paramters);

            Log.d("json object",jsonObject.toString());

            try {
                success=jsonObject.getInt("success");
                serverMessage=jsonObject.getString("message");

                data=jsonObject.getJSONArray("data");
                branches=new String[data.length()];
                branchesId=new String[data.length()];
                for(int i=0;i<data.length();i++){
                    JSONObject jobj=data.getJSONObject(i);
                    branches[i]=jobj.getString("shop_name");
                    branchesId[i]=jobj.getString("shop_id");
                    JSONArray category_data=jobj.getJSONArray("categories");
                    for (int j=0;j<category_data.length();j++){
                        JSONObject catObj=category_data.getJSONObject(j);

                        categoryData.add(new AdminCategoryData(Integer.parseInt(branchesId[i]),catObj.getString("category_id"),
                                catObj.getString("category_name"),R.mipmap.ic_launcher));
                    }
                }




            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s){

            super.onPostExecute(s);


            if (success==1){
                dialog.cancel();
                spinnerUpdate(branches,branchSpinner,0);

                initializeAdapter(categoryData);

               }
            else{
                Toast.makeText(getActivity(),serverMessage,Toast.LENGTH_SHORT).show();
            }

            showBar(false);
        }
    }


    public void showBar(boolean state){
        if (state){
            bar.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }else{
            bar.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
    }

    public void spinnerUpdate(String[] branch_array, Spinner spinner, final int state){
        final ArrayAdapter<String> adapter;
        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,branch_array);

        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

               int id=(int)adapterView.getItemIdAtPosition(i);
               if(state==0){
                   filterList(Integer.parseInt(branchesId[id]));
               }else if(state==1){
                   selectedBranchId=branchesId[id];
               }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                initializeAdapter(categoryData);
            }
        });
    }

    public void filterList(int branch_id){
        List<AdminCategoryData> filterData=new ArrayList<>();
        for (int i=0;i<categoryData.size();i++){
            int branch=categoryData.get(i).branchId;
            if(branch==branch_id){
                filterData.add(new AdminCategoryData(categoryData.get(i).branchId,categoryData.get(i).id,
                        categoryData.get(i).name,R.mipmap.ic_launcher));
            }
        }

        initializeAdapter(filterData);
    }
    AlertDialog alertDialog;

    public void addCategory(){
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.custom_pop_up_add_category,null);
        Spinner spinner=view.findViewById(R.id.admin_add_category_spinner);
        Button btn_add=view.findViewById(R.id.btn_add_category);
        final EditText cat_name=view.findViewById(R.id.ed_add_category);

        alertDialog=new AlertDialog.Builder(getActivity()).create();
        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        alertDialog.show();

        spinnerUpdate(branches,spinner,1);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catname=cat_name.getText().toString();
                if(TextUtils.isEmpty(cat_name.getText())){
                    Toast.makeText(getActivity(),"Please add a category",Toast.LENGTH_SHORT).show();
                    alertDialog.cancel();
                }else{
                    new addCategory().execute();
                   // alertDialog.cancel();
                }

            }
        });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.cancel();
            }
        });


    }

    public class addCategory extends AsyncTask<String,String,String>{
        int success=0;
        String serverMessage;
        ProgressDialog dialog=new ProgressDialog(getContext());
        protected void onPreExecute(){
            super.onPreExecute();
          dialog.setMessage("Adding Category.Please wait...");
          dialog.setCancelable(false);
          dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            paramters.add(new BasicNameValuePair("user_id",users.get_user_id()));
            paramters.add(new BasicNameValuePair("branch_id",selectedBranchId));
            paramters.add(new BasicNameValuePair("category_name",catname));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+
                            PackageConfig.add_category,
                    "GET",paramters);

            Log.d("json object",jsonObject.toString());

            try {
                success = jsonObject.getInt("success");
                serverMessage = jsonObject.getString("message");

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String s){

            super.onPostExecute(s);


            if (success==1){
                showAddBar(false);
                dialog.cancel();

                Fragment fragment;
                fragment = AdminCategories.newInstance();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment);
                transaction.commit();

                Toast.makeText(getActivity(),serverMessage,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(),serverMessage,Toast.LENGTH_SHORT).show();
            }


        }
    }

    public void showAddBar(boolean state){
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.custom_pop_up_add_category,null);
        ProgressBar bar=view.findViewById(R.id.add_category_bar);
        LinearLayout layout=view.findViewById(R.id.linear_add_category);

        if(state){
            bar.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
        }else{
            bar.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
        }
    }

    public class BranchSelection extends AsyncTask<String, String, String> {
        int success = 0;
        String serverMessage;
        JSONArray branch;
        String outlet = null;


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
            List paramters = new ArrayList();

            paramters.add(new BasicNameValuePair("user_id", users.get_user_id()));

            JSONObject jsonObject = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.admin_select_branches,
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

            if (success == 1) {
                addCategory();
            }else{
                Toast.makeText(getActivity(),"Create a Branch first",Toast.LENGTH_SHORT).show();
                Fragment fragment;
                fragment = AdminCategories.newInstance();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                transaction.replace(R.id.frame_layout, fragment);
                transaction.commit();
            }
        }
    }
}
