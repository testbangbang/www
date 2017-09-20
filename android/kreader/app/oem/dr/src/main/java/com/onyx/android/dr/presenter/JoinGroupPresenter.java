package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.JoinGroupData;
import com.onyx.android.dr.interfaces.JoinGroupView;
import com.onyx.android.dr.request.cloud.JoinGroupRequest;
import com.onyx.android.dr.request.cloud.RequestGetRelatedGroup;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.JoinGroupBean;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class JoinGroupPresenter {
    private JoinGroupView joinGroupView;
    private JoinGroupData joinGroupData;

    public JoinGroupPresenter(JoinGroupView joinGroupView) {
        this.joinGroupView = joinGroupView;
        joinGroupData = new JoinGroupData();
    }

    public void searchGroup(String name) {
        final RequestGetRelatedGroup req = new RequestGetRelatedGroup(name);
        joinGroupData.searchGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                joinGroupView.setSearchGroupResult(req.getGroup());
            }
        });
    }

    public void joinGroup(JoinGroupBean bean) {
        final JoinGroupRequest req = new JoinGroupRequest(bean);
        joinGroupData.joinGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                joinGroupView.setJoinGroupResult(req.getGroup());
            }
        });
    }
}
