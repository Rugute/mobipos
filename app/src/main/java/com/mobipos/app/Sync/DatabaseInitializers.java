package com.mobipos.app.Sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mobipos.app.Cashier.Adapters.CashierCategRvAdapter;
import com.mobipos.app.Cashier.Adapters.CashierItemRvAdapter;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Inventory;
import com.mobipos.app.database.Product_Prices;
import com.mobipos.app.database.Products;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 1/11/2018.
 */


public class DatabaseInitializers {

    public static boolean productsLoaded=false;
    public static boolean categoriesLoaded=false;
    Categories categories;
    Users users;
    Context context;
    Products productsdb;
    Product_Prices pricesdb;
    Inventory inventorydb;
    int user_type;
    public  DatabaseInitializers(Context context,int user_type){
        this.context=context;
        this.user_type=user_type;
        new CategoriesIntiliazer(context,user_type);
        new ProductIntitalizer(context,user_type);
    }
}
