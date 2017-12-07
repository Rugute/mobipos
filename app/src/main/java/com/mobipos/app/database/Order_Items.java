package com.mobipos.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by folio on 12/6/2017.
 */

public class Order_Items extends Controller {
    public Order_Items(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    public static String tb_name="tb_order_items";
    public static String col_1="tb_order_item_id";
    public static String col_2="order_id";
    public static String col_3="product_id";


    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_ORDER_ITEMS="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY,"+
            col_2+" INT(11),"+
            col_3+" INT(11))";
}
