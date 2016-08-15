package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class PanRequest extends BaseReaderRequest {

    private int x, y;

    public PanRequest(int dx, int dy) {
        x = dx;
        y = dy;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getReaderLayoutManager().setSavePosition(true);
        reader.getReaderLayoutManager().pan(x, y);
        drawVisiblePages(reader);
    }
}
