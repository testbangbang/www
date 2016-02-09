package com.onyx.kreader.host.request;

import android.graphics.RectF;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ScaleByRectRequest extends BaseRequest  {

    private RectF childInHost;

    public ScaleByRectRequest(final RectF c) {
        childInHost = c;
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().scaleByRect(childInHost);
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }
}
