package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.OnyxGroup;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class GroupUpdateRequest extends BaseCloudRequest {

    private OnyxGroup resultGroup;
    private long groupId;
    private OnyxGroup group;
    private String sessionToken;

    public GroupUpdateRequest(long groupId, OnyxGroup group, String sessionToken) {
        this.groupId = groupId;
        this.group = group;
        this.sessionToken = sessionToken;
    }

    public OnyxGroup getResultGroup() {
        return resultGroup;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call<OnyxGroup> call = ServiceFactory.getGroupService(parent.getCloudConf().getApiBase())
                .updateGroup(groupId, group, sessionToken);
        Response<OnyxGroup> response = call.execute();
        if (response.isSuccessful()) {
            resultGroup = response.body();
        }
    }
}
