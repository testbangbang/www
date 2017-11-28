package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.model.Category;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;

import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/13.
 */

public class RxBookContainerRequest extends RxBaseBookStoreRequest {
    private final String bookId;
    private Category result;

    public RxBookContainerRequest(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public RxBookContainerRequest call() throws Exception {
        try {
            Response<Category> response = getService().bookContainer(bookId).execute();
            if (response != null && response.isSuccessful()) {
                result = response.body();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return this;
    }

    public Category getResult() {
        return result;
    }
}