package com.onyx.android.dr.presenter;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.bean.CreateGroupBean;
import com.onyx.android.dr.data.CreateGroupData;
import com.onyx.android.dr.interfaces.CreateGroupView;
import com.onyx.android.dr.request.cloud.CreateGroupRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class CreateGroupPresenter {
    private CreateGroupView createGroupView;
    private CreateGroupData createGroupData;

    public CreateGroupPresenter(CreateGroupView createGroupView) {
        this.createGroupView = createGroupView;
        createGroupData = new CreateGroupData();
    }

    public void createGroup(CreateGroupBean createGroupBean) {
        final CreateGroupRequest req = new CreateGroupRequest(createGroupBean);
        createGroupData.createGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                createGroupView.setCreateGroupResult(req.getResult() != null);
            }
        });
    }

    public List<String> getGradeData() {
        List<String> list = createGroupData.loadGradeData(DRApplication.getInstance());
        return list;
    }
}
