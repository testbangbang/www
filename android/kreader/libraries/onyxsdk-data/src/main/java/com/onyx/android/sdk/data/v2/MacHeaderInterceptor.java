package com.onyx.android.sdk.data.v2;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by suicheng on 2017/12/16.
 */
public class MacHeaderInterceptor implements Interceptor {

    private String key;
    private String mac;

    public MacHeaderInterceptor(final String key, final String mac) {
        this.key = key;
        this.mac = mac;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader(key, mac)
                .build();
        return chain.proceed(request);
    }
}
