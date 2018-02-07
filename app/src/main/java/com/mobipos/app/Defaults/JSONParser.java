package com.mobipos.app.Defaults;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {
        try {
            if (method == HttpPost.METHOD_NAME) {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity((List) params));
                is = httpClient.execute(httpPost).getEntity().getContent();
            } else if (method == HttpGet.METHOD_NAME) {
                Log.d("http request",url + "?" + URLEncodedUtils.format((List)params,"utf-8"));
                is = new DefaultHttpClient().execute(new HttpGet(url + "?" + URLEncodedUtils.format((List) params, "utf-8"))).getEntity().getContent();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("http response:",json);
        } catch (Exception e4) {
            Log.e("Buffer Error", "Error converting result " + e4.toString());
        }
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e5) {
            Log.e("JSON Parser", "Error parsing data " + e5.toString());
        }
        return jObj;
    }
}
