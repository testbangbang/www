package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.model.Comment;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.GetBookCommentRequestBean;

import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/13.
 */

public class RxGetBookCommentRequest extends RxBaseBookStoreRequest {
    private final GetBookCommentRequestBean requestBean;
    public Comment result;

    public RxGetBookCommentRequest(GetBookCommentRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public RxGetBookCommentRequest call() throws Exception {
        try {
            Response<Comment> response = getService().getBookComment(requestBean.bookId, requestBean.commentId).execute();
            if (response != null && response.isSuccessful()) {
                result = response.body();
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
        return this;
    }

    public Comment getResult() {
        return result;
    }
}