package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Group;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class GroupCreateRequest extends BaseCloudRequest {

    private Group resultGroup;
    private Group group;
    private String sessionToken;

    public GroupCreateRequest(Group group, String sessionToken) {
        this.group = group;
        this.sessionToken = sessionToken;
    }

    public Group getResultGroup() {
        return resultGroup;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call<Group> call = ServiceFactory.getGroupService(parent.getCloudConf().getApiBase())
                .createGroup(group, sessionToken);
        Response<Group> response = call.execute();
        if (response.isSuccessful()) {
            resultGroup = response.body();
        }
    }
}
