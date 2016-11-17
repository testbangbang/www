package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.OnyxGroup;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class GroupCreateRequest extends BaseCloudRequest {

    private OnyxGroup resultGroup;
    private OnyxGroup group;

    public GroupCreateRequest(OnyxGroup group) {
        this.group = group;
    }

    public OnyxGroup getResultGroup() {
        return resultGroup;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call<OnyxGroup> call = ServiceFactory.getGroupService(parent.getCloudConf().getApiBase())
                .createGroup(group, getAccountSessionToken());
        Response<OnyxGroup> response = executeCall(call);
        if (response.isSuccessful()) {
            resultGroup = response.body();
        }
    }
}
