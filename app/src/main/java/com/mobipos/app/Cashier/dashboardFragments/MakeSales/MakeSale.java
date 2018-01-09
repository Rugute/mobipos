package com.mobipos.app.Cashier.dashboardFragments.MakeSales;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Cashier.Adapters.CashierItemRvAdapter;
import com.mobipos.app.Cashier.Adapters.MakeSalesAdapter;
import com.mobipos.app.Cashier.Adapters.RecyclerItemClickListener;
import com.mobipos.app.Cashier.Adapters.ViewCartAdapter;
import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories.CashierCategoryData;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItems;
import com.mobipos.app.R;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Order_Items;
import com.mobipos.app.database.Orders;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    List<String> orders_items;
    CardView total_card;
    FloatingActionButton fab_back;
    TextView text_order_no;

    RelativeLayout view_cart;
    RecyclerView rv;

    public  boolean order_created=false;
    String get_item_count;
    Orders ordersdb;

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



        ordersdb=new Orders(getActivity(),defaults.database_name,null,1);
        categoriesdb=new Categories(getActivity(), defaults.database_name,null,1);
        productsdb=new Products(getActivity(), defaults.database_name,null,1);
        orderItemsdb=new Order_Items(getActivity(), defaults.database_name,null,1);

        expandableListView=view.findViewById(R.id.make_sale_list);
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



        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableListView.setVisibility(View.VISIBLE);
                view_cart.setVisibility(View.GONE);
                total_card.setCardBackgroundColor(Color.parseColor("#34a12f"));
            }
        });

        total_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(total_value.getText().toString().equals("0")){
                    Toast.makeText(getContext(),"No products selected",Toast.LENGTH_SHORT).show();
                }else{
                    total_card.setCardBackgroundColor(Color.parseColor("#605398"));
                        expandableListView.setVisibility(View.GONE);
                        view_cart.setVisibility(View.VISIBLE);
                        initializeAdapter(PackageConfig.order_no);


                }

            }
        });

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                      TextView textView=view.findViewById(R.id.cart_quantity);
                      TextView product_id=view.findViewById(R.id.cart_item_id);
                      editCountPopUp(textView.getText().toString(),product_id.getText().toString(),
                                text_order_no.getText().toString());

                    }
                })
        );

        expandableListView.setAdapter(new MakeSalesAdapter(getActivity(),cartData()));


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                TextView textView=view.findViewById(R.id.prod_price);
                TextView id=view.findViewById(R.id.prod_id);
                String stId=id.getText().toString();

           //     PackageConfig.orders_items.add(new cartItemData(stId,"1"));

                if(!order_created){
                    Long timestamp= System.currentTimeMillis();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:MM:ss");
                    SimpleDateFormat order=new SimpleDateFormat("ddMMyyyyHHMMSS");
                    PackageConfig.order_no=order.format(new Date());
                    PackageConfig.date=simpleDateFormat.format(new Date());

                    if(!ordersdb.createOrder(PackageConfig.order_no,PackageConfig.date)){

                        order_created=true;
                        text_order_no.setText(PackageConfig.order_no);
                        Toast.makeText(getContext(),"Order created",Toast.LENGTH_SHORT).show();

                    }


                }

                if(orderItemsdb.insertOrderItem(text_order_no.getText().toString(),stId,"1")){
                    Toast.makeText(getContext(),String.valueOf(orderItemsdb.getLastId()),Toast.LENGTH_SHORT).show();
                }
                total_value.setText(String.valueOf(Integer.parseInt(total_value.getText().toString())+
                        Integer.parseInt(textView.getText().toString())));
                return true;
            }
        });

        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(),"yeee!!!",Toast.LENGTH_SHORT).show();
                return true;
            }
        });



    }

    public void initializeAdapter(String order_id){


        Log.d("list of data",orderItemsdb.getCartData(order_id).toString());
        ViewCartAdapter adapter = new ViewCartAdapter(getActivity(),orderItemsdb.getCartData(order_id),
                total_value,text_order_no.getText().toString());

        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
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

    public void editCountPopUp(String count, final String product_id, final String order_id){

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
                    initializeAdapter(text_order_no.getText().toString());
                    Toast.makeText(getActivity(),"Item updated successfully",Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();



    }

}
