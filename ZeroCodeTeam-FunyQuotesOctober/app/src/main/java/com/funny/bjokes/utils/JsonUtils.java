package com.funny.bjokes.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CJH on 2016.01.14.
 */
public class JsonUtils {

    public static String readJSON(String url){
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();

            return response.body().string().toString();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
