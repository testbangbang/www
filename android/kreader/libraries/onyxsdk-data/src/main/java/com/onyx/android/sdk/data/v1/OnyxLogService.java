package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.model.LogCollection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by suicheng on 2017/3/22.
 */
public interface OnyxLogService {

    @POST("http://47.90.54.36:8000/api/reports")
    Call<ResponseBody> reportLogCollection(@Body final LogCollection model);
}
