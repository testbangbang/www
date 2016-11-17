package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Member;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class MemberAddRequest extends BaseCloudRequest {

    private long id;
    private Member member;
    private Member resultMember;

    public MemberAddRequest(long groupId, Member member) {
        this.id = groupId;
        this.member = member;
    }

    public Member getResultMember() {
        return resultMember;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<Member> response = executeCall(ServiceFactory.getGroupService(parent.getCloudConf().getApiBase())
                .addGroupMember(id, member, getAccountSessionToken()));
        if (response.isSuccessful()) {
            resultMember = response.body();
        }
    }
}
