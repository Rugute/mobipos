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
    public static String col_3="sync_status";
    public static String col_4="sales_status";


    public static String DROP_TABLE="DROP TABLE IF  EXISTS "+ tb_name;

    public static String CREATE_TABLE_ORDERS="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" VARCHAR(50),"+
            col_3+" INT(11),"+
            col_4+" INT(11),"+
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
            values.put(col_3,0);
            values.put(col_4,0);



            db.insert(tb_name,null,values);
            Log.d("insert order number:",order);
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        Log.d("order boolean",String.valueOf(OrderExists(order)));
        return OrderExists(order);
    }

    public boolean OrderExists(String order){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name+ " WHERE "+col_1+"='"+order+"'";
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

        sql="SELECT date FROM "+tb_name+" where order_no='"+order+"'";
        Log.d("date selected",sql);
        List data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);
        String date=null;
        if(cursor.moveToFirst()){
           do {
               date = cursor.getString(cursor.getColumnIndex(col_2));
           }while (cursor.moveToNext());

        }
        Log.d("date selected ",date);
        db.close();
        //    Log.d("product count in loop:",String.valueOf(cursor.getCount()));
        return date;
    }

    public String getStatus(String order){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;

        sql="SELECT sales_status FROM "+tb_name+ " WHERE "+col_1+"= '"+order+"'";

        List data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);
        String date=null;
        if(cursor.moveToFirst()){

            date =cursor.getString(cursor.getColumnIndex(col_4));
            Log.d("date selected ",date);
        }

     //   db.close();


        //    Log.d("product count in loop:",String.valueOf(cursor.getCount()));
        return date;
    }



    public List<orders_interface> DataSync(){
        SQLiteDatabase db=getReadableDatabase();

        String sql="SELECT * FROM "+tb_name+" WHERE sync_status=0 ";
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);
        List<orders_interface> data=new ArrayList<>();

        Log.d("orders to be synced",String.valueOf(cursor.getCount()));
        if(cursor.moveToFirst()){

            do{
                data.add(new orders_interface(cursor.getString(cursor.getColumnIndex(col_1)),
                        cursor.getString(cursor.getColumnIndex(col_2))));
                Log.d("order to sync",cursor.getString(cursor.getColumnIndex(col_1)));
                Log.d("sales status",cursor.getString(cursor.getColumnIndex(col_4)));
                Log.d("date to sync",cursor.getString(cursor.getColumnIndex(col_2)));
            }while (cursor.moveToNext());
        }

        db.close();
        return data;
    }

    public void updateSalesInfo(String order_no) {
        Log.d("you reached"," here "+order_no);
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(col_4,"1");
        db.update(tb_name,values,col_1+"='"+order_no+"'",null);
        Log.d("updat reached",order_no);
        Log.d("status",getStatus(order_no));
        db.close();
    }

}
