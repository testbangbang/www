package com.onyx.kreader.common;

import com.onyx.kreader.dataprovider.DocumentOptionsProvider;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/4/15.
 */
public abstract class BaseReaderRequest extends BaseRequest {

    private static final String TAG = BaseReaderRequest.class.getSimpleName();
    private boolean saveOptions = true;
    private ReaderBitmapImpl renderBitmap;
    private ReaderViewInfo readerViewInfo;
    private volatile boolean transferBitmap = true;

    public BaseReaderRequest() {
    }

    public boolean isTransferBitmap() {
        return transferBitmap;
    }

    public void setTransferBitmap(boolean sync) {
        transferBitmap = sync;
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

    public void afterExecute(final Reader reader) {
        if (getException() != null) {
            getException().printStackTrace();
        }
        benchmarkEnd();
        reader.getReaderHelper().clearAbortFlag();
        saveReaderOptions(reader);

        // store render bitmap store to local flag to avoid multi-thread problem
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isTransferBitmap()) {
                    reader.getBitmapCopyCoordinator().copyRenderBitmapToViewport();
                }
                if (getCallback() != null) {
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

    public ReaderViewInfo createReaderViewInfo() {
        readerViewInfo = new ReaderViewInfo();
        return readerViewInfo;
    }

    private void saveReaderOptions(final Reader reader) {
        if (!isSaveOptions()) {
            return;
        }

        reader.saveOptions();
        DocumentOptionsProvider.saveDocumentOptions(getContext(),
                reader.getDocumentPath(),
                reader.getDocumentOptions());
    }
}
