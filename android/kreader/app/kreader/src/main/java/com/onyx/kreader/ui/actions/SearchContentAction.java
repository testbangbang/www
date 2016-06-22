package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.host.request.SearchRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by Joy on 2016/5/31.
 */
public class SearchContentAction extends BaseAction {

    private String page;
    private String query;
    private boolean forward;

    public SearchContentAction(final String page, final String query, final boolean forward) {
        this.page = page;
        this.query = query;
        this.forward = forward;
    }

    @Override
    public void execute(final ReaderActivity readerActivity) {
        SearchRequest request = new SearchRequest(page, query, false, false, forward);
        readerActivity.getReader().submitRequest(readerActivity, request, new BaseCallback() {
            @Override
            public void done(final BaseRequest request, Throwable e) {
                readerActivity.onSearchFinished((SearchRequest)request, e);
            }
        });
    }
}
