package com.onyx.kreader.common;

import android.util.Log;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.dataprovider.DocumentProvider;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.cache.ReaderBitmapImpl;
import com.onyx.kreader.dataprovider.LegacySdkDataUtils;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.PagePositionUtils;

/**
 * Created by zhuzeng on 10/4/15.
 */
public abstract class BaseReaderRequest extends BaseRequest {

    private static final String TAG = BaseReaderRequest.class.getSimpleName();
    private volatile boolean saveOptions = false;

    private ReaderBitmapImpl renderBitmap;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private volatile boolean transferBitmap = true;

    public BaseReaderRequest() {
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

    public ReaderBitmapImpl getRenderBitmap() {
        return renderBitmap;
    }

    public void drawVisiblePages(final Reader reader) throws ReaderException {
        ReaderDrawContext context = new ReaderDrawContext();
        context.asyncDraw = false;
        drawVisiblePages(reader, context);
    }

    public void drawVisiblePages(final Reader reader, ReaderDrawContext context) throws ReaderException {
        if (reader.getReaderLayoutManager().drawVisiblePages(reader, context, createReaderViewInfo())) {
            renderBitmap = context.renderingBitmap;
        }
    }

    public void beforeExecute(final Reader reader) {
        reader.acquireWakeLock(getContext());
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
            Log.w(TAG, tr);
        } finally {
            transferBitmapToViewport(reader);
        }
    }

    private void afterExecuteImpl(final Reader reader) throws Throwable {
        dumpException();
        benchmarkEnd();
        reader.getReaderHelper().clearAbortFlag();
        loadUserData(reader);
    }

    private void dumpException() {
        if (hasException()) {
            Log.w(TAG, getException());
        }
    }

    private void transferBitmapToViewport(final Reader reader) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isTransferBitmap() && getRenderBitmap() != null) {
                    reader.transferRenderBitmapToViewport(getRenderBitmap());
                }
                BaseCallback.invoke(getCallback(), BaseReaderRequest.this, getException());
                reader.releaseWakeLock();
        }};

        if (isRunInBackground()) {
            reader.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
        if (getRenderBitmap() != null && !isTransferBitmap()){
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
        reader.saveOptions();
        saveToDocumentOptionsProvider(reader);
        saveToLegacyDataProvider(reader);
    }

    private void saveToDocumentOptionsProvider(final Reader reader) {
        DocumentProvider.saveDocumentOptions(getContext(),
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
            }
        } catch (Throwable tr) {
            Log.w(TAG, this.getClass().toString());
            Log.w(TAG, tr);
        }
    }

    private void loadUserData(final Reader reader) {
        getReaderUserDataInfo().setDocumentPath(reader.getDocumentPath());
        if (readerViewInfo != null) {
            getReaderUserDataInfo().loadPageAnnotations(getContext(), reader, readerViewInfo.getVisiblePages());
        }
        if (readerViewInfo != null) {
            getReaderUserDataInfo().loadBookmarks(getContext(), reader, readerViewInfo.getVisiblePages());
        }
    }
}
