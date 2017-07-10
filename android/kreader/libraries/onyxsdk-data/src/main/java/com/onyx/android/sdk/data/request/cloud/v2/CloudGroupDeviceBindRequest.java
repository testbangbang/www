package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.UserInfoBind;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/6/17.
 */
public class CloudGroupDeviceBindRequest extends BaseCloudRequest {
    private String groupId;
    private List<UserInfoBind> userInfoBindList;

    public CloudGroupDeviceBindRequest(String groupId) {
        this.groupId = groupId;
    }

    public List<UserInfoBind> getUserInfoBindList() {
        return userInfoBindList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<List<UserInfoBind>> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(
                parent.getCloudConf().getApiBase()).getDeviceBindList(groupId));
        if (response.isSuccessful()) {
            userInfoBindList = response.body();
        }
    }
}
