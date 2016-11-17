package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Member;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class MemberDeleteRequest extends BaseCloudRequest {

    private long id;
    private long accountId;
    private boolean isSuccess = true;

    public MemberDeleteRequest(long groupId, long accountId) {
        this.id = groupId;
        this.accountId = accountId;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response response = executeCall(ServiceFactory.getGroupService(parent.getCloudConf().getApiBase())
                .deleteGroupMember(id, accountId, getAccountSessionToken()));
        isSuccess = response.isSuccessful();
    }
}
