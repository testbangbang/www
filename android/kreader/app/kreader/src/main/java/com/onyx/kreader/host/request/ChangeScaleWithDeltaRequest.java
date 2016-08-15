package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zengzhu on 2/23/16.
 */
public class ChangeScaleWithDeltaRequest extends BaseReaderRequest {

    private float delta;
    private String pageName;

    public ChangeScaleWithDeltaRequest(final String name, final float d) {
        pageName = name;
        delta = d;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getReaderLayoutManager().setSavePosition(true);
        reader.getReaderLayoutManager().changeScaleWithDelta(pageName, delta);
        drawVisiblePages(reader);
    }
}