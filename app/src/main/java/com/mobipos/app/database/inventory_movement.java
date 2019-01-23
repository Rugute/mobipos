package com.mobipos.app.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 12/5/2017.
 */

public class inventory_movement extends Controller {
    public inventory_movement(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, 1);
    }

    public static String tb_name="tb_inventory_movement";
    public static String col_1="tb_inv_movement_id";
    public static String col_2="product_id";
    public static String col_3="movement_type";
    public static String col_7="sale_id";
    public static String col_4="count";
    public static String col_6="DATE";
    public static String col_5="sync_status";

    public static String DROP_TABLE="DROP TABLE IF  EXISTS "+ tb_name;

    public static String CREATE_TABLE_INVENTORY_MOVEMENT="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INTEGER PRIMARY KEY,"+
            col_2+" INT(11),"+
            col_3+" VARCHAR(50),"+
            col_4+" INT(11),"+
            col_7+" VARCHAR(50),"+
            col_6+" VARCHAR(50),"+
            col_5+" INT(11))";

    public List<inventory_movement_interface> DataSync(){
        SQLiteDatabase db=getReadableDatabase();

        String sql="SELECT * FROM "+tb_name+" WHERE movement_type='STOCK_OUT' and sync_status=0";
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);
        List<inventory_movement_interface> data=new ArrayList<>();

        if(cursor.moveToFirst()){

            do{
                data.add(new inventory_movement_interface(cursor.getString(cursor.getColumnIndex(col_1)),
                        cursor.getString(cursor.getColumnIndex(col_2)),
                        cursor.getString(cursor.getColumnIndex(col_3)),
                        cursor.getString(cursor.getColumnIndex(col_4)),
                        cursor.getString(cursor.getColumnIndex(col_7)),
                        cursor.getString(cursor.getColumnIndex(col_6))
                        ));
            }while (cursor.moveToNext());
        }

        db.close();
        return data;
    }

    public String getOrderNo(int salesId){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT order_id FROM "+Sales.tb_name+" WHERE tb_sale_id="+salesId;
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        String orderno=null;
        if(cursor.moveToFirst()){
            orderno=cursor.getString(cursor.getColumnIndex("order_id"));
        }
        return orderno;
    }
}
