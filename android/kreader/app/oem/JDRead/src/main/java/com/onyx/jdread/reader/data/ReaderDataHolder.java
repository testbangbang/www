package com.onyx.jdread.reader.data;

import android.view.SurfaceView;

import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.jdread.reader.common.DocumentInfo;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class ReaderDataHolder {
    public enum DocumentOpenState {INIT, OPENING, OPENED}

    private DocumentOpenState documentOpenState = DocumentOpenState.INIT;
    private Reader reader;

    public void initReaderDataHolder(final DocumentInfo documentInfo) {
        documentOpenState = DocumentOpenState.INIT;
        reader = ReaderManager.getReader(documentInfo);
    }

    public Reader getReader() {
        return reader;
    }

    public SurfaceView getReadPageView() {
        return reader.getReaderViewHelper().getReadPageView();
    }

    public void setReadPageView(SurfaceView readPageView) {
        reader.getReaderViewHelper().setReadPageView(readPageView);
        reader.getReaderTouchHelper().setReaderViewTouchListener(readPageView);
    }

    public ReaderViewHelper getReaderViewHelper() {
        return reader.getReaderViewHelper();
    }
}
