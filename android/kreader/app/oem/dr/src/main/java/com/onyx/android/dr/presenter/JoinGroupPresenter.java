package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.JoinGroupData;
import com.onyx.android.dr.interfaces.JoinGroupView;
import com.onyx.android.dr.request.cloud.JoinGroupRequest;
import com.onyx.android.dr.request.cloud.RequestGetRelatedGroup;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;

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
                ArrayList<Boolean> checkList = req.getCheckList();
                joinGroupView.setSearchGroupResult(req.getGroup(), checkList);
            }
        });
    }

    public void joinGroup() {
        final JoinGroupRequest req = new JoinGroupRequest();
        joinGroupData.joinGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                joinGroupView.setJoinGroupResult(req.getResult() != null);
            }
        });
    }
}
