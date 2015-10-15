package com.onyx.reader.host.request;

import android.graphics.RectF;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ScaleByRectRequest extends BaseRequest  {

    private RectF child;

    public ScaleByRectRequest(final RectF c) {
        child = c;
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().scaleByRect(child);
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }
}
