package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CreateGroupCommonBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by hehai on 17-7-28.
 */

public class RequestGetSchoolInfo extends BaseCloudRequest {
    private List<CreateGroupCommonBean> groups = new ArrayList<>();
    private final String text;
    private final String parentId;

    public RequestGetSchoolInfo(String text, String parent){
        this.text = text;
        this.parentId = parent;
    }

    public List<CreateGroupCommonBean> getGroups() {
        return groups;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getRootGroupList(parent);
    }

    private void getRootGroupList(CloudManager parent) {
        try {
            Response<List<CreateGroupCommonBean>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                    .searchSchool(text, parentId));
            if (response != null) {
                groups = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
