package com.onyx.reader.host.request;

import com.onyx.reader.api.ReaderHitTestManager;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class NextScreenRequest extends BaseRequest {

    public NextScreenRequest() {

    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().nextScreen();
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }
}
