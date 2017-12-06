package com.mobipos.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by folio on 12/5/2017.
 */

public class Users extends Controller {

    public static String tb_name="tb_user";
    public static String col_1="user_id";
    public static String col_2="user_name";
    public static String col_3="email";
    public static String col_4="password";
    public static String col_5="active_status";
    public static String col_6="account_type";

    public static String tb_pin="tb_pin";
    public static String login_pin="pin";



    public static String DROP_TABLE="DROP TABLE IF NOT EXISTS "+ tb_name;
    public static String DROP_TABLE_PIN="DROP TABLE IF NOT EXISTS "+ tb_pin;

    public static String CREATE_TABLE_USERS="CREATE TABLE IF NOT EXISTS "+tb_name+" ("+
            col_1+" INT(11) PRIMARY KEY,"+
            col_2+" VARCHAR(50),"+
            col_3+" VARCHAR(50),"+
            col_4+" VARCHAR(50),"+
            col_5+" INT(11),"+
            col_6+" VARCHAR(50));";

    public static String CREATE_PIN_TABLE="CREATE TABLE IF NOT EXISTS "+tb_pin+" ("+
            login_pin+" VARCHAR(250);";



    public Users(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //check user
    public int CheckUserOrPin(String table_name){
        String sql="SELECT * from "+table_name;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql,null);

        return cursor.getCount();
    }
    public String encryptPassword(String password,SecretKey secretKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher=null;
        cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        byte[] cipherText=cipher.doFinal(password.getBytes("UTF-8"));

        return cipherText.toString();
    }

    public static  SecretKey generateKey(String user_id) throws NoSuchAlgorithmException,InvalidKeyException{
        SecretKey secret=new SecretKeySpec(user_id.getBytes(),"AES");
        return secret;
    }

    public String[] get_admin_login_details(){
        String sql="SELECT * from "+tb_name;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql,null);
        String[] user_details=new String[cursor.getCount()];
        user_details[0]=cursor.getString(2);
        user_details[1]=cursor.getString(3);

        return user_details;

    }

    public boolean insertPin(String pin,String user_id) throws InvalidKeyException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException {
        String encrypt_pin= encryptPassword(pin, generateKey(user_id));
        String checker="SELECT * FROM "+tb_pin;
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(login_pin,encrypt_pin);
        db.insert(tb_pin,null,values);
        db.close();
        SQLiteDatabase db_read=this.getReadableDatabase();

       if(CheckUserOrPin(tb_pin)>0){
           return true;
       }else{
           return false;
       }
    }

    public boolean insertUserData(String[] data){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(col_1,data[0]);
        values.put(col_2,data[1]);
        values.put(col_3,data[2]);
        values.put(col_4,data[3]);
        values.put(col_5,data[4]);
        values.put(col_6,data[5]);

        db.insert(tb_name,null,values);
        db.close();

        if(CheckUserOrPin(tb_name)>0){
            return true;
        }else{
           return false;
        }
    }

    public boolean password_match(String pin) throws InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException {
        SQLiteDatabase db=this.getReadableDatabase();
        String sql="SELECT user_id from "+tb_name;
        String sql_pin="SELECT "+login_pin+" from "+tb_pin;
        Cursor cursor=db.rawQuery(sql,null);
        Cursor cursor_pin=db.rawQuery(sql_pin,null);

        if(cursor_pin.getString(0).equals(encryptPassword(pin,generateKey(cursor.getString(0))))){
            return true;
        }else{
            return false;
        }
    }
}
