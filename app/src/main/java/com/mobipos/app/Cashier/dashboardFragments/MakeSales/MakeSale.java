package com.mobipos.app.Cashier.dashboardFragments.MakeSales;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Cashier.Adapters.ListViewCartAdapter;
import com.mobipos.app.Cashier.Adapters.MakeSalesAdapter;
import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories.CashierCategoryData;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.CheckInternetSettings;
import com.mobipos.app.Defaults.PaymentActivity;
import com.mobipos.app.Defaults.SplashPage;
import com.mobipos.app.R;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Order_Items;
import com.mobipos.app.database.Orders;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;
import com.mobipos.app.login.AdminLogin;
import com.mobipos.app.login.PinLogin;

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

    ExpandableListView expandableListView;
    Categories categoriesdb;
    Products productsdb;
    Order_Items orderItemsdb;
    TextView total_value;
    TextView navigator;
    List<String> orders_items;
    CardView total_card;
    FloatingActionButton fab_back;
    TextView text_order_no;

    ListView listView;
    ImageView refresh;

    RelativeLayout view_cart;
    RecyclerView rv;

    public  boolean order_created=false;
    String get_item_count;
    Orders ordersdb;
    Users users;

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


        view_cart_seen=false;
        ordersdb=new Orders(getActivity(),defaults.database_name,null,1);
        categoriesdb=new Categories(getActivity(), defaults.database_name,null,1);
        productsdb=new Products(getActivity(), defaults.database_name,null,1);
        orderItemsdb=new Order_Items(getActivity(), defaults.database_name,null,1);
        users=new Users(getActivity(), defaults.database_name,null,1);

        expandableListView=view.findViewById(R.id.make_sale_list);
        listView=view.findViewById(R.id.view_cart_list);
        refresh=view.findViewById(R.id.refresh);
        navigator=view.findViewById(R.id.navigator);
        text_order_no=view.findViewById(R.id.order_no);
        total_value=view.findViewById(R.id.total_value);
        total_card=view.findViewById(R.id.total_card);
        view_cart=view.findViewById(R.id.view_cart_layout);
        fab_back=view.findViewById(R.id.fab_back);
        rv=view.findViewById(R.id.view_cart_items);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);



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
                expandableListView.setAdapter(new MakeSalesAdapter(getActivity(),cartData()));

            }
        }
        expandableListView.setAdapter(new MakeSalesAdapter(getActivity(),cartData()));

        if(AppConfig.firstRefresh){
            refresh.setVisibility(View.GONE);
        }

        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_cart_seen=false;
                expandableListView.setVisibility(View.VISIBLE);
                view_cart.setVisibility(View.GONE);
                total_card.setCardBackgroundColor(Color.parseColor("#34a12f"));
                 navigator.setText("Click to Proceed");
            }
        });

        total_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(view_cart_seen){
                    startActivity(new Intent(getActivity(), PaymentActivity.class));
                }

                if(total_value.getText().toString().equals("0")){
                    Toast.makeText(getContext(),"No products selected",Toast.LENGTH_SHORT).show();
                }else{
                    view_cart_seen=true;
                    total_card.setCardBackgroundColor(Color.parseColor("#605398"));
                        expandableListView.setVisibility(View.GONE);
                        view_cart.setVisibility(View.VISIBLE);
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
                if(editCountPopUp(txt_count.getText().toString(),txt_id.getText().toString(),text_order_no.getText().toString())){
                    initializeListAdapter(text_order_no.getText().toString());
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
                        text_order_no.setText(PackageConfig.order_no);
                   //     Toast.makeText(getContext(),"Order Created",Toast.LENGTH_SHORT).show();

                    }
                }
                if(orderItemsdb.insertOrderItem(text_order_no.getText().toString(),stId,"1")){
                   // Toast.makeText(getContext(),String.valueOf(orderItemsdb.getLastId()),Toast.LENGTH_SHORT).show();
                   // total_value.setText(String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no)));

                }
                Toast.makeText(getContext(),PackageConfig.order_no,Toast.LENGTH_SHORT).show();
                total_value.setText(String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no)));

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


        ListViewCartAdapter adapter = new ListViewCartAdapter(getActivity(),
                orderItemsdb.getCartData(order_id),
                text_order_no.getText().toString(),
                total_value);


        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
        listView.setAdapter(adapter);
        //  rv.refreshDrawableState();

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
                if(productsdb.getProductCount(String.valueOf(prods.get(i).categoryId))==0){
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

                if(orderItemsdb.update_count(product_id,order_id,get_item_count)){
                    dataChange=true;
                }

                total_value.setText(String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no)));
                initializeListAdapter(text_order_no.getText().toString());


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

    public void itemListeners(){

    }

}
