package com.onyx.android.sdk.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.cache.DataCacheManager;
import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.data.utils.MetadataQueryArgsBuilder;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 8/31/16.
 */
public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();
    private RequestManager requestManager;
    private DataManagerHelper dataManagerHelper;

    public DataManager() {
        requestManager = new RequestManager();
        dataManagerHelper = new DataManagerHelper();
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
                    requestManager.removeRequest(request);
                }
            }
        };
        return runnable;
    }

    public void submit(final Context context, final BaseDataRequest request, final BaseCallback callback) {
        requestManager.submitRequest(context, request, generateRunnable(request), callback);
    }

    public final RequestManager getRequestManager() {
        return requestManager;
    }

    public final DataManagerHelper getDataManagerHelper() {
        return dataManagerHelper;
    }
}
