package com.mobipos.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 12/6/2017.
 */

public class Orders  extends Controller{
    public Orders(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    public static String tb_name="tb_orders";
    public static String col_1="order_no";
    public static String col_2="date";


    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_ORDERS="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" VARCHAR(50),"+
            col_2+" VARCHAR(50))";


    public boolean createOrder(String order,String date){
        SQLiteDatabase db=this.getWritableDatabase();
        //  String sql="DELETE from "+tb_name;

        Log.d("total orders in db:",String.valueOf(getOrderCount()));
        try{
            // db.execSQL(sql);
            ContentValues values=new ContentValues();
            values.put(col_1,order);
            values.put(col_2,date);



            db.insert(tb_name,null,values);
            Log.d("insert order number:",order);
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        return OrderExists(order);
    }

    public boolean OrderExists(String order){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name+ " WHERE "+col_1+"= "+order;
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);



        if(cursor.getCount()>0){
            if (cursor.moveToFirst()){
                Log.d("order found; ",cursor.getString(cursor.getColumnIndex(col_1)));
            }

            return true;
        }else{
            return false;
        }
    }

    public int getOrderCount(){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;

            sql="SELECT * FROM "+tb_name;

        List data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

    //    Log.d("product count in loop:",String.valueOf(cursor.getCount()));
        return cursor.getCount();
    }

    public String getOrderDate(String order){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;

        sql="SELECT date FROM "+tb_name+ " WHERE "+col_1+"= "+order;

        List data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);
        String date=null;
        if(cursor.moveToFirst()){
          date =cursor.getString(cursor.getColumnIndex(col_2));
        }

        db.close();


        //    Log.d("product count in loop:",String.valueOf(cursor.getCount()));
        return date;
    }

}
