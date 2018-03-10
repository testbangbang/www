package com.onyx.edu.homework.request;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.homework.StaticRankResult;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by lxm on 2018/1/31.
 */

public class StaticRankRequest extends BaseCloudRequest {

    private String childId;
    private String id;
    private Response<StaticRankResult> response;

    public StaticRankRequest setChildId(String childId) {
        this.childId = childId;
        return this;
    }

    public StaticRankRequest setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        response = executeCall(ServiceFactory.getHomeworkService(parent.getCloudConf().getApiBase()).staticRank(childId, id));
    }

    public StaticRankResult getStaticRank() {
        return response.body();
    }

    public boolean isSuccess() {
        return response != null && response.isSuccessful();
    }
}
