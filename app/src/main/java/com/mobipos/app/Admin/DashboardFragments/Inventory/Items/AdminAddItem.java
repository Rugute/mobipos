package com.mobipos.app.Admin.DashboardFragments.Inventory.Items;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.MeasureMarginAdapter;
import com.mobipos.app.Admin.Adapters.QuickSaleAdapter;
import com.mobipos.app.Admin.PackageConfig;
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
 * Created by folio on 2/8/2018.
 */

public class AdminAddItem extends Fragment {

    public static AdminAddItem newInstance(){
        AdminAddItem fragment = new AdminAddItem();
        return fragment;
    }

    List<MeasureMarginData> measure,tax_margins;
    List<addItemCatData> categoryData;
    Users usersdb;

    EditText branch_name,category_name,measure_name,product_name,initial_stock,buying_price,
    selling_price,low_stock,tax_mar;

    Button add_items,btn_branch_select,cat_select,tax_select,measure_select;
    ProgressBar bar;
    LinearLayout linear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_add_items, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(View view,Bundle savedInstanceState){
            usersdb=new Users(getContext(), defaults.database_name,null,1);
            branch_name=view.findViewById(R.id.selected_branch);
            category_name=view.findViewById(R.id.selected_category);
            measure_name=view.findViewById(R.id.selected_measurement);
            product_name=view.findViewById(R.id.add_product_name);
            initial_stock=view.findViewById(R.id.add_initial_stock);
            buying_price=view.findViewById(R.id.add_buying_price);
            selling_price=view.findViewById(R.id.add_selling_price);
            low_stock=view.findViewById(R.id.add_low_stock);
            tax_mar=view.findViewById(R.id.selected_tax_margins);
            add_items=view.findViewById(R.id.btn_add_product);
            btn_branch_select=view.findViewById(R.id.btn_list_branches);
            cat_select=view.findViewById(R.id.list_categories);
            measure_select=view.findViewById(R.id.list_measurement);
            tax_select=view.findViewById(R.id.list_tax_margins);
            bar=view.findViewById(R.id.bar_loader);
            linear=view.findViewById(R.id.items_layout);

            new loadData().execute();

            AdminAddItemData.initial_stock=initial_stock.getText().toString();
            AdminAddItemData.buying_price=buying_price.getText().toString();
            AdminAddItemData.selling_price=selling_price.getText().toString();
            AdminAddItemData.low_stock_count=low_stock.getText().toString();
            AdminAddItemData.item_name=low_stock.getText().toString();


            btn_branch_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    branch();
                }
            });

            cat_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    categoryPop(AdminAddItemData.select_branch);
                }
            });
           measure_select.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                  measure();
              }
          });
           tax_select.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   tax();
               }
           });

           add_items.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   uploadData();
               }
           });

    }

    public void showbar(boolean state){
        if(state){
            bar.setVisibility(View.VISIBLE);
            linear.setVisibility(View.GONE);
        }else{
            bar.setVisibility(View.GONE);
            linear.setVisibility(View.VISIBLE);
        }
    }
    public class loadData extends AsyncTask<String,String,String>{

        int success=0;
        JSONArray branchArray,categoryArray,measureArray,taxArray;
        protected void onPreExecute(){
            super.onPreExecute();
            showbar(true);
        }
        @Override
        protected String doInBackground(String... strings) {

            categoryData=new ArrayList<>();
            measure=new ArrayList<>();
            tax_margins=new ArrayList<>();

            JSONParser jsonParser=new JSONParser();
            List parameters=new ArrayList();
            parameters.add(new BasicNameValuePair("user_id",usersdb.get_user_id()));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+ PackageConfig.get_item_parameters,
                    "GET",parameters);
            Log.d("data",jsonObject.toString());

            try {
                 success=jsonObject.getInt("success");

                 branchArray=jsonObject.getJSONArray("branch_data");
                 AdminAddItemData.branchIds=new String[branchArray.length()];
                 AdminAddItemData.branchNames=new String[branchArray.length()];

                 for(int i=0;i<branchArray.length();i++){
                     JSONObject jObj=branchArray.getJSONObject(i);
                     AdminAddItemData.branchNames[i]=jObj.getString("shop_name");
                     AdminAddItemData.branchIds[i]=jObj.getString("shop_id");

                     categoryArray=jObj.getJSONArray("categories");
                     for(int j=0;j<categoryArray.length();j++){
                         JSONObject catObj=categoryArray.getJSONObject(j);
                         categoryData.add(new addItemCatData(jObj.getString("shop_id"),
                                 catObj.getString("category_id"),
                                 catObj.getString("category_name")));
                     }

                 }

                measureArray=jsonObject.getJSONArray("measurement_data");
                for(int i=0;i<measureArray.length();i++) {
                    JSONObject jObj = measureArray.getJSONObject(i);
                    measure.add(new MeasureMarginData(jObj.getString("measurement_id"),
                            jObj.getString("meaurement_name"),
                            jObj.getString("single_unit")+" Per Unit"));
                }

                taxArray=jsonObject.getJSONArray("tax_margins");
                for(int i=0;i<taxArray.length();i++) {
                    JSONObject jObj = taxArray.getJSONObject(i);
                    tax_margins.add(new MeasureMarginData(jObj.getString("tax_margin_id"),
                            jObj.getString("tax")+"%",
                            jObj.getString("margin_mode")));
                }
                tax_margins.add(new MeasureMarginData("0","0%","No Tax"));

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String s){
            super.onPostExecute(s);

            showbar(false);
        }
    }

    public void branch(){
        final AlertDialog dialog= new AlertDialog.Builder(getContext()).create();
        View view=LayoutInflater.from(getContext()).inflate(R.layout.admin_list_branches,null);
        ListView listView=view.findViewById(R.id.view_outlet);
            listView.setAdapter(new QuickSaleAdapter(getContext(), AdminAddItemData.branchNames));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int pos= (int) adapterView.getItemIdAtPosition(i);
                   AdminAddItemData.select_branch=AdminAddItemData.branchIds[pos];
                   branch_name.setText(AdminAddItemData.branchNames[pos]);
                    category_name.setText("");
                    dialog.cancel();
                }
            });
        dialog.setView(view);
        dialog.show();
    }
    public void categoryPop(String branchId){
        List<addItemCatData> tempData=new ArrayList<>();
        for(int i=0;i<categoryData.size();i++){
            String id=categoryData.get(i).branchId;
            if(id.equals(branchId)){
                tempData.add(new addItemCatData(categoryData.get(i).branchId,categoryData.get(i).categoryId
                ,categoryData.get(i).categoryName));
            }
        }


        AdminAddItemData.categoryId=new String[tempData.size()];
        AdminAddItemData.categoryName=new String[tempData.size()];

        for (int i=0;i<tempData.size();i++){
            AdminAddItemData.categoryId[i]=tempData.get(i).categoryId;
            AdminAddItemData.categoryName[i]=tempData.get(i).categoryName;
        }

        final AlertDialog dialog= new AlertDialog.Builder(getContext()).create();
        View view=LayoutInflater.from(getContext()).inflate(R.layout.admin_list_branches,null);
        ListView listView=view.findViewById(R.id.view_outlet);
        Button btn_title=view.findViewById(R.id.btn_title);
        btn_title.setText("Select Category");
        listView.setAdapter(new QuickSaleAdapter(getContext(), AdminAddItemData.categoryName));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos= (int) adapterView.getItemIdAtPosition(i);
                AdminAddItemData.select_category=AdminAddItemData.categoryId[pos];
                category_name.setText(AdminAddItemData.categoryName[pos]);

                dialog.cancel();
            }
        });
        dialog.setView(view);
        dialog.show();

    }

    public void measure(){
        final AlertDialog dialog= new AlertDialog.Builder(getContext()).create();
        View view=LayoutInflater.from(getContext()).inflate(R.layout.admin_list_branches,null);
        ListView listView=view.findViewById(R.id.view_outlet);
        Button btn_title=view.findViewById(R.id.btn_title);
        btn_title.setText("Select Measurement");
        listView.setAdapter(new MeasureMarginAdapter(getContext(), measure));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos= (int) adapterView.getItemIdAtPosition(i);
                AdminAddItemData.select_measure=measure.get(pos).id;
                measure_name.setText(measure.get(pos).name);
                dialog.cancel();

            }
        });
        dialog.setView(view);
        dialog.show();
    }

    public void tax(){
        final AlertDialog dialog= new AlertDialog.Builder(getContext()).create();
        View view=LayoutInflater.from(getContext()).inflate(R.layout.admin_list_branches,null);
        ListView listView=view.findViewById(R.id.view_outlet);
        Button btn_title=view.findViewById(R.id.btn_title);
        btn_title.setText("Select Tax Margin");
        listView.setAdapter(new MeasureMarginAdapter(getContext(), tax_margins));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos= (int) adapterView.getItemIdAtPosition(i);
                AdminAddItemData.tax_margin=tax_margins.get(pos).id;
                tax_mar.setText(tax_margins.get(pos).name+" "+tax_margins.get(pos).description);
                dialog.cancel();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    public void uploadData(){

        if(!TextUtils.isEmpty(branch_name.getText())&&!TextUtils.isEmpty(category_name.getText())
                &&!TextUtils.isEmpty(measure_name.getText())&&!TextUtils.isEmpty(product_name.getText())
                &&!TextUtils.isEmpty(initial_stock.getText())&&!TextUtils.isEmpty(buying_price.getText())
                &&!TextUtils.isEmpty(selling_price.getText())&&!TextUtils.isEmpty(low_stock.getText())
                &&!TextUtils.isEmpty(tax_mar.getText())){

                new dataUpload().execute();
        }else{
            Toast.makeText(getActivity(),"Missing some information",Toast.LENGTH_SHORT).show();
        }
    }

    public class dataUpload extends AsyncTask<String,String,String>{

        int success=0;
        String serverMessage;
        protected void onPreExecute(){
            showbar(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> jsonObjectData=new ArrayList<NameValuePair>();
            jsonObjectData.add(new BasicNameValuePair("user_id",usersdb.get_user_id()));
            jsonObjectData.add(new BasicNameValuePair("product_name",product_name.getText().toString()));
            jsonObjectData.add(new BasicNameValuePair("category_id",AdminAddItemData.select_category));
            jsonObjectData.add(new BasicNameValuePair("shop_id",AdminAddItemData.select_branch));
            jsonObjectData.add(new BasicNameValuePair("measure",AdminAddItemData.select_measure));
            jsonObjectData.add(new BasicNameValuePair("selling_price",selling_price.getText().toString()));
            jsonObjectData.add(new BasicNameValuePair("buying_price",buying_price.getText().toString()));
            jsonObjectData.add(new BasicNameValuePair("tax_mode",AdminAddItemData.tax_margin));
            jsonObjectData.add(new BasicNameValuePair("low_stock_count",low_stock.getText().toString()));
            jsonObjectData.add(new BasicNameValuePair("initial_stock",initial_stock.getText().toString()));

            JSONObject jsonObject = jsonParser.makeHttpRequest(AppConfig.protocol + AppConfig.hostname +
                            AppConfig.add_product,
                    "GET", jsonObjectData);

            Log.d("result",jsonObject.toString());

            try {
                success=jsonObject.getInt("success");
                serverMessage=jsonObject.getString("message");
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            showbar(false);
            if(success==1){
                clearData();
                Toast.makeText(getActivity(),serverMessage,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(),"error while adding to the database",Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void clearData(){
        branch_name.setText("");
        category_name.setText("");
        measure_name.setText("");
        product_name.setText("");
        initial_stock.setText("");
        buying_price.setText("");
        selling_price.setText("");
        low_stock.setText("");
        tax_mar.setText("");
    }
}
