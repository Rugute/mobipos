package com.mobipos.app.Admin.DashboardFragments.Inventory.Items;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.AdminCategRvAdapter;
import com.mobipos.app.Admin.Adapters.AdminProdRvAdapter;
import com.mobipos.app.Admin.DashboardAdmin;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Categories.AdminCategories;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Categories.AdminCategoryData;
import com.mobipos.app.Admin.DashboardFragments.Inventory.StockAlert.StockAlertFragment;
import com.mobipos.app.Admin.PackageConfig;
import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItems;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.R;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mobipos.app.Admin.PackageConfig.branches;
import static com.mobipos.app.Admin.PackageConfig.branchesId;
import static com.mobipos.app.Admin.PackageConfig.catnames;


/**
 * Created by folio on 12/29/2017.
 */

public class AdminItems extends Fragment {

    public static AdminItems newInstance(){
        AdminItems fragment = new AdminItems();
        return fragment;
    }

    Users users;
    private  List<AdminBranchSpinnerData> branchData;
    private  List<AdminCategorySpinnerData> categoryData;
    private  List<AdminProductData> productData;

    Spinner branchspinner,categoryspinner;
    RecyclerView rv;
    ProgressBar bar;

    LinearLayout search_layout,linear_with_branch;
    TextView branch_text,category_text;


    FloatingActionButton fab,search_button,close_search;
    SearchView search_items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_items, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(View view,Bundle savedInstanceState){

        users=new Users(getActivity(), defaults.database_name,null,1);
        bar = view.findViewById(R.id.progessbar_items);
        rv = view.findViewById(R.id.admin_items_rv);
        branchspinner=view.findViewById(R.id.item_branch_spinner);
        categoryspinner=view.findViewById(R.id.item_category_spinner);

        fab=view.findViewById(R.id.fab_add_items);
        search_button=view.findViewById(R.id.fab_search_butoon);
        close_search=view.findViewById(R.id.close_search_button);
        search_items=view.findViewById(R.id.search_items);

        search_layout=view.findViewById(R.id.search_layout_items);
        linear_with_branch=view.findViewById(R.id.linad2);
        branch_text=view.findViewById(R.id.cattext1);
        category_text=view.findViewById(R.id.cattext3);

        search_items.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                List<AdminProductData> searchData=new ArrayList<>();
                for(int i=0;i<productData.size();i++){
                    if(productData.get(i).name.toLowerCase().contains(s)){
                        searchData.add(productData.get(i));
                    }
                }

                initializeAdapter(searchData);
                return true;
            }
        });


        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_layout.setVisibility(View.VISIBLE);
                branchspinner.setVisibility(View.GONE);
                categoryspinner.setVisibility(View.GONE);
                branch_text.setVisibility(View.GONE);
                category_text.setVisibility(View.GONE);
                close_search.setVisibility(View.VISIBLE);
                search_button.setVisibility(View.GONE);
            }
        });
        close_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_layout.setVisibility(View.GONE);
                branchspinner.setVisibility(View.VISIBLE);
                categoryspinner.setVisibility(View.VISIBLE);
                branch_text.setVisibility(View.VISIBLE);
                category_text.setVisibility(View.VISIBLE);
                close_search.setVisibility(View.GONE);
                search_button.setVisibility(View.VISIBLE);

                search_items.clearFocus();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                fragment = AdminAddItem.newInstance();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                transaction.replace(R.id.frame_layout, fragment);
                transaction.commit();
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        showBackButton(true,"Products");
        final CheckInternetSettings internetOn=new CheckInternetSettings(getActivity());
        if(internetOn.isNetworkConnected()){
            new LoadItems().execute();
        }else{
            AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getActivity()).
                    setTitle("Items not found").
                    setMessage("Enable your internet to sync data from server").
                    setPositiveButton((CharSequence) "Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            internetOn.context.startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
                        }
                    });
            alertBuilder.show();
        }


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

    public class LoadItems extends AsyncTask<String,String,String>{


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
            branchData=new ArrayList<>();
            productData=new ArrayList<>();

            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            paramters.add(new BasicNameValuePair("user_id",users.get_user_id("admin")));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+
                            PackageConfig.get_admin_items,
                    "GET",paramters);

            Log.d("json object",jsonObject.toString());

            try {
                success=jsonObject.getInt("success");
                serverMessage=jsonObject.getString("message");

                data=jsonObject.getJSONArray("data");
                branches=new String[data.length()+1];
                branchesId=new String[data.length()+1];
                branches[0]="All Branches";
                branchesId[0]="0";
                for(int i=0;i<data.length();i++){
                    JSONObject jobj=data.getJSONObject(i);
                    Log.d("branch object",data.getJSONObject(i).toString());
                    branches[i+1]=jobj.getString("shop_name");
                    branchesId[i+1]=jobj.getString("shop_id");
                    JSONArray category_data=jobj.getJSONArray("categories");
                    for (int j=0;j<category_data.length();j++){
                        JSONObject catObj=category_data.getJSONObject(j);

                        categoryData.add(new AdminCategorySpinnerData(Integer.parseInt(branchesId[i+1]),
                                catObj.getString("category_id"),catObj.getString("category_name")));
                        JSONArray products=catObj.getJSONArray("products");

                        for(int k=0;k<products.length();k++){
                            JSONObject prodObj=products.getJSONObject(k);
                            productData.add(new AdminProductData(Integer.parseInt(branchesId[i+1]),
                                    Integer.parseInt(catObj.getString("category_id")),
                                    prodObj.getString("product_id"),
                                    prodObj.getString("product_name"),
                                    R.drawable.price,
                                    prodObj.getString("items_in_store"),
                                    prodObj.getString("selling_price"),
                                    prodObj.getString("measurement"),
                                    prodObj.getString("buying_price")
                                    ));
                        }
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
                initializeBranchSpinner(branches);
            }else{
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

    private void initializeAdapter(List<AdminProductData> data){
        AdminProdRvAdapter adapter = new AdminProdRvAdapter(data,getContext(),getActivity());
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }



    public void filterList(int category_id){
        List<AdminProductData> filterData=new ArrayList<>();
        for (int i=0;i<productData.size();i++){
            int category=productData.get(i).categoryId;
            if(category==category_id){
                filterData.add(new AdminProductData(productData.get(i).branchId,productData.get(i).categoryId,
                        productData.get(i).id, productData.get(i).name,R.mipmap.ic_launcher, productData.get(i).buying,
                        productData.get(i).selling, productData.get(i).measure,productData.get(i).buying_price));
            }
        }

        initializeAdapter(filterData);
    }

    public void initializeCategorySpinner(int branch_id){

        if(branch_id==0){
            categoryspinner.setVisibility(View.GONE);
            initializeAdapter(productData);
        }else{
            categoryspinner.setVisibility(View.VISIBLE);
            List<AdminCategorySpinnerData> tempData=new ArrayList<>();

            Log.d("id entering spinner",String.valueOf(branch_id));
            final String[] id,name;
            int getCount=0;

            for(int i=0;i<categoryData.size();i++){
                int branchid=categoryData.get(i).branchId;
                if(branch_id==branchid){
                    tempData.add(new AdminCategorySpinnerData(categoryData.get(i).branchId,categoryData.get(i).id,
                            categoryData.get(i).name));
                }
            }
            Log.d("counter",String.valueOf(getCount));
            id=new String[tempData.size()];
            catnames=new String[tempData.size()];
            for(int i=0;i<tempData.size();i++){
                id[i]=tempData.get(i).id;
                catnames[i]=tempData.get(i).name;
                Log.d("id found",id[i]);
                Log.d("cat name found",catnames[i]);
            }

            ArrayAdapter<String> adapter;
            adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,catnames);
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            categoryspinner.setAdapter(adapter);
            categoryspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    int pos=(int)adapterView.getItemIdAtPosition(i);
                    Log.d("select value",String.valueOf(pos));
                    Log.d("select branchId",String.valueOf(id[pos]));
                    try{
                        filterList(Integer.parseInt(id[pos]));
                    }catch(NumberFormatException e){
                        List<AdminProductData> data=new ArrayList<>();
                        data.add(new AdminProductData(0,0,"0","no available products",R.mipmap.ic_launcher,
                                "0","0","none","0"));
                        initializeAdapter(data);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

    }
    public void initializeBranchSpinner(String[] branches){
        final ArrayAdapter<String> adapter;
        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,branches);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        branchspinner.setAdapter(adapter);
        branchspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                int id=(int)adapterView.getItemIdAtPosition(i);
                initializeCategorySpinner(Integer.parseInt(branchesId[id]));

                Log.d("select value branch",String.valueOf(id));
                Log.d("select branchId branch",branchesId[id]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


}

























