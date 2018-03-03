package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.host.math.PositionSnapshot;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by zhuzeng on 02/02/2018.
 */

public class PreloadPreviousScreenRequest extends ReaderBaseRequest {

    public PreloadPreviousScreenRequest(Reader reader) {
        super(reader);
    }

    @Override
    public PreloadPreviousScreenRequest call() throws Exception {
        getReader().getReaderHelper().getReaderLayoutManager().setSavePosition(false);
        final PositionSnapshot snapshot = getReader().getReaderHelper().getReaderLayoutManager().getCurrentLayoutProvider().saveSnapshot();
        if (getReader().getReaderHelper().previousScreen()) {
            ReaderDrawContext context = ReaderDrawContext.create(false);
            getReader().getReaderHelper().getReaderLayoutManager().drawVisiblePages(getReader(), context, getReaderViewInfo());
            getReader().getReaderHelper().addToCache(context.renderingBitmap);
            getReader().getReaderHelper().getReaderLayoutManager().getCurrentLayoutProvider().restoreBySnapshot(snapshot);
        }
        return this;
    }

}
