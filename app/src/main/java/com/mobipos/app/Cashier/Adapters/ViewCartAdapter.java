package com.mobipos.app.Cashier.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Cashier.dashboardFragments.Inventory.CashDummy;
import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItemsData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.viewCartData;
import com.mobipos.app.R;
import com.mobipos.app.database.Order_Items;
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
            cardView=itemView.findViewById(R.id.card_item);


        }
    }
    List<viewCartData> itemsData;
    Context context;
    Order_Items orderitemsdb;
    TextView cart_total_value;
    String str_order_id;

    public static String get_item_count="1";
    public static boolean dataChanged=false;

    public ViewCartAdapter(Context context, List<viewCartData> itemsData,TextView cart_total_value, String str_order_id)
    {
        this.itemsData = itemsData;
        this.cart_total_value=cart_total_value;
        this.context=context;
        this.str_order_id=str_order_id;
        orderitemsdb=new Order_Items(context, defaults.database_name,null,1);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ItemListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cashier_custom_view_cart_items, viewGroup, false);
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

        itemListViewHolder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //     Toast.makeText(context,"Toast bread",Toast.LENGTH_SHORT).show();
                int item_total=Integer.parseInt(itemsData.get(i).count)*Integer.parseInt(itemsData.get(i).price);
                int cart_total=Integer.parseInt(cart_total_value.getText().toString());

                cart_total_value.setText(String.valueOf(cart_total-item_total));

                if(!orderitemsdb.deleteProduct(itemsData.get(i).product_id)){
                    itemsData.remove(i);
                    notifyItemRemoved(i);
                    notifyDataSetChanged();

                }else{
                    Toast.makeText(context,"delete failed",Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    public boolean editCountPopUp(String count, final String product_id, final String order_id){

        View view=LayoutInflater.from(context).inflate(R.layout.cashier_make_sale_edit_count,null);
        AlertDialog alertDialog=new AlertDialog.Builder(context).create();
        alertDialog.setView(view);
        final EditText edit_count=view.findViewById(R.id.current_count);
        edit_count.setText(count);
        edit_count.setCursorVisible(true);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                get_item_count=edit_count.getText().toString();

                if(orderitemsdb.update_count(product_id,order_id,get_item_count)){
                    dataChanged=true;
                    Toast.makeText(context,get_item_count,Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();

        return dataChanged;
    }
}


