package com.mobipos.app.Cashier.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.CashDummy;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItemsData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSale;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.viewCartData;
import com.mobipos.app.R;
import com.mobipos.app.database.Order_Items;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.Taxes;
import com.mobipos.app.database.defaults;

import java.util.List;

/**
 * Created by root on 12/15/17.
 */

public class ViewCartAdapter extends RecyclerView.Adapter<ViewCartAdapter.ItemListViewHolder>{

    public  static class ItemListViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView product_id;
        TextView product_name;
        TextView product_price;
        TextView product_quantity;
        TextView total_value;
        ImageView deleteIcon;
        ImageView edit_icon;
        CardView cardView;

        ItemListViewHolder(View itemView){
            super(itemView);
            cv = itemView.findViewById(R.id.item_cv);
            product_id = itemView.findViewById(R.id.cart_item_id);
            product_name =itemView.findViewById(R.id.cart_item_name);
            product_price = itemView.findViewById(R.id.cart_item_price);
            product_quantity = itemView.findViewById(R.id.cart_quantity);
            total_value = itemView.findViewById(R.id.total_value_per_item);
            deleteIcon = itemView.findViewById(R.id.imageView2);
            edit_icon = itemView.findViewById(R.id.edit_count);
            cardView=itemView.findViewById(R.id.card_item);


        }
    }
    List<viewCartData> itemsData;
    Context context;
    Order_Items orderitemsdb;
    Products productsdb;
    Taxes taxesdb;

    String str_order_id;

    public static String get_item_count="1";
    public static boolean dataChanged=false;

    TextView inclusive,exclusive,grand_total;

    public ViewCartAdapter(Context context, List<viewCartData> itemsData,String str_order_id,TextView inclusive,
                           TextView exclusive,TextView grand_total)
    {
        this.itemsData = itemsData;
        this.context=context;
        this.str_order_id=str_order_id;
        orderitemsdb=new Order_Items(context, defaults.database_name,null,1);
        taxesdb=new Taxes(context, defaults.database_name,null,1);
        productsdb=new Products(context, defaults.database_name,null,1);

        this.inclusive=inclusive;
        this.exclusive=exclusive;
        this.grand_total=grand_total;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ItemListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cashier_view_reciept, viewGroup, false);
        ViewCartAdapter.ItemListViewHolder pvh = new ViewCartAdapter.ItemListViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final ItemListViewHolder itemListViewHolder, final int i) {
        itemListViewHolder.product_id.setText(itemsData.get(i).product_id);
        itemListViewHolder.product_name.setText(itemsData.get(i).product_name);
        itemListViewHolder.product_price.setText(itemsData.get(i).count+"x"+itemsData.get(i).price);
        itemListViewHolder.product_quantity.setText(itemsData.get(i).count);
        itemListViewHolder.product_quantity.setVisibility(View.GONE);
        itemListViewHolder.total_value.setText(String.valueOf(Integer.parseInt(itemsData.get(i).count)*
        Integer.parseInt(itemsData.get(i).price)));

         Log.d("tax selected",String.valueOf(productsdb.taxMode(itemsData.get(i).product_id)));
        int total_product_price=Integer.parseInt(itemsData.get(i).count)*Integer.parseInt(itemsData.get(i).price);

        Log.d("tax margin for item",String.valueOf(taxesdb.tax(productsdb.taxMode(itemsData.get(i).product_id))));
        Log.d("tax id of item",String.valueOf(productsdb.taxMode(itemsData.get(i).product_id)));

        int tax_value=taxesdb.tax(productsdb.taxMode(itemsData.get(i).product_id))*total_product_price/100;

        Log.d("total tax for item",String.valueOf(tax_value));

        if(taxesdb.taxMode(productsdb.taxMode(itemsData.get(i).product_id))==1){
            PackageConfig.INCLUSIVE_TAX=PackageConfig.INCLUSIVE_TAX+Integer.parseInt(String.valueOf(tax_value));
            inclusive.setText(String.valueOf(PackageConfig.INCLUSIVE_TAX));
        }else if(taxesdb.taxMode(productsdb.taxMode(itemsData.get(i).product_id))==2){
            PackageConfig.EXCLUSIVE_TAX=PackageConfig.EXCLUSIVE_TAX+Integer.parseInt(String.valueOf(tax_value));
            exclusive.setText(String.valueOf(PackageConfig.EXCLUSIVE_TAX));
            int total=orderitemsdb.getCartTotal(PackageConfig.order_no)+PackageConfig.EXCLUSIVE_TAX;
            grand_total.setText(String.valueOf(total));
        }



        itemListViewHolder.deleteIcon.setVisibility(View.INVISIBLE);
        itemListViewHolder.edit_icon.setVisibility(View.INVISIBLE);


    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }


}


