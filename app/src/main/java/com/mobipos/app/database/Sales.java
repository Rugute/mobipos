package com.mobipos.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.viewCartData;
import com.mobipos.app.Cashier.dashboardFragments.ViewSales.PullSaleData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.PushSaleData;


import java.text.SimpleDateFormat;
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
    public static String col_9="discount";
    public static String col_11="date_of_sale";
    public static String col_10="discount_amount";



    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_SALES="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY,"+
            col_2+" VARCHAR(50),"+
            col_3+" INT(11),"+
            col_4+" INT(11),"+
            col_5+" INT(11),"+
            col_9+" INT(11),"+
            col_10+" INT(11),"+
            col_7+" VARCHAR(50),"+
            col_11+" VARCHAR(50),"+
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

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = simpleDateFormat.format(new Date());
            Log.d("date created",date);

            ContentValues values=new ContentValues();
            values.put(col_1,salesId);
            values.put(col_2,data.get(0).orderId);
            values.put(col_3,data.get(0).amountTendered);
            values.put(col_4,data.get(0).amount_total);
            values.put(col_5,"0");
            values.put(col_6,"1");
            values.put(col_11,date);
            values.put(col_7,data.get(0).transaction_type);
            values.put(col_8,data.get(0).transaction_code);
            values.put(col_9,data.get(0).discount);
            values.put(col_10,data.get(0).discounted_amount);

            db.insert(tb_name,null,values);
            Log.d("sales id: ",String.valueOf(salesId));
            db.close();

            List<viewCartData> products=getCartData(PackageConfig.order_no);
            for(int i=0;i<products.size();i++){

                updateInventory(Integer.parseInt(products.get(i).product_id),Integer.parseInt(products.get(i).count),salesId);


                Log.d("product id no:", String.valueOf(products.get(i).product_id));
                Log.d("count number", String.valueOf(products.get(i).product_id));
            }

        }catch (SQLException e){
            e.printStackTrace();

        }



        return salesIdExists("sales_id",String.valueOf(createId()));
    }

    public List<PullSaleData> getSalesData(String mode,String order){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;
        if(mode.equals("loadLocal")){
            sql= "SELECT * FROM tb_sales ORDER BY tb_sale_id DESC";


        }else{
             sql= "SELECT * FROM tb_sales WHERE  sync_status=0 AND order_id= '"+order+"'";
            Log.d("order sync sale sql",sql);

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
                            cursor.getString(cursor.getColumnIndex(col_8)),
                            cursor.getString(cursor.getColumnIndex(col_9)),
                            cursor.getString(cursor.getColumnIndex(col_10))
                          ));
                    if(mode.equals("sync")){
                        Log.d("order no sync:",cursor.getString(cursor.getColumnIndex(col_2)));
                    }
                    Log.d("order no:",cursor.getString(cursor.getColumnIndex(col_2)));

                    Log.d("sales id",cursor.getString(cursor.getColumnIndex(col_1)));
                    Log.d("AMOUNT  TOTAL",cursor.getString(cursor.getColumnIndex(col_4)));
                    Log.d("discount",cursor.getString(cursor.getColumnIndex(col_9)));
                    Log.d("amount tendered:",cursor.getString(cursor.getColumnIndex(col_3)));
                }while (cursor.moveToNext());

            }

            cursor.close();
        } catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        return data;
    }

    public List<PullSaleData> getSalesData(){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;

        sql= "SELECT * FROM tb_sales ORDER BY tb_sale_id DESC";

        List<PullSaleData> data=new ArrayList<>();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        try {
            if (cursor.moveToFirst()) {

                do{
                    data.add(new PullSaleData(
                            cursor.getString(cursor.getColumnIndex(col_1)),
                            cursor.getString(cursor.getColumnIndex(col_2)),
                            cursor.getString(cursor.getColumnIndex(col_4)),
                            cursor.getString(cursor.getColumnIndex(col_7)),
                            cursor.getString(cursor.getColumnIndex(col_8)),
                            cursor.getString(cursor.getColumnIndex(col_9)),
                            cursor.getString(cursor.getColumnIndex(col_10))

                    ));

                    Log.d("order no:",cursor.getString(cursor.getColumnIndex(col_2)));

                    Log.d("sales id",cursor.getString(cursor.getColumnIndex(col_1)));
                    Log.d("AMOUNT  TOTAL",cursor.getString(cursor.getColumnIndex(col_4)));
                    Log.d("discount",cursor.getString(cursor.getColumnIndex(col_9)));
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

    public void updateInventory(int product_id,int count,int saleId){
        SQLiteDatabase db=getReadableDatabase();

        String sql="UPDATE "+Inventory.tb_name+" SET inventory_count=inventory_count-"+count+" WHERE product_id="+product_id;
        db.execSQL(sql);
        updateMovement(product_id,count,saleId);
        db.close();
    }

    public void updateMovement(int product_id,int count,int saleId){
        SQLiteDatabase db=this.getWritableDatabase();
        //  String sql="DELETE from "+tb_name;

        int move_id=createMovementId();
        try{
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");

            ContentValues values=new ContentValues();
          //  values.put(inventory_movement.col_1,move_id);
            values.put(inventory_movement.col_2,product_id);
            values.put(inventory_movement.col_3,"STOCK_OUT");
            values.put(inventory_movement.col_4,count);
            values.put(inventory_movement.col_6,simpleDateFormat.format(new Date()));
            values.put(inventory_movement.col_5,"0");
            values.put(inventory_movement.col_7,PackageConfig.order_no);

            db.insert(inventory_movement.tb_name,null,values);
            String sql="UPDATE "+Inventory.tb_name+" SET inventory_count=inventory_count-"+count+" WHERE product_id="+product_id;

            Log.d("produc id:",String.valueOf(product_id));
        //    db.close();
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
            i= cursor.getInt(cursor.getColumnIndex(inventory_movement.col_1));
        }
        cursor.close();
        return i+1;




    }

    public void updateSyncStatus(String data_value,String table_name,String column_name){
        SQLiteDatabase db=getWritableDatabase();
        String sql=null;
        String where_clause=column_name+"="+data_value;
        ContentValues value=new ContentValues();
        value.put("sync_status",1);
        db.update(table_name,value,where_clause,null);
        db.close();
        Log.d("sync update",data_value+" was executed");
    }

    public  List<viewCartData> getCartData(String order_id){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;
        sql="SELECT tb_products.product_id,tb_products.product_name,tb_products_price.price,SUM(tb_order_items.product_count) as total " +
                "FROM tb_products " +
                "inner join tb_products_price on tb_products.product_id=tb_products_price.product_id " +
                "inner join tb_order_items on tb_products.product_id=tb_order_items.product_id " +
                "WHERE tb_order_items.order_id='"+order_id+"' GROUP BY tb_products.product_id";



        List<viewCartData> data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        try {
            if (cursor.moveToFirst()) {

                do{

                    data.add(new viewCartData(cursor.getString(cursor.getColumnIndex(Products.col_1)),
                            cursor.getString(cursor.getColumnIndex(Products.col_2)),
                            cursor.getString(cursor.getColumnIndex(Product_Prices.col_3)),
                            cursor.getString(cursor.getColumnIndex("total"))));

                    Log.d("order item id:",cursor.getString(cursor.getColumnIndex(Products.col_1)));
                    Log.d("order item name:",cursor.getString(cursor.getColumnIndex(Products.col_2)));
                    Log.d("total:",  cursor.getString(cursor.getColumnIndex("total")));

                }while (cursor.moveToNext());

            }

            cursor.close();
        } catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        return data;
    }

    public String getSaleDate(String order){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;

        sql="SELECT * FROM "+Orders.tb_name+ " WHERE "+Orders.col_1+"= "+order;

        List data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);
        String date=null;
        if(cursor.moveToFirst()){

            date =cursor.getString(cursor.getColumnIndex("date"));
            Log.d("date selected ",date);
        }

        cursor.close();


        //    Log.d("product count in loop:",String.valueOf(cursor.getCount()));
        return date;
    }

    public int sales_total(String order_id){
        List<PullSaleData> data=getSalesData("total",order_id);

        int total=0;
        for(int i=0;i<data.size();i++){
            Log.d("sales total",data.get(i).amount_total);
            total=total+Integer.parseInt(data.get(i).amount_total);

        }

        Log.d("total sale:",String.valueOf(total));

        return total;
    }
}
