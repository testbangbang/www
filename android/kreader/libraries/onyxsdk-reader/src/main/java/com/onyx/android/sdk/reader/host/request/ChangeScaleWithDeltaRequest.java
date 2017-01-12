package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

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