package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.ApplyForGroupData;
import com.onyx.android.dr.interfaces.ApplyForGroupView;
import com.onyx.android.dr.request.cloud.DisposePendingGroupRequest;
import com.onyx.android.dr.request.cloud.RequestGetPendingGroups;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class ApplyForGroupPresenter {
    private ApplyForGroupView applyForGroupView;
    private ApplyForGroupData applyForGroupData;

    public ApplyForGroupPresenter(ApplyForGroupView applyForGroupView) {
        this.applyForGroupView = applyForGroupView;
        applyForGroupData = new ApplyForGroupData();
    }

    public void getPendingGroups() {
        final RequestGetPendingGroups req = new RequestGetPendingGroups();
        applyForGroupData.getPendingGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                applyForGroupView.setGetPendingGroupResult(req.getGroup());
            }
        });
    }

    public void disposePendingGroup(String id, int param) {
        final DisposePendingGroupRequest req = new DisposePendingGroupRequest(id, param);
        applyForGroupData.disposePendingGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                applyForGroupView.setDisposePendingGroupResult(req.getGroup());
            }
        });
    }
}
