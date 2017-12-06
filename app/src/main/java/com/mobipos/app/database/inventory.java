package com.mobipos.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by folio on 12/5/2017.
 */

public class inventory extends Controller {
    public inventory(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    public static String tb_name="tb_inventory";
 //   public static String col_1="tb_inventory_id";
    public static String col_2="product_id";
    public static String col_3="inventory_count";
    public static String col_4="sync_status";
    public static String col_5="active_status";

    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_INVENTORY="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
          //  col_1+" INT(11) PRIMARY KEY AUTO_INCREMENT,"+
            col_2+" INT(11),"+
            col_3+" INT(11),"+
            col_4+" INT(11),"+
            col_5+" INT(11));";
}
