package com.mobipos.app.Cashier.dashboardFragments.MakeSales;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Cashier.Adapters.MakeSalesAdapter;
import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories.CashierCategoryData;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItems;
import com.mobipos.app.R;
import com.mobipos.app.database.Categories;
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
    TextView total_value;
    List<String> orders_items;
    CardView total_card;


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

        getActivity().setTitle("Make Sale");

        ordersdb=new Orders(getActivity(),defaults.database_name,null,1);
        categoriesdb=new Categories(getActivity(), defaults.database_name,null,1);
        productsdb=new Products(getActivity(), defaults.database_name,null,1);

        expandableListView=view.findViewById(R.id.make_sale_list);
        total_value=view.findViewById(R.id.total_value);
        total_card=view.findViewById(R.id.total_card);


        total_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(total_value.getText().toString().equals("0")){
                    Toast.makeText(getContext(),"No products selected",Toast.LENGTH_SHORT).show();
                }else{
                    Long timestamp= System.currentTimeMillis();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:MM:ss");
                    SimpleDateFormat order=new SimpleDateFormat("ddMMyyyyHHMMSS");
                    PackageConfig.order_no=order.format(new Date());
                    PackageConfig.date=simpleDateFormat.format(new Date());

                    if(!ordersdb.createOrder(PackageConfig.order_no,PackageConfig.date)){
                        Toast.makeText(getContext(),"Order created",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),"Order not created",Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });

        expandableListView.setAdapter(new MakeSalesAdapter(getActivity(),cartData()));

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                TextView textView=view.findViewById(R.id.prod_price);
                TextView id=view.findViewById(R.id.prod_id);
                String stId=id.getText().toString();

                PackageConfig.orders_items.add(stId);

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
}
