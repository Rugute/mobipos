package com.mobipos.app.Sync;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mobipos.app.Cashier.dashboardFragments.ViewSales.PullSaleData;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.database.Orders;
import com.mobipos.app.database.Sales;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;
import com.mobipos.app.login.PackageConfig;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 1/14/2018.
 */

public class SalesSync{

    Context context;
    List<PullSaleData> saleData=new ArrayList<>();
    Sales salesdb;
    Users usersdb;
    Orders ordersdb;
    int successState=0;
    public SalesSync(Context context){
        this.context=context;
        salesdb=new Sales(context,defaults.database_name,null,1);
        usersdb=new Users(context,defaults.database_name,null,1);
        ordersdb=new Orders(context,defaults.database_name,null,1);

        new DataLoad().execute();
    }

    class DataLoad extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute(){
            saleData=salesdb.getSalesData("sync");
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();

            for (int i=0;i<saleData.size();i++){
                List parameters=new ArrayList();
                parameters.add(new BasicNameValuePair("user_id",usersdb.get_user_id()));
                parameters.add(new BasicNameValuePair("order_id",saleData.get(i).orderId));
                parameters.add(new BasicNameValuePair("amount_total",saleData.get(i).amount_total));
                parameters.add(new BasicNameValuePair("app_sale_id",saleData.get(i).sale_id));
                parameters.add(new BasicNameValuePair("trans_type",saleData.get(i).transaction_type));
                parameters.add(new BasicNameValuePair("trans_code",saleData.get(i).transaction_code));
                parameters.add(new BasicNameValuePair("date_of",ordersdb.getOrderDate(saleData.get(i).orderId)));

                JSONObject jsonObject=jsonParser.makeHttpRequest(PackageConfig.protocol+PackageConfig.hostname+SyncDefaults.sync_sales,
                        "GET",parameters);

                Log.d("sync sales status:",jsonObject.toString());
                salesdb.updateSyncStatus(saleData.get(i).sale_id,salesdb.tb_name,salesdb.col_5);
                try{
                    int success=jsonObject.getInt("success");
                    if (success==1){
                        successState=1;

                        Log.d("sale id:",saleData.get(i).sale_id);
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
