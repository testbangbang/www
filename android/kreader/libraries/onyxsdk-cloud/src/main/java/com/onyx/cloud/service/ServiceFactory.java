package com.onyx.cloud.service;

import retrofit2.Retrofit;

/**
 * Created by zhuzeng on 8/10/16.
 */
public class ServiceFactory {

    public static final OnyxAccountService getAccountService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://store.onyx-international.cn:9000/api/1/")
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
        return retrofit.create(OnyxAccountService.class);
    }

}
