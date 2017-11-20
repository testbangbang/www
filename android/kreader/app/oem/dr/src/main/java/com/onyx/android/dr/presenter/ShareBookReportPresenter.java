package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.GroupMemberData;
import com.onyx.android.dr.data.LoginData;
import com.onyx.android.dr.interfaces.ShareBookReportView;
import com.onyx.android.dr.request.cloud.RequestGetMyGroup;
import com.onyx.android.dr.request.cloud.RequestGroupMember;
import com.onyx.android.dr.request.cloud.ShareBookReportRequest;
import com.onyx.android.dr.request.cloud.ShareInformalEssayRequest;
import com.onyx.android.dr.request.cloud.ShareReadingRateRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.GroupBean;
import com.onyx.android.sdk.data.model.v2.GroupMemberBean;
import com.onyx.android.sdk.data.model.v2.ShareBookReportRequestBean;

import java.util.List;

/**
 * Created by li on 2017/9/27.
 */

public class ShareBookReportPresenter {
    private ShareBookReportView shareBookReportView;
    private LoginData loginData;
    private GroupMemberData groupMemberData;

    public ShareBookReportPresenter(ShareBookReportView shareBookReportView) {
        this.shareBookReportView = shareBookReportView;
        loginData = new LoginData();
        groupMemberData = new GroupMemberData();
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

    public void getGroupMember(String id, String param) {
        final RequestGroupMember req = new RequestGroupMember(id, param);
        groupMemberData.requestGroupMember(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GroupMemberBean group = req.getGroup();
                if(group != null) {
                    shareBookReportView.setGroupMemberResult(group);
                }
            }
        });
    }

    public void shareImpression(String library, String[] childrenId) {
        ShareBookReportRequestBean requestBean = new ShareBookReportRequestBean();
        requestBean.setChildren(childrenId);
        final ShareBookReportRequest rq = new ShareBookReportRequest(library, requestBean);
        groupMemberData.shareImpression(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void shareInformalEssay(String library, String[] childrenId) {
        ShareBookReportRequestBean requestBean = new ShareBookReportRequestBean();
        requestBean.setChildren(childrenId);
        final ShareInformalEssayRequest rq = new ShareInformalEssayRequest(library, requestBean);
        groupMemberData.shareInformalEssay(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void shareReadingRate(String library, String[] childrenId) {
        ShareBookReportRequestBean requestBean = new ShareBookReportRequestBean();
        requestBean.setChildren(childrenId);
        final ShareReadingRateRequest rq = new ShareReadingRateRequest(library, requestBean);
        groupMemberData.shareReadingRate(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
