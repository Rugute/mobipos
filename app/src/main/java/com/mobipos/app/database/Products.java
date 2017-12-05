package com.mobipos.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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

    public static String CREATE_TABLE_PRODUCTS="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY AUTO_INCREMENT,"+
            col_2+" VARCHAR(50),"+
            col_3+" INT(11),"+
            col_4+" INT(11),"+
            col_5+" INT(11));";

}
