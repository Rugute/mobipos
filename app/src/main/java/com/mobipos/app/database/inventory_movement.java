package com.mobipos.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by folio on 12/5/2017.
 */

public class inventory_movement extends Controller {
    public inventory_movement(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, 1);
    }

    public static String tb_name="tb_inventory_movement";
    public static String col_1="tb_inv_movement_id";
    public static String col_2="product_id";
    public static String col_3="movement_type";
    public static String col_4="count";
    public static String col_5="sync_status";

    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_INVENTORY_MOVEMENT="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY,"+
            col_2+" INT(11),"+
            col_3+" VARCHAR(50),"+
            col_4+" INT(11),"+
            col_5+" INT(11))";
}
