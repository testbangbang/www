package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.model.homework.Homework;
import com.onyx.android.sdk.data.model.homework.HomeworkReviewResult;
import com.onyx.android.sdk.data.model.homework.HomeworkSubmitBody;
import com.onyx.android.sdk.data.model.homework.StaticRankResult;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lxm on 2017/12/5.
 */

public interface OnyxHomeworkService {

    @GET("homeworks/{id}/forDevice" )
    Call<Homework> getHomeworks(@Path("id") String id);

    @Multipart
    @POST("homeworks/{id}/anwser")
    Call<ResponseBody> submitAnswers(@Path("id") String id,
                                     @Part("data") HomeworkSubmitBody body,
                                     @PartMap Map<String, RequestBody> noteFileMap);

    @GET("homeworks/myAnwserOnly" )
    Call<HomeworkReviewResult> getAnwsers(@Query("id") String id);

    @GET("homeworks/{id}/staticRank")
    Call<StaticRankResult> staticRank(@Path("id") String id);
}
