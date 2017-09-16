package com.onyx.android.dr.presenter;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.CreateGroupData;
import com.onyx.android.dr.interfaces.CreateGroupView;
import com.onyx.android.dr.request.cloud.CreateGroupRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.CreateGroupBean;
import com.onyx.android.sdk.utils.StringUtils;

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

    public void createGroup(CreateGroupBean createGroupBean) {
        final CreateGroupRequest req = new CreateGroupRequest(createGroupBean);
        createGroupData.createGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (StringUtils.isNullOrEmpty(e + tag)){
                    createGroupView.setCreateGroupResult(req.getResult());
                }else{
                    CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.create_group_success));
                }
            }
        });
    }

    public List<String> getGradeData() {
        List<String> list = createGroupData.loadGradeData(DRApplication.getInstance());
        return list;
    }

    public List<String> getAnnualData() {
        List<String> list = createGroupData.loadAnnualData(DRApplication.getInstance());
        return list;
    }

    public List<String> getClassData() {
        List<String> list = createGroupData.loadClassData(DRApplication.getInstance());
        return list;
    }
}
