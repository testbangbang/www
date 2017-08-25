package com.onyx.android.dr.reader.requests;


import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.reader.api.ReaderSearchOptions;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.impl.ReaderSearchOptionsImpl;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class SearchRequest extends BaseReaderRequest {

    private ReaderSearchOptionsImpl searchOptions;
    private String currentPage;
    private ReaderPresenter readerPresenter;

    public SearchRequest(final String currentPage, final String text, boolean caseSensitive, boolean match, int contentLength, ReaderPresenter readerPresenter) {
        searchOptions = new ReaderSearchOptionsImpl(currentPage, text, caseSensitive, match);
        searchOptions.setContextLength(contentLength);
        this.currentPage = currentPage;
        this.readerPresenter = readerPresenter;
    }

    // in document coordinates system. forward to layout manager to scale
    public void execute(final Reader reader) throws Exception {
        createReaderViewInfo();
        reader.getReaderLayoutManager().getPageManager().collectVisiblePages();
        reader.getSearchManager().searchInPage(PagePositionUtils.getPageNumber(currentPage), searchOptions, true);
        if (reader.getSearchManager().searchResults().size() > 0) {
            LayoutProviderUtils.updateReaderViewInfo(reader, getReaderViewInfo(), reader.getReaderLayoutManager());
            readerPresenter.getReaderUserDataInfo().saveSearchResults(reader.getSearchManager().searchResults());
        }
    }

    public ReaderSearchOptions getSearchOptions() {
        return searchOptions;
    }
}
