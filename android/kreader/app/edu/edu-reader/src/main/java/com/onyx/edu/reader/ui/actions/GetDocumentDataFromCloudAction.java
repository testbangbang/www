package com.onyx.edu.reader.ui.actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.cloud.GetDocumentDataFromCloudRequest;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.data.ReviewDocumentData;

/**
 * Created by ming on 2017/6/12.
 */

public class GetDocumentDataFromCloudAction extends BaseAction {

    private String cloudDocId;
    private StringBuffer token;

    private String errorMessage;
    private String reviewDocumentData;

    public GetDocumentDataFromCloudAction(String cloudDocId, StringBuffer token) {
        this.cloudDocId = cloudDocId;
        this.token = token;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final GetDocumentDataFromCloudRequest cloudRequest = new GetDocumentDataFromCloudRequest(cloudDocId, token.toString());
        readerDataHolder.getCloudManager().submitRequest(readerDataHolder.getContext(), cloudRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                errorMessage = cloudRequest.getErrorMessage();
                reviewDocumentData = cloudRequest.getDocumentData();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getReviewDocumentData() {
        return reviewDocumentData;
    }

    public static GetDocumentDataFromCloudAction create(String cloudDocId, StringBuffer token) {
        return new GetDocumentDataFromCloudAction(cloudDocId, token);
    }
}
