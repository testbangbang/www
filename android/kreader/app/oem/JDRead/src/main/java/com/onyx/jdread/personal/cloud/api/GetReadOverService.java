package com.onyx.jdread.personal.cloud.api;

import com.onyx.jdread.main.common.AppBaseInfo;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadOverInfoBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by li on 2018/1/2.
 */

public interface GetReadOverService {
    @GET("mark_books/stat")
    Call<ReadOverInfoBean> getReadOverBook(@Query(AppBaseInfo.JD_USER_NAME) String userName,
                                           @QueryMap Map<String, String> map);
}
