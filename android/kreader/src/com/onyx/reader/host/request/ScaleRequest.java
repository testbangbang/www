package com.onyx.reader.host.request;

import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;

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
        reader.getReaderHelper().scalingManager.changeScale(scale, x, y);
        setRenderBitmap(reader.getReaderHelper().renderBitmap);
        reader.getReaderHelper().renderToBitmap();
    }
}
