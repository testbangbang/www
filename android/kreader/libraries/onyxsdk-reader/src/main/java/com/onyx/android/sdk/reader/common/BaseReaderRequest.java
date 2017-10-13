package com.onyx.android.sdk.reader.common;

import android.util.Log;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.cache.ReaderBitmapReferenceImpl;
import com.onyx.android.sdk.reader.dataprovider.ContentSdKDataUtils;
import com.onyx.android.sdk.reader.dataprovider.LegacySdkDataUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

import java.util.List;

/**
 * Created by zhuzeng on 10/4/15.
 */
public abstract class BaseReaderRequest extends BaseRequest {

    private static final String TAG = BaseReaderRequest.class.getSimpleName();
    private volatile boolean saveOptions = false;

    private ReaderBitmapReferenceImpl renderBitmap;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private volatile boolean transferBitmap = true;
    private boolean loadPageAnnotation = true;
    private boolean loadBookmark = true;
    private boolean loadPageLinks = true;
    private boolean loadPageImages = true;
    private boolean loadFormFields = true;

    public BaseReaderRequest() {
        super();
    }

    public boolean isTransferBitmap() {
        return transferBitmap;
    }

    public void setTransferBitmap(boolean sync) {
        transferBitmap = sync;
    }

    public void setSaveOptions(boolean save) {
        saveOptions = save;
    }

    public boolean isSaveOptions() {
        return saveOptions;
    }

    public ReaderBitmapReferenceImpl getRenderBitmap() {
        return renderBitmap;
    }

    public void drawVisiblePages(final Reader reader) throws ReaderException {
        ReaderDrawContext context = ReaderDrawContext.create(false);
        drawVisiblePages(reader, context);
    }

    public void drawVisiblePages(final Reader reader, ReaderDrawContext context) throws ReaderException {
        if (reader.getReaderLayoutManager().drawVisiblePages(reader, context, createReaderViewInfo())) {
            renderBitmap = context.renderingBitmap;
        }
    }

    public void beforeExecute(final Reader reader) {
        reader.acquireWakeLock(getContext(), getClass().getSimpleName());
        benchmarkStart();
        if (isAbort()) {
            reader.getReaderHelper().setAbortFlag();
        }
        if (getCallback() == null) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getCallback().start(BaseReaderRequest.this);
            }
        };
        if (isRunInBackground()) {
            reader.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public abstract void execute(final Reader reader) throws Exception;

    /**
     * must not throw out exception from the method
     *
     * @param reader
     */
    public void afterExecute(final Reader reader) {
        try {
            afterExecuteImpl(reader);
        } catch (Throwable tr) {
            Log.w(getClass().getSimpleName(), tr);
        } finally {
            beforeDone(reader);
            transferBitmapToViewport(reader);
        }
    }

    private void afterExecuteImpl(final Reader reader) throws Throwable {
        dumpException();
        benchmarkEnd();
        loadUserData(reader);
        cleanup(reader);
    }

    private void dumpException() {
        if (hasException()) {
            Log.w(TAG, getException());
        }
    }

    private void beforeDone(final Reader reader) {
        try {
            final Runnable beforeDoneRunnable = new Runnable() {
                @Override
                public void run() {
                    BaseCallback.invokeBeforeDone(getCallback(), BaseReaderRequest.this, getException());
                }
            };
            if (isRunInBackground()) {
                reader.getLooperHandler().post(beforeDoneRunnable);
            } else {
                beforeDoneRunnable.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void transferBitmapToViewport(final Reader reader) {
        final Runnable doneRunnable = new Runnable() {
            @Override
            public void run() {
                if (isTransferBitmap() && getRenderBitmap() != null) {
                    reader.transferRenderBitmapToViewport(getRenderBitmap());
                }
                BaseCallback.invoke(getCallback(), BaseReaderRequest.this, getException());
                reader.releaseWakeLock(BaseReaderRequest.this.getClass().getSimpleName());
            }
        };

        if (isRunInBackground()) {
            reader.getLooperHandler().post(doneRunnable);
        } else {
            doneRunnable.run();
        }
        if (getRenderBitmap() != null && !isTransferBitmap()) {
            reader.returnBitmapToCache(getRenderBitmap());
        }
    }

    public final ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public final ReaderUserDataInfo getReaderUserDataInfo() {
        if (readerUserDataInfo == null) {
            readerUserDataInfo = new ReaderUserDataInfo();
        }
        return readerUserDataInfo;
    }

    public ReaderViewInfo createReaderViewInfo() {
        readerViewInfo = new ReaderViewInfo();
        return readerViewInfo;
    }

    public void saveReaderOptions(final Reader reader) {
        if (reader.getDocument().saveOptions()) {
            reader.saveOptions();
            saveToDocumentOptions(reader);
        }
        saveToLegacyDataProvider(reader);
    }

    private void saveToDocumentOptions(final Reader reader) {
        ContentSdKDataUtils.getDataProvider().saveDocumentOptions(getContext(),
                reader.getDocumentPath(),
                reader.getDocumentMd5(),
                reader.getDocumentOptions().toJSONString());
    }
    
    private void saveToLegacyDataProvider(final Reader reader) {
        try {
            if (reader.getNavigator() != null) {
                int currentPage = PagePositionUtils.getPageNumber(reader.getReaderLayoutManager().getCurrentPageInfo() != null ?
                        reader.getReaderLayoutManager().getCurrentPageInfo().getName() :
                        reader.getNavigator().getInitPosition());
                int totalPage = reader.getNavigator().getTotalPage();
                LegacySdkDataUtils.updateProgress(getContext(), reader.getDocumentPath(),
                        currentPage, totalPage);
                ContentSdKDataUtils.updateProgress(getContext(), reader.getDocumentPath(),
                        currentPage, totalPage);
                LegacySdkDataUtils.recordFinishReading(getContext(), currentPage, totalPage);
            }
        } catch (Throwable tr) {
            Log.w(TAG, this.getClass().toString());
            Log.w(TAG, tr);
        }
    }

    private void loadUserData(final Reader reader) {
        getReaderUserDataInfo().setDocumentPath(reader.getDocumentPath());
        getReaderUserDataInfo().setDocumentCategory(reader.getDocumentOptions().getDocumentCategory());
        getReaderUserDataInfo().setDocumentCodePage(reader.getDocumentOptions().getCodePage());
        getReaderUserDataInfo().setChineseConvertType(reader.getDocumentOptions().getChineseConvertType());
        getReaderUserDataInfo().setDocumentMetadata(reader.getDocumentMetadata());
        if (readerViewInfo != null && loadPageAnnotation) {
            getReaderUserDataInfo().loadPageAnnotations(getContext(), reader, readerViewInfo.getVisiblePages());
            if (!reader.getRendererFeatures().supportScale()) {
                // if document doesn't support scale, means it's a flow document,
                // then we need update selection rectangles as they may change
                updateAnnotationRectangles(reader);
            }
        }
        if (readerViewInfo != null && loadBookmark) {
            getReaderUserDataInfo().loadPageBookmarks(getContext(), reader, readerViewInfo.getVisiblePages());
        }
        if (readerViewInfo != null && loadPageLinks) {
            getReaderUserDataInfo().loadPageLinks(getContext(), reader, readerViewInfo.getVisiblePages());
        }
        if (readerViewInfo != null && loadPageImages) {
            getReaderUserDataInfo().loadPageImages(getContext(), reader, readerViewInfo.getVisiblePages());
        }
        if (readerViewInfo != null && loadFormFields && reader.getReaderHelper().getFormManager().isCustomFormEnabled()) {
            getReaderUserDataInfo().loadFormFields(getContext(), reader, readerViewInfo.getVisiblePages());
        }
    }

    private void updateAnnotationRectangles(final Reader reader) {
        for (PageInfo pageInfo : readerViewInfo.getVisiblePages()) {
            List<PageAnnotation> annotations = getReaderUserDataInfo().getPageAnnotations(pageInfo);
            if (annotations == null) {
                continue;
            }
            for (PageAnnotation annotation : annotations) {
                ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
                ReaderSelection selection = hitTestManager.selectOnScreen(pageInfo.getPosition(),
                        annotation.getAnnotation().getLocationBegin(),
                        annotation.getAnnotation().getLocationEnd());
                annotation.getRectangles().clear();
                if (selection != null) {
                    annotation.getRectangles().addAll(selection.getRectangles());
                }
            }
        }
    }

    private void cleanup(final Reader reader) {
        reader.getReaderHelper().clearAbortFlag();
        reader.getReaderHelper().setLayoutChanged(false);
    }

    public void setLoadPageAnnotation(boolean loadPageAnnotation) {
        this.loadPageAnnotation = loadPageAnnotation;
    }

    public void setLoadBookmark(boolean loadBookmark) {
        this.loadBookmark = loadBookmark;
    }
}
