package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.model.ProductShared;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;

import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/13.
 */

public class RxProductShareListRequest extends RxBaseBookStoreRequest {
    private final String bookId;
    public ProductResult<ProductShared> result;

    public RxProductShareListRequest(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public RxProductShareListRequest call() throws Exception {
        try {
            Response<ProductResult<ProductShared>> response = getService().productSharedList(bookId).execute();
            if (response != null && response.isSuccessful()) {
                result = response.body();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return this;
    }

    public ProductResult<ProductShared> getResult() {
        return result;
    }
}