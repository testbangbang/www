package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.model.JsonRespone;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.model.PushRecord;
import com.onyx.android.sdk.data.model.StatisticsResult;
import com.squareup.okhttp.Response;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ming on 2017/2/7.
 */

public interface OnyxStatisticsService {

    @POST("statistics/person/")
    Call<JsonRespone> pushStatistics(@Body final List<OnyxStatisticsModel> onyxStatisticseModels);

    @GET("statistics/person/{mac}")
    Call<StatisticsResult> getStatistics(@Path("mac") final String mac);
}
