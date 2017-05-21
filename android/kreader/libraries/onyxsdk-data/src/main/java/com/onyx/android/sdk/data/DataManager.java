package com.onyx.android.sdk.data;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.manager.CacheManager;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.List;

/**
 * Created by zhuzeng on 8/31/16.
 */
public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();
    private RequestManager requestManager;
    private DataProviderManager dataProviderManager = new DataProviderManager();
    private FileSystemManager fileSystemManager;
    private CacheManager cacheManager;

    public DataManager() {
        requestManager = new RequestManager();
        fileSystemManager = new FileSystemManager();
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

    private final Runnable generateRunnable(final BaseDataRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.execute(DataManager.this);
                } catch (java.lang.Exception exception) {
                    Log.e(TAG, Log.getStackTraceString(exception));
                    request.setException(exception);
                } finally {
                    request.afterExecute(DataManager.this);
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request, getIdentifier(request));
                }
            }
        };
        return runnable;
    }

    public void submit(final Context context, final BaseDataRequest request, final BaseCallback callback) {
        requestManager.submitRequest(context, getIdentifier(request), request, generateRunnable(request), callback);
    }

    public void submitToMulti(final Context context, final BaseDataRequest request, final BaseCallback callback) {
        requestManager.submitRequestToMultiThreadPool(context, getIdentifier(request), request, generateRunnable(request), callback);
    }

    private final String getIdentifier(final BaseRequest request) {
        return request.getIdentifier();
    }

    public final RequestManager getRequestManager() {
        return requestManager;
    }

    public final DataProviderManager getDataProviderManager() {
        return dataProviderManager;
    }

    public DataProviderBase getRemoteContentProvider() {
        return getDataProviderManager().getRemoteDataProvider();
    }

    public final FileSystemManager getFileSystemManager() {
        return fileSystemManager;
    }

    public CacheManager getCacheManager() {
        if (cacheManager == null) {
            cacheManager = new CacheManager();
        }
        return cacheManager;
    }
}
