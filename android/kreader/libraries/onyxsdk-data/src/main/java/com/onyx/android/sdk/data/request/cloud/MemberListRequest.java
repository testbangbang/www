package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Member;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class MemberListRequest extends BaseCloudRequest {

    private long id;
    private List<Member> memberList;

    public MemberListRequest(long groupId) {
        this.id = groupId;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<List<Member>> response = ServiceFactory.getGroupService(parent.getCloudConf().getApiBase())
                .getMemberList(id, getAccountSessionToken()).execute();
        if (response.isSuccessful()) {
            memberList = response.body();
        }
    }
}
