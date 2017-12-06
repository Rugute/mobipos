package com.mobipos.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
    public static String col_7="time_of_sale";
    public static String col_5="sync_status";
    public static String col_6="active_status";

    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_SALES="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY AUTO_INCREMENT,"+
            col_2+" INT(11),"+
            col_3+" INT(11),"+
            col_4+" INT(11),"+
            col_5+" INT(11),"+
            col_7+" VARCHAR(50),"+
            col_6+" INT(11));";
}
