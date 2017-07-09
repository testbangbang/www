package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/7/7.
 */

public class CloudGroupRecursiveListRequest extends BaseCloudRequest {

    List<CloudGroup> rootGroupList = new ArrayList<>();

    public List<CloudGroup> getRootGroupList() {
        return this.rootGroupList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<List<CloudGroup>> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(
                parent.getCloudConf().getApiBase()).getRecursiveGroupList());
        if (response.isSuccessful()) {
            rootGroupList = response.body();
        }
    }
}
