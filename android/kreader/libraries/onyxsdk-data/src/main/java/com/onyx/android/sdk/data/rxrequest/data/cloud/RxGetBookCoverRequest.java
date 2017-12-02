package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.GetBookCoverRequestBean;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/7.
 */

public class RxGetBookCoverRequest extends RxBaseBookStoreRequest {

    private final GetBookCoverRequestBean requestBean;
    private ResponseBody result;

    public RxGetBookCoverRequest(GetBookCoverRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public RxGetBookCoverRequest call() throws Exception {
        try {
            Response<ResponseBody> response = getService().getBookCover(requestBean.uniqueId, requestBean.type).execute();
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