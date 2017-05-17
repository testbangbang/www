package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.host.request.GetPositionFromPageNumberRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by ming on 2017/2/24.
 */

public class GetPositionFromPageNumberAction extends BaseAction {

    private List<String> documentPositions;
    private List<Integer> pageNumbers;
    private boolean abortPendingTasks = false;

    public GetPositionFromPageNumberAction(List<Integer> pageNumbers, final boolean abortPendingTasks) {
        this.pageNumbers = pageNumbers;
        this.abortPendingTasks = abortPendingTasks;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final GetPositionFromPageNumberRequest readerRequest = new GetPositionFromPageNumberRequest(pageNumbers, abortPendingTasks);
        readerDataHolder.submitNonRenderRequest(readerRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                documentPositions = readerRequest.getDocumentPositions();
                callback.invoke(callback, request, e);
            }
        });
    }

    public List<String> getDocumentPositions() {
        return documentPositions;
    }
}
