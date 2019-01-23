package com.mobipos.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobipos.app.Cashier.dashboardFragments.Inventory.Items.CashierItemsData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSaleInterfaceData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSaleProductData;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSaleProductInfo;
import com.mobipos.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 12/5/2017.
 */

public class Products extends Controller {

    public Products(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    public static String tb_name="tb_products";
    public static String col_1="product_id";
    public static String col_2="product_name";
    public static String col_3="category_id";
    public static String col_4="sync_status";
    public static String col_5="active_status";
    public static String col_6="measure";
    public static String col_7="tax_margin";

    public static String DROP_TABLE="DROP TABLE IF  EXISTS "+ tb_name;

    public static String CREATE_TABLE_PRODUCTS="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY,"+
            col_2+" VARCHAR(50),"+
            col_3+" INT(11),"+
            col_4+" INT(11),"+
            col_7+" INT(11),"+
            col_6+" VARCHAR(50),"+
            col_5+" INT(11))";

    public int getProductCount(String category_id){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;
        if(category_id.equals("all")){
            sql="SELECT * FROM "+tb_name;
        }else{
            sql="SELECT * FROM "+tb_name+" WHERE category_id="+category_id;
        }


        List data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        int i=cursor.getCount();
        cursor.close();
      //  Log.d("product count in loop:",String.valueOf(cursor.getCount()));
        return i;
    }

    public boolean ProductExists(String id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name+ " WHERE "+col_1+"='"+id+"'";
        Log.d("sql statement",sql);
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);



        if(cursor.getCount()>0){
            if(cursor.moveToFirst()){
                Log.d(cursor.getString(cursor.getColumnIndex(col_2)),cursor.getString(cursor.getColumnIndex(col_1)));
            }
            return true;
        }else{
            return false;
        }
    }

    public int getCount(){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_name;

        List data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        return cursor.getCount();
    }


    public boolean insertProduct(String id,String name,String category_id,String measure,String tax_mode){
        SQLiteDatabase db=this.getWritableDatabase();
        //  String sql="DELETE from "+tb_name;

        if(ProductExists(id)){
            return  true;
        }else{
            try{
                // db.execSQL(sql);
                ContentValues values=new ContentValues();
                values.put(col_1,id);
                values.put(col_2,name);
                values.put(col_3,category_id);
                values.put(col_4,"1");
                values.put(col_5,"1");
                values.put(col_6,measure);
                values.put(col_7,tax_mode);


                db.insert(tb_name,null,values);
                Log.d("insert product name:",name);
                db.close();
            }catch (SQLException e){
                e.printStackTrace();
            }

            return ProductExists(id);
        }



    }

    public List<CashierItemsData> getProducts(String category){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;
        if(category.equals("all")){
            sql="SELECT tb_products.product_id,tb_products.tax_margin,tb_products.product_name,tb_products.measure," +
                    "tb_products_price.price,tb_inventory.inventory_count" +
                    "  FROM "+tb_name+
                    " INNER JOIN "+Product_Prices.tb_name+" ON tb_products.product_id=tb_products_price.product_id INNER JOIN " +
                    Inventory.tb_name+" ON tb_inventory.product_id=tb_products.product_id ORDER BY tb_products.product_name ASC";

        }else{
            sql="SELECT tb_products.product_id,tb_products.tax_margin,tb_products.product_name,tb_products.measure,tb_products_price.price,tb_inventory.inventory_count" +
                    "  FROM "+tb_name+
                    " INNER JOIN "+Product_Prices.tb_name+" ON tb_products.product_id=tb_products_price.product_id INNER JOIN " +
                    Inventory.tb_name+" ON tb_inventory.product_id=tb_products.product_id  WHERE " +
                    "tb_products.category_id='"+category+"' ORDER BY tb_products.product_name ASC";
        }


        List<CashierItemsData> data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        try {
            if (cursor.moveToFirst()) {

                do{
                    data.add(new CashierItemsData(cursor.getString(cursor.getColumnIndex(col_1)),
                            cursor.getString(cursor.getColumnIndex(col_2)),cursor.getString(cursor.getColumnIndex(
                                    Product_Prices.col_3)),cursor.getString(cursor.getColumnIndex(Inventory.col_3)),
                            cursor.getString(cursor.getColumnIndex(col_6)),R.mipmap.ic_launcher));

                    Log.d("product name:",cursor.getString(cursor.getColumnIndex(col_2)));
                    Log.d("product id:",cursor.getString(cursor.getColumnIndex(col_1)));
                    Log.d("price:",cursor.getString(cursor.getColumnIndex(Product_Prices.col_3)));
                    Log.d("tax margin:",cursor.getString(cursor.getColumnIndex(col_7)));
                }while (cursor.moveToNext());

            }

            cursor.close();
        } catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        return data;
    }

    public List<MakeSaleProductData> getSalesProduct(){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;

            sql="SELECT tb_products.product_id,tb_products.product_name,tb_products.category_id,tb_products.measure,tb_products_price.price,tb_inventory.inventory_count" +
                    "  FROM "+tb_name+
                    " INNER JOIN "+Product_Prices.tb_name+" ON tb_products.product_id=tb_products_price.product_id INNER JOIN " +
                    Inventory.tb_name+" ON tb_inventory.product_id=tb_products.product_id GROUP BY tb_products.product_name ORDER BY tb_products.product_name ASC";



        List<MakeSaleProductData> data=new ArrayList();
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        try {
            if (cursor.moveToFirst()) {

                do{
                    data.add(new MakeSaleProductData(Integer.parseInt(cursor.getString(cursor.getColumnIndex(col_3))),
                            cursor.getString(cursor.getColumnIndex(col_1)),
                            cursor.getString(cursor.getColumnIndex(col_2)),
                            cursor.getString(cursor.getColumnIndex(Product_Prices.col_3)),
                            cursor.getString(cursor.getColumnIndex(Inventory.col_3)),
                            cursor.getString(cursor.getColumnIndex(col_6)),
                            R.mipmap.ic_launcher));

                    Log.d("make sale product name:",cursor.getString(cursor.getColumnIndex(col_2)));
                    Log.d("make sale product id:",cursor.getString(cursor.getColumnIndex(col_1)));
                    Log.d("make sale price:",cursor.getString(cursor.getColumnIndex(Product_Prices.col_3)));
                }while (cursor.moveToNext());

            }

            cursor.close();
        } catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        return data;
    }

    public int taxMode(String product_id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT tax_margin FROM tb_products WHERE product_id="+product_id;

        Cursor cursor=db.rawQuery(sql,null);
        int i=0;

        try{
            if(cursor.moveToFirst()){
                    Log.d("selected tax id",cursor.getString(cursor.getColumnIndex("tax_margin")));
                    i=cursor.getInt(cursor.getColumnIndex(col_7));
            }

            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }

    public List<MakeSaleInterfaceData> search(String s){
        SQLiteDatabase db=getReadableDatabase();

        String cat_sql="SELECT * FROM "+Categories.tb_name;

        List<MakeSaleInterfaceData> data_list=new ArrayList<>();

        Cursor product_cursor=null;
        Cursor category_cursor=null;


        category_cursor=db.rawQuery(cat_sql,null);

        try{

            if(category_cursor.moveToFirst()){
                do{
                    MakeSaleInterfaceData list_data=new MakeSaleInterfaceData();
                    String category_id=category_cursor.getString(category_cursor.getColumnIndex(Categories.col_1));

                    String sql="SELECT * FROM "+tb_name+" WHERE "+col_2+" LIKE '%"+s+"%' AND category_id="+category_id;
                    product_cursor=db.rawQuery(sql,null);

                    List<MakeSaleProductInfo> new_product=new ArrayList<>();
                    try {
                        if(product_cursor.getCount()==0){
                            continue;
                        }else{
                            if(product_cursor.moveToFirst()){

                                int serial=0;
                                do{


                                    MakeSaleProductInfo productInfo=new MakeSaleProductInfo();
                                    String product_id=product_cursor.getString(product_cursor.getColumnIndex(col_1));
                                    productInfo.setProduct_id(product_id);
                                    productInfo.setProduct_name(product_cursor.getString(product_cursor.getColumnIndex(col_2)));
                                    productInfo.setId(serial);
                                    productInfo.setProduct_price(get_product_price(product_id));

                                    new_product.add(productInfo);

                                    Log.d("return search",product_cursor.getString(product_cursor.getColumnIndex(col_2)));
                                    Log.d("return search cat name",get_category_name(category_id));

                                    list_data.setProduct(new_product);



                                    serial++;
                                }while (product_cursor.moveToNext());
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    product_cursor.close();
                    list_data.setCategory_id(category_id);
                    list_data.setCategory_name(get_category_name(category_id));

                    data_list.add(list_data);
                }while (category_cursor.moveToNext());
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        category_cursor.close();

        return data_list;
    }

    public String get_product_price(String product_id){
        SQLiteDatabase db=getReadableDatabase();
        String sql=null;

        sql="SELECT price FROM "+Product_Prices.tb_name+" WHERE "+Product_Prices.col_2+"="+product_id+" LIMIT 1";

        String s=null;

        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        if (cursor.moveToFirst()){
            s=cursor.getString(cursor.getColumnIndex("price"));
        }

        cursor.close();

        return s;
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
