package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.SearchHistory;
import com.onyx.android.sdk.reader.host.request.GetSearchHistoryRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by ming on 16/8/8.
 */
public class GetSearchHistoryAction extends BaseAction {

    private int count;
    private CallBack callBack;

    public interface CallBack{
        void loadFinished(List<SearchHistory> searchHistoryList);
    }

    public GetSearchHistoryAction(int count,CallBack callBack) {
        this.count = count;
        this.callBack = callBack;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final GetSearchHistoryRequest searchRequest = new GetSearchHistoryRequest(count);
        readerDataHolder.submitNonRenderRequest(searchRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                if (callBack != null){
                    callBack.loadFinished(searchRequest.getReaderUserDataInfo().getSearchHistoryList());
                }
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
