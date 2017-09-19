package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CreateGroupCommonBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class DeleteGroupMemberRequest extends BaseCloudRequest {
    private CreateGroupCommonBean createGroupResultBean;

    public CreateGroupCommonBean getResult() {
        return createGroupResultBean;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getDeleteGroupMemberState(parent);
    }

    private void getDeleteGroupMemberState(CloudManager parent) {
        createGroupResultBean  = new CreateGroupCommonBean();
    }
}
