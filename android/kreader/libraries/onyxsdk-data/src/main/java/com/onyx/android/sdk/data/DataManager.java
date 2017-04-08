package com.onyx.android.sdk.data;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 8/31/16.
 */
public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();
    private RequestManager requestManager;
    private DataProviderManager dataProviderManager = new DataProviderManager();
    private FileSystemManager fileSystemManager;

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

    private final String getIdentifier(final BaseDataRequest request) {
        return request.getIdentifier();
    }

    public final RequestManager getRequestManager() {
        return requestManager;
    }

    public final DataProviderManager getDataProviderManager() {
        return dataProviderManager;
    }

    public DataProviderBase getDataProviderBase() {
        return getDataProviderManager().getDataProvider();
    }

    public final FileSystemManager getFileSystemManager() {
        return fileSystemManager;
    }

    public List<Metadata> getMetadataListWithLimit(Context context, QueryArgs queryArgs) {
        return getDataProviderManager().getDataProvider().findMetadataByQueryArgs(context, queryArgs);
    }

    public long countMetadataList(Context context, QueryArgs queryArgs) {
        return getDataProviderManager().getDataProvider().count(context, queryArgs);
    }

    public void saveLibrary(Library library) {
        getDataProviderBase().addLibrary(library);
    }
}
