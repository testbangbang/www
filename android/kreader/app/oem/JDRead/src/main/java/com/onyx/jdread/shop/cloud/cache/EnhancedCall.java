package com.onyx.jdread.shop.cloud.cache;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.common.Constants;
import com.onyx.jdread.common.CommonUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 2017/04/21.
 *
 * @author hehai
 */

public class EnhancedCall<T> {
    private Call<T> mCall;
    private Class dataClassName;
    // Whether to use cache
    private boolean mUseCache = true;

    public EnhancedCall(Call<T> call) {
        this.mCall = call;
    }

    public EnhancedCall<T> dataClassName(Class className) {
        dataClassName = className;
        return this;
    }

    public EnhancedCall<T> useCache(boolean useCache) {
        mUseCache = useCache;
        return this;
    }

    public void enqueue(final EnhancedCallback<T> enhancedCallback) {
        mCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                enhancedCallback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if (!mUseCache || CommonUtils.isNetworkConnected(JDReadApplication.getInstance())) {
                    enhancedCallback.onFailure(call, t);
                    return;
                }

                Request request = call.request();
                String url = request.url().toString();
                RequestBody requestBody = request.body();
                Charset charset = Charset.forName(Constants.UTF_8);
                StringBuilder sb = new StringBuilder();
                sb.append(url);
                if (request.method().equals(Constants.POST)) {
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(Charset.forName(Constants.UTF_8));
                    }
                    Buffer buffer = new Buffer();
                    try {
                        requestBody.writeTo(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sb.append(buffer.readString(charset));
                    buffer.close();
                }

                String cache = CacheManager.getInstance().getCache(sb.toString());
                Log.d(CacheManager.TAG, "get cache->" + cache);

                if (!TextUtils.isEmpty(cache) && dataClassName != null) {
                    Object obj = new Gson().fromJson(cache, dataClassName);
                    if (obj != null) {
                        enhancedCallback.onGetCache((T) obj);
                        return;
                    }
                }
                enhancedCallback.onFailure(call, t);
                Log.d(CacheManager.TAG, "onFailure->" + t.getMessage());
            }
        });
    }

    private T execute() {
        T t = null;
        try {
            Response<T> response = mCall.execute();
            t = response.body();
        } catch (IOException e) {
            if (mUseCache && !CommonUtils.isNetworkConnected(JDReadApplication.getInstance())) {
                Request request = mCall.request();
                String url = request.url().toString();
                RequestBody requestBody = request.body();
                Charset charset = Charset.forName(Constants.UTF_8);
                StringBuilder sb = new StringBuilder();
                sb.append(url);
                if (request.method().equals(Constants.POST)) {
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(Charset.forName(Constants.UTF_8));
                    }
                    Buffer buffer = new Buffer();
                    try {
                        requestBody.writeTo(buffer);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    sb.append(buffer.readString(charset));
                    buffer.close();
                }
                String cache = CacheManager.getInstance().getCache(sb.toString());
                Log.d(CacheManager.TAG, "get cache->" + cache);

                if (!TextUtils.isEmpty(cache) && dataClassName != null) {
                    Object obj = new Gson().fromJson(cache, dataClassName);
                    if (obj != null) {
                        t = (T) obj;
                    }
                }
                e.printStackTrace();
            }
        }
        return t;
    }

    public T execute(Call<T> call, Class clazz) {
        if (Constants.isUseCache) {
            EnhancedCall<T> enhancedCall = new EnhancedCall<>(call);
            return enhancedCall.useCache(true)
                    .dataClassName(clazz)
                    .execute();
        } else {
            Response<T> execute = null;
            try {
                execute = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return execute.body();
        }
    }

    public static OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new EnhancedCacheInterceptor())
                .build();
        return client;
    }
}
