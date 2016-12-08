package com.onyx.kreader.host.request;

import com.onyx.android.sdk.data.provider.SearchHistoryProvider;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by ming on 16/8/8.
 */
public class DeleteSearchHistoryRequest extends BaseReaderRequest {

    public void execute(final Reader reader) throws Exception {
        SearchHistoryProvider.deleteSearchHistory(reader.getDocumentMd5());
        LayoutProviderUtils.updateReaderViewInfo(reader, createReaderViewInfo(), reader.getReaderLayoutManager());
    }
}
