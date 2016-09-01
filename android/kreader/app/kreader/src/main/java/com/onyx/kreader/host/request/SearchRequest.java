package com.onyx.kreader.host.request;

import com.onyx.kreader.api.ReaderSearchOptions;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.impl.ReaderSearchOptionsImpl;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.utils.PagePositionUtils;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class SearchRequest extends BaseReaderRequest {

    private ReaderSearchOptionsImpl searchOptions;
    private String currentPage;
    private ReaderDataHolder readerDataHolder;

    public SearchRequest(final String currentPage, final String text, boolean caseSensitive, boolean match, int contentLength, ReaderDataHolder readerDataHolder) {
        searchOptions = new ReaderSearchOptionsImpl(currentPage, text, caseSensitive, match);
        searchOptions.setContextLength(contentLength);
        this.currentPage = currentPage;
        this.readerDataHolder = readerDataHolder;
    }

    // in document coordinates system. forward to layout manager to scale
    public void execute(final Reader reader) throws Exception {
        createReaderViewInfo();
        reader.getReaderLayoutManager().getPageManager().collectVisiblePages();
        reader.getSearchManager().searchInPage(PagePositionUtils.getPageNumber(currentPage),searchOptions,true);
        if (reader.getSearchManager().searchResults().size() > 0) {
            LayoutProviderUtils.updateReaderViewInfo(getReaderViewInfo(), reader.getReaderLayoutManager());
            readerDataHolder.getReaderUserDataInfo().saveSearchResults(reader.getSearchManager().searchResults());
        }
    }

    public ReaderSearchOptions getSearchOptions() {
        return searchOptions;
    }
}
