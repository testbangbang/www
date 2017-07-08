package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/6/17.
 */
public class CloudGroupListRequest extends BaseCloudRequest {

    private String parentGroupId;
    private CloudGroup childGroup;

    public CloudGroupListRequest(String parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

    public CloudGroup getChildGroup() {
        return childGroup;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<CloudGroup> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .getGroupList(parentGroupId));
        if (response.isSuccessful()) {
            childGroup = response.body();
        }
    }
}
