package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.model.Category;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;

import java.util.List;

import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/13.
 */

public class RxBookContainerListRequest extends RxBaseBookStoreRequest {
    private List<Category> result;

    public RxBookContainerListRequest() {
    }

    @Override
    public RxBookContainerListRequest call() throws Exception {
        try {
            Response<List<Category>> response = getService().bookContainerList().execute();
            if (response != null && response.isSuccessful()) {
                result = response.body();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return this;
    }

    public List<Category> getResult() {
        return result;
    }
}