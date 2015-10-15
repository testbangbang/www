package com.onyx.reader.host.request;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.math.EntryInfo;
import com.onyx.reader.host.math.EntryManager;
import com.onyx.reader.host.wrapper.Reader;
import com.onyx.reader.plugins.adobe.AdobeDocumentPositionImpl;

import java.util.List;

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
