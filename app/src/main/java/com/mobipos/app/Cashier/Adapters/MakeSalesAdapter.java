package com.mobipos.app.Cashier.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


import com.mobipos.app.Cashier.dashboardFragments.Inventory.Categories.CashierCategoryData;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItemsData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSaleProductData;
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
    private List<CashierCategoryData> category;
    private List<MakeSaleProductData> products;
    public static LayoutInflater inflater=null;
    Products productsdb;


    public MakeSalesAdapter(Context context, List<CashierCategoryData> category, List<MakeSaleProductData> products) {
        this.context = context;
        this.category = category;
        this.products = products;

        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getGroupCount() {
        return category.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return products.size();
    }

    @Override
    public Object getGroup(int i) {
        return category.get(i);
    }

    @Override
    public List<MakeSaleProductData> getChild(int i, int i1) {
        int cat_id=Integer.parseInt(category.get(i).id);
        List<MakeSaleProductData> childData=new ArrayList<>();
        List<MakeSaleProductData> tempData=new ArrayList<>();
        int size=products.size()-1;
        for(int j=0;j<products.size();j++) {
            int category_id = products.get(j).categoryId;
            if (cat_id == category_id) {
                childData.add(new MakeSaleProductData(cat_id, products.get(j).product_id, products.get(j).product_name, products.get(j).price,
                        products.get(j).stock, products.get(j).measure, R.mipmap.ic_launcher));
            }
        }



        return childData;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

      //  catData= (ArrayList<CashierCategoryData>) getGroup(i);

       if(view==null) {
           view = inflater.inflate(R.layout.cashier_make_sale_custom_category, null);
       }
        TextView category_name=view.findViewById(R.id.cat_name);
        TextView category_id=view.findViewById(R.id.cat_id);
        category_name.setText(category.get(i).name);
        category_id.setText(category.get(i).id);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        Log.d("size of child:",String.valueOf(getChild(i,i1).size()));
        Log.d("child position:",String.valueOf(i1));
        Log.d("group position:",String.valueOf(i));
        List<MakeSaleProductData> childData=getChild(i,i1);

        if(view==null) {
            view = inflater.inflate(R.layout.cashier_make_sale_custom_products, null);
        }
        TextView product_name=view.findViewById(R.id.prod_name);
        TextView product_id=view.findViewById(R.id.prod_id);
        TextView product_price=view.findViewById(R.id.prod_price);



        product_name.setText(childData.get(0).product_name);
        product_id.setText(childData.get(0).product_id);
        product_price.setText(childData.get(0).price);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
