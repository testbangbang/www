package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/5/15.
 * redraw request
 */
public class EmboldenGlyphRequest extends BaseReaderRequest {
    private int emboldenLevel = 0;

    public EmboldenGlyphRequest(int emboldenLevel) {
        this.emboldenLevel = emboldenLevel;
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getDocumentOptions().setEmboldenLevel(emboldenLevel);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }

}
