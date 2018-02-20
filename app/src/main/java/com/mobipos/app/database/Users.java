package com.mobipos.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobipos.app.Cashier.dashboardFragments.Account.CashierData;


import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class Users extends Controller {

    public static String tb_name="tb_user";
    private static String col_1="user_id";
    private static String col_2="user_name";
    private static String col_3="email";
    private static String col_4="password";
    private static String col_5="active_status";
    private static String col_6="account_type";
    private static String col_7="phone_number";


    private static String tb_pin="tb_pin";
    private static String login_pin="pin";


    private static String tb_branch="tb_branch";
    private static String col_branch_id="branch_id";
    private static String col_branch_name="branch_name";

    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;
    public static String DROP_TABLE_PIN="DROP TABLE IF NOT EXISTS "+ tb_pin;
    public static String DROP_TABLE_BRANCH="DROP TABLE IF NOT EXISTS "+ tb_branch;

    public static String CREATE_TABLE_USERS="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY,"+
            col_2+" VARCHAR(50),"+
            col_3+" VARCHAR(50),"+
            col_4+" VARCHAR(50),"+
            col_5+" INT(11),"+
            col_7+" VARCHAR(50),"+
            col_6+" VARCHAR(50))";

    public static String CREATE_PIN_TABLE="CREATE TABLE IF NOT EXISTS "+tb_pin+" ("+
            login_pin+" VARCHAR(250))";
    public static String CREATE_TABLE_BRANCH="CREATE TABLE IF NOT EXISTS "+tb_branch+" ("+
            col_branch_id+" INT(11),"+col_branch_name+" VARCHAR(50))";



    public Users(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //check user
    public int CheckUserOrPin(String table_name){
        String sql="SELECT * from "+table_name;
        String sql_pin="SELECT * FROM "+tb_pin;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=null;
        int count=0;
        try {
            if(table_name.equals(tb_name)){
                cursor =db.rawQuery(sql,null);
            }else{
                cursor =db.rawQuery(sql_pin,null);
            }

         count=cursor.getCount();


         if(table_name.equals(tb_name)){
             if(cursor.moveToFirst()){
                 Log.d("Value found", cursor.getString(cursor.getColumnIndex(col_2)));
                 Log.d("size found", String.valueOf(cursor.getCount()));
             }
         }else if(table_name.equals(tb_pin)){
             if(cursor.moveToFirst()){
                 Log.d("Value found", cursor.getString(cursor.getColumnIndex(login_pin)));
            //     Log.d("size found", String.valueOf(cursor.getCount()));
             }
         }


        }catch (SQLException e){
            e.printStackTrace();
        }finally {
           db.close();
        }


        return count;
    }
    public String encryptPassword(String password,SecretKey secretKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException,
            BadPaddingException, IllegalBlockSizeException {



        Cipher cipher;
        cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        byte[] cipherText=cipher.doFinal(password.getBytes("UTF-8"));

        return cipherText.toString();
    }

    public static  SecretKey generateKey(String user_id) throws NoSuchAlgorithmException,InvalidKeyException{
        SecureRandom random=new SecureRandom();
        byte[] salt=new byte[16];
        random.nextBytes(salt);
        SecretKey secret= new SecretKeySpec(salt,"AES");
        return secret;
    }

    public String[] get_login_details(){
        String sql="SELECT * from "+tb_name +" LIMIT 1";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql,null);
        String[] user_details=new String[3];

        if (cursor.moveToFirst()) {

            user_details[0]=cursor.getString(cursor.getColumnIndex(col_3));
            Log.d("email",user_details[0]);
            user_details[1]=cursor.getString(cursor.getColumnIndex(col_4));
            Log.d("password",user_details[1]);
            user_details[2]=cursor.getString(cursor.getColumnIndex(col_6));
            Log.d("ac type",user_details[2]);



        }

        db.close();
        return user_details;

    }

    public boolean insertPin(String pin,String user_id) throws InvalidKeyException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException {
     //   String encrypt_pin= encryptPassword(pin, generateKey(user_id));

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(login_pin,pin);

        try{
            db.insert(tb_pin,null,values);
            db.close();
          //  SQLiteDatabase db_read=this.getReadableDatabase();

        }catch (Exception e){
            e.printStackTrace();
        }
        return CheckUserOrPin(tb_pin) > 0;
    }

    public boolean insertUserData(String[] data){
        SQLiteDatabase db=this.getWritableDatabase();
        String sql="DELETE from "+tb_name;

        try{
            db.execSQL(sql);
            ContentValues values=new ContentValues();
            values.put(col_1,data[0]);
            values.put(col_2,data[1]);
            values.put(col_3,data[2]);
            values.put(col_4,data[3]);
            values.put(col_5,data[4]);
            values.put(col_6,data[5]);
            values.put(col_7,data[6]);

            db.insert(tb_name,null,values);
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
//        finally {
//            db.endTransaction();
//        }


        return CheckUserOrPin(tb_name) > 0;
    }

    public boolean password_match(String pin) throws InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException {
        SQLiteDatabase db=this.getReadableDatabase();
     //   String sql="SELECT password from "+tb_name ;
        String sql_pin="SELECT * from "+tb_pin + " LIMIT 1";
      //   Cursor cursor=null;
       //   Cursor cursor_pin=null;
          String the_pin=null;

         //   db.beginTransaction();
         //    cursor=db.rawQuery(sql,null);
           Cursor  cursor_pin=db.rawQuery(sql_pin,null);


        if(cursor_pin.moveToFirst()){
            Log.d("password count:",String.valueOf(cursor_pin.getCount()));
            Log.d("password count:",cursor_pin.getString(cursor_pin.getColumnIndex(login_pin)));
            the_pin=cursor_pin.getString(cursor_pin.getColumnIndex(login_pin));
        }
        db.close();

        return the_pin.equals(pin);
    }

    public String get_user_id(){
        SQLiteDatabase db=this.getReadableDatabase();
        String sql_pin="SELECT * from "+tb_name + " LIMIT 1";
        String user_id=null;

        Cursor  cursor=db.rawQuery(sql_pin,null);


        if(cursor.moveToFirst()){
            user_id=cursor.getString(cursor.getColumnIndex(col_1));
            Log.d("User id:",cursor.getString(cursor.getColumnIndex(col_1)));
        }
        db.close();

        return user_id;
    }

    public boolean check_branch(String id){
        SQLiteDatabase db=getReadableDatabase();
        String sql="SELECT * FROM "+tb_branch+ " WHERE "+col_branch_id+"= "+id;
        Cursor cursor=null;
        cursor=db.rawQuery(sql,null);

        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean insert_branch(String id,String name){
        String sql="DELETE from "+tb_branch;
        SQLiteDatabase db=this.getWritableDatabase();
        try{

            ContentValues values=new ContentValues();
            values.put(col_branch_id,id);
            values.put(col_branch_name,name);

            db.insert(tb_branch,null,values);
            Log.d("insert branch name:",name);
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        return check_branch(id);
    }

    public int clearUserData(){
        String sql="DELETE from "+tb_name;
        String sql_pin="DELETE from "+tb_pin;
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(sql);
        db.execSQL(sql_pin);

        return CheckUserOrPin(tb_name);
    }

    public List<CashierData> getCashierData(){
        SQLiteDatabase db=this.getReadableDatabase();
        String sql_pin="SELECT * from "+tb_name + " LIMIT 1";
        Cursor  cursor=db.rawQuery(sql_pin,null);
        List<CashierData> data=new ArrayList<>();

        if(cursor.moveToFirst()){
            data.add(new CashierData(cursor.getString(cursor.getColumnIndex(col_3)),
                    cursor.getString(cursor.getColumnIndex(col_7)),
                    cursor.getString(cursor.getColumnIndex(col_4)),
                    cursor.getString(cursor.getColumnIndex(col_2))));
        }
        db.close();

        return data;
    }
}
