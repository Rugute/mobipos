package com.mobipos.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by folio on 12/5/2017.
 */

public class Product_Prices extends Controller {
    public Product_Prices(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, 1);
    }

    public static String tb_name="tb_products_price";
    public static String col_1="price_id";
    public static String col_2="product_id";
    public static String col_3="price";
    public static String col_4="sync_status";
    public static String col_5="active_status";

    public static String DROP_TABLE="DROP TABLE IF  EXISTS "+ tb_name;

    public static String CREATE_TABLE_PRODUCTS_PRICE="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11),"+
            col_2+" VARCHAR(50),"+
            col_3+" INT(11),"+
            col_4+" INT(11),"+
            col_5+" INT(11))";

    public boolean priceExists(String id){
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

    public boolean insertPrices(String price_id,String product_id,String price){
        SQLiteDatabase db=this.getWritableDatabase();
        //  String sql="DELETE from "+tb_name;

        try{
            // db.execSQL(sql);
            ContentValues values=new ContentValues();
            values.put(col_1,price_id);
            values.put(col_2,product_id);
            values.put(col_3,price);
            values.put(col_4,"1");
            values.put(col_5,"1");



            db.insert(tb_name,null,values);
            Log.d("insert price data:",price);
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        return priceExists(price_id);
    }


}


