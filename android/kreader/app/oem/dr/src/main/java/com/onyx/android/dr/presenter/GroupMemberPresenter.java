package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.GroupMemberData;
import com.onyx.android.dr.interfaces.GroupMemberView;
import com.onyx.android.dr.request.cloud.DeleteGroupMemberRequest;
import com.onyx.android.dr.request.cloud.RequestGroupMember;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class GroupMemberPresenter {
    private GroupMemberView groupMemberView;
    private GroupMemberData groupMemberData;

    public GroupMemberPresenter(GroupMemberView groupMemberView) {
        this.groupMemberView = groupMemberView;
        groupMemberData = new GroupMemberData();
    }

    public void getGroupMember() {
        final RequestGroupMember req = new RequestGroupMember();
        groupMemberData.requestGroupMember(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                groupMemberView.setGroupMemberResult(req.getGroup());
            }
        });
    }

    public void deleteGroupMember() {
        final DeleteGroupMemberRequest req = new DeleteGroupMemberRequest();
        groupMemberData.requestDeleteGroupMember(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                groupMemberView.setDeleteGroupMemberResult(req.getResult() != null);
            }
        });
    }
}
