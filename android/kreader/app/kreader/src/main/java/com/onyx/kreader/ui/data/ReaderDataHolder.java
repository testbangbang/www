package com.onyx.kreader.ui.data;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Rect;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
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
import com.onyx.kreader.note.receiver.DeviceReceiver;
import com.onyx.kreader.tts.ReaderTtsManager;
import com.onyx.kreader.ui.actions.ShowReaderMenuAction;
import com.onyx.kreader.ui.events.*;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.highlight.ReaderSelectionManager;
import com.onyx.kreader.utils.PagePositionUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ming on 16/7/27.
 */
public class ReaderDataHolder {

    private Context context;
    private String documentPath;
    private Reader reader;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;

    private HandlerManager handlerManager;
    private ReaderSelectionManager selectionManager;
    private ReaderTtsManager ttsManager;
    private NoteManager noteManager;
    private DeviceReceiver deviceReceiver = new DeviceReceiver();
    private EventBus eventBus = new EventBus();

    private boolean preRender = true;
    private boolean preRenderNext = true;
    private boolean documentOpened = false;

    private int displayWidth;
    private int displayHeight;
    private int optionsSkippedTimes = 0;

    /**
     * can be either Dialog or DialogFragment, so we store it as basic Object
     */
    private Set<Object> activeDialogs = new HashSet<>();

    public ReaderDataHolder(Context context) {
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

    public final ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public final List<PageInfo> getVisiblePages() {
        return getReaderViewInfo().getVisiblePages();
    }

    public final ReaderUserDataInfo getReaderUserDataInfo() {
        return readerUserDataInfo;
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
        registerDeviceReceiver();
    }

    public void onDocumentInitRendered() {
        getEventBus().post(new DocumentInitRenderedEvent());
        prepareNoteManager();
    }

    public boolean isDocumentOpened() {
        return documentOpened && reader != null;
    }

    public boolean inNoteWritingProvider() {
        return getHandlerManager().getActiveProviderName().equals(HandlerManager.SCRIBBLE_PROVIDER);
    }

    public boolean isNoteDirty() {
        return noteManager != null && getNoteManager().isNoteDirty();
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

    private void registerDeviceReceiver() {
        deviceReceiver.setSystemUIChangeListener(new DeviceReceiver.SystemUIChangeListener() {
            @Override
            public void onSystemUIChanged(String type, boolean open) {
                getEventBus().post(new SystemUIChangedEvent(open));
            }

            @Override
            public void onHomeClicked() {
                getEventBus().post(new HomeClickEvent());
            }
        });
        deviceReceiver.registerReceiver(getContext());
    }

    private void unregisterReceiver() {
        deviceReceiver.unregisterReceiver(getContext());
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
            ttsManager = new ReaderTtsManager(this);
        }
        return ttsManager;
    }

    public void notifyTtsStateChanged() {
        getEventBus().post(new TtsStateChangedEvent());
    }

    public void notifyTtsRequestSentence() {
        getEventBus().post(new TtsRequestSentenceEvent());
    }

    public void notifyTtsError() {
        getEventBus().post(new TtsErrorEvent());
    }

    private void updateReaderMenuState() {
        if (ShowReaderMenuAction.isReaderMenuShown()) {
            new ShowReaderMenuAction().execute(ReaderDataHolder.this, null);
        }
    }

    public NoteManager getNoteManager() {
        if (noteManager == null) {
            noteManager = new NoteManager(this);
        }
        return noteManager;
    }

    public void prepareNoteManager() {
        getNoteManager().startRawEventProcessor();
        getNoteManager().pauseRawEventProcessor();
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

    public boolean supportNoteExport() {
        if (StringUtils.isNullOrEmpty(documentPath) ||
                !documentPath.toLowerCase().endsWith(".pdf")) {
            return false;
        }
        return true;
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
        getNoteManager().resetNoteDataInfo();
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
        onRenderRequestFinished(request, e, true, true);
    }

    public void changeEpdUpdateMode(final UpdateMode mode) {
        getEventBus().post(new ChangeEpdUpdateModeEvent(mode));
    }

    public void resetEpdUpdateMode() {
        getEventBus().post(new ResetEpdUpdateModeEvent());
    }

    public void onRenderRequestFinished(final BaseReaderRequest request,
                                        Throwable e,
                                        boolean applyGCIntervalUpdate,
                                        boolean renderShapeData) {
        if (e != null || request.isAbort()) {
            return;
        }
        saveReaderViewInfo(request);
        saveReaderUserDataInfo(request);
        getEventBus().post(RequestFinishEvent.createEvent(applyGCIntervalUpdate, renderShapeData));
    }


    public void redrawPage() {
        if (getReader() != null) {
            submitRenderRequest(new RenderRequest());
        }
    }

    public void showReaderSettings() {
        getEventBus().post(new ShowReaderSettingsEvent());
    }

    public void addActiveDialog(Dialog dialog) {
        activeDialogs.add(dialog);
    }

    public void addActiveDialog(DialogFragment dialog) {
        activeDialogs.add(dialog);
    }

    public void removeActiveDialog(Dialog dialog) {
        activeDialogs.remove(dialog);
    }

    public void removeActiveDialog(DialogFragment dialog) {
        activeDialogs.remove(dialog);
    }

    public void closeActiveDialogs() {
        for (Object dialog : activeDialogs) {
            if (dialog instanceof Dialog) {
                ((Dialog) dialog).dismiss();
            }
            if (dialog instanceof DialogFragment) {
                ((DialogFragment) dialog).dismiss();
            }
        }
        activeDialogs.clear();
    }

    public void destroy(final BaseCallback callback) {
        unregisterReceiver();
        closeActiveDialogs();
        closeTts();
        closeNoteManager();
        closeDocument(callback);
    }

    private void closeDocument(final BaseCallback callback) {
        documentOpened = false;
        if (reader == null || reader.getDocument() == null) {
            BaseCallback.invoke(callback, null, null);
            return;
        }

        final CloseRequest closeRequest = new CloseRequest();
        submitNonRenderRequest(closeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ReaderManager.releaseReader(documentPath);
                BaseCallback.invoke(callback, request, e);
            }
        });
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
        getNoteManager().enableRawEventProcessor(false);
        getNoteManager().stopRawEventProcessor();
    }
}

