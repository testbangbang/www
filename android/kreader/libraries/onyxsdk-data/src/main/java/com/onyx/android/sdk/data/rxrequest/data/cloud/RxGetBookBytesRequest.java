package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.GetBookBytesRequestBean;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/7.
 */

public class RxGetBookBytesRequest extends RxBaseBookStoreRequest {
    private final GetBookBytesRequestBean requestBean;
    private ResponseBody result;

    public RxGetBookBytesRequest(GetBookBytesRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public RxGetBookBytesRequest call() throws Exception {
        try {
            Response<ResponseBody> response = getService().getBookBytes(requestBean.uniqueId, requestBean.type, requestBean.sessionToken).execute();
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