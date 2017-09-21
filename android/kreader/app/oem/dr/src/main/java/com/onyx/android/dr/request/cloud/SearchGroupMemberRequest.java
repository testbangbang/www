package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.GroupMemberBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class SearchGroupMemberRequest extends BaseCloudRequest {
    private final String id;
    private final String text;
    private GroupMemberBean groupMember = new GroupMemberBean();

    public SearchGroupMemberRequest(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public GroupMemberBean getGroup() {
        return groupMember;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        try {
            Response<GroupMemberBean> response = executeCall(ServiceFactory.getContentService(
                    parent.getCloudConf().getApiBase()).searchGroupMember(id, text));
            if (response != null) {
                groupMember = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
