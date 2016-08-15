package com.onyx.cloud.service;

import retrofit2.Retrofit;


/**
 * Created by zhuzeng on 8/10/16.
 */
public class ServiceFactory {

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



}
