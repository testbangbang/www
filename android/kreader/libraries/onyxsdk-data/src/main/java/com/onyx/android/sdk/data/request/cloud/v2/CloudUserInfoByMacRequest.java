package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.GroupUserInfo;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/7/8.
 */
public class CloudUserInfoByMacRequest extends BaseCloudRequest {

    private String mac;
    private GroupUserInfo groupUserInfo;

    public CloudUserInfoByMacRequest(String mac) {
        this.mac = mac;
    }

    public GroupUserInfo getGroupUserInfo() {
        return groupUserInfo;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<GroupUserInfo> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(
                parent.getCloudConf().getApiBase()).getGroupUserInfo(mac));
        if (response.isSuccessful()) {
            groupUserInfo = response.body();
        }
    }
}
