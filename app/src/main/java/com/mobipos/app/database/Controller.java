package com.mobipos.app.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by folio on 12/5/2017.
 */

public class Controller extends SQLiteOpenHelper implements Serializable {



    public Controller(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, defaults.database_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try{
            if(dropTables(sqLiteDatabase)){
                if( createTables(sqLiteDatabase)){
                    Log.d("database status","tables created successfully");
                }else{
                    Log.d("database status","problem creating tables");
                }

            }
        }   catch (SQLException e){
            e.getMessage();
        }

    }

    public boolean createTables(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL(Users.CREATE_TABLE_USERS);
        sqLiteDatabase.execSQL(Users.CREATE_PIN_TABLE);
        sqLiteDatabase.execSQL(Users.CREATE_TABLE_BRANCH);
        sqLiteDatabase.execSQL(Categories.CREATE_TABLE_CATEGORIES);
        sqLiteDatabase.execSQL(Products.CREATE_TABLE_PRODUCTS);
        sqLiteDatabase.execSQL(Product_Prices.CREATE_TABLE_PRODUCTS_PRICE);
        sqLiteDatabase.execSQL(Inventory.CREATE_TABLE_INVENTORY);
        sqLiteDatabase.execSQL(inventory_movement.CREATE_TABLE_INVENTORY_MOVEMENT);
        sqLiteDatabase.execSQL(Sales.CREATE_TABLE_SALES);
        sqLiteDatabase.execSQL(Order_Items.CREATE_TABLE_ORDER_ITEMS);
        sqLiteDatabase.execSQL(Orders.CREATE_TABLE_ORDERS);
        sqLiteDatabase.execSQL(Taxes.CREATE_TABLE_TAXES);


        return true;
    }
    public boolean dropTables(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL(Users.DROP_TABLE);
        sqLiteDatabase.execSQL(Users.DROP_TABLE_PIN);
        sqLiteDatabase.execSQL(Users.DROP_TABLE_BRANCH);
        sqLiteDatabase.execSQL(Categories.DROP_TABLE);
        sqLiteDatabase.execSQL(Products.DROP_TABLE);
        sqLiteDatabase.execSQL(Product_Prices.DROP_TABLE);
        sqLiteDatabase.execSQL(Inventory.DROP_TABLE);
        sqLiteDatabase.execSQL(inventory_movement.DROP_TABLE);
        sqLiteDatabase.execSQL(Orders.DROP_TABLE);
        sqLiteDatabase.execSQL(Order_Items.DROP_TABLE);
        sqLiteDatabase.execSQL(Sales.DROP_TABLE);
        sqLiteDatabase.execSQL(Taxes.DROP_TABLE);

        return true;
    }
}
