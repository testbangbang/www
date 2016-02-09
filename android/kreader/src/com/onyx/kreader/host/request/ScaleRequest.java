package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class ScaleRequest extends BaseRequest {

    private float scale;
    private float x, y;

    public ScaleRequest(float s, float viewportX, float viewportY) {
        scale = s;
        x = viewportX;
        y = viewportY;
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().setScale(scale, x, y);
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }
}
