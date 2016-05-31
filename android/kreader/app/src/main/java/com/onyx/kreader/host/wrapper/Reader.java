package com.onyx.kreader.host.wrapper;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.onyx.kreader.api.*;
import com.onyx.kreader.cache.BitmapLruCache;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.RequestManager;
import com.onyx.kreader.host.impl.ReaderViewOptionsImpl;
import com.onyx.kreader.host.layout.ReaderLayoutManager;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.reflow.ImageReflowManager;
import com.onyx.kreader.reflow.ImageReflowSettings;


/**
 * Created by zhuzeng on 10/2/15.
 * Job management.
 */
public class Reader {

    private static final String TAG = Reader.class.getSimpleName();
    private RequestManager requestManager;
    private ReaderHelper readerHelper = null;

    public Reader() {
        readerHelper = new ReaderHelper();
        requestManager = new RequestManager();
    }

    public boolean init() {
        return true;
    }

    public void acquireWakeLock(Context context) {
        requestManager.acquireWakeLock(context);
    }

    public void releaseWakeLock() {
        requestManager.releaseWakeLock();
    }

    private boolean beforeSubmitRequest(final Context context, final BaseReaderRequest request, final BaseCallback callback) {
        if (request == null) {
            if (callback != null) {
                callback.done(null,  null);
            }
            return false;
        }
        request.setContext(context);
        request.setCallback(callback);
        if (request.isAbortPendingTasks()) {
            requestManager.abortAllRequests();
        }
        requestManager.addRequest(request);
        return true;
    }

    private final Runnable generateRunnable(final BaseReaderRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(Reader.this);
                    request.execute(Reader.this);
                } catch (ReaderException exception) {
                    request.setException(exception);
                } catch (java.lang.Exception exception) {
                    Log.d(TAG, Log.getStackTraceString(exception));
                    request.setException(exception);
                } finally {
                    request.afterExecute(Reader.this);
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request);
                }
            }
        };
        return runnable;
    }

    public boolean submitRequest(final Context context, final BaseReaderRequest request, final BaseCallback callback) {
        if (!beforeSubmitRequest(context, request, callback)) {
            return false;
        }

        final Runnable runnable = generateRunnable(request);
        if (request.isRunInBackground()) {
            requestManager.getSingleThreadPool().submit(runnable);
        } else {
            runnable.run();
        }
        return true;
    }

    public Handler getLooperHandler() {
        return requestManager.getLooperHandler();
    }

    public ReaderHelper getReaderHelper() {
        return readerHelper;
    }

    public ReaderPlugin getPlugin() {
        return getReaderHelper().getPlugin();
    }

    public ReaderDocument getDocument() {
        return getReaderHelper().getDocument();
    }

    public ReaderView getView() {
        return getReaderHelper().getView();
    }

    public ReaderNavigator getNavigator() {
        return getReaderHelper().getNavigator();
    }

    public ReaderRenderer getRenderer() {
        return getReaderHelper().getRenderer();
    }

    public ReaderRendererFeatures getRendererFeatures() {
        return getReaderHelper().getRendererFeatures();
    }

    public ReaderSearchManager getSearchManager() {
        return getReaderHelper().getSearchManager();
    }

    public ReaderPluginOptions getPluginOptions() {
        return getReaderHelper().getPluginOptions();
    }

    public ReaderViewOptionsImpl getViewOptions() {
        return getReaderHelper().getViewOptions();
    }

    public BaseOptions getDocumentOptions() {
        return getReaderHelper().getDocumentOptions();
    }

    public ReaderLayoutManager getReaderLayoutManager() {
        return getReaderHelper().getReaderLayoutManager();
    }

    public BitmapLruCache getBitmapLruCache() {
        return getReaderHelper().getBitmapLruCache();
    }

    public ReaderBitmap getRenderBitmap() {
        return getReaderHelper().getRenderBitmap();
    }

    public boolean isRenderBitmapDirty() {
        return getReaderHelper().isRenderBitmapDirty();
    }

    public ReaderBitmap getViewportBitmap() {
        return getReaderHelper().getViewportBitmap();
    }

    public ReaderHelper.BitmapCopyCoordinator getBitmapCopyCoordinator() {
        return getReaderHelper().getBitmapCopyCoordinator();
    }

    public ImageReflowManager getImageReflowManager() {
        return getReaderHelper().getImageReflowManager();
    }

    public ImageReflowSettings getImageReflowSettings() {
        return getReaderHelper().getImageReflowManager().getSettings();
    }

}
