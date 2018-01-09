package com.mobipos.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItemsData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.cartItemData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.viewCartData;
import com.mobipos.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 12/6/2017.
 */

public class Order_Items extends Controller {
    public Order_Items(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    public static String tb_name="tb_order_items";
    public static String col_1="tb_order_item_id";
    public static String col_2="order_id";
    public static String col_3="product_id";
    public static String col_4="product_count";


    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_ORDER_ITEMS="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY,"+
            col_2+" VARCHAR(50),"+
            col_3+" INT(11),"+
            col_4+" INT(11))";

    public boolean insertOrderItem(String order_id, String product_id,String count){
        SQLiteDatabase db=this.getWritableDatabase();
        String sql="SELECT * from "+tb_name;

        Cursor cursor;
        cursor=db.rawQuery(sql,null);
        int i=0;

        if(cursor.getCount()==0){
            i=1;
        }else{
            i=1+getLastId();
        }

        try{

            ContentValues values=new ContentValues();
                values.put(col_1,i);
                values.put(col_2,order_id);
                values.put(col_3,product_id);
                values.put(col_4,count);

                db.insert(tb_name,null,values);
                Log.d("product id in orders: ",product_id);

            db.close();
        }catch (SQLException e){
            e.printStackTrace();

        }

        return orderItemExists("order",String.valueOf(i));
    }

    public List<viewCartData> getCartData(String order_id){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;
        sql="SELECT tb_products.product_id,tb_products.product_name,tb_products_price.price,SUM(tb_order_items.product_count) as total " +
                    "FROM tb_products " +
                "inner join tb_products_price on tb_products.product_id=tb_products_price.product_id " +
                "inner join tb_order_items on tb_products.product_id=tb_order_items.product_id " +
                    "WHERE tb_order_items.order_id='"+order_id+"' GROUP BY tb_products.product_id";



        List<viewCartData> data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        try {
            if (cursor.moveToFirst()) {

                do{

                    data.add(new viewCartData(cursor.getString(cursor.getColumnIndex(Products.col_1)),
                            cursor.getString(cursor.getColumnIndex(Products.col_2)),
                            cursor.getString(cursor.getColumnIndex(Product_Prices.col_3)),
                            cursor.getString(cursor.getColumnIndex("total"))));

                    Log.d("order item id:",cursor.getString(cursor.getColumnIndex(Products.col_1)));
                    Log.d("order item name:",cursor.getString(cursor.getColumnIndex(Products.col_2)));
                    Log.d("total:",  cursor.getString(cursor.getColumnIndex("total")));

                }while (cursor.moveToNext());

            }

            cursor.close();
        } catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        return data;
    }

    public int getItemCount(String order_id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name+ " WHERE "+col_2+"='"+order_id+"'";
        String sql1="SELECT * FROM "+tb_name;

        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do{
                Log.d("order item id",cursor.getString(cursor.getColumnIndex(col_1)));
                Log.d("order id",cursor.getString(cursor.getColumnIndex(col_2)));
                Log.d("product id",cursor.getString(cursor.getColumnIndex(col_3)));
                Log.d("item count",cursor.getString(cursor.getColumnIndex(col_4)));
            }while(cursor.moveToNext());
        }

        Log.d("number of order items:",String.valueOf(cursor.getCount()));
        return cursor.getCount();
    }

    public int getLastId(){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;
        sql="SELECT * FROM "+tb_name+" ORDER BY tb_order_item_id DESC LIMIT 1";
        List data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        int i=0;
        if(cursor.moveToFirst()){
            i= cursor.getInt(cursor.getColumnIndex(col_1));
        }
        cursor.close();
        return i;

    }

    public boolean orderItemExists(String type,String id){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;
        if(type.equals("order")){
           sql="SELECT * FROM "+tb_name+ " WHERE "+col_1+"= "+id;
        }else if(type.equals("product")){
            sql="SELECT * FROM "+tb_name+ " WHERE "+col_3+"= "+id;
        }

        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean deleteProduct(String id){
        SQLiteDatabase db=getReadableDatabase();
        String sql;
        sql="DELETE FROM "+tb_name+ " WHERE "+col_3+"= "+id;
        db.execSQL(sql);
        db.close();

        return orderItemExists("product",id);


    }

    public boolean update_count(String product_id,String order_id,String count){
        if(!deleteProduct(product_id)){
            if(insertOrderItem(order_id,product_id,count)){
                return true;
            }else{
                return false;
            }
        }else {
            return false;
        }
    }

}
