package com.mobipos.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by folio on 12/5/2017.
 */

public class Controller extends SQLiteOpenHelper {



    public Controller(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Users.CREATE_TABLE_USERS);
        sqLiteDatabase.execSQL(Categories.CREATE_TABLE_CATEGORIES);
        sqLiteDatabase.execSQL(Products.CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
