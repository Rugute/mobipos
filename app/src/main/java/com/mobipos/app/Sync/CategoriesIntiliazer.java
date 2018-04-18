package com.mobipos.app.Sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Defaults.AppConfig;
import com.mobipos.app.Defaults.JSONParser;
import com.mobipos.app.database.Categories;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by folio on 1/18/2018.
 */

public class CategoriesIntiliazer {
    Users users;
    Categories categories;
    Context context;
    int user_type;
    public CategoriesIntiliazer(Context context,int user_type){
        this.context=context;
        users=new Users(context, defaults.database_name,null,1);
        categories=new Categories(context, defaults.database_name,null,1);
        this.user_type=user_type;
        new loadCategories().execute();
    }

    public class loadCategories extends AsyncTask<String,String,String> {

        int success=0;
        String serverMessage;
        JSONArray categoryArray;


        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            JSONObject jsonObject=null;
            List paramters=new ArrayList();

            if(user_type==0){
                //admin loading data
                paramters.add(new BasicNameValuePair("branch_id",AppConfig.selected_branch_id));

                jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+ AppConfig.get_categories,
                        "GET",paramters);
            }else if(user_type==1){
                //cashier loading data
                paramters.add(new BasicNameValuePair("user_id",users.get_user_id("cashier")));

                jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+ PackageConfig.get_categories,
                        "GET",paramters);
            }


            Log.d("json object",jsonObject.toString());

            try {
                success=jsonObject.getInt("success");
                serverMessage=jsonObject.getString("message");

                categoryArray=jsonObject.getJSONArray("data");

                PackageConfig.categoryArrayId=new String[categoryArray.length()];
                PackageConfig.categoryArrayName=new String[categoryArray.length()];

                for(int i=0;i<categoryArray.length();i++){
                    JSONObject jObj=categoryArray.getJSONObject(i);

                    PackageConfig.categoryArrayName[i]=jObj.getString("cat_name");
                    PackageConfig.categoryArrayId[i]=jObj.getString("cat_id");

                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);
            if (success==1){


                    if(categories.getCategoryCount()==0){
                        for(int i=0;i<PackageConfig.categoryArrayId.length;i++){
                            try{
                                if(!categories.insertCategory(PackageConfig.categoryArrayId[i],PackageConfig.categoryArrayName[i])) {
                                    Log.d("error inserting data","data not inserted");

                                }
                            }catch (ArrayIndexOutOfBoundsException e){
                                e.printStackTrace();
                            }

                        }
                    }else{
                        //check if category exists
                        for(int i=0;i<PackageConfig.categoryArrayId.length;i++){
                            if(!categories.CategoryExists(PackageConfig.categoryArrayId[i])){
                                if(!categories.insertCategory(PackageConfig.categoryArrayId[i],PackageConfig.categoryArrayName[i])){
                                    Log.d("error inserting data","data not inserted");
                                }
                            }
                        }
                    }

               // categoriesLoaded=true;
            }else{
                Toast.makeText(context,serverMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
