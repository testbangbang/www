package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.SearchRequest;

import java.util.List;

/**
 * Created by huxiaomao on 2018/1/31.
 */

public class SearchContentAction extends BaseReaderAction {
    private String query;
    private SearchRequest request;
    private OnSearchContentCallBack onSearchContentCallBack;
    private boolean stopSearch = false;
    private int contentLength;
    private int startPage;
    private int searchCount;
    private int currentCount;

    public void setOnSearchContentCallBack(OnSearchContentCallBack onSearchContentCallBack) {
        this.onSearchContentCallBack = onSearchContentCallBack;
    }

    public interface OnSearchContentCallBack {
        void OnNext(List<ReaderSelection> results, int page);

        void OnFinishedSearch(int endPage);
    }

    public SearchContentAction(final String query, int contentLength, int startPage, int searchCount) {
        this.query = query;
        this.contentLength = contentLength;
        this.startPage = startPage;
        this.searchCount = searchCount;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        stopSearch = false;
        requestSearchBySequence(readerDataHolder, startPage, query);
    }

    private void requestSearchBySequence(final ReaderDataHolder readerDataHolder, final int page, final String query) {
        if (page >= readerDataHolder.getPageCount() || stopSearch || currentCount >= searchCount) {
            resetSearchRequest();
            onSearchContentCallBack.OnFinishedSearch(page);
            return;
        }

        request = new SearchRequest(PagePositionUtils.fromPageNumber(page), query, false, false, contentLength, readerDataHolder.getReader());

        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<ReaderSelection> selections = request.getReaderUserDataInfo().getSearchResults();
                if (onSearchContentCallBack != null) {
                    onSearchContentCallBack.OnNext(selections, page);
                }
                if (selections != null) {
                    currentCount += selections.size();
                }
                int next = page + 1;
                if (!readerDataHolder.supportSearchByPage()) {
                    resetSearchRequest();
                    onSearchContentCallBack.OnFinishedSearch(page);
                } else {
                    requestSearchBySequence(readerDataHolder, next, query);
                }
            }
        });
    }

    public void proceedSearch(final ReaderDataHolder readerDataHolder, final int startPage) {
        currentCount = 0;
        requestSearchBySequence(readerDataHolder, startPage, query);
    }

    public void stopSearch() {
        currentCount = 0;
        stopSearch = true;
        if (request != null) {
            request.setAbort(true);
        }
    }

    private void resetSearchRequest() {
        if (request != null) {
            request.setAbort(false);
            request = null;
        }
    }
}
