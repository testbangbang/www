package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.LoginData;
import com.onyx.android.dr.interfaces.ShareBookReportView;
import com.onyx.android.dr.request.cloud.RequestGetMyGroup;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.GroupBean;

import java.util.List;

/**
 * Created by li on 2017/9/27.
 */

public class ShareBookReportPresenter {
    private ShareBookReportView shareBookReportView;
    private LoginData loginData;

    public ShareBookReportPresenter(ShareBookReportView shareBookReportView) {
        this.shareBookReportView = shareBookReportView;
        loginData = new LoginData();
    }

    public void getAllGroup() {
        final RequestGetMyGroup req = new RequestGetMyGroup();
        loginData.getMyGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<GroupBean> groups = req.getGroups();
                if (groups != null && groups.size() > 0) {
                    groups.remove(0);
                    shareBookReportView.setGroupData(groups);
                }
            }
        });
    }
}
