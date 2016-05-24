package com.onyx.kreader.host.wrapper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import com.onyx.kreader.api.*;
import com.onyx.kreader.cache.BitmapLruCache;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.impl.ReaderViewOptionsImpl;
import com.onyx.kreader.host.layout.ReaderLayoutManager;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.reflow.ImageReflowManager;
import com.onyx.kreader.reflow.ImageReflowSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


/**
 * Created by zhuzeng on 10/2/15.
 * Job management.
 */
public class Reader {

    private static final String TAG = Reader.class.getSimpleName();
    private PowerManager.WakeLock wakeLock;
    private int wakeLockCounting = 0;
    private ExecutorService threadPool = null;
    private boolean debugWakelock = false;
    private List<BaseRequest> requestList;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ReaderHelper readerHelper = null;

    public Reader() {
        readerHelper = new ReaderHelper();
        initRequestList();
    }

    public boolean init() {
        return true;
    }

    private void initRequestList() {
        requestList = Collections.synchronizedList(new ArrayList<BaseRequest>());
    }

    private void removeRequest(final BaseRequest request) {
        synchronized (requestList) {
            requestList.remove(request);
        }
    }

    private void addRequest(final BaseRequest request) {
        synchronized (requestList) {
            requestList.add(request);
        }
    }

    private void abortAllRequests() {
        synchronized (requestList) {
            for(BaseRequest request : requestList) {
                request.setAbort();
            }
        }
    }

    public void acquireWakeLock(Context context) {
        try {
            if (wakeLock == null) {
                PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Reader: ");
            }
            wakeLock.acquire();
            ++wakeLockCounting;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public void releaseWakeLock() {
        try {
            if (wakeLock != null) {
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
                if (--wakeLockCounting <= 0) {
                    wakeLock = null;
                }
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    private void dumpWakelocks() {
        if (debugWakelock) {
            if (wakeLock != null || wakeLockCounting > 0) {
                Log.w(TAG, "wake lock not released. check wake lock." + wakeLock.toString() + " counting: " + wakeLockCounting);
            }
        }
    }

    private ExecutorService getThreadPool()   {
        if (threadPool == null) {
            threadPool = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setPriority(Thread.MAX_PRIORITY);
                    return t;
                }
            });
        }
        return threadPool;
    }

    private boolean beforeSubmitRequest(final Context context, final BaseRequest request, final BaseCallback callback) {
        if (request == null) {
            if (callback != null) {
                callback.done(null,  null);
            }
            return false;
        }
        request.setContext(context);
        request.setCallback(callback);
        if (request.isAbortPendingTasks()) {
            abortAllRequests();
        }
        addRequest(request);
        return true;
    }

    private final Runnable generateRunnable(final BaseRequest request) {
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
                    dumpWakelocks();
                    removeRequest(request);
                }
            }
        };
        return runnable;
    }

    public boolean submitRequest(final Context context, final BaseRequest request, final BaseCallback callback) {
        if (!beforeSubmitRequest(context, request, callback)) {
            return false;
        }

        final Runnable runnable = generateRunnable(request);
        if (request.isRunInBackground()) {
            getThreadPool().submit(runnable);
        } else {
            runnable.run();
        }
        return true;
    }

    public Handler getLooperHandler() {
        return handler;
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

    public BaseOptions loadDocumentOptions(final Context context, final String path) {
        return getReaderHelper().loadDocumentOptions(context, path);
    }
}
