package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.model.Comment;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.PostBookCommentRequestBean;

import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/13.
 */

public class RxPostBookCommentRequest extends RxBaseBookStoreRequest {
    private final PostBookCommentRequestBean requestBean;
    public Comment result;

    public RxPostBookCommentRequest(PostBookCommentRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public RxPostBookCommentRequest call() throws Exception {
        try {
            Response<Comment> response = getService().postBookComment(requestBean.bookId, requestBean.comment, requestBean.sessionToken).execute();
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