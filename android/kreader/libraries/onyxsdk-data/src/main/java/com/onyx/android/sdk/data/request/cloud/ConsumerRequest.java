package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Consumer;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class ConsumerRequest extends BaseCloudRequest {

    private Consumer consumer;
    private long consumerId;
    private String sessionToken;

    public ConsumerRequest(long id, String sessionToken) {
        this.consumerId = id;
        this.sessionToken = sessionToken;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call<Consumer> call = ServiceFactory.getConsumerService(parent.getCloudConf().getApiBase())
                .getConsumer(consumerId, sessionToken);
        Response<Consumer> response = call.execute();
        if (response.isSuccessful()) {
            consumer = response.body();
        }
    }
}
