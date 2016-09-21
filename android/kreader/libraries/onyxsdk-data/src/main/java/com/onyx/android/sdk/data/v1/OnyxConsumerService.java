package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.model.Consumer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by suicheng on 2016/9/20.
 */
public interface OnyxConsumerService {

    @GET("consumer")
    Call<List<Consumer>> getConsumerList();

    @GET("consumer/{id}")
    Call<Consumer> getConsumer(@Path("id") final long id);

}
