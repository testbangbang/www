package com.onyx.android.dr.reader.data;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;

import com.onyx.android.dr.reader.device.DeviceConfig;
import com.onyx.android.dr.reader.event.ActivityPauseEvent;
import com.onyx.android.dr.reader.event.ActivityResumeEvent;
import com.onyx.android.dr.reader.event.AnnotationEvent;
import com.onyx.android.dr.reader.event.ChangeEpdUpdateModeEvent;
import com.onyx.android.dr.reader.event.DictionaryLookupEvent;
import com.onyx.android.dr.reader.event.DocumentCloseEvent;
import com.onyx.android.dr.reader.event.DocumentInitRenderedEvent;
import com.onyx.android.dr.reader.event.DocumentOpenEvent;
import com.onyx.android.dr.reader.event.EventReceiver;
import com.onyx.android.dr.reader.event.LayoutChangeEvent;
import com.onyx.android.dr.reader.event.NetworkChangedEvent;
import com.onyx.android.dr.reader.event.PinchZoomEvent;
import com.onyx.android.dr.reader.event.RequestFinishEvent;
import com.onyx.android.dr.reader.event.ResetEpdUpdateModeEvent;
import com.onyx.android.dr.reader.event.ShowReaderSettingsEvent;
import com.onyx.android.dr.reader.event.SlideshowStartEvent;
import com.onyx.android.dr.reader.event.TextSelectionEvent;
import com.onyx.android.dr.reader.event.TtsErrorEvent;
import com.onyx.android.dr.reader.event.TtsRequestSentenceEvent;
import com.onyx.android.dr.reader.event.TtsStateChangedEvent;
import com.onyx.android.dr.reader.event.UpdateSlideshowEvent;
import com.onyx.android.dr.reader.highlight.ReaderSelectionManager;
import com.onyx.android.dr.reader.note.NoteManager;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.dr.reader.receiver.ReaderBroadcastReceiver;
import com.onyx.android.dr.reader.tts.ReaderTtsManager;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.DocumentInfo;
import com.onyx.android.sdk.reader.api.ReaderDocumentMetadata;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.common.ReaderUserDataInfo;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.request.CloseRequest;
import com.onyx.android.sdk.reader.host.request.PreRenderRequest;
import com.onyx.android.sdk.reader.host.request.RenderRequest;
import com.onyx.android.sdk.reader.host.request.SaveDocumentOptionsRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.host.wrapper.ReaderManager;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ming on 16/7/27.
 */
public class ReaderDataHolder {
    private ReaderPresenter readerPresenter;
    public enum DocumentOpenState { INIT, OPENING, OPENED }
    private Context context;
    private String documentPath;
    private Reader reader;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private ReaderSelectionManager selectionManager;
    private ReaderTtsManager ttsManager;
    private NoteManager noteManager;
    private DataManager dataManager;
    private DeviceReceiver deviceReceiver = new DeviceReceiver();
    private EventBus eventBus = new EventBus();
    private EventReceiver eventReceiver;
    private boolean preRender = true;
    private boolean preRenderNext = true;
    private DocumentOpenState documentOpenState = DocumentOpenState.INIT;
    private boolean documentInitRendered = false;
    private int displayWidth;
    private int displayHeight;
    private int optionsSkippedTimes = 0;
    private int lastRequestSequence;
    /**
     * can be either Dialog or DialogFragment, so we store it as basic Object
     */
    private Set<Object> activeDialogs = new HashSet<>();

    public ReaderDataHolder(Context context) {
        this.context = context;
        ReaderBroadcastReceiver.setEventBus(eventBus);
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

    public boolean supportTextPage() {
        return getReaderViewInfo() != null && getReaderViewInfo().supportTextPage;
    }

    public boolean isFixedPageDocument() {
        return getReaderViewInfo() != null && getReaderViewInfo().isFixedDocument;
    }

    public boolean isFlowDocument() {
        return !isFixedPageDocument();
    }

    public boolean supportNoteFunc() {
        return !DeviceConfig.sharedInstance(getContext()).isDisableNoteFunc() && isFixedPageDocument();
    }

    public boolean supportScalable() {
        return getReaderViewInfo() != null && getReaderViewInfo().supportScalable;
    }

    public boolean supportFontSizeAdjustment() {
        return getReaderViewInfo() != null && getReaderViewInfo().supportFontSizeAdjustment;
    }

    public boolean supportTypefaceAdjustment() {
        return getReaderViewInfo() != null && getReaderViewInfo().supportTypefaceAdjustment;
    }

    public boolean canPan() {
        return getReaderViewInfo() != null && getReaderViewInfo().canPan();
    }

    public boolean supportSearchByPage() {
        return isFixedPageDocument() && supportScalable();
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
        documentOpenState = DocumentOpenState.OPENING;
        documentPath = path;
        reader = ReaderManager.getReader(documentPath);
    }

    public boolean isDocumentOpening() {
        return documentOpenState == DocumentOpenState.OPENING;
    }

    public boolean isDocumentOpened() {
        return documentOpenState == DocumentOpenState.OPENED && reader != null;
    }

    public boolean isNoteDirty() {
        return noteManager != null && getNoteManager().isNoteDirty();
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

    public ReaderSelectionManager getSelectionManager() {
        if (selectionManager == null) {
            selectionManager = new ReaderSelectionManager();
        }
        return selectionManager;
    }

    public void notifyTtsStateChanged() {
        getEventBus().post(new TtsStateChangedEvent());
    }

    public void notifyUpdateSlideshowStatusBar() {
        getEventBus().post(new UpdateSlideshowEvent());
    }

    public void notifyTtsRequestSentence() {
        getEventBus().post(new TtsRequestSentenceEvent());
    }

    public void notifyTtsError() {
        getEventBus().post(new TtsErrorEvent());
    }

    public NoteManager getNoteManager() {
        if (noteManager == null) {
            noteManager = new NoteManager(this);
        }
        return noteManager;
    }

    public DataManager getDataManager() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public void updateRawEventProcessor() {
        if (!supportScalable()) {
            getNoteManager().stopRawEventProcessor();
            return;
        }
        getNoteManager().startRawEventProcessor();
        getNoteManager().pauseRawEventProcessor();
    }

    public void stopRawEventProcessor() {
        if (!supportScalable()) {
            return;
        }
        getNoteManager().stopRawEventProcessor();
    }

    public void enablePenShortcut()  {
        if (!supportScalable()) {
            return;
        }
        getNoteManager().setEnableShortcutDrawing(true);
        getNoteManager().setEnableShortcutErasing(true);
    }

    public void disablePenShortcut() {
        if (!supportScalable()) {
            return;
        }
        getNoteManager().setEnableShortcutDrawing(false);
        getNoteManager().setEnableShortcutErasing(false);
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
        return getFirstVisiblePageWithBookmark() != null;
    }

    public PageInfo getFirstVisiblePageWithBookmark() {
        for (PageInfo pageInfo : getVisiblePages()) {
            if (getReaderUserDataInfo().hasBookmark(pageInfo)) {
                return pageInfo;
            }
        }
        return null;
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

    public void submitRenderRequest(ReaderPresenter readerPresenter, final BaseReaderRequest renderRequest) {
        this.readerPresenter = readerPresenter;
        reader = readerPresenter.getReader();
        submitRenderRequest(renderRequest, null);
    }

    public void submitRenderRequest(final BaseReaderRequest renderRequest, final BaseCallback callback) {
        beforeSubmitRequest();
        readerPresenter.getReader().submitRequest(context, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                onRenderRequestFinished(renderRequest, e);
                callback.invoke(callback, request, e);
                onPageDrawFinished(renderRequest, e);
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
        Debug.d(getClass(), "onRenderRequestFinished: " + request);
        if (e != null || request.isAbort()) {
            return;
        }
        saveReaderViewInfo(request);
        saveReaderUserDataInfo(request);
        setLastRequestSequence(request.getRequestSequence());
        getEventBus().post(RequestFinishEvent.createEvent(request.getRequestSequence(), applyGCIntervalUpdate, renderShapeData, false));
        if (getReaderViewInfo() != null && getReaderViewInfo().layoutChanged) {
            getEventBus().post(new LayoutChangeEvent());
        }
    }

    public void updatePinchZoomMenu(final PinchZoomEvent event) {
        getEventBus().post(event);
    }

    public void redrawPage() {
        if (getReader() != null) {
            submitRenderRequest(readerPresenter, new RenderRequest());
        }
    }

    public void showReaderSettings() {
        getEventBus().post(new ShowReaderSettingsEvent());
    }

    private void addActiveDialog(Dialog dialog) {
        activeDialogs.add(dialog);
    }

    public void removeActiveDialog(Dialog dialog) {
        activeDialogs.remove(dialog);
    }

    public void trackDialog(final Dialog dialog) {
        if (dialog == null) {
            return;
        }
        addActiveDialog(dialog);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                removeActiveDialog(dialog);
            }
        });
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

    public boolean isAnyActiveDialog() {
        return activeDialogs.size() > 0;
    }

    public void destroy(final BaseCallback callback) {
        closeActiveDialogs();
        closeTts();
        closeNoteManager();
        closeDocument(callback);
    }

    private void closeDocument(final BaseCallback callback) {
        documentInitRendered = false;
        documentOpenState = DocumentOpenState.INIT;
        if (reader == null || reader.getDocument() == null) {
            BaseCallback.invoke(callback, null, null);
            return;
        }

        final CloseRequest closeRequest = new CloseRequest();
        submitNonRenderRequest(closeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                onDocumentClosed();
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

    public int getLastRequestSequence() {
        return lastRequestSequence;
    }

    public void setLastRequestSequence(int lastRequestSequence) {
        this.lastRequestSequence = lastRequestSequence;
    }

    public void prepareEventReceiver() {
        if (eventReceiver == null) {
            eventReceiver = new EventReceiver(getContext());
            getEventBus().register(eventReceiver);
        }
    }

    public void onDocumentOpened() {
        prepareEventReceiver();
        documentOpenState = DocumentOpenState.OPENED;
        ReaderDocumentMetadata metadata = getReader().getDocumentMetadataSafely();
        DocumentInfo documentInfo = DocumentInfo.create(metadata.getAuthors(),
                getReader().getDocumentMd5(),
                getBookName(),
                documentPath,
                metadata.getTitle());
        getEventBus().post(new DocumentOpenEvent(getContext(), documentInfo));
    }

    public void onDocumentClosed() {
        getEventBus().post(new DocumentCloseEvent(getContext()));
        if (eventReceiver != null) {
            getEventBus().unregister(eventReceiver);
            eventReceiver = null;
        }
    }

    public void onActivityPause() {
        getEventBus().post(new ActivityPauseEvent(getContext()));
    }

    public void onActivityResume() {
        getEventBus().post(new ActivityResumeEvent(getContext()));
    }

    public void onDocumentInitRendered() {
        documentInitRendered = true;
        getEventBus().post(new DocumentInitRenderedEvent());
    }

    public boolean isDocumentInitRendered() {
        return documentInitRendered;
    }

    public void onTextSelected(final Annotation annotation) {
        if (annotation == null) {
            return;
        }
        String originText = annotation.getQuote();
        String userNote = annotation.getNote();
        if (StringUtils.isBlank(userNote)) {
            final TextSelectionEvent event = TextSelectionEvent.onTextSelected(getContext(), originText);
            getEventBus().post(event);
            return;
        }
        final AnnotationEvent event = AnnotationEvent.onAddAnnotation(getContext(), originText, userNote);
        getEventBus().post(event);
    }

    public void onDictionaryLookup(final String text) {
        final DictionaryLookupEvent event = DictionaryLookupEvent.create(getContext(), text);
        getEventBus().post(event);
    }

    public void onNetworkChanged(boolean connected, int networkType) {
        final NetworkChangedEvent event = NetworkChangedEvent.create(getContext(), connected, networkType);
        getEventBus().post(event);
    }

    public void enterSlideshow() {
        getEventBus().post(new SlideshowStartEvent());
    }
}


