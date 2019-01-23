package com.mobipos.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobipos.app.Admin.AdminStockAlertData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 12/5/2017.
 */

public class Inventory extends Controller {
    public Inventory(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    public static String tb_name="tb_inventory";
 //   public static String col_1="tb_inventory_id";
    public static String col_2="product_id";
    public static String col_3="inventory_count";
    public static String col_6="low_stock_count";
    public static String col_4="sync_status";
    public static String col_5="active_status";

    public static  String DROP_TABLE="DROP TABLE IF  EXISTS "+ tb_name;

    public static String CREATE_TABLE_INVENTORY="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
          //  col_1+" INT(11) PRIMARY KEY AUTO_INCREMENT,"+
            col_2+" INT(11),"+
            col_3+" INT(11),"+
            col_4+" INT(11),"+
            col_6+" INT(11),"+
            col_5+" INT(11))";


    public boolean ProductExists(String id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name+ " WHERE "+col_2+"= '"+id+"'";
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean insertStock(String product_id,String stock_count,String low_stock){
        SQLiteDatabase db=this.getWritableDatabase();
        //  String sql="DELETE from "+tb_name;

        try{
            // db.execSQL(sql);
            ContentValues values=new ContentValues();
            values.put(col_2,product_id);
            values.put(col_3,stock_count);
            values.put(col_4,"1");
            values.put(col_5,"1");
            values.put(col_6,low_stock);



            db.insert(tb_name,null,values);
            Log.d("insert stock count:",stock_count);
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        return ProductExists(product_id);
    }

    public String getOpeningStock(String product_id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name+ " WHERE "+col_2+"='"+product_id+"'";
        Cursor cursor=null;
        String count=null;
        Log.d("query statement ",sql);
        cursor=db.rawQuery(sql,null);

        try {
            Log.d("count of items",String.valueOf(cursor.getCount()));
            if(cursor.moveToFirst()){
                    count=cursor.getString(cursor.getColumnIndex(col_3));
                    Log.d("inventory count from db",cursor.getString(cursor.getColumnIndex(col_3)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();

//        Log.d("inventory count db2",count);
        return count;
    }

    public String LowStockValue(String product_id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT low_stock_count FROM "+tb_name+ " WHERE "+col_2+"="+product_id;
        Cursor cursor=null;
        String count=null;
        cursor=db.rawQuery(sql,null);

        try {
            if(cursor.moveToFirst()){
                count=cursor.getString(cursor.getColumnIndex(col_6));

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return count;
    }


    public boolean updateStock(String product_id,String stock_count){
        SQLiteDatabase db=this.getWritableDatabase();
        //  String sql="DELETE from "+tb_name;

        try{
            // db.execSQL(sql);
            ContentValues values=new ContentValues();
            values.put(col_3,stock_count);

           db.update(tb_name,values,"product_id="+product_id,null);
            Log.d("insert stock count:",stock_count);
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        if(getOpeningStock(product_id).equals(stock_count)){
            return true;
        }else{
            return false;
        }
    }

    public int getStockCount(String product_id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT tb_inventory.inventory_count,SUM(tb_order_items.product_count) as total from  tb_inventory inner" +
                " join tb_order_items on tb_inventory.product_id=tb_order_items.product_id where tb_inventory.product_id="
                +product_id;
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);
        int count=0;
        try {
            if(cursor.moveToFirst()){
                count=cursor.getInt(cursor.getColumnIndex(col_3))-cursor.getInt(cursor.getColumnIndex("total"));

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return count;
    }

    public List<AdminStockAlertData> getLowStockProducts(){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM tb_products";
        Cursor cursor=null;
        List<AdminStockAlertData> data=new ArrayList<>();

        cursor=db.rawQuery(sql,null);

        try{
            if(cursor.moveToFirst()){
                do {


                    try{

                        String product_id=cursor.getString(cursor.getColumnIndex("product_id"));
                        String category_id=cursor.getString(cursor.getColumnIndex("category_id"));

                        Log.d("product id",product_id);
                        String stock_count=getOpeningStock(product_id);
                        String low_stock=LowStockValue(product_id);

                        Log.d("stock count value",stock_count);
                        Log.d("low stock value",low_stock);

                        if(Integer.parseInt(low_stock)>=Integer.parseInt(stock_count)){
                            if(Integer.parseInt(stock_count)>0){
                                AdminStockAlertData handler=new AdminStockAlertData(product_id,
                                        cursor.getString(cursor.getColumnIndex(Products.col_2)),
                                        get_category_name(category_id),
                                        "PRODUCT STOCK IS LOW" ,
                                        "1",
                                         stock_count);

                                data.add(handler);
                            }else{
                                AdminStockAlertData handler=new AdminStockAlertData(product_id,
                                        cursor.getString(cursor.getColumnIndex(Products.col_2)),
                                        get_category_name(category_id),
                                        "PRODUCT OUT OF STOCK",
                                        "2",
                                        stock_count
                                        );
                                data.add(handler);
                            }
                        }
                    }catch (NumberFormatException e){
                        Log.d("product id with null",cursor.getString(cursor.getColumnIndex("product_id")));
                    }catch (NullPointerException e){
                     //   Log.d(" null pointer",cursor.getString(cursor.getColumnIndex("product_id")));
                    }


                    

                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return data;
    }

    public String get_category_name(String category_id){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;

        sql="SELECT category_name FROM "+Categories.tb_name+" WHERE "+Categories.col_1+"="+category_id+" LIMIT 1";

        String s=null;

        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        if (cursor.moveToFirst()){
            s=cursor.getString(cursor.getColumnIndex("category_name"));
        }

        cursor.close();

        return s;
    }

}
