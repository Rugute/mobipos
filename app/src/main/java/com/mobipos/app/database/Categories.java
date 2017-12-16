package com.mobipos.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobipos.app.Cashier.dashboardFragments.CashierCategoryData;
import com.mobipos.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 12/5/2017.
 */

public class Categories extends Controller {

    public Categories(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    public static String tb_name="tb_categories";
    public static String col_1="category_id";
    public static String col_2="category_name";
    public static String col_4="active_status";

    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_CATEGORIES="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY ,"+
            col_2+" VARCHAR(50),"+
            col_4+" INT(11))";


    public List<CashierCategoryData> getCategories(){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name;

        List<CashierCategoryData> data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        try {
            if (cursor.moveToFirst()) {

                do{
                    data.add(new CashierCategoryData(cursor.getString(cursor.getColumnIndex(col_1)),
                            cursor.getString(cursor.getColumnIndex(col_2)), R.mipmap.ic_launcher));

                    Log.d("category name:",cursor.getString(cursor.getColumnIndex(col_2)));
                }while (cursor.moveToNext());

            }

            cursor.close();
        } catch (OutOfMemoryError e){
                e.printStackTrace();
         }


       // db.close();

        return data;

    }

    public int getCategoryCount(){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name;

        List data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        return cursor.getCount();
    }

    public boolean CategoryExists(String id){
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

    public boolean insertCategory(String id,String name){
        SQLiteDatabase db=this.getWritableDatabase();
      //  String sql="DELETE from "+tb_name;

        try{
           // db.execSQL(sql);
            ContentValues values=new ContentValues();
            values.put(col_1,id);
            values.put(col_2,name);
            values.put(col_4,"1");


            db.insert(tb_name,null,values);
            Log.d("insert category name:",name);
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        return CategoryExists(id);
    }
}
