package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.common.ReaderDrawContext;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/16/16.
 */
public class PrerenderRequest extends BaseRequest {

    private boolean forward;

    public PrerenderRequest(boolean next) {
        super();
        forward = next;
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        final ReaderDrawContext drawContext = new ReaderDrawContext();
        final PositionSnapshot snapshot = reader.getReaderLayoutManager().getCurrentLayoutProvider().saveSnapshot();
        if (forward && reader.getReaderLayoutManager().nextScreen()) {
            reader.getReaderLayoutManager().drawVisiblePages(reader, drawContext, getRenderBitmap(), createReaderViewInfo());
            reader.getReaderLayoutManager().getCurrentLayoutProvider().restoreBySnapshot(snapshot);
        } else if (!forward && reader.getReaderLayoutManager().prevScreen()) {
            reader.getReaderLayoutManager().drawVisiblePages(reader, drawContext, getRenderBitmap(), createReaderViewInfo());
            reader.getReaderLayoutManager().getCurrentLayoutProvider().restoreBySnapshot(snapshot);
        }
        reader.getReaderHelper().setRenderBitmapDirty(false);
    }
}
