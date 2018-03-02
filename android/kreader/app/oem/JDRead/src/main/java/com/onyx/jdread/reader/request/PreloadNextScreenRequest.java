package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.host.math.PositionSnapshot;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by zhuzeng on 02/02/2018.
 */

public class PreloadNextScreenRequest extends ReaderBaseRequest {
    private Reader reader;

    public PreloadNextScreenRequest(Reader reader) {
        super(reader);
        this.reader = reader;
    }

    @Override
    public PreloadNextScreenRequest call() throws Exception {
        reader.getReaderHelper().getReaderLayoutManager().setSavePosition(false);
        final PositionSnapshot snapshot = reader.getReaderHelper().getReaderLayoutManager().getCurrentLayoutProvider().saveSnapshot();
        if (reader.getReaderHelper().nextScreen()) {
            ReaderDrawContext context = ReaderDrawContext.create(false);
            reader.getReaderHelper().getReaderLayoutManager().drawVisiblePages(reader, context, getReaderViewInfo());
            reader.getReaderHelper().addToCache(context.renderingBitmap);
            reader.getReaderHelper().getReaderLayoutManager().getCurrentLayoutProvider().restoreBySnapshot(snapshot);
        }
        return this;
    }

}
