package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.Dictionary;
import com.onyx.android.sdk.data.model.ProductResult;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by suicheng on 2016/8/12.
 */
public interface OnyxDictionaryService {

    @GET("dictionary/getValuesList")
    Call<ProductResult<Dictionary>> dictionaryList(@Query(Constant.WHERE_TAG) final String param);

    @GET("dictionary/{id}")
    Call<Dictionary> dictionaryItem(@Path(Constant.ID_TAG) final String guid);

    @GET("dictionary/{id}/{type}/cover")
    Call<ResponseBody> dictionaryCover(@Path(Constant.ID_TAG) final String guid, @Path(Constant.TYPE_TAG) final String type);

    @GET("dictionary/{id}/{format}/data")
    Call<ResponseBody> dictionaryData(@Path(Constant.ID_TAG) final String guid,
                                      @Path(Constant.FORMAT_TAG) final String type,
                                      @Header(Constant.SESSION_TOKEN_TAG) final String token);



}
