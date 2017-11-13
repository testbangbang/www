package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.model.Comment;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.UpdataBookCommentRequestBean;

import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/13.
 */

public class RxUpdataBookCommentRequest extends RxBaseBookStoreRequest {
    private final UpdataBookCommentRequestBean requestBean;
    public Comment result;

    public RxUpdataBookCommentRequest(UpdataBookCommentRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public RxUpdataBookCommentRequest call() throws Exception {
        try {
            Response<Comment> response = getService().updateBookComment(requestBean.bookId, requestBean.commentId, requestBean.comment, requestBean.sessionToken).execute();
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