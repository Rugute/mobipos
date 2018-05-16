package com.mobipos.app.Cashier.dashboardFragments.MakeSales;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.QuickSaleAdapter;
import com.mobipos.app.Admin.DashboardAdmin;
import com.mobipos.app.Admin.DashboardFragments.Inventory.Items.AdminAddItemData;
import com.mobipos.app.Cashier.Adapters.DiscountAdapter;
import com.mobipos.app.Cashier.Adapters.ListViewCartAdapter;
import com.mobipos.app.Cashier.Adapters.MakeSalesAdapter;
import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories.CashierCategoryData;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.Defaults.PaymentActivity;
import com.mobipos.app.Defaults.SplashPage;
import com.mobipos.app.R;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Controller;
import com.mobipos.app.database.DiscountInterface;
import com.mobipos.app.database.Discounts;
import com.mobipos.app.database.Inventory;
import com.mobipos.app.database.Order_Items;
import com.mobipos.app.database.Orders;
import com.mobipos.app.database.Printers;
import com.mobipos.app.database.Product_Prices;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.Taxes;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;
import com.mobipos.app.login.AdminLogin;
import com.mobipos.app.login.PinLogin;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by folio on 1/3/2018.
 */

public class MakeSale extends Fragment {

    public static MakeSale newInstance(){
        MakeSale fragment = new MakeSale();
        return fragment;
    }

    SearchView searchView;
    ExpandableListView expandableListView;
    Categories categoriesdb;
    Products productsdb;
    Order_Items orderItemsdb;
    Product_Prices pricesdb;
    TextView total_value;
    TextView navigator;
    List<String> orders_items;
    CardView total_card;
    FloatingActionButton fab_back;
    TextView text_order_no;
    LinearLayout src_layout;
    TextView new_order_no;
    Controller controllerdb;

    ListView listView;

    MakeSalesAdapter adapter;
    ImageView refresh,src_img;

    RelativeLayout view_cart;
    RecyclerView rv;

    public  boolean order_created=false;
    String get_item_count;
    Orders ordersdb;
    Users users;
    Inventory inventorydb;
    Discounts discountdb;
    Discounts discountsdb;
    Printers printersdb;
    Taxes taxesdb;

    public boolean view_cart_seen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.cashier_make_sale, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(View view,Bundle savedInstanceState){

        PackageConfig.orders_items=new ArrayList<>();
        PackageConfig.EXCLUSIVE_TAX=0;
        PackageConfig.INCLUSIVE_TAX=0;

        view_cart_seen=false;
        ordersdb=new Orders(getActivity(),defaults.database_name,null,1);
        categoriesdb=new Categories(getActivity(), defaults.database_name,null,1);
        productsdb=new Products(getActivity(), defaults.database_name,null,1);
        orderItemsdb=new Order_Items(getActivity(), defaults.database_name,null,1);
        users=new Users(getActivity(), defaults.database_name,null,1);
        inventorydb=new Inventory(getActivity(), defaults.database_name,null,1);
        discountdb=new Discounts(getActivity(), defaults.database_name,null,1);
        pricesdb=new Product_Prices(getActivity(), defaults.database_name,null,1);
        controllerdb=new Controller(getActivity(), defaults.database_name,null,1);
        discountsdb=new Discounts(getActivity(), defaults.database_name,null,1);
        printersdb=new Printers(getActivity(), defaults.database_name,null,1);
        taxesdb=new Taxes(getActivity(), defaults.database_name,null,1);

        expandableListView=view.findViewById(R.id.make_sale_list);
        listView=view.findViewById(R.id.view_cart_list);
        refresh=view.findViewById(R.id.refresh);
       navigator=view.findViewById(R.id.navigator);
       src_img=view.findViewById(R.id.search_img);
        text_order_no=view.findViewById(R.id.txt_order_no);
        new_order_no=view.findViewById(R.id.new_order_no);
        total_value=view.findViewById(R.id.total_value);
        total_card=view.findViewById(R.id.total_card);
        view_cart=view.findViewById(R.id.view_cart_layout);
        fab_back=view.findViewById(R.id.fab_back);
        rv=view.findViewById(R.id.view_cart_items);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        adapter=new MakeSalesAdapter(getActivity(),cartData());
        searchView=view.findViewById(R.id.search);
        src_layout=view.findViewById(R.id.search_layout);


        expandableListView.setAdapter(adapter);

        src_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setVisibility(View.VISIBLE);
                src_img.setVisibility(View.GONE);
            }
        });


        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setEnabled(true);
                searchView.requestFocus();
            }
        });

        SearchManager searchManager=(SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
     //   searchView.setSearchableInfo(searchManager.getSearchableInfo();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                dataFilter(s);
                return false;


            }

            @Override
            public boolean onQueryTextChange(String s) {

                dataFilter(s);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                MakeSalesAdapter adapter=new MakeSalesAdapter(getContext(),cartData());
                adapter.notifyDataSetChanged();
                expandableListView.setAdapter(adapter);
                return false;
            }
        });




        Toast.makeText(getContext(),String.valueOf(categoriesdb.getCategoryCount()),Toast.LENGTH_SHORT).show();

        if(categoriesdb.getCategoryCount()==0){
            new firstDataLoad().execute();

        }

        showBackButton(false,"Make Sale");

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Fragment fragment=null;
                    fragment=MakeSale.newInstance();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack("Back");
                    if(users.get_login_details()[2].equals("cashier")){
                        transaction.replace(R.id.frame_layout_new, fragment);
                        transaction.commit();
                    }else {
                        transaction.replace(R.id.frame_layout, fragment);
                        transaction.commit();
                    }


            }
        });

        final CheckInternetSettings internetOn=new CheckInternetSettings(getActivity());
        if(internetOn.isNetworkConnected()){
//            if(new DatabaseInitializers(getContext()).loaded()){
//               AppConfig.firstRefresh=true;
//            }
        }else{
            if(categoriesdb.getCategoryCount()==0){
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getActivity()).
                        setTitle("Data not found").
                        setMessage("Enable your internet to sync data from server").
                        setPositiveButton((CharSequence) "Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                internetOn.context.startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
                            }
                        });
                alertBuilder.show();
            }else{
                refresh.setVisibility(View.GONE);
                expandableListView.setAdapter(adapter);

            }
        }


        if(AppConfig.firstRefresh){
            refresh.setVisibility(View.GONE);
        }

        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_cart_seen=false;
                expandableListView.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                view_cart.setVisibility(View.GONE);
                src_layout.setVisibility(View.VISIBLE);
                total_card.setCardBackgroundColor(Color.parseColor("#34a12f"));
                 navigator.setText("Click to Proceed");
            }
        });

        total_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(view_cart_seen){
                    discountPopup();
                }

                if(total_value.getText().toString().equals("0")){
                    Toast.makeText(getContext(),"No products selected",Toast.LENGTH_SHORT).show();
                }else{
                    view_cart_seen=true;
                    total_card.setCardBackgroundColor(Color.parseColor("#605398"));
                        expandableListView.setVisibility(View.GONE);
                        view_cart.setVisibility(View.VISIBLE);
                        src_layout.setVisibility(View.GONE);
                    searchView.setVisibility(View.GONE);
                    initializeListAdapter(PackageConfig.order_no);
                    navigator.setText("Click to Cash out");

                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView txt_count=view.findViewById(R.id.cart_quantity);
                TextView txt_id=view.findViewById(R.id.cart_item_id);
                if(editCountPopUp(txt_count.getText().toString(),txt_id.getText().toString(),new_order_no.getText().toString())){
                    initializeListAdapter(new_order_no.getText().toString());
                }

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                TextView textView=view.findViewById(R.id.prod_price);
                TextView id=view.findViewById(R.id.prod_id);
                String stId=id.getText().toString();

           //     PackageConfig.orders_items.add(new cartItemData(stId,"1"));

                if(!order_created){
                    Long timestamp= System.currentTimeMillis();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
                    SimpleDateFormat order=new SimpleDateFormat("ddMMyyyyHHMMSS");
                    PackageConfig.order_no=order.format(new Date());
                    PackageConfig.date=simpleDateFormat.format(new Date());

                    if(ordersdb.createOrder(PackageConfig.order_no,PackageConfig.date)){

                        order_created=true;
                      //  text_order_no.setText(PackageConfig.order_no);
                        new_order_no.setText(PackageConfig.order_no);
                   //     Toast.makeText(getContext(),"Order Created",Toast.LENGTH_SHORT).show();

                    }
                }
                if(Integer.parseInt(inventorydb.getOpeningStock(stId))>0){
                    Toast.makeText(getActivity(),"item remaining "+inventorydb.getOpeningStock(stId),Toast.LENGTH_SHORT).show();
                    if(orderItemsdb.insertOrderItem(new_order_no.getText().toString(),stId,"1")){
                    }
                    Toast.makeText(getContext(),PackageConfig.order_no,Toast.LENGTH_SHORT).show();
                    total_value.setText(String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no)));


                }else{
                    Toast.makeText(getActivity(),"Product Out Of Stock!!",Toast.LENGTH_SHORT).show();
                }


          //      Toast.makeText(getActivity(),"order no: "+new_order_no.getText().toString(),Toast.LENGTH_SHORT).show();

                return true;
            }
        });

    }

    public void initializeExtend(){

        expandableListView.invalidate();
        MakeSalesAdapter adapter=new MakeSalesAdapter(getActivity(),cartData());
        adapter.notifyDataSetChanged();
        expandableListView.setAdapter(adapter);
        Toast.makeText(getContext(),"we are here too",Toast.LENGTH_SHORT).show();
      //  expandableListView.setAdapter();
    }

    public void initializeListAdapter(String order_id){


        Log.d("list of data",orderItemsdb.getCartData(order_id).toString());


        final ListViewCartAdapter adapter = new ListViewCartAdapter(getActivity(),
                orderItemsdb.getCartData(order_id),
                new_order_no.getText().toString(),
                total_value);




        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
        listView.setAdapter(adapter);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return false;
            }
        });
        //  rv.refreshDrawableState();

    }

    private void expandAll() {
        int count = adapter.getGroupCount();
        for (int i = 0; i < count; i++){
            expandableListView.expandGroup(i);
        }
    }

    public void showBackButton(Boolean state,String title) {
        if(state){
            if (getActivity() instanceof DashboardCashier) {
                try {
                    ((DashboardCashier) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getActivity().setTitle(title);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public List<MakeSaleInterfaceData> cartData(){
        List<CashierCategoryData> categories;
        List<MakeSaleInterfaceData> data=new ArrayList<>();
        List<MakeSaleProductData> prods;
        prods=productsdb.getSalesProduct();
        categories=categoriesdb.getCategories();


        for(int i=0;i<categories.size();i++){
            MakeSaleInterfaceData data_transfer=new MakeSaleInterfaceData();
            String id=categories.get(i).id;
            String name=categories.get(i).name;

            data_transfer.setCategory_id(id);
            data_transfer.setCategory_name(name);
           List<MakeSaleProductInfo> product_data=new ArrayList<>();




            int serial=0;
            for(int j=0;j<prods.size();j++){
                if(productsdb.getProductCount(String.valueOf(prods.get(j).categoryId))==0){
                    Log.d("category without items",String.valueOf(i));
                    continue;
                }
                int cat_id=prods.get(j).categoryId;
                if(cat_id==Integer.parseInt(id)){
                    MakeSaleProductInfo productInfo=new MakeSaleProductInfo();
                    productInfo.setId(serial);
                    productInfo.setProduct_id(prods.get(j).product_id);
                    productInfo.setProduct_name(prods.get(j).product_name);
                    productInfo.setProduct_price(prods.get(j).price);
                    product_data.add(productInfo);
                    data_transfer.setProduct(product_data);
                }

              //  product_data.add(productInfo);
            //    data_transfer.setProduct(product_data);
                serial++;
            }

            data.add(data_transfer);


        }

        return data;
    }

    public static boolean dataChange=false;

    public boolean editCountPopUp(String count, final String product_id,
                                  final String order_id){

        View view=LayoutInflater.from(getActivity()).inflate(R.layout.cashier_make_sale_edit_count,null);
        AlertDialog alertDialog=new AlertDialog.Builder(getActivity()).create();
        alertDialog.setView(view);
        final EditText edit_count=view.findViewById(R.id.current_count);
        edit_count.setText(count);
        edit_count.setCursorVisible(true);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                get_item_count=edit_count.getText().toString();

                if(Integer.parseInt(inventorydb.getOpeningStock(product_id))>=Integer.parseInt(get_item_count)){
                    if(orderItemsdb.update_count(product_id,order_id,get_item_count)){
                        dataChange=true;
                    }

                    total_value.setText(String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no)));
                    initializeListAdapter(new_order_no.getText().toString());
                }

              Toast.makeText(getActivity(),"Update Too Large. Items in store are: "+
                      inventorydb.getOpeningStock(product_id),Toast.LENGTH_SHORT).show();
               // initializeListAdapter(PackageConfig.order_no);


            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();

        return  dataChange;

    }

    public void dataFilter(String s){

        MakeSalesAdapter adapter=new MakeSalesAdapter(getContext(),productsdb.search(s));
        adapter.notifyDataSetChanged();
        expandableListView.setAdapter(adapter);
    }

    public void discountPopup(){

        final android.support.v7.app.AlertDialog dialog= new android.support.v7.app.AlertDialog.Builder(getContext()).create();
        View view=LayoutInflater.from(getContext()).inflate(R.layout.admin_list_branches,null);
        ListView listView=view.findViewById(R.id.view_outlet);
        Button title=view.findViewById(R.id.btn_title);
        title.setText("SELECT DISCOUNT");

        List<DiscountInterface> discount_values=new ArrayList<>();
        for(int i=0;i<discountsdb.getDiscounts().size();i++){
                discount_values.add(discountdb.getDiscounts().get(i));
        }

        discount_values.add(new DiscountInterface("0","Enter fixed discount amount","0"));

        listView.setAdapter(new DiscountAdapter(getContext(),discount_values));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos= (int) adapterView.getItemIdAtPosition(i);

                TextView textView=view.findViewById(R.id.unit_measure);
                if(textView.getText().toString().equals("Enter fixed discount amount")){
                    discount_pop_up();
                    PackageConfig.DISCOUNT_NAME="absolute value";
                    PackageConfig.DISCOUNT_VALUE="0";
                }else{
                    PackageConfig.DISCOUNT_NAME=discountdb.getDiscounts().get(pos).name;
                    PackageConfig.DISCOUNT_VALUE=discountdb.getDiscounts().get(pos).value;

                    startActivity(new Intent(getActivity(), PaymentActivity.class));
                }


                dialog.cancel();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

//    public void sync_data(){
//        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext()).
//                setTitle("Missing Inventory").
//                setMessage("Click below to synchronize Data from the server").
//                setPositiveButton( "Synchronize", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                            new firstDataLoad().execute();
//                            new firstDataLoad().execute();
//
//                    }
//                });
//
//        alertDialog.show();
//    }

    public class firstDataLoad extends AsyncTask<String,String,String> {

        ProgressDialog dialog=new ProgressDialog(getContext());

        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setMessage("Synchronizing data from server. Please wait...");
            dialog.setCancelable(false);
            dialog.show();


            new load_parameters().execute();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            paramters.add(new BasicNameValuePair("user_id",users.get_user_id("cashier")));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+
                            com.mobipos.app.Cashier.PackageConfig.get_categories,
                    "GET",paramters);

            try {
                int success=jsonObject.getInt("success");
                String serverMessage=jsonObject.getString("message");

                JSONArray categoryArray=jsonObject.getJSONArray("data");

                if(success==1){
                    for(int i=0;i<categoryArray.length();i++){
                        JSONObject jObj=categoryArray.getJSONObject(i);
                        if(!categoriesdb.insertCategory(jObj.getString("cat_id"),jObj.getString("cat_name"))) {
                            Log.d("error inserting data","data not inserted");
                        }

                    }

                    List params=new ArrayList();
                    params.add(new BasicNameValuePair("user_id",users.get_user_id("cashier")));
                    jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+
                                    com.mobipos.app.Cashier.PackageConfig.get_items,
                            "GET",params);

                    try{
                        int  success_state=jsonObject.getInt("success");
                        serverMessage=jsonObject.getString("message");
                        JSONArray dataitems=jsonObject.getJSONArray("data");

                        com.mobipos.app.Cashier.PackageConfig.itemArrayId=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.itemArrayName=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.categoryArrayId=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.itemArrayMeasurement=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.price_id=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.price=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.stockData=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.lowStockData=new String[dataitems.length()];
                        com.mobipos.app.Cashier.PackageConfig.tax_margin=new String[dataitems.length()];

                        for(int l=0;l<dataitems.length();l++){
                            JSONObject jObj=dataitems.getJSONObject(l);

                            com.mobipos.app.Cashier.PackageConfig.itemArrayId[l]=jObj.getString("product_id");
                            com.mobipos.app.Cashier.PackageConfig.itemArrayName[l]=jObj.getString("product_name");
                            com.mobipos.app.Cashier.PackageConfig.categoryArrayId[l]=jObj.getString("category_id");
                            com.mobipos.app.Cashier.PackageConfig.itemArrayMeasurement[l]=jObj.getString("measurement_name");
                            com.mobipos.app.Cashier.PackageConfig.price_id[l]=jObj.getString("price_id");
                            com.mobipos.app.Cashier.PackageConfig.price[l]=jObj.getString("price");
                            com.mobipos.app.Cashier.PackageConfig.stockData[l]=jObj.getString("opening_stock");
                            com.mobipos.app.Cashier.PackageConfig.lowStockData[l]=jObj.getString("low_stock_count");
                            com.mobipos.app.Cashier.PackageConfig.tax_margin[l]=jObj.getString("tax_mode");

                            Log.d("check this prod id",jObj.getString("product_id"));
                            Log.d(jObj.getString("product_id"),String.valueOf(productsdb.ProductExists(jObj.getString("product_id"))));

                            if(!productsdb.ProductExists(jObj.getString("product_id"))){
                                if(!productsdb.insertProduct(jObj.getString("product_id"),
                                        jObj.getString("product_name"),
                                        jObj.getString("category_id"),
                                        jObj.getString("measurement_name"),
                                        jObj.getString("tax_mode"))){
                                    Log.d("err inserting products","not inserted");
                                }else{
                                    if(!pricesdb.insertPrices(jObj.getString("price_id"),
                                            jObj.getString("product_id"),
                                            jObj.getString("price"))){
                                        Log.d("err inserting prices","not inserted");
                                    }else{
                                        if(!inventorydb.insertStock(jObj.getString("product_id"),
                                                jObj.getString("opening_stock"),
                                                jObj.getString("low_stock_count"))){
                                            Log.d("err inserting stocks","not inserted");
                                        }
                                    }
                                }
                            }

                            List params_a=new ArrayList();
                            params.add(new BasicNameValuePair("product_id",jObj.getString("product_id")));
//                            JSONObject UpdateSyncStatus=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+
//                                            com.mobipos.app.Cashier.PackageConfig.sync_product_movement,
//                                    "GET",params_a);
//
//                            try {
//                                int state=UpdateSyncStatus.getInt("success");
//                                if(state==1){
//                                    Log.d("product sync meso",UpdateSyncStatus.getString("message"));
//                                }
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }


                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }




            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);
            dialog.cancel();
           startActivity(new Intent(getContext(),DashboardCashier.class));
           getActivity().finish();
        }
    }


    public class load_parameters extends AsyncTask<String,String,String>{


        @Override
        protected String doInBackground(String... strings) {

            JSONParser jsonParser=new JSONParser();
            List params=new ArrayList();

            params.add(new BasicNameValuePair("user_id",users.get_user_id("cashier")));

            JSONObject jobjTaxes=jsonParser.makeHttpRequest(com.mobipos.app.login.PackageConfig.protocol+ com.mobipos.app.login.PackageConfig.hostname+
                    com.mobipos.app.login.PackageConfig.tax_load,"GET",params);


         int tax_success=0;
         JSONArray tax_data,printerArray;

            try{
                tax_success=jobjTaxes.getInt("success");

                if(tax_success==1){
                    tax_data=jobjTaxes.getJSONArray("data");

                    for (int i=0;i<tax_data.length();i++){
                        JSONObject j=tax_data.getJSONObject(i);
                        if(!taxesdb.insertTax(j.getString("tb_tax_id"),
                                j.getString("tax_margin"),j.getString("margin_mode"))){
                            Toast.makeText(getContext(),"Error inserting tax",Toast.LENGTH_SHORT).show();
                        }
                    }

                    List printerParameters=new ArrayList();
                    printerParameters.add(new BasicNameValuePair("user_id",users.get_user_id("user_id")));

                    JSONObject printerObject=jsonParser.makeHttpRequest(com.mobipos.app.login.PackageConfig.protocol+ com.mobipos.app.login.PackageConfig.hostname+
                            com.mobipos.app.login.PackageConfig.printer_load,"GET",params);

                    try {

                        int printer_success=printerObject.getInt("success");
                        printerArray=printerObject.getJSONArray("data");

                        if(printer_success==1){

                            Log.d("printer success",String.valueOf(printer_success));
                            Log.d("printer data",printerArray.toString());
                            for(int i=0;i<printerArray.length();i++){
                                JSONObject jprinterObj=printerArray.getJSONObject(i);

                                Log.d("printer id",jprinterObj.getString("id"));
                                Log.d("printer name",jprinterObj.getString("printer_name"));
                                Log.d("printer mac",jprinterObj.getString("printer_mac"));

                                if(!printersdb.InsertPrinter(jprinterObj.getString("id"),
                                        jprinterObj.getString("printer_name"),
                                        jprinterObj.getString("printer_mac"))){
                                    Toast.makeText(getContext(),"Error inserting printer",Toast.LENGTH_SHORT).show();
                                }
                            }

                            List discounts_params=new ArrayList();
                            discounts_params.add(new BasicNameValuePair("user_id",users.get_user_id("cashier")));
                            JSONObject discounts_object=jsonParser.makeHttpRequest(com.mobipos.app.login.PackageConfig.protocol+ com.mobipos.app.login.PackageConfig.hostname+
                                    com.mobipos.app.login.PackageConfig.discounts_load,"GET",discounts_params);

                            try{
                                int discounts_success=discounts_object.getInt("success");
                                if(discounts_success==1){
                                    JSONArray discounts_array=discounts_object.getJSONArray("data");
                                    for(int k=0;k<discounts_array.length();k++){

                                        JSONObject discounts_obj=discounts_array.getJSONObject(k);
                                        if(!discountsdb.InsertDiscount(discounts_obj.getString("id"),
                                                discounts_obj.getString("discount_name"),
                                                discounts_obj.getString("discount_value"))){
                                            Toast.makeText(getContext(),"Error inserting discounts",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }catch (Exception e){

                            }
                        }

                    }catch (Exception e){

                    }

                }


            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public void discount_pop_up(){

        View view= LayoutInflater.from(getContext()).inflate(R.layout.absolute_discount,null);
        final AlertDialog alertDialog=new AlertDialog.Builder(getContext()).create();
        alertDialog.setView(view);

        final EditText discount_amount_ed;
        discount_amount_ed=view.findViewById(R.id.discounted_amounte);

        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                   PackageConfig.discounted_amount=Integer.parseInt(discount_amount_ed.getText().toString());

                startActivity(new Intent(getActivity(), PaymentActivity.class));
                Toast.makeText(getContext(),String.valueOf(PackageConfig.discounted_amount),Toast.LENGTH_SHORT).show();
                   alertDialog.cancel();
            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();


    }

}

