package com.onyx.android.dr.presenter;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.data.CreateGroupData;
import com.onyx.android.dr.interfaces.CreateGroupView;
import com.onyx.android.dr.request.cloud.CreateGroupRequest;
import com.onyx.android.dr.request.cloud.RequestCheckGroupNameExist;
import com.onyx.android.dr.request.cloud.RequestGetSchoolInfo;
import com.onyx.android.dr.request.cloud.RequestGetYearData;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.CreateGroupCommonBean;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class CreateGroupPresenter {
    private CreateGroupView createGroupView;
    private CreateGroupData createGroupData;
    private String tag = "";

    public CreateGroupPresenter(CreateGroupView createGroupView) {
        this.createGroupView = createGroupView;
        createGroupData = new CreateGroupData();
    }

    public void createGroup(CreateGroupCommonBean bean) {
        final CreateGroupRequest req = new CreateGroupRequest(bean);
        createGroupData.createGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                createGroupView.setCreateGroupResult(req.getResult());
            }
        });
    }

    public void checkGroupName(String text, String parent) {
        final RequestCheckGroupNameExist req = new RequestCheckGroupNameExist(text, parent);
        createGroupData.checkGroupName(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                createGroupView.setCheckGroupNameResult(req.getGroups());
            }
        });
    }

    public void getSchoolData(String text, String parent) {
        final RequestGetSchoolInfo req = new RequestGetSchoolInfo(text, parent);
        createGroupData.getSchoolInfo(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                createGroupView.setSchoolInfo(req.getGroups());
            }
        });
    }

    public void getYearData(String parent) {
        final RequestGetYearData req = new RequestGetYearData(parent);
        createGroupData.getYearData(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                createGroupView.setYearInfo(req.getGroups());
            }
        });
    }

    public void getGradeData(String parent) {
        final RequestGetYearData req = new RequestGetYearData(parent);
        createGroupData.getYearData(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                createGroupView.setGradeInfo(req.getGroups());
            }
        });
    }

    public void getClassData(String parent) {
        final RequestGetYearData req = new RequestGetYearData(parent);
        createGroupData.getYearData(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                createGroupView.setClassInfo(req.getGroups());
            }
        });
    }

    public List<String> getAnnualData() {
        List<String> list = createGroupData.loadAnnualData(DRApplication.getInstance());
        return list;
    }
}
