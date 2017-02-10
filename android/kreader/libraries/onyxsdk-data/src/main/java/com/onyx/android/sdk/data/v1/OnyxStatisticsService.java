package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.model.JsonRespone;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.model.PushRecord;
import com.squareup.okhttp.Response;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ming on 2017/2/7.
 */

public interface OnyxStatisticsService {

    @POST("statistics")
    Call<JsonRespone> pushStatistics(@Body final List<OnyxStatisticsModel> onyxStatisticseModels);
}
