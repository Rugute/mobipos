package com.mobipos.app.database;

/**
 * Created by folio on 12/5/2017.
 */

public class Users {

    public static String tb_name="tb_user";
    public static String col_1="user_id";
    public static String col_2="user_name";
    public static String col_3="email";
    public static String col_4="password";
    public static String col_5="active_status";
    public static String col_6="account_type";

    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;

    public static String CREATE_TABLE_USERS="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY,"+
            col_2+" VARCHAR(50),"+
            col_3+" VARCHAR(50),"+
            col_4+" VARCHAR(50),"+
            col_5+" INT(11),"+
            col_6+" VARCHAR(50));";
}
