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
    List<PullSaleData> saleData=new ArrayList<>();
    Sales salesdb;
    Users usersdb;
    Orders ordersdb;
    int successState=0;
    int successStateSales=0;
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
                parameters.add(new BasicNameValuePair("user_id",usersdb.get_user_id("cashier")));
                parameters.add(new BasicNameValuePair("order_id",orders.get(i).order_id));
                parameters.add(new BasicNameValuePair("date",orders.get(i).date));

                saleData=new ArrayList<>();
                saleData=salesdb.getSalesData("sync",orders.get(i).order_id);

                JSONObject jsonObject=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+
                                SyncDefaults.sync_orders,
                        "GET",parameters);

                Log.d("sync order status:",jsonObject.toString());
                salesdb.updateSyncStatus(orders.get(i).order_id,ordersdb.tb_name,ordersdb.col_1);
                try{
                    int success=jsonObject.getInt("success");
                    if (success==1){

                        List param=new ArrayList();
                        param.add(new BasicNameValuePair("user_id",usersdb.get_user_id("cashier")));
                        param.add(new BasicNameValuePair("order_id",saleData.get(0).orderId));
                        param.add(new BasicNameValuePair("amount_total",saleData.get(0).amount_total));
                        param.add(new BasicNameValuePair("app_sale_id",saleData.get(0).sale_id));
                        param.add(new BasicNameValuePair("trans_type",saleData.get(0).transaction_type));
                        param.add(new BasicNameValuePair("trans_code",saleData.get(0).transaction_code));
                        param.add(new BasicNameValuePair("date",orders.get(i).date));

                        JSONObject jsonObject1=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+
                                        SyncDefaults.sync_sales,
                                "GET",param);

                        Log.d("sync sales status:",jsonObject1.toString());
                        //    salesdb.updateSyncStatus(saleData.get(i).sale_id,salesdb.tb_name,salesdb.col_5);
                        try{
                            int successSales=jsonObject1.getInt("success");
                            if (successSales==1){
                                successStateSales=1;
                                //  server=jsonObject.getString("message");

                                Log.d("sale id:",saleData.get(i).sale_id);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        successState=1;

                        Log.d("sale id:",orders.get(i).order_id);
                    }
                }catch (Exception e){
                    e.printStackTrace();
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