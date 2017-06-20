package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class GotoPageRequest extends BaseReaderRequest {

    private int page;

    public GotoPageRequest(int p) {
        super();
        page = p;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getReaderLayoutManager().setSavePosition(true);
        if (!reader.getReaderHelper().getReaderLayoutManager().gotoPage(page)) {
            throw ReaderException.outOfRange();
        }
        drawVisiblePages(reader);
    }

}
