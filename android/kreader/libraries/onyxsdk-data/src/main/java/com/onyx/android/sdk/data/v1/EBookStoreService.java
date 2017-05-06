package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.CloudMetadata;
import com.onyx.android.sdk.data.model.ProductResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by suicheng on 2017/4/26.
 */

public interface EBookStoreService {

    @GET("books")
    Call<ProductResult<CloudMetadata>> loadBookList(@Query(Constant.WHERE_TAG) final String param);

    @GET("books/{id}")
    Call<CloudMetadata> loadBook(@Path(Constant.ID_TAG) final String idString);
}
