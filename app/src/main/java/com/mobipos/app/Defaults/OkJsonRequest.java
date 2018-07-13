package com.mobipos.app.Defaults;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.OkHttpClient;

/**
 * Created by folio on 7/12/2018.
 */

public class OkJsonRequest  {

    OkHttpClient client;
    JSONObject jsonObject;
    public JSONObject makeRequest(String url){
        Log.d("request made to server",url);
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
        String json_data=null;

        try {
            client=new OkHttpClient();
            okhttp3.Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " +
                            responseHeaders.value(i));
                }

                json_data=response.body().string();
                Log.d("response from server",json_data);
                System.out.println(json_data);


            }
            else{
                json_data="error in fetching data";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jsonObject=new JSONObject(json_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
