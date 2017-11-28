package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.model.Comment;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.GetBookCommentListRequestBean;

import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/7.
 */

public class RxGetBookCommentListRequest extends RxBaseBookStoreRequest {
    private final GetBookCommentListRequestBean requestBean;
    private ProductResult<Comment> result;

    public RxGetBookCommentListRequest(GetBookCommentListRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public RxGetBookCommentListRequest call() throws Exception {
        try {
            Response<ProductResult<Comment>> response = getService().getBookCommentList(requestBean.uniqueId, requestBean.params).execute();
            if (response != null && response.isSuccessful()) {
                result = response.body();
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
        return this;
    }

    public ProductResult<Comment> getResult() {
        return result;
    }
}