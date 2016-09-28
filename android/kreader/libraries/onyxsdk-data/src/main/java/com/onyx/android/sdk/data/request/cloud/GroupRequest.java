package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Group;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class GroupRequest extends BaseCloudRequest {

    private long id;
    private String param;
    private String sessionToken;
    private Group group;

    public GroupRequest(long groupId, String param, String sessionToken) {
        this.id = groupId;
        this.param = param;
        this.sessionToken = sessionToken;
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call<Group> call = ServiceFactory.getGroupService(parent.getCloudConf().getApiBase())
                .getGroup(id, param, sessionToken);
        Response<Group> response = call.execute();
        if (response.isSuccessful()) {
            group = response.body();
        }
    }
}
