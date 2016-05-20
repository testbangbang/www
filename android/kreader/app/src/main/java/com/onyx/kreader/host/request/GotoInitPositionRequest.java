package com.onyx.kreader.host.request;

import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.StringUtils;

/**
 * Created by zhuzeng on 5/19/16.
 */
public class GotoInitPositionRequest extends BaseRequest {

    public GotoInitPositionRequest() {
        super();
    }

    public void execute(final Reader reader) throws Exception {
        String documentPosition = reader.getReaderHelper().getNavigator().getInitPosition();
        if (!reader.getReaderHelper().getReaderLayoutManager().gotoPosition(documentPosition)) {
            throw ReaderException.outOfRange();
        }
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }
}
