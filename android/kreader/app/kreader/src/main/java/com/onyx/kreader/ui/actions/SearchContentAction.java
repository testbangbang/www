package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.host.request.SearchRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.PopupSearchMenu;

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
    public void execute(final ReaderDataHolder readerDataHolder) {
        SearchRequest request = new SearchRequest(page, query, false, false, forward);
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), request, new BaseCallback() {
            @Override
            public void done(final BaseRequest request, Throwable e) {
                onSearchFinished((SearchRequest)request, e,readerDataHolder);
            }
        });
    }

    public void onSearchFinished(SearchRequest request, Throwable e,ReaderDataHolder readerDataHolder) {
        if (e != null) {
            return;
        }

        PopupSearchMenu.SearchResult result = PopupSearchMenu.SearchResult.EMPTY;
        if (request.getReaderUserDataInfo().hasSearchResults()) {
            result = PopupSearchMenu.SearchResult.SUCCEED;
            readerDataHolder.onRenderRequestFinished(request, e);
        }
        new ShowSearchMenuAction(request.getSearchOptions(), result).execute(readerDataHolder);
    }
}
