package com.mobipos.app.Cashier.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Cashier.dashboardFragments.MakeSales.viewCartData;
import com.mobipos.app.R;
import com.mobipos.app.database.Order_Items;
import com.mobipos.app.database.defaults;

import java.util.List;

/**
 * Created by folio on 1/11/2018.
 */

public class ListViewCartAdapter extends BaseAdapter {
    Context context;
    List<viewCartData> data;
    public static LayoutInflater inflater=null;
    Order_Items orderitemsdb;
    String str_order_id;
    TextView cart_total_value;

    public ListViewCartAdapter(Context context, List<viewCartData> data,String str_order_id,TextView cart_total_value){
        this.context=context;
        this.cart_total_value=cart_total_value;
        this.data=data;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        orderitemsdb=new Order_Items(context, defaults.database_name,null,1);
        this.str_order_id=str_order_id;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        view=inflater.inflate(R.layout.cashier_custom_view_cart_items,null);

        TextView product_id = view.findViewById(R.id.cart_item_id);
        TextView product_name =view.findViewById(R.id.cart_item_name);
        TextView product_price = view.findViewById(R.id.cart_item_price);
        final TextView product_quantity = view.findViewById(R.id.cart_quantity);
        TextView total_value = view.findViewById(R.id.total_value_per_item);
        ImageView deleteIcon = view.findViewById(R.id.imageView2);
        ImageView edit_icon = view.findViewById(R.id.edit_count);
        CardView cardView=view.findViewById(R.id.card_item);

       product_id.setText(data.get(i).product_id);
       product_name.setText(data.get(i).product_name);
       product_price.setText(data.get(i).count+"x"+data.get(i).price);
       product_quantity.setText(data.get(i).count);
       product_quantity.setVisibility(View.GONE);

        total_value.setText(String.valueOf(Integer.parseInt(data.get(i).count)*
                Integer.parseInt(data.get(i).price)));

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!orderitemsdb.deleteProduct(data.get(i).product_id)){
                    cart_total_value.setText(String.valueOf(orderitemsdb.getCartTotal(str_order_id)));
                    data.remove(i);
                    notifyDataSetInvalidated();
                    notifyDataSetChanged();

                }else{
                    Toast.makeText(context,"delete failed",Toast.LENGTH_SHORT).show();
                }


            }
        });

        return view;
    }

}
