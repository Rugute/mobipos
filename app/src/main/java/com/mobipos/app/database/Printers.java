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

public class Printers extends Controller {
    public Printers(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static String tb_name="tb_printers";

    public static String col_1="printer_id";
    public static String col_2="printer_name";
    public static String col_3="printer_mac";

    public static  String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_PRINTERS="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11),"+
            col_2+" VARCHAR(11),"+
            col_3+" VARCHAR(11))";

    public boolean PrinterExists(String id){
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

    public boolean InsertPrinter(String id,String name,String mac){
        SQLiteDatabase db=this.getWritableDatabase();

        try{
            ContentValues values=new ContentValues();
            values.put(col_1,id);
            values.put(col_2,name);
            values.put(col_3,mac);

            db.insert(tb_name,null,values);
            db.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return PrinterExists(id);
    }

    public List<PrinterInterface> getPrintes(){
  //  public void getPrinters(){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;

        sql="SELECT * FROM tb_printers";

        List<PrinterInterface> data=new ArrayList<>();

        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        try {
            if (cursor.moveToFirst()) {

                do{
                    data.add(new PrinterInterface(cursor.getString(cursor.getColumnIndex(col_1)),
                            cursor.getString(cursor.getColumnIndex(col_2)),
                            cursor.getColumnName(cursor.getColumnIndex(col_3))));

                    Log.d("printer id:",cursor.getString(cursor.getColumnIndex(col_1)));
                    Log.d("printer name:",cursor.getString(cursor.getColumnIndex(col_2)));
                    Log.d("printer mac:",cursor.getString(cursor.getColumnIndex(col_3)));
                }while (cursor.moveToNext());

            }

            db.close();
        } catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        return data;

    }

}
