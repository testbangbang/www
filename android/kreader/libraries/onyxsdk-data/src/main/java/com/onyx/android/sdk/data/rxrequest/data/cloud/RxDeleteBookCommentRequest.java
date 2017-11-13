package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.DeleteBookCommentRequestBean;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/13.
 */

public class RxDeleteBookCommentRequest extends RxBaseBookStoreRequest {
    private final DeleteBookCommentRequestBean requestBean;
    public ResponseBody result;

    public RxDeleteBookCommentRequest(DeleteBookCommentRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public RxDeleteBookCommentRequest call() throws Exception {
        try {
            Response<ResponseBody> response = getService().deleteBookComment(requestBean.bookId, requestBean.commentId, requestBean.sessionToken).execute();
            if (response != null && response.isSuccessful()) {
                result = response.body();
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
        return this;
    }

    public ResponseBody getResult() {
        return result;
    }
}