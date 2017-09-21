package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.ManageGroupData;
import com.onyx.android.dr.interfaces.ManageGroupView;
import com.onyx.android.dr.request.cloud.RequestAllGroup;
import com.onyx.android.dr.request.cloud.RequestExitGroup;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.DeleteGroupMemberBean;

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

    public void getAllGroup() {
        final RequestAllGroup req = new RequestAllGroup();
        manageGroupData.requestAllGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                managerGroupView.setGroupMemberResult(req.getGroup());
            }
        });
    }

    public void exitGroup(String id, DeleteGroupMemberBean bean) {
        final RequestExitGroup req = new RequestExitGroup(id, bean);
        manageGroupData.requestExitGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                managerGroupView.setExitGroupResult(req.getResult());
            }
        });
    }
}
