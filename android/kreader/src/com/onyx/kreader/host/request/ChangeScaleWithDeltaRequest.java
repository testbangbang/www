package com.onyx.kreader.host.request;

import android.graphics.RectF;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zengzhu on 2/23/16.
 */
public class ChangeScaleWithDeltaRequest extends BaseRequest {

    private float delta;
    private String pageName;

    public ChangeScaleWithDeltaRequest(final String name, final float d) {
        pageName = name;
        delta = d;
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().changeScaleWithDelta(pageName, delta);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }
}