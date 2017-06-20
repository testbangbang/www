package com.onyx.android.sdk.reader.host.wrapper;

import android.content.Context;
import android.os.Handler;

import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderDocumentMetadata;
import com.onyx.android.sdk.reader.api.ReaderFormManager;
import com.onyx.android.sdk.reader.api.ReaderNavigator;
import com.onyx.android.sdk.reader.api.ReaderPlugin;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.api.ReaderRenderer;
import com.onyx.android.sdk.reader.api.ReaderRendererFeatures;
import com.onyx.android.sdk.reader.api.ReaderSearchManager;
import com.onyx.android.sdk.reader.api.ReaderView;
import com.onyx.android.sdk.reader.cache.BitmapReferenceLruCache;
import com.onyx.android.sdk.reader.cache.ReaderBitmapReferenceImpl;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentMetadataImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderViewOptionsImpl;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.layout.ReaderLayoutManager;
import com.onyx.android.sdk.reader.reflow.ImageReflowManager;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;


/**
 * Created by zhuzeng on 10/2/15.
 * Job management.
 */
public class Reader {

    private RequestManager requestManager;
    private ReaderHelper readerHelper = null;

    public Reader() {
        readerHelper = new ReaderHelper();
        requestManager = new RequestManager();
    }

    public boolean init() {
        return true;
    }

    public void acquireWakeLock(final Context context, final String tag) {
        requestManager.acquireWakeLock(context, tag);
    }

    public void releaseWakeLock(final String tag) {
        requestManager.releaseWakeLock();
    }

    private final Runnable generateRunnable(final BaseReaderRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(Reader.this);
                    if (!request.isAbort()) {
                        request.execute(Reader.this);
                    }
                } catch (Throwable tr) {
                    request.setException(tr);
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
        final Runnable runnable = generateRunnable(request);
        return requestManager.submitRequest(context, request, runnable, callback);
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

    public ReaderFormManager getFormManager() {
        return getReaderHelper().getFormManager();
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

    public String getDocumentPath() {
        return getReaderHelper().getDocumentPath();
    }

    public String getBookName(){
        return getReaderHelper().getBookName();
    }

    public boolean setBookName(final String bookName){
        getReaderHelper().setBookName(bookName);
        return true;
    }

    public String getDocumentMd5() {
        return getReaderHelper().getDocumentMd5();
    }

    public ReaderDocumentMetadata getDocumentMetadata() {
        return getReaderHelper().getDocumentMetadata();
    }

    public ReaderDocumentMetadata getDocumentMetadataSafely() {
        ReaderDocumentMetadata metadata = getReaderHelper().getDocumentMetadata();
        if (metadata != null) {
            return metadata;
        }
        return new ReaderDocumentMetadataImpl();
    }


    public ReaderLayoutManager getReaderLayoutManager() {
        return getReaderHelper().getReaderLayoutManager();
    }

    public void transferRenderBitmapToViewport(ReaderBitmapReferenceImpl renderBitmap) {
        getReaderHelper().transferRenderBitmapToViewport(renderBitmap);
    }

    public void returnBitmapToCache(ReaderBitmapReferenceImpl bitmap) {
        getReaderHelper().returnBitmapToCache(bitmap);
    }

    public BitmapReferenceLruCache getBitmapCache() {
        return getReaderHelper().getBitmapCache();
    }

    public ReaderBitmap getViewportBitmap() {
        return getReaderHelper().getViewportBitmap();
    }

    public ImageReflowManager getImageReflowManager() {
        return getReaderHelper().getImageReflowManager();
    }

    public ImageReflowSettings getImageReflowSettings() {
        return getReaderHelper().getImageReflowManager().getSettings();
    }

    public void saveOptions() {
        getReaderHelper().saveOptions();
    }
}
