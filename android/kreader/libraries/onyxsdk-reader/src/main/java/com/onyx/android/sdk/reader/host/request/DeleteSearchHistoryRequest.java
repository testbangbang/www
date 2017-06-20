package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.data.provider.SearchHistoryProvider;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by ming on 16/8/8.
 */
public class DeleteSearchHistoryRequest extends BaseReaderRequest {

    public void execute(final Reader reader) throws Exception {
        SearchHistoryProvider.deleteSearchHistory(reader.getDocumentMd5());
        LayoutProviderUtils.updateReaderViewInfo(reader, createReaderViewInfo(), reader.getReaderLayoutManager());
    }
}
