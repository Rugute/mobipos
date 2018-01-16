package com.mobipos.app.Sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mobipos.app.Cashier.dashboardFragments.ViewSales.PullSaleData;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.database.Controller;
import com.mobipos.app.database.Orders;
import com.mobipos.app.database.Sales;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;
import com.mobipos.app.database.orders_interface;
import com.mobipos.app.login.PackageConfig;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 1/15/2018.
 */


public class OrdersSync{

    Context context;
    List<orders_interface> orders=new ArrayList<>();
    Sales salesdb;
    Users usersdb;
    Orders ordersdb;
    int successState=0;
    Controller controller;
    public OrdersSync(Context context){
        this.context=context;
        controller=new Controller(context,defaults.database_name,null,1);
        usersdb=new Users(context,defaults.database_name,null,1);
        ordersdb=new Orders(context,defaults.database_name,null,1);
        salesdb=new Sales(context,defaults.database_name,null,1);
        controller=new Controller(context,defaults.database_name,null,1);
        new DataLoad().execute();
    }

    class DataLoad extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute(){
            orders=ordersdb.DataSync();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();

            for (int i=0;i<orders.size();i++){
                List parameters=new ArrayList();
                parameters.add(new BasicNameValuePair("user_id",usersdb.get_user_id()));
                parameters.add(new BasicNameValuePair("order_id",orders.get(i).order_id));
                parameters.add(new BasicNameValuePair("date",orders.get(i).date));

                JSONObject jsonObject=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+SyncDefaults.sync_orders,
                        "GET",parameters);

                Log.d("sync order status:",jsonObject.toString());
                salesdb.updateSyncStatus(orders.get(i).order_id,ordersdb.tb_name,ordersdb.col_1);
                try{
                    int success=jsonObject.getInt("success");
                    if (success==1){
                        successState=1;

                        Log.d("sale id:",orders.get(i).order_id);
                    }
                }catch (Exception e){

                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            if(successState==1){
                Toast.makeText(context,"sync successful",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"sync failed!!",Toast.LENGTH_SHORT).show();
            }
        }
    }


}