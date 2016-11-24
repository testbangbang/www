package com.onyx.kreader.host.request;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

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
