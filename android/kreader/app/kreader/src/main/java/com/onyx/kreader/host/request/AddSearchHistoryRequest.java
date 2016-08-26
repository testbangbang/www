package com.onyx.kreader.host.request;

import com.onyx.android.sdk.dataprovider.SearchHistory;
import com.onyx.android.sdk.dataprovider.SearchHistoryProvider;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.kreader.host.wrapper.Reader;

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
        LayoutProviderUtils.updateReaderViewInfo(createReaderViewInfo(), reader.getReaderLayoutManager());
    }

    private SearchHistory createSearchHistory(final Reader reader) {
        SearchHistory dbHistory = SearchHistoryProvider.getSearchHistory(reader.getDocumentMd5(),content);
        if (dbHistory != null){
            return dbHistory;
        }
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setUniqueId(reader.getDocumentMd5());
        searchHistory.setContent(content);
        return searchHistory;
    }
}
