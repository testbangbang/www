package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class ScaleRequest extends BaseReaderRequest {

    private String pageName;
    private float scale;
    private float x, y;

    public ScaleRequest(final String name, float s, float viewportX, float viewportY) {
        scale = s;
        x = viewportX;
        y = viewportY;
        pageName = name;
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().setSavePosition(true);
        reader.getReaderLayoutManager().setScale(pageName, scale, x, y);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }
}
