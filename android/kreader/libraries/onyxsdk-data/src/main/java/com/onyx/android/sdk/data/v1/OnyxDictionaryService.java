package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.Dictionary;
import com.onyx.android.sdk.data.model.ProductResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by suicheng on 2016/8/12.
 */
public interface OnyxDictionaryService {

    @GET("dictionary/list")
    Call<ProductResult<Dictionary>> dictionaryList(@Query(Constant.WHERE_TAG) final String param);
}
