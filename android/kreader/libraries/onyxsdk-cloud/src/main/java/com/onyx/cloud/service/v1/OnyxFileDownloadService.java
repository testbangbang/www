package com.onyx.cloud.service.v1;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by suicheng on 2016/8/13.
 */
public interface OnyxFileDownloadService {
    @Streaming
    @GET
    Call<ResponseBody> fileDownload(@Url final String url);
}
