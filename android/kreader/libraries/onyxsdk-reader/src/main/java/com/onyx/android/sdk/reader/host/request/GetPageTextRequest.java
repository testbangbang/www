package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class GetPageTextRequest extends BaseReaderRequest {

    private int page;
    private String text;

    public GetPageTextRequest(int page) {
    }

    public void execute(final Reader reader) throws Exception {
        text = reader.getDocument().getPageText(PagePositionUtils.fromPageNumber(page));
    }

    public String getText() {
        return text;
    }
}
