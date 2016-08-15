package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.host.request.SearchRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.utils.PagePositionUtils;

import java.util.List;

/**
 * Created by Joy on 2016/5/31.
 */
public class SearchContentAction extends BaseAction {
    private static final String TAG = SearchContentAction.class.getSimpleName();

    private String query;
    private OnSearchContentCallBack onSearchContentCallBack;
    private boolean stopSearch = false;
    private int contentLength;

    public interface OnSearchContentCallBack{
        void OnNext(List<ReaderSelection> results);
    }

    public SearchContentAction(final String query, int contentLength) {
        this.query = query;
        this.contentLength = contentLength;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        execute(readerDataHolder,onSearchContentCallBack);
    }

    public void execute(final ReaderDataHolder readerDataHolder, final OnSearchContentCallBack onSearchContentCallBack) {
        this.onSearchContentCallBack = onSearchContentCallBack;
        stopSearch = false;
        requestSearchBySequence(readerDataHolder,0,query);
    }

    private void requestSearchBySequence(final ReaderDataHolder readerDataHolder, final int page, final String query){
        if (page >= readerDataHolder.getPageCount() || stopSearch){
            return;
        }
        SearchRequest request = new SearchRequest(PagePositionUtils.fromPageNumber(page), query, false, false, contentLength, readerDataHolder);
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), request, new BaseCallback() {
            @Override
            public void done(final BaseRequest request, Throwable e) {
                List<ReaderSelection> selections = readerDataHolder.getReaderUserDataInfo().getSearchResults();
                if (onSearchContentCallBack != null){
                    onSearchContentCallBack.OnNext(selections);
                }
                int next = page + 1;
                requestSearchBySequence(readerDataHolder,next,query);
            }
        });
    }

    public void stopSearch(){
        stopSearch = true;
    }
}
