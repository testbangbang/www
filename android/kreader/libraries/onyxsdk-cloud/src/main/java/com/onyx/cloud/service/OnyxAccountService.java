package com.onyx.cloud.service;

import com.onyx.cloud.model.OnyxAccount;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by zhuzeng on 8/10/16.
 */
public interface OnyxAccountService {

    @POST("account/signup")
    Call<OnyxAccount> signup(@Body final OnyxAccount account);

}
