package com.onyx.phone.reader.reader.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.common.ReaderUserDataInfo;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.request.PreRenderRequest;
import com.onyx.android.sdk.reader.host.request.RenderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.phone.reader.event.DocumentInitRenderedEvent;
import com.onyx.phone.reader.event.RenderRequestFinishedEvent;
import com.onyx.phone.reader.reader.handler.HandlerManager;
import com.onyx.phone.reader.reader.highlight.SelectionManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ming on 2017/4/13.
 */

public class ReaderDataHolder {

    private EventBus eventBus = new EventBus();

    private Context context;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;

    private HandlerManager handlerManager;
    private SelectionManager selectionManager;
    private DataManager dataManager;
    private Reader reader;
    private LruCache<Integer, Bitmap> readerPageCache = new LruCache<>(5);
    private int displayWidth;
    private int displayHeight;
    private boolean documentInitRendered = false;

    public ReaderDataHolder(Context context) {
        this.context = context;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void registerEventBus(final Object subscriber) {
        getEventBus().register(subscriber);
    }

    public void unRegisterEventBus(final Object subscriber) {
        getEventBus().unregister(subscriber);
    }

    public Context getContext() {
        return context;
    }

    public final HandlerManager getHandlerManager() {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(this);
        }
        return handlerManager;
    }

    public final SelectionManager getSelectionManager() {
        if (selectionManager == null) {
            selectionManager = new SelectionManager(this);
        }
        return selectionManager;
    }

    public Reader getReader() {
        if (reader == null) {
            reader = new Reader();
        }
        return reader;
    }

    public DataManager getDataManager() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public void submitNonRenderRequest(final BaseReaderRequest request) {
        submitNonRenderRequest(request, null);
    }

    public void submitNonRenderRequest(final BaseReaderRequest request, final BaseCallback callback) {
        getReader().submitRequest(context, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    public void submitRenderRequest(final BaseReaderRequest renderRequest) {
        submitRenderRequest(renderRequest, null);
    }

    public void submitRenderRequest(final BaseReaderRequest renderRequest, final BaseCallback callback) {
        getReader().submitRequest(context, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                onRenderRequestFinished(renderRequest, e);
                BaseCallback.invoke(callback, request, e);
                onPageDrawFinished(renderRequest, e);
            }
        });
    }

    public void onRenderRequestFinished(final BaseReaderRequest request, Throwable e) {
        if (e != null || request.isAbort()) {
            return;
        }
        saveReaderViewInfo(request);
        saveReaderUserDataInfo(request);
        bufferRenderPage(getCurrentPage(), getReader().getViewportBitmap().getBitmap());
        getEventBus().post(new RenderRequestFinishedEvent());
    }

    public void redrawPage() {
        if (getReader() != null) {
            submitRenderRequest(new RenderRequest());
        }
    }

    private void onPageDrawFinished(BaseReaderRequest request, Throwable e) {
        if (e != null || request.isAbort()) {
            return;
        }
        preRenderPage(true);
    }

    public void preRenderPage(final boolean forward) {
        final PreRenderRequest preRenderRequest = new PreRenderRequest(forward, false);
        getReader().submitRequest(context, preRenderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                String pageName = preRenderRequest.getReaderViewInfo().getFirstVisiblePage().getName();
                int pagePosition = PagePositionUtils.getPageNumber(pageName);
                bufferRenderPage(pagePosition, preRenderRequest.getPreRenderBitmap());
            }
        });
    }

    public void onDocumentInitRendered() {
        documentInitRendered = true;
        getEventBus().post(new DocumentInitRenderedEvent());
    }

    public boolean inReadingProvider() {
        return getHandlerManager().getActiveProviderName().equals(HandlerManager.READING_PROVIDER);
    }

    public Bitmap getReaderPageCache(final int position) {
        return readerPageCache.get(position);
    }

    public Bitmap bufferRenderPage(final int position, final Bitmap bitmap) {
        return readerPageCache.put(position, bitmap);
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public void setDisplaySize(int displayWidth, int displayHeight) {
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
    }

    public int getPageCount() {
        return reader.getNavigator().getTotalPage();
    }

    public String getCurrentPageName() {
        return getReaderViewInfo().getFirstVisiblePage().getName();
    }

    public int getCurrentPage() {
        return PagePositionUtils.getPageNumber(getCurrentPageName());
    }

    public String getCurrentPagePosition() {
        return getReaderViewInfo().getFirstVisiblePage().getPositionSafely();
    }

    public ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public void saveReaderViewInfo(BaseReaderRequest readerRequest) {
        this.readerViewInfo = readerRequest.getReaderViewInfo();
    }

    public ReaderUserDataInfo getReaderUserDataInfo() {
        return readerUserDataInfo;
    }

    public void saveReaderUserDataInfo(BaseReaderRequest readerRequest) {
        this.readerUserDataInfo = readerRequest.getReaderUserDataInfo();
    }
}
