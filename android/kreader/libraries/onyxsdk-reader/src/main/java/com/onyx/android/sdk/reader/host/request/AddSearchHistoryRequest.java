package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.data.model.SearchHistory;
import com.onyx.android.sdk.data.provider.SearchHistoryProvider;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;

/**
 * Created by ming on 16/8/8.
 */
public class AddSearchHistoryRequest extends BaseReaderRequest {

    private String content;

    public AddSearchHistoryRequest(String content) {
        this.content = content;
    }

    public void execute(final Reader reader) throws Exception {
        SearchHistoryProvider.addSearchHistory(createSearchHistory(reader));
        LayoutProviderUtils.updateReaderViewInfo(reader, createReaderViewInfo(), reader.getReaderLayoutManager());
    }

    private SearchHistory createSearchHistory(final Reader reader) {
        SearchHistory dbHistory = SearchHistoryProvider.getSearchHistory(reader.getDocumentMd5(),content);
        if (dbHistory != null){
            return dbHistory;
        }
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setIdString(reader.getDocumentMd5());
        searchHistory.setContent(content);
        return searchHistory;
    }
}
