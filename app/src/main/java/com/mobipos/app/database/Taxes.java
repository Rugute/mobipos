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
 * Created by folio on 1/31/2018.
 */

public class Taxes extends Controller {

    public Taxes(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    public static String tb_name="tb_taxes";
    public static String col_1="tb_tax_id";
    public static String col_2="tax_margin";
    public static String col_3="margin_mode";

    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_TAXES="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11),"+
            col_2+" VARCHAR(50),"+
            col_3+" INT(11))";


    public boolean insertTax(String id,String margin,String margin_mode){
        SQLiteDatabase db=this.getWritableDatabase();

            try{
                ContentValues values=new ContentValues();
                values.put(col_1,id);
                values.put(col_2,margin);
                values.put(col_3,margin_mode);

                db.insert(tb_name,null,values);
                db.close();

            }catch (Exception e){
                e.printStackTrace();
            }

        return TaxExists(id);
    }

    public boolean TaxExists(String id){
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

    public void getTaxes(){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;

        sql="SELECT * FROM tb_taxes";

        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        try {
            if (cursor.moveToFirst()) {

                do{
                    Log.d("tax id:",cursor.getString(cursor.getColumnIndex(col_1)));
                    Log.d("tax margin:",cursor.getString(cursor.getColumnIndex(col_2)));
                    Log.d("mode:",cursor.getString(cursor.getColumnIndex(col_3)));
                }while (cursor.moveToNext());

            }

            db.close();
        } catch (OutOfMemoryError e){
            e.printStackTrace();
        }

    }

    public String[] taxMode(int tax_margin_id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT tax_margin,margin_mode FROM tb_taxes WHERE tb_tax_id="+tax_margin_id;
        Cursor cursor=db.rawQuery(sql,null);

        String[] tax=new String[2];
        try{
            if(cursor.moveToFirst()){
                tax[0]=cursor.getString(cursor.getColumnIndex(col_2));
                tax[1]=cursor.getString(cursor.getColumnIndex(col_3));

                Log.d("tax margin",cursor.getString(cursor.getColumnIndex(col_2)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return tax;
    }

}
