package com.mobipos.app.Cashier.dashboardFragments.MakeSales;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.mobipos.app.Cashier.Adapters.MakeSalesAdapter;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories.CashierCategoryData;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItems;
import com.mobipos.app.R;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import java.util.ArrayList;
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
        categoriesdb=new Categories(getActivity(), defaults.database_name,null,1);
        productsdb=new Products(getActivity(), defaults.database_name,null,1);

        expandableListView=view.findViewById(R.id.make_sale_list);

        expandableListView.setAdapter(new MakeSalesAdapter(getActivity(),categoriesdb.getCategories(),
                productsdb.getSalesProduct()));

    }

    public List<MakeSaleCategoryData> cartData(){
        List<CashierCategoryData> categories=new ArrayList<>();
        List<CashierCategoryData> products=new ArrayList<>();
        categories=categoriesdb.getCategories();
        for(int i=0;i++;)
    }
}
