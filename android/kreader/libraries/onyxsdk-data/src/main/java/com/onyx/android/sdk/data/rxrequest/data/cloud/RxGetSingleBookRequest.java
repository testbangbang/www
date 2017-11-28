package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;

import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/7.
 */

public class RxGetSingleBookRequest extends RxBaseBookStoreRequest {

    private final String uniqueId;
    private Product result;

    public RxGetSingleBookRequest(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public RxGetSingleBookRequest call() throws Exception {
        try {
            Response<Product> response = getService().book(uniqueId).execute();
            if (response != null && response.isSuccessful()) {
                result = response.body();
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return this;
    }

    public Product getResult() {
        return result;
    }
}