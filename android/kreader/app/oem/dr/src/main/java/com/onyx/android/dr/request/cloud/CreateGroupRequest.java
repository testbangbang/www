package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CreateGroupCommonBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class CreateGroupRequest extends BaseCloudRequest {
    private CreateGroupCommonBean createGroupBeen;
    private CreateGroupCommonBean result;

    public CreateGroupRequest(CreateGroupCommonBean bean) {
        this.createGroupBeen = bean;
    }

    public CreateGroupCommonBean getResult() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getCreateGroupState(parent);
    }

    private void getCreateGroupState(CloudManager parent) {
        try {
            Response<CreateGroupCommonBean> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).createGroup(createGroupBeen));
            if (response != null) {
                result = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
