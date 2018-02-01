package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.layout.PageOverlayMarker;

/**
 * Created by huxiaomao on 2017/12/27.
 */

public class NextScreenRequest extends ReaderBaseRequest {
    private Reader reader;

    public NextScreenRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public NextScreenRequest call() throws Exception {
        reader.getReaderHelper().nextScreen();
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(reader);
        preloadNextScreen(reader);
        return this;
    }

    private void preloadNextScreen(Reader reader) throws Exception{
        reader.getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        PageOverlayMarker.saveCurrentPageAndViewport(reader);
        reader.getReaderHelper().nextScreen();
        PageOverlayMarker.markLastViewportOverlayPointWhenNecessary(reader, getReaderViewInfo());
    }
}
