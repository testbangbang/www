package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.ManageGroupData;
import com.onyx.android.dr.interfaces.ManageGroupView;
import com.onyx.android.dr.request.cloud.DeleteGroupMemberRequest;
import com.onyx.android.dr.request.cloud.RequestGroupMember;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class ManageGroupPresenter {
    private ManageGroupView managerGroupView;
    private ManageGroupData manageGroupData;

    public ManageGroupPresenter(ManageGroupView managerGroupView) {
        this.managerGroupView = managerGroupView;
        manageGroupData = new ManageGroupData();
    }

    public void getGroupMember() {
        final RequestGroupMember req = new RequestGroupMember();
        manageGroupData.requestGroupMember(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                managerGroupView.setGroupMemberResult(req.getGroup());
            }
        });
    }

    public void deleteGroupMember() {
        final DeleteGroupMemberRequest req = new DeleteGroupMemberRequest();
        manageGroupData.requestDeleteGroupMember(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                managerGroupView.setDeleteGroupMemberResult(req.getResult() != null);
            }
        });
    }
}
