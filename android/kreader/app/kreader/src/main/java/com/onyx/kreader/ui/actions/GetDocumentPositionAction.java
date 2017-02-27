package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.host.request.GetDocumentPositionRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by ming on 2017/2/24.
 */

public class GetDocumentPositionAction extends BaseAction {

    private String documentPosition;
    private int pageNumber;

    public GetDocumentPositionAction(int pageNumbers) {
        this.pageNumber = pageNumbers;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final GetDocumentPositionRequest readerRequest = new GetDocumentPositionRequest(pageNumber);
        readerDataHolder.submitNonRenderRequest(readerRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                documentPosition = readerRequest.getDocumentPosition();
                callback.invoke(callback, request, e);
            }
        });
    }

    public String getDocumentPosition() {
        return documentPosition;
    }
}
