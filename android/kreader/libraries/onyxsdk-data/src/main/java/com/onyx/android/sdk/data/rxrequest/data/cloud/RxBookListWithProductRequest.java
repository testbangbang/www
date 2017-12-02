package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;

import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/13.
 */

public class RxBookListWithProductRequest extends RxBaseBookStoreRequest {

    private final String params;
    private ProductResult<Product> result;

    public RxBookListWithProductRequest(String params) {
        this.params = params;
    }

    @Override
    public RxBookListWithProductRequest call() throws Exception {
        try {
            Response<ProductResult<Product>> response = getService().bookList(params).execute();
            if (response != null && response.isSuccessful()) {
                result = response.body();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return this;
    }

    public ProductResult<Product> getResult() {
        return result;
    }
}