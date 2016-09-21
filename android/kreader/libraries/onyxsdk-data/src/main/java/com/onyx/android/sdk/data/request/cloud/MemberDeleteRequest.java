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
    private String sessionToken;
    private boolean isSuccess = true;

    public MemberDeleteRequest(long groupId, long accountId, String sessionToken) {
        this.id = groupId;
        this.accountId = accountId;
        this.sessionToken = sessionToken;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response response = ServiceFactory.getGroupService(parent.getCloudConf().getApiBase())
                .deleteGroupMember(id, accountId, sessionToken).execute();
        isSuccess = response.isSuccessful();
    }
}
