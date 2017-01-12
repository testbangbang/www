package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

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
