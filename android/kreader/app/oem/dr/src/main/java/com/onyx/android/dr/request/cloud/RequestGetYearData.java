package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CreateGroupCommonBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by hehai on 17-7-28.
 */

public class RequestGetYearData extends BaseCloudRequest {
    private CreateGroupCommonBean groups = new CreateGroupCommonBean();
    private final String parentId;

    public RequestGetYearData(String parent){
        this.parentId = parent;
    }

    public CreateGroupCommonBean getGroups() {
        return groups;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getRootGroupList(parent);
    }

    private void getRootGroupList(CloudManager parent) {
        try {
            Response<CreateGroupCommonBean> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                    .getYearData(parentId));
            if (response != null) {
                groups = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
