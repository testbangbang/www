package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.host.request.GetDocumentPageRequest;
import com.onyx.android.sdk.reader.host.request.GetDocumentPositionRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/2/24.
 */

public class GetDocumentPageAction extends BaseAction {

    private int pageNumber;
    private String documentPosition;

    public GetDocumentPageAction(String documentPosition) {
        this.documentPosition = documentPosition;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final GetDocumentPageRequest readerRequest = new GetDocumentPageRequest(documentPosition);
        readerDataHolder.submitNonRenderRequest(readerRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                pageNumber = readerRequest.getPageNumber();
                callback.invoke(callback, request, e);
            }
        });
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
