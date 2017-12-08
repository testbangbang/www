package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.model.HomeworkRequestModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by lxm on 2017/12/5.
 */

public interface OnyxHomeworkService {

    @GET("homeworks/{id}" )
    Call<HomeworkRequestModel> getHomeworks(@Path("id") String id);

}
