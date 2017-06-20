package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 5/19/16.
 */
public class GotoInitPositionRequest extends BaseReaderRequest {

    public GotoInitPositionRequest() {
        super();
    }

    public void execute(final Reader reader) throws Exception {
        String documentPosition = reader.getReaderHelper().getNavigator().getInitPosition();
        if (!reader.getReaderHelper().getReaderLayoutManager().gotoPosition(documentPosition)) {
            throw ReaderException.outOfRange();
        }
        drawVisiblePages(reader);
    }
}
