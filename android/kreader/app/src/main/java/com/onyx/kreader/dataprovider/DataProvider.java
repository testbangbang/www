package com.onyx.kreader.dataprovider;

import android.content.Context;
import android.util.Log;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.RequestManager;
import com.onyx.kreader.dataprovider.request.BaseDataProviderRequest;
import com.onyx.kreader.dataprovider.request.SaveBookmarkRequest;

import java.util.List;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class DataProvider {

    private static final String TAG = BookmarkProvider.class.getSimpleName();
    private RequestManager requestManager;

    public DataProvider() {
        requestManager = new RequestManager();
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
