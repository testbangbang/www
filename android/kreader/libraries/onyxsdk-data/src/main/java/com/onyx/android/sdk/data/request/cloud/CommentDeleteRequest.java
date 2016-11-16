package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class CommentDeleteRequest extends BaseCloudRequest {

    private String bookId;
    private String commentId;
    private boolean isSuccess = true;

    public CommentDeleteRequest(String bookId, String commentId) {
        this.bookId = bookId;
        this.commentId = commentId;
    }

    public boolean getResult() {
        return isSuccess;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response response = executeCall(ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase())
                .deleteBookComment(bookId, commentId, getAccountSessionToken()));
        isSuccess = response.isSuccessful();
    }
}
