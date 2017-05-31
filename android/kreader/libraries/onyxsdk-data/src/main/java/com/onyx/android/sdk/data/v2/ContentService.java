package com.onyx.android.sdk.data.v2;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.v2.AuthToken;
import com.onyx.android.sdk.data.model.v2.CloudLibrary;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.ProductResult;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by suicheng on 2017/4/26.
 */

public interface ContentService {
    String CONTENT_AUTH_PREFIX = "Bearer ";

    @POST("auth/local")
    Call<AuthToken> getAccountToken(@Body final BaseAuthAccount account);

    @GET("users/me")
    Call<ResponseBody> getAccount(@Header(Constant.HEADER_AUTHORIZATION) final String auth);

    @GET("librarys/my")
    Call<List<CloudLibrary>> loadLibraryList(@Header(Constant.HEADER_AUTHORIZATION) final String token);

    @GET("librarys/books")
    Call<ProductResult<CloudMetadata>> loadBookList(@Query(Constant.WHERE_TAG) final String param);

    @GET("librarys/{id}/books")
    Call<ProductResult<CloudMetadata>> loadBookList(@Path(Constant.ID_TAG) final String libraryId,
                                                    @Query(Constant.WHERE_TAG) final String param);

    @GET("books/{id}")
    Call<CloudMetadata> loadBook(@Path(Constant.ID_TAG) final String idString);
}
