package com.onyx.android.sdk.data.v2;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by suicheng on 2017/6/1.
 */
public class TokenHeaderInterceptor implements Interceptor {

    private String tokenKey;
    private String token;

    public TokenHeaderInterceptor(final String tokenKey, final String token) {
        this.tokenKey = tokenKey;
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader(tokenKey, token)
                .build();
        return chain.proceed(request);
    }
}