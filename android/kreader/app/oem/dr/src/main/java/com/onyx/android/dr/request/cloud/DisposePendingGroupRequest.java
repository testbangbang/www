package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.ChangePendingGroupBean;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class DisposePendingGroupRequest extends AutoNetWorkConnectionBaseCloudRequest {
    private final String id;
    private final int text;
    private ChangePendingGroupBean groupMember;

    public DisposePendingGroupRequest(String id, int text) {
        this.id = id;
        this.text = text;
    }

    public ChangePendingGroupBean getGroup() {
        return groupMember;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        try {
            Response<ChangePendingGroupBean> response = executeCall(ServiceFactory.getContentService(
                    parent.getCloudConf().getApiBase()).changePendingGroupState(id, text));
            if (response != null) {
                groupMember = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
