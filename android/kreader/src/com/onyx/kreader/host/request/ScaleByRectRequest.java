package com.onyx.kreader.host.request;

import android.graphics.RectF;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ScaleByRectRequest extends BaseRequest  {

    private RectF childInHost;
    private String pageName;

    public ScaleByRectRequest(final String name, final RectF c) {
        pageName = name;
        childInHost = c;
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().scaleByRect(pageName, childInHost);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }
}
