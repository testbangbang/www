package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.layout.PageOverlayMarker;

/**
 * Created by huxiaomao on 2017/12/27.
 */

public class PreviousScreenRequest extends ReaderBaseRequest {
    private Reader reader;

    public PreviousScreenRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public PreviousScreenRequest call() throws Exception {
        reader.getReaderHelper().previousScreen();
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(reader);
        preloadPreviousScreen(reader);
        return this;
    }

    private void preloadPreviousScreen(Reader reader) throws Exception{
        reader.getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        PageOverlayMarker.saveCurrentPageAndViewport(reader);
        reader.getReaderHelper().previousScreen();
        PageOverlayMarker.markLastViewportOverlayPointWhenNecessary(reader, getReaderViewInfo());
    }
}
