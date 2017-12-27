package com.mobipos.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItemsData;
import com.mobipos.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 12/5/2017.
 */

public class Products extends Controller {

    public Products(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    public static String tb_name="tb_products";
    public static String col_1="product_id";
    public static String col_2="product_name";
    public static String col_3="category_id";
    public static String col_4="sync_status";
    public static String col_5="active_status";
    public static String col_6="measure";

    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_PRODUCTS="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY,"+
            col_2+" VARCHAR(50),"+
            col_3+" INT(11),"+
            col_4+" INT(11),"+
            col_6+" VARCHAR(50),"+
            col_5+" INT(11))";

    public int getProductCount(){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name;

        List data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        return cursor.getCount();
    }

    public boolean ProductExists(String id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name+ " WHERE "+col_1+"= "+id;
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }
    public boolean insertProduct(String id,String name,String category_id,String measure){
        SQLiteDatabase db=this.getWritableDatabase();
        //  String sql="DELETE from "+tb_name;

        try{
            // db.execSQL(sql);
            ContentValues values=new ContentValues();
            values.put(col_1,id);
            values.put(col_2,name);
            values.put(col_3,category_id);
            values.put(col_4,"1");
            values.put(col_5,"1");
            values.put(col_6,measure);


            db.insert(tb_name,null,values);
            Log.d("insert product name:",name);
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        return ProductExists(id);
    }

    public List<CashierItemsData> getProducts(String category){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;
        if(category.equals("all")){
            sql="SELECT tb_products.product_id,tb_products.product_name,tb_products.measure,tb_products_price.price,tb_inventory.inventory_count" +
                    "  FROM "+tb_name+
                    " INNER JOIN "+Product_Prices.tb_name+" ON tb_products.product_id=tb_products_price.product_id INNER JOIN " +
                    Inventory.tb_name+" ON tb_inventory.product_id=tb_products.product_id";

        }else{
            sql="SELECT tb_products.product_id,tb_products.product_name,tb_products.measure,tb_products_price.price,tb_inventory.inventory_count" +
                    "  FROM "+tb_name+
                    " INNER JOIN "+Product_Prices.tb_name+" ON tb_products.product_id=tb_products_price.product_id INNER JOIN " +
                    Inventory.tb_name+" ON tb_inventory.product_id=tb_products.product_id  WHERE " +
                    "tb_products.category_id='"+category+"'";
        }


        List<CashierItemsData> data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        try {
            if (cursor.moveToFirst()) {

                do{
                    data.add(new CashierItemsData(cursor.getString(cursor.getColumnIndex(col_1)),
                            cursor.getString(cursor.getColumnIndex(col_2)),cursor.getString(cursor.getColumnIndex(
                                    Product_Prices.col_3)),cursor.getString(cursor.getColumnIndex(Inventory.col_3)),
                            cursor.getString(cursor.getColumnIndex(col_6)),R.mipmap.ic_launcher));

                    Log.d("product name:",cursor.getString(cursor.getColumnIndex(col_2)));
                    Log.d("product id:",cursor.getString(cursor.getColumnIndex(col_1)));
                    Log.d("price:",cursor.getString(cursor.getColumnIndex(Product_Prices.col_3)));
                }while (cursor.moveToNext());

            }

            cursor.close();
        } catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        return data;
    }

}
