package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Consumer;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class ConsumerListRequest extends BaseCloudRequest {

    private List<Consumer> consumerList;
    private String sessionToken;

    public ConsumerListRequest(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public List<Consumer> getConsumerList() {
        return consumerList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call<List<Consumer>> call = ServiceFactory.getConsumerService(parent.getCloudConf().getApiBase())
                .getConsumerList(sessionToken);
        Response<List<Consumer>> response = call.execute();
        if (response.isSuccessful()) {
            consumerList = response.body();
        }
    }
}
