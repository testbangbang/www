package com.onyx.kreader.dataprovider;

import android.content.Context;
import android.util.Log;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.RequestManager;
import com.onyx.kreader.dataprovider.request.BaseDataProviderRequest;
import com.onyx.kreader.scribble.data.ShapeModel;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class DataProvider {

    private static final String TAG = DataProvider.class.getSimpleName();
    private RequestManager requestManager;

    public DataProvider() {
        requestManager = new RequestManager();
    }

    public static void init(final Context context) {
        FlowConfig.Builder builder = new FlowConfig.Builder(context);
        FlowManager.init(builder.build());
    }

    public static void cleanUp() {
        Delete.table(ShapeModel.class);
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
