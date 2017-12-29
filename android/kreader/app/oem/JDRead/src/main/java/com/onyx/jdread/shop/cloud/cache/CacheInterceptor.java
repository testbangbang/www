package com.onyx.jdread.shop.cloud.cache;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.common.CommonUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created on 2017/04/21.
 *
 * @author hehai
 */

public class CacheInterceptor implements Interceptor {
    private static final long MAX_AGE = 60 * 60;
    private static final long MAX_STALE = 7 * 24 * 60 * 60;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean netAvailable = CommonUtils.isNetworkConnected(JDReadApplication.getInstance());

        if (netAvailable) {
            request = request.newBuilder()
                    //from net work
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build();
        } else {
            request = request.newBuilder()
                    //get cache
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response response = chain.proceed(request);
        if (netAvailable) {
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    // Cache timeout one hour
                    .header("Cache-Control", "public, max-age=" + MAX_AGE)
                    .build();
        } else {
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    // Cache timeout a week
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + MAX_STALE)
                    .build();
        }
        return response;
    }
}