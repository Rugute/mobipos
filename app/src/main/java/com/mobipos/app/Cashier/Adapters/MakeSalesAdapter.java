package com.mobipos.app.Cashier.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories.CashierCategoryData;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItemsData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSaleInterfaceData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSaleProductData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSaleProductInfo;
import com.mobipos.app.R;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.defaults;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by folio on 1/3/2018.
 */

public class MakeSalesAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<MakeSaleInterfaceData> data;
    public static LayoutInflater inflater=null;
    Products productsdb;


    public MakeSalesAdapter(Context context, List<MakeSaleInterfaceData> data) {
        this.context = context;
        this.data = data;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int i) {
        try {
            List<MakeSaleProductInfo> products=data.get(i).getProduct();
            return products.size();
        }catch (NullPointerException e){
            return 0;
        }
    }

    @Override
    public Object getGroup(int i) {
        return data.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {

        List<MakeSaleProductInfo> products;
        products=data.get(i).getProduct();

        return products.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        List<MakeSaleProductInfo> prod=data.get(i).getProduct();
        int serial=prod.get(i1).getId();
        return serial;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        MakeSaleInterfaceData category=(MakeSaleInterfaceData)getGroup(i);
       if(view==null) {
           view = inflater.inflate(R.layout.cashier_make_sale_custom_category, null);
       }
        TextView category_name=view.findViewById(R.id.cat_name);
        TextView category_id=view.findViewById(R.id.cat_id);
        category_name.setText(category.getCategory_name());
        category_id.setText(category.getCategory_id());

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        MakeSaleProductInfo product_info=(MakeSaleProductInfo) getChild(i, i1);

        if(view==null) {
            view = inflater.inflate(R.layout.cashier_make_sale_custom_products, null);
        }
        TextView product_name=view.findViewById(R.id.prod_name);
        TextView product_id=view.findViewById(R.id.prod_id);
        TextView product_price=view.findViewById(R.id.prod_price);
        ImageView image=view.findViewById(R.id.imageView);
        image.setVisibility(View.INVISIBLE);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"you clicked this guy",Toast.LENGTH_SHORT).show();
            }
        });



        product_name.setText(product_info.getProduct_name());
        product_id.setText(product_info.getProduct_id());
        product_price.setText(product_info.getProduct_price());

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
