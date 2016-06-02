package com.onyx.kreader.host.request;

import com.onyx.kreader.api.ReaderSearchOptions;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.impl.ReaderSearchOptionsImpl;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.StringUtils;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class SearchRequest extends BaseReaderRequest {

    private ReaderSearchOptionsImpl searchOptions;
    private boolean searchForward;

    public SearchRequest(final String fromPage, final String text,  boolean caseSensitive, boolean match, boolean forward) {
        searchOptions = new ReaderSearchOptionsImpl(fromPage, text, caseSensitive, match);
        searchForward = forward;
    }

    // in document coordinates system. forward to layout manager to scale
    public void execute(final Reader reader) throws Exception {
        createReaderViewInfo();
        reader.getReaderLayoutManager().getPageManager().collectVisiblePages();
        if (searchForward) {
            reader.getSearchManager().searchNext(searchOptions);
        } else {
            reader.getSearchManager().searchPrevious(searchOptions);
        }
        if (reader.getSearchManager().searchResults().size() > 0) {
            String page = reader.getSearchManager().searchResults().get(0).getPagePosition();
            new GotoLocationRequest(page).execute(reader);
            
            getReaderViewInfo().saveSearchResults(reader.getSearchManager().searchResults());
            LayoutProviderUtils.updateReaderViewInfo(getReaderViewInfo(), reader.getReaderLayoutManager());
        }
    }

    public ReaderSearchOptions getSearchOptions() {
        return searchOptions;
    }

    public boolean hasSearchResults(final Reader reader) {
        return reader.getSearchManager().searchResults().size() > 0;
    }
}
