package com.onyx.cloud.service;

import com.onyx.cloud.model.OnyxAccount;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

/**
 * Created by zhuzeng on 8/10/16.
 */
public interface OnyxBookStoreService {

    @GET("book/list")
    Call<Object> bookList();


}
