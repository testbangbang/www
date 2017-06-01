package com.onyx.android.sdk.data.utils;

import com.onyx.android.sdk.data.common.ContentException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/6/1.
 */

public class RetrofitUtils {

    public static  <T> Response<T> executeCall(Call<T> call) throws Exception {
        Response<T> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new ContentException.NetworkException(e);
        }
        if (!response.isSuccessful()) {
            String errorBody = response.errorBody().string();
            throw new ContentException.CloudException(JSONObjectParseUtils.parseObject(errorBody, ResultCode.class));
        }
        return response;
    }
}
