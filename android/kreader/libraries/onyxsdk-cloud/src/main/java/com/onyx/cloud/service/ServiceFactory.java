package com.onyx.cloud.service;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;


/**
 * Created by zhuzeng on 8/10/16.
 */
public class ServiceFactory {
    private static Map<String, Retrofit> retrofitMap = new HashMap<>();

    private static Retrofit getRetrofit(String baseUrl) {
        if (!retrofitMap.containsKey(baseUrl)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .build();
            retrofitMap.put(baseUrl, retrofit);
        }
        return retrofitMap.get(baseUrl);
    }

    public static final String API_V1_BASE = "http://store.onyx-international.cn:9000/api/1/";

    public static final OnyxAccountService getAccountService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_V1_BASE)
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
        return retrofit.create(OnyxAccountService.class);
    }

    public static final OnyxBookStoreService getBookStoreService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_V1_BASE)
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
        return retrofit.create(OnyxBookStoreService.class);
    }

    public static final <T> T getSpecService(final Class<T> service, final String baseUrl) {
        return getRetrofit(baseUrl).create(service);
    }
}
