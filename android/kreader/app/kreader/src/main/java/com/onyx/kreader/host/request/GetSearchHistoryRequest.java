package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by ming on 16/8/8.
 */
public class GetSearchHistoryRequest extends BaseReaderRequest {

    int count;

    public GetSearchHistoryRequest(int count) {
        this.count = count;
    }

    public void execute(final Reader reader) throws Exception {
        createReaderViewInfo();
        getReaderUserDataInfo().loadSearchHistory(getContext(), reader,count);
    }
}
