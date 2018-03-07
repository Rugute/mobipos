package com.mobipos.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 2/28/2018.
 */

public class Discounts extends Controller {
    public Discounts(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static String tb_name="tb_discounts";

    public static String col_1="discount_id";
    public static String col_2="discount_name";
    public static String col_3="discount_value";

    public static  String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_DISCOUNTS="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11),"+
            col_2+" VARCHAR(11),"+
            col_3+" VARCHAR(11))";

    public boolean DiscountsExists(String id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name+ " WHERE "+col_1+"="+id;
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }
    public boolean InsertDiscount(String id,String name,String value){
        SQLiteDatabase db=this.getWritableDatabase();

        try{
            ContentValues values=new ContentValues();
            values.put(col_1,id);
            values.put(col_2,name);
            values.put(col_3,value);

            db.insert(tb_name,null,values);
            db.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return DiscountsExists(id);
    }

    public void getDiscounts(){
  //  public List<DiscountInterface> getDiscounts(){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;

        sql="SELECT * FROM tb_discounts";

        List<DiscountInterface> data=new ArrayList<>();

        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        try {
            if (cursor.moveToFirst()) {

                do{
                    data.add(new DiscountInterface(cursor.getString(cursor.getColumnIndex(col_1)),
                            cursor.getString(cursor.getColumnIndex(col_2)),
                            cursor.getColumnName(cursor.getColumnIndex(col_3))));
                    Log.d("discount id",cursor.getString(cursor.getColumnIndex(col_1)));
                    Log.d("discount name",cursor.getString(cursor.getColumnIndex(col_2)));
                    Log.d("discount value",cursor.getString(cursor.getColumnIndex(col_3)));
                }while (cursor.moveToNext());

            }

            db.close();
        } catch (OutOfMemoryError e){
            e.printStackTrace();
        }

     //   return data;

    }

}
