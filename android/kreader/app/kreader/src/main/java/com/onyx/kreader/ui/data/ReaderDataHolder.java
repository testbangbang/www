package com.onyx.kreader.ui.data;

import android.content.Context;
import android.graphics.Rect;
import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.common.ReaderUserDataInfo;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.request.CloseRequest;
import com.onyx.kreader.host.request.PreRenderRequest;
import com.onyx.kreader.host.request.RenderRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.kreader.tts.ReaderTtsManager;
import com.onyx.kreader.ui.actions.ShowReaderMenuAction;
import com.onyx.kreader.ui.events.DocumentOpenEvent;
import com.onyx.kreader.ui.events.RequestFinishEvent;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.highlight.ReaderSelectionManager;
import com.onyx.kreader.utils.PagePositionUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by ming on 16/7/27.
 */
public class ReaderDataHolder {
    private static final String TAG = ReaderDataHolder.class.getSimpleName();

    private Context context;
    private String documentPath;
    private Reader reader;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private ShapeDataInfo shapeDataInfo;
    private boolean preRender = true;
    private boolean preRenderNext = true;
    private boolean documentOpened = false;

    private int displayWidth;
    private int displayHeight;

    private HandlerManager handlerManager;
    private ReaderSelectionManager selectionManager;
    private ReaderTtsManager ttsManager;
    private NoteViewHelper noteViewHelper;
    private EventBus eventBus = new EventBus();

    public ReaderDataHolder(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void saveReaderViewInfo(final BaseReaderRequest request) {
        Debug.d(TAG, "saveReaderViewInfo: " + JSON.toJSONString(request.getReaderViewInfo().getFirstVisiblePage()));
        readerViewInfo = request.getReaderViewInfo();
    }

    public void saveReaderUserDataInfo(final BaseReaderRequest request) {
        readerUserDataInfo = request.getReaderUserDataInfo();
    }

    public void saveShapeDataInfo(final BaseNoteRequest request) {
        shapeDataInfo = request.getShapeDataInfo();
    }

    public boolean hasShapes() {
        if (shapeDataInfo == null) {
            return false;
        }
        return shapeDataInfo.hasShapes();
    }

    public void resetShapeData() {
        shapeDataInfo = null;
    }

    public final ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public final ReaderUserDataInfo getReaderUserDataInfo() {
        return readerUserDataInfo;
    }

    public final ShapeDataInfo getShapeDataInfo() {
        return shapeDataInfo;
    }

    public void setPreRenderNext(boolean preRenderNext) {
        this.preRenderNext = preRenderNext;
    }

    public Reader getReader() {
        return reader;
    }

    public void initReaderFromPath(final String path) {
        documentOpened = false;
        documentPath = path;
        reader = ReaderManager.getReader(documentPath);
    }

    public void onDocumentOpened() {
        documentOpened = true;
        getEventBus().post(new DocumentOpenEvent(documentPath));
    }

    public boolean isDocumentOpened() {
        return documentOpened;
    }

    public String getCurrentPageName() {
        return getReaderViewInfo().getFirstVisiblePage().getName();
    }

    public int getCurrentPage() {
        return PagePositionUtils.getPosition(getCurrentPageName());
    }

    public int getPageCount() {
        return reader.getNavigator().getTotalPage();
    }

    public final PageInfo getFirstPageInfo() {
        return getReaderViewInfo().getFirstVisiblePage();
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public Rect getDisplayRect() {
        return new Rect(0, 0, getDisplayWidth(), getDisplayHeight());
    }

    public void setDisplaySize(int width, int height) {
        displayWidth = width;
        displayHeight = height;
    }

    public final HandlerManager getHandlerManager() {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(this);
        }
        return handlerManager;
    }

    public ReaderSelectionManager getSelectionManager() {
        if (selectionManager == null) {
            selectionManager = new ReaderSelectionManager();
        }
        return selectionManager;
    }

    public ReaderTtsManager getTtsManager() {
        if (ttsManager == null) {
            ttsManager = new ReaderTtsManager(this, new ReaderTtsManager.Callback() {
                @Override
                public void onStateChanged() {
                    if (ShowReaderMenuAction.isReaderMenuShown()) {
                        new ShowReaderMenuAction().execute(ReaderDataHolder.this);
                    }
                }
            });
        }
        return ttsManager;
    }

    public NoteViewHelper getNoteViewHelper() {
        if (noteViewHelper == null) {
            noteViewHelper = new NoteViewHelper();
        }
        return noteViewHelper;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public String getBookName() {
        Debug.d("getBookName: " + getDocumentPath());
        return FileUtils.getFileName(getDocumentPath());
    }

    public boolean hasBookmark() {
        return getReaderUserDataInfo().hasBookmark(getFirstPageInfo());
    }

    public void submitNonRenderRequest(final BaseReaderRequest request) {
        submitNonRenderRequest(request, null);
    }

    public void submitNonRenderRequest(final BaseReaderRequest request, final BaseCallback callback) {
        beforeSubmitRequest();
        reader.submitRequest(context, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || request.isAbort()) {
                    return;
                }
                callback.invoke(callback, request, e);
            }
        });
    }

    public void submitRenderRequest(final BaseReaderRequest renderRequest) {
        submitRenderRequest(renderRequest, null);
    }

    public void submitRenderRequest(final BaseReaderRequest renderRequest, final BaseCallback callback) {
        beforeSubmitRequest();
        reader.submitRequest(context, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || request.isAbort()) {
                    return;
                }
                onRenderRequestFinished(renderRequest, e);
                callback.invoke(callback, request, e);
                preRenderNext();
            }
        });
    }

    private void beforeSubmitRequest() {
        resetShapeData();
    }

    public void preRenderNext() {
        if (!preRender) {
            return;
        }
        final PreRenderRequest request = new PreRenderRequest(preRenderNext);
        getReader().submitRequest(context, request, null);
    }

    public void onRenderRequestFinished(final BaseReaderRequest request, Throwable e) {
        Debug.d(TAG, "onRenderRequestFinished: " + request + ", " + e);
        saveReaderViewInfo(request);
        saveReaderUserDataInfo(request);
        eventBus.post(RequestFinishEvent.fromRequest(request, e));
    }

    public void redrawPage() {
        if (getReader() != null) {
            submitRenderRequest(new RenderRequest());
        }
    }

    public void destroy() {
        closeDocument();
        closeTts();
    }

    private void closeDocument() {
        documentOpened = false;
        if (reader != null && reader.getDocument() != null) {
            CloseRequest closeRequest = new CloseRequest();
            submitNonRenderRequest(closeRequest);
        }
        ReaderManager.releaseReader(documentPath);
    }

    private void closeTts() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
    }
}
