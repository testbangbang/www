package com.onyx.kreader.ui.data;

import android.content.Context;
import android.graphics.Rect;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.ReaderUserDataInfo;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.request.CloseRequest;
import com.onyx.kreader.host.request.PreRenderRequest;
import com.onyx.kreader.host.request.RenderRequest;
import com.onyx.kreader.host.request.SaveDocumentOptionsRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.data.ReaderNoteDataInfo;
import com.onyx.kreader.note.request.ReaderBaseNoteRequest;
import com.onyx.kreader.tts.ReaderTtsManager;
import com.onyx.kreader.ui.actions.ShowReaderMenuAction;
import com.onyx.kreader.ui.events.*;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.highlight.ReaderSelectionManager;
import com.onyx.kreader.utils.PagePositionUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by ming on 16/7/27.
 */
public class ReaderDataHolder {

    private Context context;
    private String documentPath;
    private Reader reader;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private ReaderNoteDataInfo noteDataInfo;

    private HandlerManager handlerManager;
    private ReaderSelectionManager selectionManager;
    private ReaderTtsManager ttsManager;
    private NoteManager noteManager;
    private EventBus eventBus = new EventBus();

    private boolean preRender = true;
    private boolean preRenderNext = true;
    private boolean documentOpened = false;

    private int displayWidth;
    private int displayHeight;
    private int optionsSkippedTimes = 0;

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
        readerViewInfo = request.getReaderViewInfo();
    }

    public void saveReaderUserDataInfo(final BaseReaderRequest request) {
        readerUserDataInfo = request.getReaderUserDataInfo();
    }

    public void saveShapeDataInfo(final ReaderBaseNoteRequest request) {
        noteDataInfo = request.getShapeDataInfo();
    }

    public boolean hasShapes() {
        return noteDataInfo != null;
    }

    public void resetShapeData() {
        noteDataInfo = null;
    }

    public final ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public final ReaderUserDataInfo getReaderUserDataInfo() {
        return readerUserDataInfo;
    }

    public final ReaderNoteDataInfo getNoteDataInfo() {
        return noteDataInfo;
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

    public void onDocumentInitRendered() {
        getEventBus().post(new DocumentInitRendered());
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

    public boolean canCurrentPageScaleDown() {
        final float toPageScale = PageUtils.scaleToPage(getFirstPageInfo().getOriginWidth(),
                getFirstPageInfo().getOriginHeight(),
                getReader().getViewOptions().getViewWidth(),
                getReader().getViewOptions().getViewHeight());
        return getReaderViewInfo().getFirstVisiblePage().getActualScale() > toPageScale;
    }

    public boolean canCurrentPageScaleUp() {
        return getReaderViewInfo().getFirstVisiblePage().getActualScale() < PageConstants.MAX_SCALE;
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
            ttsManager = new ReaderTtsManager(context);
        }
        return ttsManager;
    }

    private void updateReaderMenuState() {
        if (ShowReaderMenuAction.isReaderMenuShown()) {
            new ShowReaderMenuAction().execute(ReaderDataHolder.this);
        }
    }

    public NoteManager getNoteManager() {
        if (noteManager == null) {
            noteManager = new NoteManager(context);
        }
        return noteManager;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public String getBookName() {
        return FileUtils.getFileName(getDocumentPath());
    }

    public String getBookTitle() {
        if (getReaderUserDataInfo().getDocumentMetadata() == null) {
            return null;
        }
        return getReaderUserDataInfo().getDocumentMetadata().getTitle();
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
                onRenderRequestFinished(renderRequest, e);
                callback.invoke(callback, request, e);
                onPageDrawFinished(renderRequest, e);

                updateReaderMenuState();
            }
        });
    }

    private void beforeSubmitRequest() {
        resetShapeData();
    }

    private void onPageDrawFinished(BaseReaderRequest request, Throwable e) {
        if (e != null || request.isAbort()) {
            return;
        }
        saveDocumentOptions(request);
        preRenderNext();
    }

    private void saveDocumentOptions(final BaseReaderRequest request) {
        if (!request.isSaveOptions()) {
            return;
        }
        final int MAX_SKIP_TIMES = 5;
        if (optionsSkippedTimes < MAX_SKIP_TIMES) {
            optionsSkippedTimes++;
        } else {
            submitNonRenderRequest(new SaveDocumentOptionsRequest());
            optionsSkippedTimes = 0;
        }
    }

    public void preRenderNext() {
        if (!preRender) {
            return;
        }
        final PreRenderRequest request = new PreRenderRequest(preRenderNext);
        getReader().submitRequest(context, request, null);
    }

    public void onRenderRequestFinished(final BaseReaderRequest request, Throwable e) {
        onRenderRequestFinished(request, e, true);
    }

    public void changeEpdUpdateMode(final UpdateMode mode) {
        eventBus.post(new ChangeEpdUpdateMode(mode));
    }

    public void resetEpdUpdateMode() {
        eventBus.post(new ResetEpdUpdateMode());
    }

    public void onRenderRequestFinished(final BaseReaderRequest request, Throwable e, boolean applyGCIntervalUpdate) {
        if (e != null || request.isAbort()) {
            return;
        }
        saveReaderViewInfo(request);
        saveReaderUserDataInfo(request);
        eventBus.post(RequestFinishEvent.fromRequest(request, e, applyGCIntervalUpdate));
    }


    public void redrawPage() {
        if (getReader() != null) {
            submitRenderRequest(new RenderRequest());
        }
    }

    public void showReaderSettings() {
        eventBus.post(new ShowReaderSettingsEvent());
    }

    public void destroy() {
        closeNoteManager();
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
            ttsManager = null;
        }
    }

    private void closeNoteManager() {
        if (noteManager == null) {
            return;
        }
        getNoteManager().close();
    }
}
