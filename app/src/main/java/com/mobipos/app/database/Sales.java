package com.mobipos.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobipos.app.Cashier.dashboardFragments.MakeSales.viewCartData;
import com.mobipos.app.Cashier.dashboardFragments.ViewSales.PullSaleData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.PushSaleData;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by folio on 12/6/2017.
 */

public class Sales extends Controller {
    public Sales(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    public static String tb_name="tb_sales";
    public static String col_1="tb_sale_id";
    public static String col_2="order_id";
    public static String col_3="amount_tendered";
    public static String col_4="amount_total";
    public static String col_5="sync_status";
    public static String col_6="active_status";
    public static String col_7="trans_type";
    public static String col_8="trans_code";

    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_SALES="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY,"+
            col_2+" INT(11),"+
            col_3+" INT(11),"+
            col_4+" INT(11),"+
            col_5+" INT(11),"+
            col_7+" VARCHAR(50),"+
            col_8+" VARCHAR(50),"+
            col_6+" INT(11))";

    public int createId(){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;
        sql="SELECT * FROM "+tb_name+" ORDER BY tb_sale_id DESC LIMIT 1";
        List data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        int i=0;
        if(cursor.moveToFirst()){
            i= cursor.getInt(cursor.getColumnIndex(col_1));
        }
        cursor.close();
        return i+1;

    }

    public boolean salesIdExists(String type,String id){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;

            if(type.equals("sales_id")){
                sql="SELECT * FROM "+tb_name+ " WHERE "+col_1+"= "+id;
            }else{
                sql="SELECT * FROM "+tb_name+ " WHERE "+col_2+"= "+id;
            }



        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        if(cursor.moveToFirst()){
            Log.d("transcation type",cursor.getString(cursor.getColumnIndex(col_7)));
            Log.d("order id",cursor.getString(cursor.getColumnIndex(col_2)));
            Log.d("total amount",cursor.getString(cursor.getColumnIndex(col_3)));

        }
        db.close();

        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean addSaleData(List<PushSaleData> data){
        SQLiteDatabase db=this.getWritableDatabase();
        String sql="SELECT * from "+tb_name;

        int salesId=createId();
        Cursor cursor;
        cursor=db.rawQuery(sql,null);

        try{

            ContentValues values=new ContentValues();
            values.put(col_1,salesId);
            values.put(col_2,data.get(0).orderId);
            values.put(col_3,data.get(0).amountTendered);
            values.put(col_4,data.get(0).amount_total);
            values.put(col_5,"0");
            values.put(col_6,"1");
            values.put(col_7,data.get(0).transaction_type);
            values.put(col_8,data.get(0).transaction_code);

            db.insert(tb_name,null,values);
            Log.d("sales id: ",String.valueOf(salesId));
            db.close();

            List<productInterface> products=getProductCount(data.get(0).orderId);
            for(int i=0;i<products.size();i++){

                updateInventory(products.get(i).product_id,products.get(i).count);
                updateMovement(products.get(i).product_id,products.get(i).count);

                Log.d("product id no:", String.valueOf(products.get(i).product_id));
                Log.d("count number", String.valueOf(products.get(i).product_id));
            }

        }catch (SQLException e){
            e.printStackTrace();

        }

        return salesIdExists("sales_id",String.valueOf(createId()));
    }

    public List<PullSaleData> getSalesData(String mode){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;
        if(mode.equals("loadLocal")){
            sql="SELECT * from "+tb_name+" ORDER BY tb_sale_id DESC";

        }else{
            sql="SELECT * from "+tb_name+" WHERE sync_status=0";

        }

        List<PullSaleData> data=new ArrayList<>();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        try {
            if (cursor.moveToFirst()) {

                do{
                    data.add(new PullSaleData(cursor.getString(cursor.getColumnIndex(col_1)),
                            cursor.getString(cursor.getColumnIndex(col_2)),
                            cursor.getString(cursor.getColumnIndex(col_4)),
                            cursor.getString(cursor.getColumnIndex(col_7)),
                            cursor.getString(cursor.getColumnIndex(col_8))
                          ));
                    Log.d("order no:",cursor.getString(cursor.getColumnIndex(col_2)));
                    Log.d("sales id",cursor.getString(cursor.getColumnIndex(col_1)));
                    Log.d("amount tendered:",cursor.getString(cursor.getColumnIndex(col_3)));
                }while (cursor.moveToNext());

            }

            cursor.close();
        } catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        return data;
    }

    public List<productInterface> getProductCount(String order_id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT product_id,SUM(product_count) as total FROM "+Order_Items.tb_name+ " WHERE "+Order_Items.col_2+"="+order_id;

        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);
        List<productInterface> data=new ArrayList<>();

        if(cursor.moveToFirst()){

            do{
                data.add(new productInterface(cursor.getInt(cursor.getColumnIndex("product_id")),
                        cursor.getInt(cursor.getColumnIndex("total"))));
            }while (cursor.moveToNext());
        }

        return data;
    }

    public void updateInventory(int product_id,int count){
        SQLiteDatabase db=getReadableDatabase();

        String sql="UPDATE "+Inventory.tb_name+" SET inventory_count=inventory_count-"+count+" WHERE product_id="+product_id;
        db.execSQL(sql);
        db.close();
    }

    public void updateMovement(int product_id,int count){
        SQLiteDatabase db=this.getWritableDatabase();
        //  String sql="DELETE from "+tb_name;


        try{
            // db.execSQL(sql);
            ContentValues values=new ContentValues();
            values.put(inventory_movement.col_1,createMovementId());
            values.put(inventory_movement.col_2,product_id);
            values.put(inventory_movement.col_3,"OUT");
            values.put(inventory_movement.col_4,count);
            values.put(inventory_movement.col_6,new Date().toString());
            values.put(inventory_movement.col_5,"0");

            db.insert(inventory_movement.tb_name,null,values);
            Log.d("produc id:",String.valueOf(product_id));
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int createMovementId(){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;
        sql="SELECT * FROM "+inventory_movement.tb_name+" ORDER BY tb_inv_movement_id DESC LIMIT 1";
        List data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        int i=0;
        if(cursor.moveToFirst()){
            i= cursor.getInt(cursor.getColumnIndex(col_1));
        }
        cursor.close();
        return i+1;
    }

    public void updateSyncStatus(String sales_id){
        SQLiteDatabase db=getWritableDatabase();
        String sql=null;
        ContentValues value=new ContentValues();
        value.put("sync_status",1);
        db.update(tb_name,value,"tb_sale_id="+sales_id,null);
        db.close();
        Log.d("sync update",sales_id+" was executed");
    }
}
