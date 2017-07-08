package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/6/16.
 */
public class AccountBindByDeviceRequest extends BaseCloudRequest {

    private NeoAccountBase account;
    private List<CloudGroup> groupList;
    private DeviceBind deviceBind;

    public AccountBindByDeviceRequest(List<CloudGroup> groupList, DeviceBind deviceBind) {
        this.groupList = groupList;
        this.deviceBind = deviceBind;
    }

    public NeoAccountBase getAccount() {
        return account;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<NeoAccountBase> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(
                parent.getCloudConf().getApiBase()).createUserByDevice(groupList.get(0)._id, deviceBind));
        if (response.isSuccessful()) {
            account = response.body();
        }
    }
}
