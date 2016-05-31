package com.onyx.kreader.ui.actions;

import com.onyx.kreader.host.request.SearchRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by Joy on 2016/5/31.
 */
public class SearchContentAction extends BaseAction {

    private String query;
    private boolean forward;

    public SearchContentAction(final String query, final boolean forward) {
        this.query = query;
        this.forward = forward;
    }

    @Override
    public void execute(ReaderActivity readerActivity) {
        SearchRequest request = new SearchRequest(readerActivity.getCurrentPageName(), query, false, false, forward);
        readerActivity.submitRenderRequest(request);
    }
}
