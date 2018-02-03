package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by zhuzeng on 02/02/2018.
 */

public class PreloadNextScreenRequest extends ReaderBaseRequest {
    private Reader reader;

    public PreloadNextScreenRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public PreloadNextScreenRequest call() throws Exception {
        reader.getReaderHelper().getReaderLayoutManager().setSavePosition(false);
        if (reader.getReaderHelper().nextScreen()) {
            ReaderDrawContext context = ReaderDrawContext.create(false);
            reader.getReaderHelper().getReaderLayoutManager().drawVisiblePages(reader, context, getReaderViewInfo());
            reader.getReaderHelper().saveToCache(context.renderingBitmap);
            reader.getReaderHelper().previousScreen();
        }
        return this;
    }

}
