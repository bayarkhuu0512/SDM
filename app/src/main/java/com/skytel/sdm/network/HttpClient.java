package com.skytel.sdm.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Zolbayar on 9/13/2016.
 * Request yavuulj baigaa buh gazar shineer client uusgej baisniig end singleton -r negtgen oorchilson
 */

public class HttpClient {

    private static OkHttpClient client;

    public static OkHttpClient getInstance() {
        if(client == null){
            client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }
}
