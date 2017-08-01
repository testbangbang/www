package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.GroupBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Response;

/**
 * Created by hehai on 17-7-31.
 */

public class RequestGetMyGroup extends BaseCloudRequest {

    private List<GroupBean> groups;

    public List<GroupBean> getGroups() {
        return groups;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        try {
            Response<List<GroupBean>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                    .getMyGroup());
            if (response != null) {
                groups = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
