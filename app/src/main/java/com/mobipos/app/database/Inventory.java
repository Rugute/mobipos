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

public class Inventory extends Controller {
    public Inventory(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    public static String tb_name="tb_inventory";
 //   public static String col_1="tb_inventory_id";
    public static String col_2="product_id";
    public static String col_3="inventory_count";
    public static String col_4="sync_status";
    public static String col_5="active_status";

    public static  String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_INVENTORY="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
          //  col_1+" INT(11) PRIMARY KEY AUTO_INCREMENT,"+
            col_2+" INT(11),"+
            col_3+" INT(11),"+
            col_4+" INT(11),"+
            col_5+" INT(11))";


    public boolean ProductExists(String id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name+ " WHERE "+col_2+"= "+id;
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean insertStock(String product_id,String stock_count){
        SQLiteDatabase db=this.getWritableDatabase();
        //  String sql="DELETE from "+tb_name;

        try{
            // db.execSQL(sql);
            ContentValues values=new ContentValues();
            values.put(col_2,product_id);
            values.put(col_3,stock_count);
            values.put(col_4,"1");
            values.put(col_5,"1");



            db.insert(tb_name,null,values);
            Log.d("insert stock count:",stock_count);
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        return ProductExists(product_id);
    }
}
