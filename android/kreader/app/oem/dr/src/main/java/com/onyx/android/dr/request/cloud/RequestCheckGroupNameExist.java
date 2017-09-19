package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.GroupNameExistBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by hehai on 17-7-28.
 */

public class RequestCheckGroupNameExist extends BaseCloudRequest {
    private final String text;
    private final String parentId;
    private GroupNameExistBean groups;

    public RequestCheckGroupNameExist(String text, String parent){
        this.text = text;
        this.parentId = parent;
    }

    public GroupNameExistBean getGroups() {
        return groups;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getRootGroupList(parent);
    }

    private void getRootGroupList(CloudManager parent) {
        try {
            Response<GroupNameExistBean> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                    .checkExist(text, parentId));
            if (response != null) {
                groups = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
