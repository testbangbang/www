package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.host.request.GetPageNumberFromPositionRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/2/24.
 */

public class GetPageNumberFromPositionAction extends BaseAction {

    private int pageNumber;
    private String documentPosition;

    public GetPageNumberFromPositionAction(String documentPosition) {
        this.documentPosition = documentPosition;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final GetPageNumberFromPositionRequest readerRequest = new GetPageNumberFromPositionRequest(documentPosition);
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
