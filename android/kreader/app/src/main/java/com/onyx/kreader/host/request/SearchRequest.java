package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.impl.ReaderSearchOptionsImpl;
import com.onyx.kreader.host.wrapper.Reader;

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
    }
}
