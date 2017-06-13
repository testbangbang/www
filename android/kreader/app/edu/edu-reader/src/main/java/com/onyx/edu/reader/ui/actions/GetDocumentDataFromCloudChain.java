package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.Constant;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.data.ReviewDocumentData;

/**
 * Created by ming on 2017/6/12.
 */

public class GetDocumentDataFromCloudChain extends BaseAction {

    private String errorMessage;
    private ReviewDocumentData reviewDocumentData;

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        ActionChain actionChain = new ActionChain();
        AccountLoadFromLocalAction accountLoadFromLocalAction = AccountLoadFromLocalAction.create();
        final GetDocumentDataFromCloudAction getDocumentDataFromCloudAction = GetDocumentDataFromCloudAction.create(Constant.SYNC_API_BASE, readerDataHolder.getCloudDocId(), accountLoadFromLocalAction.getToken());
        actionChain.addAction(accountLoadFromLocalAction);
        actionChain.addAction(getDocumentDataFromCloudAction);
        actionChain.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                errorMessage = getDocumentDataFromCloudAction.getErrorMessage();
                reviewDocumentData = getDocumentDataFromCloudAction.getReviewDocumentData();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ReviewDocumentData getReviewDocumentData() {
        return reviewDocumentData;
    }
}
