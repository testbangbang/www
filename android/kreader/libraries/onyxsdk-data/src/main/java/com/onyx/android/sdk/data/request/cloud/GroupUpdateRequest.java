package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Group;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class GroupUpdateRequest extends BaseCloudRequest {

    private Group resultGroup;
    private long groupId;
    private Group group;
    private String sessionToken;

    public GroupUpdateRequest(long groupId, Group group, String sessionToken) {
        this.groupId = groupId;
        this.group = group;
        this.sessionToken = sessionToken;
    }

    public Group getResultGroup() {
        return resultGroup;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call<Group> call = ServiceFactory.getGroupService(parent.getCloudConf().getApiBase())
                .updateGroup(groupId, group, sessionToken);
        Response<Group> response = call.execute();
        if (response.isSuccessful()) {
            resultGroup = response.body();
        }
    }
}
