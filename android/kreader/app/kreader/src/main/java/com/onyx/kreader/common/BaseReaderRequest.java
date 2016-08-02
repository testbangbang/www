package com.onyx.kreader.common;

import android.util.Log;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.dataprovider.compatability.LegacySdkDataUtils;
import com.onyx.kreader.dataprovider.DocumentOptionsProvider;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.kreader.host.wrapper.Reader;

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

    public void useRenderBitmap(final Reader reader) {
        renderBitmap = reader.getReaderHelper().getRenderBitmap();
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
            copyBitmapToViewport(reader);
        }
    }

    private void afterExecuteImpl(final Reader reader) throws Throwable {
        dumpException();
        benchmarkEnd();
        reader.getReaderHelper().clearAbortFlag();
        saveReaderOptions(reader);
        loadUserData(reader);
    }

    private void dumpException() {
        if (hasException()) {
            Log.w(TAG, getException());
        }
    }

    private void copyBitmapToViewport(final Reader reader) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isTransferBitmap()) {
                    reader.getBitmapCopyCoordinator().copyRenderBitmapToViewport();
                }
                if (getCallback() != null) {
                    // we can't foresee what's will be in done(), so we protect it with catch clause
                    getCallback().done(BaseReaderRequest.this, getException());
                }
                reader.releaseWakeLock();
        }};

        if (isRunInBackground()) {
            reader.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
        reader.getBitmapCopyCoordinator().waitCopy();
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

    private void saveReaderOptions(final Reader reader) {
        if (hasException() || !isSaveOptions()) {
            return;
        }

        reader.saveOptions();
        DocumentOptionsProvider.saveDocumentOptions(getContext(),
                reader.getDocumentPath(),
                reader.getDocumentMd5(),
                reader.getDocumentOptions());

        int currentPage = Integer.parseInt(reader.getDocumentOptions().getCurrentPage());
        int totalPage = reader.getDocumentOptions().getTotalPage();
        LegacySdkDataUtils.updateProgress(getContext(), reader.getDocumentPath(),
                currentPage + 1, totalPage);
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
