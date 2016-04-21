package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by Joy on 2016/4/21.
 */
public class ScaleByValueRequest extends BaseRequest {

    private String pageName;
    private float scale;

    public ScaleByValueRequest(String pageName, float scale) {
        this.pageName = pageName;
        this.scale = scale;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().setScale(pageName, scale, 0, 0);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }
}
