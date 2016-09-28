package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class GroupDeleteRequest extends BaseCloudRequest {

    private long id;
    private String sessionToken;
    private boolean isSuccess = true;

    public GroupDeleteRequest(long groupId, String sessionToken) {
        this.id = groupId;
        this.sessionToken = sessionToken;
    }

    public boolean getResult() {
        return isSuccess;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call call = ServiceFactory.getGroupService(parent.getCloudConf().getApiBase())
                .deleteGroup(id, sessionToken);
        Response response = call.execute();
        isSuccess = response.isSuccessful();
    }
}
