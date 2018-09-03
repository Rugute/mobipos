package com.mobipos.app.Defaults;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by folio on 9/1/2018.
 */

public class VolleyRequest  {
    JSONObject jsonObject;
    String request,thisData;
    RequestQueue queue;

    VolleyData tryData = new VolleyData();

    Context context;
    public VolleyRequest(String request,Context context) {
        this.request=request;
        this.context=context;
    }

    public void onResponseData(String data){

        AppConfig.store_request=data;
    }

    public JSONObject requestData(){



        StringRequest data = new StringRequest(Request.Method.GET, request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("store a request",response);
                onResponseData(response);

                tryData.setData(response);
//                try {
//                    jsonObject = new JSONObject(response);
//                    Log.d("json_object",response);
//                    AppConfig.store_request = response;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    jsonObject = new JSONObject("{\"success\":0,\"message\":\"error in network connection\"}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        queue = Volley.newRequestQueue(context);
        queue.add(data);

        Log.d("responded data",tryData.getData());

        try {
            jsonObject = new JSONObject("{\"success\":0,\"message\":\"error in network connection\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
