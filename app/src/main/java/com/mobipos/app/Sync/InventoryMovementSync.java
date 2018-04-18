package com.mobipos.app.Sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.database.Controller;
import com.mobipos.app.database.Inventory;
import com.mobipos.app.database.Orders;
import com.mobipos.app.database.Sales;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;
import com.mobipos.app.database.inventory_movement;
import com.mobipos.app.database.inventory_movement_interface;
import com.mobipos.app.database.orders_interface;
import com.mobipos.app.login.PackageConfig;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 1/15/2018.
 */

public class InventoryMovementSync{

    Context context;
    List<inventory_movement_interface> stock_out=new ArrayList<>();
    Sales salesdb;
    Users usersdb;
    Inventory inventorydb;
    inventory_movement stockMovedb;
    Orders ordersdb;
    int successState=0;
    Controller controller;

    public InventoryMovementSync(Context context){
        this.context=context;
        controller=new Controller(context, defaults.database_name,null,1);
        stockMovedb=new inventory_movement(context, defaults.database_name,null,1);
        usersdb=new Users(context, defaults.database_name,null,1);
        inventorydb=new Inventory(context, defaults.database_name,null,1);
        salesdb=new Sales(context, defaults.database_name,null,1);
        new DataLoad().execute();
    }

    class DataLoad extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute(){
            stock_out=stockMovedb.DataSync();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();

            for (int i=0;i<stock_out.size();i++){
                List parameters=new ArrayList();
                parameters.add(new BasicNameValuePair("user_id",usersdb.get_user_id("cashier")));
                parameters.add(new BasicNameValuePair("id",stock_out.get(i).id));
                parameters.add(new BasicNameValuePair("product_id",stock_out.get(i).product_id));
                parameters.add(new BasicNameValuePair("movement_type",stock_out.get(i).type));
                parameters.add(new BasicNameValuePair("count",stock_out.get(i).count));
                parameters.add(new BasicNameValuePair("sale_id",stock_out.get(i).saleId));
                parameters.add(new BasicNameValuePair("date",stock_out.get(i).date));

                JSONObject jsonObject=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+SyncDefaults.sync_stock_movement,
                        "GET",parameters);

                Log.d("sync order status:",jsonObject.toString());
                salesdb.updateSyncStatus(stock_out.get(i).id,stockMovedb.tb_name,stockMovedb.col_1);
                try{
                    int success=jsonObject.getInt("success");
                    if (success==1){
                        successState=1;

                        Log.d("movement id:",stock_out.get(i).id);
                        String stock_count=inventorydb.getOpeningStock(stock_out.get(i).product_id);
                        String low_count=inventorydb.LowStockValue(stock_out.get(i).product_id);



                        if(Integer.parseInt(low_count)>=Integer.parseInt(stock_count)){
                            List alert_parameters=new ArrayList();
                            alert_parameters.add(new BasicNameValuePair("user_id",usersdb.get_user_id("cashier")));
                            alert_parameters.add(new BasicNameValuePair("product_id",stock_out.get(i).product_id));

                            if(Integer.parseInt(stock_count)>0){
                                alert_parameters.add(new BasicNameValuePair("message","PRODUCT STOCK IS LOW"));
                                alert_parameters.add(new BasicNameValuePair("alert_type","1"));
                            }else{
                                alert_parameters.add(new BasicNameValuePair("alert_type","2"));
                                alert_parameters.add(new BasicNameValuePair("message","PRODUCT OUT OF STOCK"));
                            }

                            JSONObject alertObject=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+
                                            SyncDefaults.sync_alert_message,
                                    "GET",alert_parameters);
                          try{
                              int alert_response_success=alertObject.getInt("success");
                              if(alert_response_success==1){
                                  Log.d("alert response",alertObject.getString("message"));
                              }
                          }catch (Exception e){

                          }
                        }
                    }
                }catch (Exception e){

                }

            }

            List params=new ArrayList();
            params.add(new BasicNameValuePair("user_id",usersdb.get_user_id("cashier")));

            JSONObject stock_in_object=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+
                            SyncDefaults.sync_stock_in_movement,
                    "GET",params);
            try{
                int success=stock_in_object.getInt("success");
                JSONArray updateArray=stock_in_object.getJSONArray("data");
                if(success==1){

                    if(updateArray.length()>0){
                        for (int i=0;i<updateArray.length();i++){
                            JSONObject updateObject=updateArray.getJSONObject(i);
                            String inventory_count=inventorydb.getOpeningStock(updateObject.getString("product_id"));
                            String inventory_update=updateObject.getString("quantity");
                            String count=String.valueOf(Integer.parseInt(inventory_count)+Integer.parseInt(inventory_update));

                            if(!inventorydb.updateStock(updateObject.getString("product_id"),count)){
                                Log.d("inventory stock in","update failed for "+updateObject.getString("product_id"));
                            }else{
                                List par=new ArrayList();
                                par.add(new BasicNameValuePair("movement_id",updateObject.getString("movement_id")));

                                JSONObject serverUpdateObject=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+
                                                SyncDefaults.sync_stock_in_server_update,
                                        "GET",par);

                                try {
                                    int successSt=serverUpdateObject.getInt("success");
                                    if(successSt==1){
                                        Log.d("server message",serverUpdateObject.getString("message"));
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                        }
                    }


                }
            }catch (Exception e){
                e.printStackTrace();
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
