package com.onyx.android.sdk.dataprovider;

import android.content.Context;
import android.util.Log;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.dataprovider.request.BaseDataProviderRequest;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.List;

/**
 * Created by zhuzeng on 5/31/16.
 * fetch from local memory cache or data provider(local data provider or remote data provider)
 */
public class AsyncDataProvider {

    private static final String TAG = AsyncDataProvider.class.getSimpleName();
    private RequestManager requestManager;

    public AsyncDataProvider() {
        requestManager = new RequestManager();
    }

    public static void init(final Context context, final List<Class<? extends DatabaseHolder>> list) {
        FlowConfig.Builder builder = new FlowConfig.Builder(context);
        if (list != null) {
            for (Class tClass : list) {
                builder.addDatabaseHolder(tClass);
            }
        }
        FlowManager.init(builder.build());
    }

    public static void cleanUp() {
    }

    private final Runnable generateRunnable(final BaseDataProviderRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.execute();
                } catch (java.lang.Exception exception) {
                    Log.d(TAG, Log.getStackTraceString(exception));
                    request.setException(exception);
                } finally {
                    request.afterExecute(requestManager);
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request);
                }
            }
        };
        return runnable;
    }

    public void submit(final Context context, final BaseDataProviderRequest request, final BaseCallback callback) {
        requestManager.submitRequest(context, request, generateRunnable(request), callback);
    }



}
